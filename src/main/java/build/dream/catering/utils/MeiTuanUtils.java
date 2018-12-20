package build.dream.catering.utils;

import build.dream.catering.constants.Constants;
import build.dream.common.beans.WebResponse;
import build.dream.common.utils.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

import java.util.Map;
import java.util.TreeMap;

public class MeiTuanUtils {
    public static String generateSignature(String signKey, Map<String, String> requestParameters) {
        Map<String, String> sortedRequestParameters = new TreeMap<String, String>(requestParameters);
        StringBuffer finalData = new StringBuffer(signKey);
        for (Map.Entry<String, String> sortedRequestParameter : sortedRequestParameters.entrySet()) {
            finalData.append(sortedRequestParameter.getKey()).append(sortedRequestParameter.getValue());
        }
        return DigestUtils.sha1Hex(finalData.toString());
    }

    public static Map<String, Object> callMeiTuanSystem(String tenantId, String branchId, String signKey, Map<String, String> requestParameters, String url, String requestMethod) {
        putSystemLevelParameter(tenantId, branchId, signKey, requestParameters);
        WebResponse webResponse = null;
        if (Constants.REQUEST_METHOD_GET.equals(requestMethod)) {
            webResponse = OutUtils.doGetWithRequestParameters(url, requestParameters);
        } else if (Constants.REQUEST_METHOD_POST.equals(requestMethod)) {
            StringBuffer requestUrl = new StringBuffer(url).append("?");
            requestUrl.append("?").append("appAuthToken").append("=").append(requestParameters.remove("appAuthToken"));
            requestUrl.append("&").append("charset").append("=").append(requestParameters.remove("charset"));
            requestUrl.append("&").append("timestamp").append("=").append(requestParameters.remove("timestamp"));
            requestUrl.append("&").append("version").append("=").append(requestParameters.remove("version"));
            requestUrl.append("&").append("sign").append("=").append(requestParameters.remove("sign"));
            requestParameters.put("url", requestUrl.toString());
            webResponse = OutUtils.doPostWithRequestParameters(url, requestParameters);
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
        String meiTuanAppAuthToken = CacheUtils.hget(Constants.KEY_MEI_TUAN_APP_AUTH_TOKENS, tenantId + "_" + branchId);
        Validate.notNull(meiTuanAppAuthToken, "门店未绑定美团！");
        return meiTuanAppAuthToken;
    }

    public static void putSystemLevelParameter(String tenantId, String branchId, String signKey, Map<String, String> requestParameters) {
        requestParameters.put("appAuthToken", getMeiTuanAppAuthToken(tenantId, branchId));
        requestParameters.put("charset", Constants.CHARSET_NAME_UTF_8);
        requestParameters.put("timestamp", String.valueOf(System.currentTimeMillis()));
        requestParameters.put("version", "1");
        requestParameters.put("sign", generateSignature(signKey, requestParameters));
    }
}
