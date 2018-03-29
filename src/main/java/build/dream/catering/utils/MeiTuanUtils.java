package build.dream.catering.utils;

import build.dream.catering.constants.Constants;
import build.dream.catering.tools.MeiTuanConsumerThread;
import build.dream.common.api.ApiRest;
import build.dream.common.utils.*;
import net.sf.json.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.Validate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

public class MeiTuanUtils {
    private static String generateSignature(String signKey, Map<String, String> requestParameters) {
        Map<String, String> sortedRequestParameters = new TreeMap<String, String>(requestParameters);
        StringBuffer finalData = new StringBuffer(signKey);
        for (Map.Entry<String, String> sortedRequestParameter : sortedRequestParameters.entrySet()) {
            finalData.append(sortedRequestParameter.getKey()).append(sortedRequestParameter.getValue());
        }
        return DigestUtils.sha1Hex(finalData.toString());
    }

    public static ApiRest callMeiTuanSystem(String tenantId, String branchId, String signKey, Map<String, String> requestParameters, String url, String requestMethod) throws IOException {
        putSystemLevelParameter(tenantId, branchId, signKey, requestParameters);
        ApiRest apiRest = null;
        if (WebUtils.RequestMethod.GET.equals(requestMethod)) {
            requestParameters.put("url", url);
            apiRest = ProxyUtils.doGetWithRequestParameters(Constants.SERVICE_NAME_OUT, "meiTuan", "callMeiTuanSystem", requestParameters);
        } else if (WebUtils.RequestMethod.POST.equals(requestMethod)) {
            StringBuffer requestUrl = new StringBuffer(url).append("?");
            requestUrl.append("?").append("appAuthToken").append("=").append(requestParameters.remove("appAuthToken"));
            requestUrl.append("&").append("charset").append("=").append(requestParameters.remove("charset"));
            requestUrl.append("&").append("timestamp").append("=").append(requestParameters.remove("timestamp"));
            requestUrl.append("&").append("version").append("=").append(requestParameters.remove("version"));
            requestUrl.append("&").append("sign").append("=").append(requestParameters.remove("sign"));
            requestParameters.put("url", requestUrl.toString());
            apiRest = ProxyUtils.doPostWithRequestParameters(Constants.SERVICE_NAME_OUT, "meiTuan", "callMeiTuanSystem", requestParameters);
        }
        return apiRest;
    }

    public static ApiRest callMeiTuanSystem(String tenantId, String branchId, Map<String, String> requestParameters, String url, String requestMethod) throws IOException {
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

    public static void addMeiTuanMessage(JSONObject meiTuanMessageJsonObject, String uuid, int count, int type) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("callbackParameters", meiTuanMessageJsonObject);
        map.put("uuid", uuid);
        map.put("count", count);
        map.put("type", type);
        QueueUtils.rpush(Constants.KEY_MEI_TUAN_CALLBACK_MESSAGE, GsonUtils.toJson(map));
    }

    public static String takeMeiTuanMessage() throws IOException {
        String key = Constants.KEY_MEI_TUAN_CALLBACK_MESSAGE + "_" + ConfigurationUtils.getConfiguration(Constants.PARTITION_CODE);
        String elemeMessage = QueueUtils.blpop(key, 1, TimeUnit.HOURS);
        return elemeMessage;
    }

    public static void startMeiTuanConsumerThread() {
        new Thread(new MeiTuanConsumerThread()).start();
    }
}
