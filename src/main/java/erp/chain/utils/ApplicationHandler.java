package erp.chain.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by liuyandong on 2017/3/24.
 */
public class ApplicationHandler {
    public static ServletRequestAttributes getServletRequestAttributes() {
        return (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    }

    public static HttpServletRequest getHttpServletRequest() {
        return getServletRequestAttributes().getRequest();
    }

    public static HttpServletResponse getHttpServletResponse() {
        return getServletRequestAttributes().getResponse();
    }

    public static Map<String, String> getRequestParameters() {
        HttpServletRequest httpServletRequest = getHttpServletRequest();
        Map<String, String> requestParameters = new LinkedHashMap<>();
        Map<String, String[]> parameterMap = httpServletRequest.getParameterMap();
        for (Map.Entry<String, String[]> parameterEntry : parameterMap.entrySet()) {
            requestParameters.put(parameterEntry.getKey(), StringUtils.trimToNull(org.springframework.util.StringUtils.arrayToCommaDelimitedString(parameterEntry.getValue())));
        }
        return requestParameters;
    }

    public static String getRequestParameter(String requestParameterName) {
        return StringUtils.trimToNull(org.springframework.util.StringUtils.arrayToCommaDelimitedString(getHttpServletRequest().getParameterValues(requestParameterName)));
    }

    public static String getSessionId() {
        return getServletRequestAttributes().getSessionId();
    }

    public static ServletContext getServletContext() {
        return getHttpServletRequest().getServletContext();
    }

    public static String getRemoteAddress() {
        return getHttpServletRequest().getRemoteAddr();
    }

    public static HttpSession getHttpSession() {
        return getHttpServletRequest().getSession();
    }

    public static <T> T instantiateDomain(Class<T> domainClass, Map<String, String> arguments) throws IllegalAccessException, InstantiationException, NoSuchFieldException, ParseException {
        T domain = domainClass.newInstance();
        Set<Map.Entry<String, String>> entries = arguments.entrySet();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (Map.Entry<String, String> entry : entries) {
            String fieldName = entry.getKey();
            String fieldValue = entry.getValue();
            Field field = domainClass.getField(fieldName);
            field.setAccessible(true);
            Class<?> fieldClass = field.getType();
            if (fieldClass == Byte.class || fieldClass == byte.class) {
                field.set(domain, Byte.valueOf(fieldValue));
            } else if (fieldClass == Short.class || fieldClass == short.class) {
                field.set(domain, Short.valueOf(fieldValue));
            } else if (fieldClass == Integer.class || fieldClass == int.class) {
                field.set(domain, Integer.valueOf(fieldValue));
            } else if (fieldClass == Long.class || fieldClass == long.class) {
                field.set(domain, Long.valueOf(fieldValue));
            } else if (fieldClass == Float.class || fieldClass == float.class) {
                field.set(domain, Float.valueOf(fieldValue));
            } else if (fieldClass == Double.class || fieldClass == double.class) {
                field.set(domain, Double.valueOf(fieldValue));
            } else if (fieldClass == Character.class || fieldClass == char.class) {
                field.set(domain, fieldValue.charAt(0));
            } else if (fieldClass == Boolean.class || fieldClass == boolean.class) {
                field.set(domain, Boolean.valueOf(fieldValue));
            } else if (fieldClass == Date.class) {
                field.set(domain, simpleDateFormat.parse(fieldValue));
            } else if (fieldClass == BigInteger.class) {
                field.set(domain, BigInteger.valueOf(Long.valueOf(fieldValue)));
            } else if (fieldClass == BigDecimal.class) {
                field.set(domain, BigDecimal.valueOf(Double.valueOf(fieldValue)));
            }
        }
        return domain;
    }

    public static void validateNotNull(Map<String, String> parameters, String... names) {
        for (String name : names) {
            Validate.notNull(parameters.get(name), "参数(" + name +")不能为空！");
        }
    }
}