package build.dream.catering.aspects;

import build.dream.common.annotations.ApiRestAction;
import build.dream.common.annotations.ModelAndViewAction;
import build.dream.common.api.ApiRest;
import build.dream.common.constants.Constants;
import build.dream.common.constants.ErrorConstants;
import build.dream.common.exceptions.CustomException;
import build.dream.common.exceptions.Error;
import build.dream.common.models.BasicModel;
import build.dream.common.utils.*;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
@Component
@Order
public class CallActionAspect {
    @Autowired
    private ApplicationContext applicationContext;
    private ConcurrentHashMap<Class<?>, Object> serviceMap = new ConcurrentHashMap<Class<?>, Object>();

    private Object obtainService(Class<?> serviceClass) {
        if (!serviceMap.contains(serviceClass)) {
            serviceMap.put(serviceClass, applicationContext.getBean(serviceClass));
        }
        return serviceMap.get(serviceClass);
    }

    @Around(value = "execution(public * build.dream.catering.controllers.*.*(..)) && @annotation(apiRestAction)")
    public Object callApiRestAction(ProceedingJoinPoint proceedingJoinPoint, ApiRestAction apiRestAction) {
        HttpServletRequest httpServletRequest = ApplicationHandler.getHttpServletRequest();
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters(httpServletRequest);
        ApiRest apiRest = null;

        Throwable throwable = null;
        try {
            apiRest = callApiRestAction(proceedingJoinPoint, requestParameters, apiRestAction.modelClass(), apiRestAction.serviceClass(), apiRestAction.serviceMethodName());
        } catch (InvocationTargetException e) {
            throwable = e.getTargetException();
        } catch (Throwable t) {
            throwable = t;
        }

        if (throwable != null) {
            LogUtils.error(apiRestAction.error(), proceedingJoinPoint.getTarget().getClass().getName(), proceedingJoinPoint.getSignature().getName(), throwable, requestParameters);
            if (throwable instanceof CustomException) {
                CustomException customException = (CustomException) throwable;
                apiRest = ApiRest.builder().error(new Error(customException.getCode(), customException.getMessage())).build();
            } else {
                apiRest = ApiRest.builder().error(new Error(ErrorConstants.ERROR_CODE_UNKNOWN_ERROR, apiRestAction.error())).build();
            }
        }

        String datePattern = apiRestAction.datePattern();

        if (apiRestAction.zipped()) {
            apiRest.zipData(datePattern);
        }

        if (apiRestAction.encrypted()) {
            String publicKey = TenantUtils.obtainPublicKey();
            apiRest.encryptData(publicKey, datePattern);
        }

        if (apiRestAction.signed()) {
            String platformPrivateKey = ConfigurationUtils.getConfiguration(Constants.PLATFORM_PRIVATE_KEY);
            apiRest.sign(platformPrivateKey, datePattern);
        }

        String returnValue = GsonUtils.toJson(apiRest, datePattern);

        httpServletRequest.setAttribute(Constants.RESPONSE_CONTENT, returnValue);
        return returnValue;
    }

    private ApiRest callApiRestAction(ProceedingJoinPoint proceedingJoinPoint, Map<String, String> requestParameters, Class<? extends BasicModel> modelClass, Class<?> serviceClass, String serviceMethodName) throws Throwable {
        Object returnValue = callAction(proceedingJoinPoint, requestParameters, modelClass, serviceClass, serviceMethodName);
        ApiRest apiRest = null;
        if (returnValue instanceof String) {
            apiRest = ApiRest.fromJson(returnValue.toString());
        } else {
            apiRest = (ApiRest) returnValue;
        }
        return apiRest;
    }

    @Around(value = "execution(public * build.dream.catering.controllers.*.*(..)) && @annotation(modelAndViewAction)")
    public Object callModelAndViewAction(ProceedingJoinPoint proceedingJoinPoint, ModelAndViewAction modelAndViewAction) {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        Object returnValue = null;

        Throwable throwable = null;
        try {
            returnValue = callAction(proceedingJoinPoint, requestParameters, modelAndViewAction.modelClass(), modelAndViewAction.serviceClass(), modelAndViewAction.serviceMethodName());
        } catch (InvocationTargetException e) {
            throwable = e.getTargetException();
        } catch (Throwable t) {
            throwable = t;
        }

        ModelAndView modelAndView = new ModelAndView();
        if (throwable != null) {
            LogUtils.error(modelAndViewAction.error(), proceedingJoinPoint.getTarget().getClass().getName(), proceedingJoinPoint.getSignature().getName(), throwable, requestParameters);
        } else {
            modelAndView.setViewName(modelAndViewAction.viewName());
            if (returnValue instanceof Map) {
                modelAndView.addAllObjects((Map<String, ?>) returnValue);
            }
        }
        return modelAndView;
    }

    private Object callAction(ProceedingJoinPoint proceedingJoinPoint, Map<String, String> requestParameters, Class<? extends BasicModel> modelClass, Class<?> serviceClass, String serviceMethodName) throws Throwable {
        Object returnValue = null;
        if (modelClass != BasicModel.class && serviceClass != Object.class && StringUtils.isNotBlank(serviceMethodName)) {
            BasicModel model = ApplicationHandler.instantiateObject(modelClass, requestParameters);
            model.validateAndThrow();

            Method method = serviceClass.getDeclaredMethod(serviceMethodName, modelClass);
            method.setAccessible(true);

            returnValue = method.invoke(obtainService(serviceClass), model);
        } else {
            returnValue = proceedingJoinPoint.proceed();
        }
        return returnValue;
    }
}