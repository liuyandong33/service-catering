package build.dream.catering.utils;

import build.dream.catering.constants.Constants;
import build.dream.common.api.ApiRest;
import build.dream.common.utils.CacheUtils;
import build.dream.common.utils.ConfigurationUtils;
import build.dream.common.utils.GsonUtils;
import build.dream.common.utils.ProxyUtils;
import net.sf.json.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.RandomUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AnubisUtils {
    public static String generateSignature(String appId, String data, int salt, String accessToken) throws UnsupportedEncodingException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("app_id=" + appId);
        stringBuilder.append("access_token=" + accessToken);
        stringBuilder.append("data=" + URLEncoder.encode(data, Constants.CHARSET_NAME_UTF_8));
        stringBuilder.append("salt=" + salt);
        return DigestUtils.md5Hex(stringBuilder.toString());
    }

    public static Map<String, Object> obtainAccessToken(String url, String appId, String appSecret) throws IOException {
        int salt = RandomUtils.nextInt(1000, 9999);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("app_id=" + appId);
        stringBuilder.append("&salt=" + salt);
        stringBuilder.append("&secret_key=" + appSecret);
        String signature = DigestUtils.md5Hex(URLEncoder.encode(stringBuilder.toString(), Constants.CHARSET_NAME_UTF_8));
        Map<String, String> obtainAccessTokenRequestParameters = new HashMap<String, String>();
        obtainAccessTokenRequestParameters.put("url", url);
        obtainAccessTokenRequestParameters.put("appId", appId);
        obtainAccessTokenRequestParameters.put("salt", String.valueOf(salt));
        obtainAccessTokenRequestParameters.put("signature", signature);
        ApiRest obtainAccessTokenApiRest = ProxyUtils.doGetWithRequestParameters(Constants.SERVICE_NAME_OUT, "anubis", "obtainAccessToken", obtainAccessTokenRequestParameters);
        Validate.isTrue(obtainAccessTokenApiRest.isSuccessful(), obtainAccessTokenApiRest.getError());
        return (Map<String, Object>) obtainAccessTokenApiRest.getData();
    }

    public static Map<String, Object> obtainAccessToken() throws IOException {
        String appId = ConfigurationUtils.getConfiguration(Constants.ANUBIS_APP_ID);
        String appSecret = ConfigurationUtils.getConfiguration(Constants.ANUBIS_APP_SECRET);
        String url = ConfigurationUtils.getConfiguration(Constants.ANUBIS_SERVICE_URL) + Constants.ANUBIS_GET_ACCESS_TOKEN_URI;
        return obtainAccessToken(url, appId, appSecret);
    }

    public static ApiRest callAnubisSystem(String url, String appId, Map<String, Object> data) throws IOException {
        String accessToken = null;
        String accessTokenJson = CacheUtils.get(Constants.KEY_ANUBIS_TOKEN);
        boolean isRetrieveAccessToken = false;
        if (StringUtils.isNotBlank(accessTokenJson)) {
            JSONObject accessTokenJsonObject = JSONObject.fromObject(accessTokenJson);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(accessTokenJsonObject.getLong("expire_time"));

            Date currentTime = new Date();
            if (currentTime.before(calendar.getTime())) {
                accessToken = accessTokenJsonObject.getString("access_token");
            } else {
                isRetrieveAccessToken = true;
            }
        } else {
            isRetrieveAccessToken = true;
        }
        if (isRetrieveAccessToken) {
            Map<String, Object> accessTokenMap = obtainAccessToken();
            CacheUtils.set(Constants.KEY_ANUBIS_TOKEN, GsonUtils.toJson(accessTokenMap));
            accessToken = MapUtils.getString(accessTokenMap, "access_token");
        }

        int salt = RandomUtils.nextInt(1000, 9999);
        String signature = generateSignature(appId, GsonUtils.toJson(data), salt, accessToken);

        Map<String, Object> requestBody = new HashMap<String, Object>();
        requestBody.put("app_id", appId);
//        requestBody.put("access_token", accessToken);
        requestBody.put("data", data);
        requestBody.put("salt", salt);
        requestBody.put("signature", signature);

        Map<String, String> callAnubisSystemRequestParameters = new HashMap<String, String>();
        callAnubisSystemRequestParameters.put("url", url);
        callAnubisSystemRequestParameters.put("requestBody", GsonUtils.toJson(requestBody));
        ApiRest apiRest = ProxyUtils.doPostWithRequestParameters(Constants.SERVICE_NAME_OUT, "anubis", "callAnubisSystem", callAnubisSystemRequestParameters);
        return apiRest;
    }
}
