package build.dream.catering.utils;

import build.dream.catering.constants.Constants;
import build.dream.common.utils.CacheUtils;
import build.dream.common.utils.ConfigurationUtils;
import build.dream.common.utils.GsonUtils;
import build.dream.common.utils.ProxyUtils;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DingtalkUtils {
    public static String obtainAccessToken() throws IOException {
        String accessToken = null;
        String tokenJson = CacheUtils.get(Constants.KEY_DINGTALK_TOKEN);
        boolean isRetrieveAccessToken = false;
        if (StringUtils.isNotBlank(tokenJson)) {
            JSONObject tokenJsonObject = JSONObject.fromObject(tokenJson);
            long fetchTime = tokenJsonObject.getLong("fetch_time");
            long expiresIn = tokenJsonObject.getLong("expires_in");
            if ((System.currentTimeMillis() - fetchTime) / 1000 >= expiresIn) {
                isRetrieveAccessToken = true;
            } else {
                accessToken = tokenJsonObject.getString("access_token");
            }
        } else {
            isRetrieveAccessToken = true;
        }
        if (isRetrieveAccessToken) {
            String corpId = ConfigurationUtils.getConfiguration(Constants.DINGTALK_CORP_ID);
            String corpSecret = ConfigurationUtils.getConfiguration(Constants.DINGTALK_CORP_SECRET);
            Map<String, String> doGetRequestParameters = new HashMap<String, String>();
            String url = ConfigurationUtils.getConfiguration(Constants.DINGTALK_SERVICE_URL) + Constants.DINGTALK_GET_TOKEN_URI + "?corpid=" + corpId + "&corpsecret=" + corpSecret;
            doGetRequestParameters.put("url", url);
            String result = ProxyUtils.doGetOriginalWithRequestParameters(Constants.SERVICE_NAME_OUT, "proxy", "doGet", doGetRequestParameters);
            JSONObject resultJsonObject = JSONObject.fromObject(result);
            int errcode = resultJsonObject.getInt("errcode");
            Validate.isTrue(errcode == 0, resultJsonObject.optString("errmsg"));

            Map<String, Object> tokenMap = new HashMap<String, Object>();
            accessToken = resultJsonObject.getString("access_token");
            tokenMap.put("access_token", accessToken);
            tokenMap.put("expires_in", resultJsonObject.getLong("expires_in"));
            tokenMap.put("fetch_time", System.currentTimeMillis());
            CacheUtils.set(Constants.KEY_DINGTALK_TOKEN, GsonUtils.toJson(tokenMap));
        }
        return accessToken;
    }
}
