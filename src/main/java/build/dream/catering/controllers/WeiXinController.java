package build.dream.catering.controllers;

import build.dream.catering.constants.Constants;
import build.dream.catering.models.weixin.*;
import build.dream.catering.services.WeiXinService;
import build.dream.common.annotations.ApiRestAction;
import build.dream.common.api.ApiRest;
import build.dream.common.controllers.BasicController;
import build.dream.common.utils.*;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "/weiXin")
public class WeiXinController extends BasicController {
    @Autowired
    private WeiXinService weiXinService;

    /**
     * 创建会员开
     *
     * @param httpServletRequest
     * @return
     */
    @RequestMapping(value = "/createMemberCard")
    @ResponseBody
    @ApiRestAction(error = "创建会员卡失败")
    public String createMemberCard(HttpServletRequest httpServletRequest) throws Exception {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters(httpServletRequest);
        CreateMemberCardModel createMemberCardModel = ApplicationHandler.instantiateObject(CreateMemberCardModel.class, requestParameters);
        createMemberCardModel.validateAndThrow();

        Validate.isTrue(httpServletRequest instanceof MultipartHttpServletRequest, "请上传logo！");
        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) httpServletRequest;

        MultipartFile backgroundPicFile = multipartHttpServletRequest.getFile("backgroundPic");

        MultipartFile logoFile = multipartHttpServletRequest.getFile("logo");
        Validate.notNull(logoFile, "请上传logo！");

        ApiRest apiRest = weiXinService.createMemberCard(createMemberCardModel, backgroundPicFile, logoFile);
        return GsonUtils.toJson(apiRest);
    }

    /**
     * 开通支付即使会员
     *
     * @return
     */
    @RequestMapping(value = "/addPayGiftCard")
    @ResponseBody
    @ApiRestAction(modelClass = PayGiftCardModel.class, serviceClass = WeiXinService.class, serviceMethodName = "addPayGiftCard", error = "开通支付即会员失败")
    public String addPayGiftCard() {
        return null;
    }

    /**
     * 删除微信会员卡
     *
     * @return
     */
    @RequestMapping(value = "/deleteWeiXinMemberCard")
    @ResponseBody
    @ApiRestAction(modelClass = DeleteWeiXinMemberCardModel.class, serviceClass = WeiXinService.class, serviceMethodName = "deleteWeiXinMemberCard", error = "删除微信会员卡失败")
    public String deleteWeiXinMemberCard() {
        return null;
    }

    /**
     * 删除微信会员卡
     *
     * @return
     */
    @RequestMapping(value = "/listWeiXinMemberCards")
    @ResponseBody
    @ApiRestAction(modelClass = ListWeiXinMemberCardsModel.class, serviceClass = WeiXinService.class, serviceMethodName = "listWeiXinMemberCards", error = "查询会员卡列表失败")
    public String listWeiXinMemberCards() {
        return null;
    }

    /**
     * 获取微信授权信息
     *
     * @return
     */
    @RequestMapping(value = "/obtainWeiXinAuthorizerInfo")
    @ResponseBody
    @ApiRestAction(modelClass = ObtainWeiXinAuthorizerInfoModel.class, serviceClass = WeiXinService.class, serviceMethodName = "obtainWeiXinAuthorizerInfo", error = "获取微信授权信息失败")
    public String obtainWeiXinAuthorizerInfo() {
        return null;
    }

    /**
     * 生成授权链接
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/generateComponentLoginPageUrl")
    @ResponseBody
    @ApiRestAction(error = "生成授权链接失败")
    public String generateComponentLoginPageUrl() throws Exception {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        GenerateComponentLoginPageUrlModel generateComponentLoginPageUrlModel = ApplicationHandler.instantiateObject(GenerateComponentLoginPageUrlModel.class, requestParameters);
        generateComponentLoginPageUrlModel.validateAndThrow();

        String authType = generateComponentLoginPageUrlModel.getAuthType();
        BigInteger tenantId = generateComponentLoginPageUrlModel.obtainTenantId();
        String partitionCode = generateComponentLoginPageUrlModel.obtainPartitionCode();
        String clientType = generateComponentLoginPageUrlModel.obtainClientType();

        String componentAppId = ConfigurationUtils.getConfiguration(Constants.WEI_XIN_OPEN_PLATFORM_APPLICATION_APP_ID);
        String componentAppSecret = ConfigurationUtils.getConfiguration(Constants.WEI_XIN_OPEN_PLATFORM_APPLICATION_APP_SECRET);

        String preAuthCode = WeiXinUtils.obtainPreAuthCode(componentAppId, componentAppSecret);

        String serviceName = null;
        if (Constants.CLIENT_TYPE_APP.equals(clientType)) {
            serviceName = Constants.SERVICE_NAME_APPAPI;
        } else if (Constants.CLIENT_TYPE_POS.equals(clientType)) {
            serviceName = Constants.SERVICE_NAME_POSAPI;
        } else if (Constants.CLIENT_TYPE_WEB.equals(clientType)) {
            serviceName = Constants.SERVICE_NAME_WEBAPI;
        }

        String redirectUri = CommonUtils.getOutsideServiceDomain(serviceName) + "/proxy/doGetPermit/" + partitionCode + "/" + Constants.SERVICE_NAME_CATERING + "/weiXin/authCallback?tenantId=" + tenantId + "&componentAppId=" + componentAppId;
        String url = WeiXinUtils.generateComponentLoginPageUrl(componentAppId, preAuthCode, redirectUri, authType);

        ApiRest apiRest = ApiRest.builder().data(url).message("生成授权链接成功！").successful(true).build();
        return GsonUtils.toJson(apiRest);
    }

    /**
     * 授权回调
     *
     * @return
     */
    @RequestMapping(value = "/authCallback")
    public ModelAndView authCallback() {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        ModelAndView modelAndView = new ModelAndView();
        try {
            AuthCallbackModel authCallbackModel = ApplicationHandler.instantiateObject(AuthCallbackModel.class, requestParameters);
            authCallbackModel.validateAndThrow();
            Map<String, Object> info = weiXinService.handleAuthCallback(authCallbackModel);

            Map<String, Object> model = new HashMap<String, Object>();
            String partitionCode = ConfigurationUtils.getConfiguration(Constants.PARTITION_CODE);
            String baseUrl = CommonUtils.getServiceDomain(partitionCode, Constants.SERVICE_NAME_CATERING);

            String clientType = authCallbackModel.getClientType();
            String proxyUrl = null;
            if (Constants.CLIENT_TYPE_APP.equals(clientType)) {
                proxyUrl = CommonUtils.getOutsideUrl(Constants.SERVICE_NAME_APPAPI, "proxy", "doGetPermitWithUrl");
            } else if (Constants.CLIENT_TYPE_POS.equals(clientType)) {
                proxyUrl = CommonUtils.getOutsideUrl(Constants.SERVICE_NAME_POSAPI, "proxy", "doGetPermitWithUrl");
            } else if (Constants.CLIENT_TYPE_WEB.equals(clientType)) {
                proxyUrl = CommonUtils.getOutsideUrl(Constants.SERVICE_NAME_WEBAPI, "proxy", "doGetPermitWithUrl");
            }

            model.put("baseUrl", baseUrl);
            model.put("proxyUrl", proxyUrl);
            model.put("info", info);

            modelAndView.addAllObjects(model);
            modelAndView.setViewName("weiXin/authSuccess");
        } catch (Exception e) {
            modelAndView.setViewName("weiXin/authFailure");
        }
        return modelAndView;
    }
}
