package build.dream.erp.utils;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class BeanUtils {
    public static Map<String, Object> beanToMap(Object object) {
        Class<?> beanClass = object.getClass();
        Field[] fields = beanClass.getDeclaredFields();
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        for (Field field : fields) {
            ReflectionUtils.makeAccessible(field);
            map.put(field.getName(), ReflectionUtils.getField(field, object));
        }
        return map;
    }
}
