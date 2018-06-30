package build.dream.catering.controllers;

import build.dream.catering.models.weixin.CreateMemberCardModel;
import build.dream.catering.models.weixin.DeleteWeiXinMemberCardModel;
import build.dream.catering.models.weixin.ListWeiXinMemberCardsModel;
import build.dream.catering.models.weixin.PayGiftCardModel;
import build.dream.catering.services.WeiXinService;
import build.dream.common.annotations.ApiRestAction;
import build.dream.common.api.ApiRest;
import build.dream.common.controllers.BasicController;
import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.GsonUtils;
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
}
