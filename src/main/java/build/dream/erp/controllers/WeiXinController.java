package build.dream.erp.controllers;

import build.dream.common.api.ApiRest;
import build.dream.common.controllers.BasicController;
import build.dream.common.saas.domains.WeiXinOpenPlatformApplication;
import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.GsonUtils;
import build.dream.common.utils.LogUtils;
import build.dream.common.utils.ProxyUtils;
import build.dream.erp.constants.Constants;
import build.dream.erp.models.weixin.ObtainOAuthAccessTokenModel;
import org.apache.commons.lang.Validate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "/weiXin")
public class WeiXinController extends BasicController {
    @RequestMapping(value = "/obtainOAuthAccessToken")
    @ResponseBody
    public String obtainOAuthAccessToken() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            ObtainOAuthAccessTokenModel obtainOAuthAccessTokenModel = ApplicationHandler.instantiateObject(ObtainOAuthAccessTokenModel.class, requestParameters);
            obtainOAuthAccessTokenModel.validateAndThrow();

            Map<String, String> findWeiXinOpenPlatformApplicationRequestParameters = new HashMap<String, String>();
            findWeiXinOpenPlatformApplicationRequestParameters.put("appId", obtainOAuthAccessTokenModel.getAppId());
            ApiRest findWeiXinOpenPlatformApplicationApiRest = ProxyUtils.doGetWithRequestParameters(Constants.SERVICE_NAME_PLATFORM, "weiXin", "findWeiXinOpenPlatformApplication", findWeiXinOpenPlatformApplicationRequestParameters);
            Validate.isTrue(findWeiXinOpenPlatformApplicationApiRest.isSuccessful(), findWeiXinOpenPlatformApplicationApiRest.getError());
            WeiXinOpenPlatformApplication weiXinOpenPlatformApplication = (WeiXinOpenPlatformApplication) findWeiXinOpenPlatformApplicationApiRest.getData();

            Map<String, String> obtainOAuthAccessTokenRequestParameters = new HashMap<String, String>();
            obtainOAuthAccessTokenRequestParameters.put("appId", weiXinOpenPlatformApplication.getAppId());
            obtainOAuthAccessTokenRequestParameters.put("appSecret", weiXinOpenPlatformApplication.getAppSecret());
            obtainOAuthAccessTokenRequestParameters.put("code", obtainOAuthAccessTokenModel.getCode());
            ApiRest obtainOAuthAccessTokenApiRest = ProxyUtils.doGetWithRequestParameters(Constants.SERVICE_NAME_OUT, "weiXin", "obtainOAuthAccessToken", obtainOAuthAccessTokenRequestParameters);
            Validate.isTrue(obtainOAuthAccessTokenApiRest.isSuccessful(), obtainOAuthAccessTokenApiRest.getError());

            apiRest = new ApiRest();
            apiRest.setData(obtainOAuthAccessTokenApiRest.getData());
            apiRest.setMessage("通过code换取网页授权access_token成功！");
            apiRest.setSuccessful(true);
        } catch (Exception e) {
            LogUtils.error("通过code换取网页授权access_token失败", controllerSimpleName, "obtainAccessToken", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }
}
