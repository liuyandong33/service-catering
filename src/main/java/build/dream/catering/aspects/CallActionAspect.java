package build.dream.catering.aspects;

import build.dream.common.annotations.ApiRestAction;
import build.dream.common.api.ApiRest;
import build.dream.common.constants.Constants;
import build.dream.common.exceptions.ApiException;
import build.dream.common.models.BasicModel;
import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.GsonUtils;
import build.dream.common.utils.LogUtils;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

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
            apiRest = callAction(proceedingJoinPoint, requestParameters, apiRestAction.modelClass(), apiRestAction.serviceClass(), apiRestAction.serviceMethodName());
        } catch (InvocationTargetException e) {
            throwable = e.getTargetException();
        } catch (Throwable t) {
            throwable = t;
        }

        if (throwable != null) {
            LogUtils.error(apiRestAction.error(), proceedingJoinPoint.getTarget().getClass().getName(), proceedingJoinPoint.getSignature().getName(), throwable, requestParameters);
            if (throwable instanceof ApiException) {
                apiRest = new ApiRest(throwable);
            } else {
                apiRest = ApiRest.builder().error(apiRestAction.error()).build();
            }
        }

        String datePattern = apiRestAction.datePattern();

        if (apiRestAction.zipped()) {
            apiRest.zipData(datePattern);
        }

        if (apiRestAction.signed()) {
            apiRest.sign(datePattern);
        }

        String returnValue = GsonUtils.toJson(apiRest, datePattern);

        httpServletRequest.setAttribute(Constants.RESPONSE_CONTENT, returnValue);
        return returnValue;
    }

    private ApiRest callAction(ProceedingJoinPoint proceedingJoinPoint, Map<String, String> requestParameters, Class<? extends BasicModel> modelClass, Class<?> serviceClass, String serviceMethodName) throws Throwable {
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

        ApiRest apiRest = null;
        if (returnValue instanceof String) {
            apiRest = ApiRest.fromJson(returnValue.toString());
        } else {
            apiRest = (ApiRest) returnValue;
        }
        return apiRest;
    }
}
