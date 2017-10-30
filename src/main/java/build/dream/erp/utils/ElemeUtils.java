package build.dream.erp.utils;

import build.dream.common.api.ApiRest;
import build.dream.common.utils.CacheUtils;
import build.dream.common.utils.ConfigurationUtils;
import build.dream.common.utils.GsonUtils;
import build.dream.common.utils.ProxyUtils;
import build.dream.erp.constants.Constants;
import net.sf.json.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

/**
 * Created by liuyandong on 2017/3/13.
 */
public class ElemeUtils {
    public static final Map<String, String> HEADERS = new HashMap<String, String>();
    static {
        HEADERS.put("Content-Type", "application/json;charset=utf-8");
    }

    public static String obtainAccessToken(String tenantId) throws IOException {
        String tokenJson = CacheUtils.hget(Constants.KEY_ELEME_TOKEN, Constants.KEY_ELEME_TOKEN + "_" + tenantId);
        JSONObject tokenJsonObject = JSONObject.fromObject(tokenJson);
        return tokenJsonObject.getString("access_token");
    }

    public static String obtainAccessToken(String tenantId, String branchId) throws IOException {
        String tokenJson = CacheUtils.hget(Constants.KEY_ELEME_TOKEN, Constants.KEY_ELEME_TOKEN + "_" + tenantId + "_" + branchId);
        JSONObject tokenJsonObject = JSONObject.fromObject(tokenJson);
        return tokenJsonObject.getString("access_token");
    }

    public static String obtainAccessToken(String tenantType, String tenantId, String branchType, String branchId) throws IOException {
        String accessToken = null;
        if ("1".equals(tenantType)) {
            if (Constants.BRANCH_TYPE_DIRECT_SALE_STORE.equals(tenantType) || Constants.BRANCH_TYPE_HEADQUARTERS.equals(branchType)) {
                accessToken = obtainAccessToken(tenantId);
            } else if ("3".equals(tenantType)) {
                accessToken = obtainAccessToken(tenantId, branchId);
            }
        } else if ("3".equals(tenantType)) {
            accessToken = obtainAccessToken(tenantId, branchId);
        }
        accessToken = "4f4774b7e1eabdb73377826382bb4d32";
        return accessToken;
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

    public static String constructRequestBody(String tenantType, String tenantId, String branchType, String branchId, String action, Map<String, Object> params) throws Exception {
        String appKey = ConfigurationUtils.getConfiguration(Constants.ELEME_APP_KEY);
        String appSecret = ConfigurationUtils.getConfiguration(Constants.ELEME_APP_SECRET);
        Map<String, Object> metas = new HashMap<String, Object>();
        Long timestamp = System.currentTimeMillis() / 1000;
        metas.put("app_key", appKey);
        metas.put("timestamp", timestamp);
        if (params == null) {
            params = new HashMap<String, Object>();
        }
        String accessToken = obtainAccessToken(tenantType, tenantId, branchType, branchId);
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

    public static ApiRest callElemeSystem(String tenantType, String tenantId, String branchType, String branchId, String action, Map<String, Object> params) throws Exception {
        String requestBody = constructRequestBody(tenantType, tenantId, branchType, branchId, action, params);
        Map<String, String> callElemeSystemRequestParameters = new HashMap<String, String>();
        callElemeSystemRequestParameters.put("requestBody", requestBody);
        return ProxyUtils.doPostWithRequestParameters(Constants.SERVICE_NAME_OUT, "eleme", "callElemeSystem", callElemeSystemRequestParameters);
    }
}
