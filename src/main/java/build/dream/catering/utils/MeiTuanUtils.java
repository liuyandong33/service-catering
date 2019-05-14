package build.dream.catering.utils;

import build.dream.catering.constants.Constants;
import build.dream.common.beans.WebResponse;
import build.dream.common.utils.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class MeiTuanUtils {
    public static String generateSignature(String signKey, Map<String, String> requestParameters) {
        Map<String, String> sortedRequestParameters = new TreeMap<String, String>(requestParameters);
        StringBuilder finalData = new StringBuilder(signKey);
        for (Map.Entry<String, String> sortedRequestParameter : sortedRequestParameters.entrySet()) {
            finalData.append(sortedRequestParameter.getKey()).append(sortedRequestParameter.getValue());
        }
        return DigestUtils.sha1Hex(finalData.toString());
    }

    public static Map<String, Object> callMeiTuanSystem(String tenantId, String branchId, String signKey, Map<String, String> requestParameters, String url, String requestMethod) {
        String appAuthToken = getMeiTuanAppAuthToken(tenantId, branchId);
        String charset = Constants.CHARSET_NAME_UTF_8;
        String timestamp = String.valueOf(System.currentTimeMillis());
        String version = "1";

        Map<String, String> params = new HashMap<String, String>(requestParameters);
        params.put("appAuthToken", appAuthToken);
        params.put("charset", charset);
        params.put("charset", charset);
        params.put("timestamp", timestamp);
        params.put("version", version);

        String sign = generateSignature(signKey, params);
        params.put("sign", sign);

        WebResponse webResponse = null;
        if (Constants.REQUEST_METHOD_GET.equals(requestMethod)) {
            webResponse = OutUtils.doGetWithRequestParameters(url, params);
        } else if (Constants.REQUEST_METHOD_POST.equals(requestMethod)) {
            StringBuilder requestUrl = new StringBuilder(url);
            requestUrl.append("?appAuthToken=").append(appAuthToken);
            requestUrl.append("&charset=").append(charset);
            requestUrl.append("&timestamp=").append(timestamp);
            requestUrl.append("&version=").append(version);
            requestUrl.append("&sign=").append(sign);
            webResponse = OutUtils.doPostWithRequestParameters(requestUrl.toString(), requestParameters);
        }
        String result = webResponse.getResult();
        Map<String, Object> resultMap = JacksonUtils.readValueAsMap(result, String.class, Object.class);
        String code = MapUtils.getString(resultMap, "code");
        ValidateUtils.isTrue(StringUtils.isBlank(code), MapUtils.getString(resultMap, "msg"));
        return resultMap;
    }

    public static Map<String, Object> callMeiTuanSystem(String tenantId, String branchId, Map<String, String> requestParameters, String url, String requestMethod) {
        String signKey = ConfigurationUtils.getConfiguration(Constants.MEI_TUAN_SIGN_KEY);
        return callMeiTuanSystem(tenantId, branchId, signKey, requestParameters, url, requestMethod);
    }

    public static String getMeiTuanAppAuthToken(String tenantId, String branchId) {
        String meiTuanAppAuthToken = CommonRedisUtils.hget(Constants.KEY_MEI_TUAN_APP_AUTH_TOKENS, tenantId + "_" + branchId);
        ValidateUtils.notNull(meiTuanAppAuthToken, "门店未绑定美团！");
        return meiTuanAppAuthToken;
    }
}
