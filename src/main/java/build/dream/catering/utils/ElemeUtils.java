package build.dream.catering.utils;

import build.dream.common.api.ApiRest;
import build.dream.common.utils.*;
import build.dream.catering.constants.Constants;
import build.dream.catering.tools.ElemeConsumerThread;
import net.sf.json.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.Validate;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by liuyandong on 2017/3/13.
 */
public class ElemeUtils {
    public static final Map<String, String> HEADERS = new HashMap<String, String>();
    static {
        HEADERS.put("Content-Type", "application/json;charset=utf-8");
    }

    public static String obtainAccessToken(String tenantId, String branchId, Integer elemeAccountType) throws IOException {
        String tokenJson = null;
        if (elemeAccountType == Constants.ELEME_ACCOUNT_TYPE_CHAIN_ACCOUNT) {
            tokenJson = CacheUtils.hget(Constants.KEY_ELEME_TOKENS, Constants.ELEME_TOKEN + "_" + tenantId);
        } else if (elemeAccountType == Constants.ELEME_ACCOUNT_TYPE_INDEPENDENT_ACCOUNT) {
            tokenJson = CacheUtils.hget(Constants.KEY_ELEME_TOKENS, Constants.ELEME_TOKEN + "_" + tenantId + "_" + branchId);
        }
        Validate.notNull(tokenJson, "未检索到访问令牌！");
        return JSONObject.fromObject(tokenJson).getString("access_token");
    }

    private static String generateSignature(String appKey, String appSecret, long timestamp, String action, String accessToken, Map<String, Object> params) throws Exception {
        Map<String, Object> sorted = new TreeMap<String, Object>(params);
        sorted.put("app_key", appKey);
        sorted.put("timestamp", timestamp);
        StringBuffer stringBuffer = new StringBuffer();
        for (Map.Entry<String, Object> entry : sorted.entrySet()) {
            stringBuffer.append(entry.getKey()).append("=").append(GsonUtils.toJson(entry.getValue()));
        }
        return DigestUtils.md5Hex(String.format("%s%s%s%s", action, accessToken, stringBuffer, appSecret)).toUpperCase();
    }

    public static String constructRequestBody(String tenantId, String branchId, Integer elemeAccountType, String action, Map<String, Object> params) throws Exception {
        String appKey = ConfigurationUtils.getConfiguration(Constants.ELEME_APP_KEY);
        String appSecret = ConfigurationUtils.getConfiguration(Constants.ELEME_APP_SECRET);
        Map<String, Object> metas = new HashMap<String, Object>();
        Long timestamp = System.currentTimeMillis() / 1000;
        metas.put("app_key", appKey);
        metas.put("timestamp", timestamp);
        if (params == null) {
            params = new HashMap<String, Object>();
        }
        String accessToken = obtainAccessToken(tenantId, branchId, elemeAccountType);
        String signature = generateSignature(appKey, appSecret, timestamp, action, accessToken, params);
        Map<String, Object> requestBody = new HashMap<String, Object>();
        String requestId = UUID.randomUUID().toString();
        requestBody.put("id", requestId);
        requestBody.put("action", action);
        requestBody.put("token", accessToken);
        requestBody.put("metas", metas);
        requestBody.put("params", params);
        requestBody.put("signature", signature);
        requestBody.put("nop", "1.0.0");
        return GsonUtils.toJson(requestBody);
    }

    public static ApiRest callElemeSystem(String tenantId, String branchId, Integer elemeAccountType, String action, Map<String, Object> params) throws Exception {
        String requestBody = constructRequestBody(tenantId, branchId, elemeAccountType, action, params);
        Map<String, String> callElemeSystemRequestParameters = new HashMap<String, String>();
        callElemeSystemRequestParameters.put("requestBody", requestBody);
        return ProxyUtils.doPostWithRequestParameters(Constants.SERVICE_NAME_OUT, "eleme", "callElemeSystem", callElemeSystemRequestParameters);
    }

    public static boolean checkSignature(JSONObject callbackJsonObject, String appSecret) {
        Map<String, Object> map = new TreeMap<String, Object>(callbackJsonObject);
        String signature = map.remove("signature").toString();
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            stringBuilder.append(entry.getKey()).append("=").append(entry.getValue());
        }
        stringBuilder.append(appSecret);
        return DigestUtils.md5Hex(stringBuilder.toString()).toUpperCase().equals(signature);
    }

    public static void addElemeMessage(String elemeMessage, String uuid, Integer count) throws IOException {
        Map<String, String> messageMap = new HashMap<String, String>();
        messageMap.put("callbackRequestBody", elemeMessage);
        messageMap.put("uuid", uuid);
        messageMap.put("count", count.toString());

        String partitionCode = ConfigurationUtils.getConfiguration(Constants.PARTITION_CODE);
        QueueUtils.rpush(Constants.KEY_ELEME_CALLBACK_MESSAGE + "_" + partitionCode, GsonUtils.toJson(messageMap));
    }

    public static Map<String, String> takeElemeMessage() throws IOException {
        String partitionCode = ConfigurationUtils.getConfiguration(Constants.PARTITION_CODE);
        String message = QueueUtils.blpop(Constants.KEY_ELEME_CALLBACK_MESSAGE + "_" + partitionCode, 1, TimeUnit.HOURS);
        Map<String, String> messageMap = JacksonUtils.readValue(message, Map.class);
        if (!messageMap.containsKey("count")) {
            messageMap.put("count", "10");
        }
        return messageMap;
    }

    public static void startElemeConsumerThread() {
        new Thread(new ElemeConsumerThread()).start();
    }
}
