package build.dream.catering.utils;

import build.dream.catering.constants.Constants;
import build.dream.common.api.ApiRest;
import build.dream.common.utils.GsonUtils;
import build.dream.common.utils.ProxyUtils;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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

    public static ApiRest callAnubisSystem(String url, String appId, Map<String, Object> data, int salt) throws IOException {
        String accessToken = "3b393e8e-e35b-41da-b0c0-1893afa56203";
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
