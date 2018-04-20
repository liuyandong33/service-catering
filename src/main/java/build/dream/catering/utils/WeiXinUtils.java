package build.dream.catering.utils;

import build.dream.catering.constants.Constants;
import build.dream.common.api.ApiRest;
import build.dream.common.beans.WeiXinAccessToken;
import build.dream.common.saas.domains.WeiXinPublicAccount;
import build.dream.common.utils.ProxyUtils;
import org.apache.commons.lang.Validate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WeiXinUtils {
    public static WeiXinPublicAccount obtainWeiXinPublicAccount(String tenantId) throws IOException {
        Map<String, String> obtainWeiXinPublicAccountRequestParameters = new HashMap<String, String>();
        obtainWeiXinPublicAccountRequestParameters.put("tenantId", tenantId);
        ApiRest apiRest = ProxyUtils.doGetWithRequestParameters(Constants.SERVICE_NAME_PLATFORM, "weiXin", "obtainWeiXinPublicAccount", obtainWeiXinPublicAccountRequestParameters);
        Validate.isTrue(apiRest.isSuccessful(), apiRest.getError());
        WeiXinPublicAccount weiXinPublicAccount = (WeiXinPublicAccount) apiRest.getData();
        return weiXinPublicAccount;
    }

    public static String obtainAccessToken(String appId, String appSecret) throws IOException {
        Map<String, String> obtainAccessTokenRequestParameters = new HashMap<String, String>();
        obtainAccessTokenRequestParameters.put("appId", appId);
        obtainAccessTokenRequestParameters.put("appSecret", appSecret);

        ApiRest apiRest = ProxyUtils.doGetWithRequestParameters(Constants.SERVICE_NAME_PLATFORM, "weiXin", "obtainAccessToken", obtainAccessTokenRequestParameters);
        Validate.isTrue(apiRest.isSuccessful(), apiRest.getError());

        WeiXinAccessToken weiXinAccessToken = (WeiXinAccessToken) apiRest.getData();
        return weiXinAccessToken.getAccessToken();
    }
}
