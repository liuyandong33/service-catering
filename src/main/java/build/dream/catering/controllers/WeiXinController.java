package build.dream.catering.controllers;

import build.dream.catering.models.weixin.CreateMemberCardModel;
import build.dream.catering.models.weixin.DeleteWeiXinMemberCardModel;
import build.dream.catering.models.weixin.PayGiftCardModel;
import build.dream.catering.services.WeiXinService;
import build.dream.common.api.ApiRest;
import build.dream.common.controllers.BasicController;
import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.GsonUtils;
import build.dream.common.utils.LogUtils;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
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
    public String createMemberCard(HttpServletRequest httpServletRequest) {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters(httpServletRequest);
        try {
            CreateMemberCardModel createMemberCardModel = ApplicationHandler.instantiateObject(CreateMemberCardModel.class, requestParameters);
            createMemberCardModel.validateAndThrow();

            Validate.isTrue(httpServletRequest instanceof MultipartHttpServletRequest, "请上传logo！");
            MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) httpServletRequest;

            MultipartFile backgroundPicFile = multipartHttpServletRequest.getFile("backgroundPic");

            MultipartFile logoFile = multipartHttpServletRequest.getFile("logo");
            Validate.notNull(logoFile, "请上传logo！");

            apiRest = weiXinService.createMemberCard(createMemberCardModel, backgroundPicFile, logoFile);
        } catch (Exception e) {
            LogUtils.error("创建会员卡失败", controllerSimpleName, "createMemberCard", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    /**
     * 开通支付即使会员
     *
     * @return
     */
    @RequestMapping(value = "/payGiftCard")
    @ResponseBody
    public String payGiftCard() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            PayGiftCardModel payGiftCardModel = ApplicationHandler.instantiateObject(PayGiftCardModel.class, requestParameters);
            payGiftCardModel.validateAndThrow();

            apiRest = weiXinService.payGiftCard(payGiftCardModel);
        } catch (Exception e) {
            LogUtils.error("开通支付即会员失败", controllerSimpleName, "payGiftCard", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    /**
     * 删除微信会员卡
     *
     * @return
     */
    @RequestMapping(value = "/deleteWeiXinMemberCard")
    @ResponseBody
    public String deleteWeiXinMemberCard() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            DeleteWeiXinMemberCardModel deleteWeiXinMemberCardModel = ApplicationHandler.instantiateObject(DeleteWeiXinMemberCardModel.class, requestParameters);
            deleteWeiXinMemberCardModel.validateAndThrow();
            apiRest = weiXinService.deleteWeiXinMemberCard(deleteWeiXinMemberCardModel);
        } catch (Exception e) {
            LogUtils.error("删除微信会员卡失败", controllerSimpleName, "deleteWeiXinMemberCard", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }
}
