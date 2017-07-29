package build.dream.erp.utils;

import build.dream.erp.constants.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by liuyandong on 2017/3/23.
 */
public class GsonUtils {
    private static Gson instantiateGson(String datePattern) {
        GsonBuilder gsonBuilder = new GsonBuilder().setDateFormat(datePattern).serializeNulls();
        return gsonBuilder.create();
    }

    public static Gson instantiateGson() {
        return instantiateGson(Constants.DEFAULT_DATE_PATTERN);
    }

    public static <T> T jsonToObject(String jsonString, Class<T> clazz, String datePattern) {
        return instantiateGson(datePattern).fromJson(jsonString, clazz);
    }

    public static <T> T jsonToObject(String jsonString, Class<T> clazz) {
        return jsonToObject(jsonString, clazz, Constants.DEFAULT_DATE_PATTERN);
    }

    public static <T> List<T> jsonToList(String jsonString, Class<T> clazz, String datePattern) {
        Gson gson = instantiateGson(datePattern);
        Type type = new TypeToken<ArrayList<JsonObject>>() {
        }.getType();
        List<JsonObject> jsonObjects = gson.fromJson(jsonString, type);
        List<T> list = new ArrayList<T>();
        for (JsonObject jsonObject : jsonObjects) {
            list.add(gson.fromJson(jsonObject, clazz));
        }
        return list;
    }

    public static <T> List<T> jsonToList(String jsonString, Class<T> clazz) {
        return jsonToList(jsonString, clazz, Constants.DEFAULT_DATE_PATTERN);
    }

    public static <K, V> Map<K, V> jsonToMap(String jsonString, Class<K> keyClass, Class<V> valueClass) {
        Gson gson = instantiateGson(Constants.DEFAULT_DATE_PATTERN);
        return gson.fromJson(jsonString, new TypeToken<Map<K, V>>() {}.getType());
    }

    public static <K, V> Map<K, V> jsonToMap(String jsonString, String datePattern, Class<K> keyClass, Class<V> valueClass) {
        Gson gson = instantiateGson(datePattern);
        return gson.fromJson(jsonString, new TypeToken<Map<K, V>>() {}.getType());
    }

    public static String toJson(Object object, String datePattern) {
        return instantiateGson(datePattern).toJson(object);
    }

    public static String toJson(Object object) {
        return toJson(object, Constants.DEFAULT_DATE_PATTERN);
    }
}
