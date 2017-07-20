package erp.chain.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuyandong on 2017/3/23.
 */
public class GsonUtils {
    private static final String defaultDatePattern = "yyyy-MM-dd HH:mm:ss";

    private static Gson instantiateGson(String datePattern) {
        GsonBuilder gsonBuilder = new GsonBuilder().setDateFormat(datePattern).serializeNulls();
        return gsonBuilder.create();
    }

    public static Gson instantiateGson() {
        return instantiateGson(defaultDatePattern);
    }

    public static <T> T jsonToObject(String jsonString, Class<T> clazz, String datePattern) {
        return instantiateGson(datePattern).fromJson(jsonString, clazz);
    }

    public static <T> T jsonToObject(String jsonString, Class<T> clazz) {
        return jsonToObject(jsonString, clazz, defaultDatePattern);
    }

    public static <T> List<T> jsonToList(String jsonString, Class<T> clazz, String datePattern) {
        Gson gson = instantiateGson(datePattern);
        Type type = new TypeToken<ArrayList<JsonObject>>() {}.getType();
        List<JsonObject> jsonObjects = gson.fromJson(jsonString, type);
        List<T> list = new ArrayList<T>();
        for (JsonObject jsonObject : jsonObjects) {
            list.add(gson.fromJson(jsonObject, clazz));
        }
        return list;
    }

    public static <T> List<T> jsonToList(String jsonString, Class<T> clazz) {
        return jsonToList(jsonString, clazz, defaultDatePattern);
    }

    public static String toJson(Object object, String datePattern) {
        return instantiateGson(datePattern).toJson(object);
    }

    public static String toJson(Object object) {
        return toJson(object, defaultDatePattern);
    }
}
