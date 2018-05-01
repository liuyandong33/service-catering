package build.dream.catering.controllers;

import build.dream.catering.models.weixin.CreateMemberCardModel;
import build.dream.catering.models.weixin.DeleteWeiXinMemberCardModel;
import build.dream.catering.models.weixin.ListWeiXinMemberCardsModel;
import build.dream.catering.models.weixin.PayGiftCardModel;
import build.dream.catering.services.WeiXinService;
import build.dream.common.controllers.BasicController;
import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.MethodCaller;
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
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters(httpServletRequest);
        MethodCaller methodCaller = () -> {
            CreateMemberCardModel createMemberCardModel = ApplicationHandler.instantiateObject(CreateMemberCardModel.class, requestParameters);
            createMemberCardModel.validateAndThrow();

            Validate.isTrue(httpServletRequest instanceof MultipartHttpServletRequest, "请上传logo！");
            MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) httpServletRequest;

            MultipartFile backgroundPicFile = multipartHttpServletRequest.getFile("backgroundPic");

            MultipartFile logoFile = multipartHttpServletRequest.getFile("logo");
            Validate.notNull(logoFile, "请上传logo！");

            return weiXinService.createMemberCard(createMemberCardModel, backgroundPicFile, logoFile);
        };
        return ApplicationHandler.callMethod(methodCaller, "创建会员卡失败", requestParameters);
    }

    /**
     * 开通支付即使会员
     *
     * @return
     */
    @RequestMapping(value = "/addPayGiftCard")
    @ResponseBody
    public String addPayGiftCard() {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        MethodCaller methodCaller = () -> {
            PayGiftCardModel payGiftCardModel = ApplicationHandler.instantiateObject(PayGiftCardModel.class, requestParameters);
            payGiftCardModel.validateAndThrow();

            return weiXinService.addPayGiftCard(payGiftCardModel);
        };
        return ApplicationHandler.callMethod(methodCaller, "开通支付即会员失败", requestParameters);
    }

    /**
     * 删除微信会员卡
     *
     * @return
     */
    @RequestMapping(value = "/deleteWeiXinMemberCard")
    @ResponseBody
    public String deleteWeiXinMemberCard() {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        MethodCaller methodCaller = () -> {
            DeleteWeiXinMemberCardModel deleteWeiXinMemberCardModel = ApplicationHandler.instantiateObject(DeleteWeiXinMemberCardModel.class, requestParameters);
            deleteWeiXinMemberCardModel.validateAndThrow();
            return weiXinService.deleteWeiXinMemberCard(deleteWeiXinMemberCardModel);
        };
        return ApplicationHandler.callMethod(methodCaller, "删除微信会员卡失败", requestParameters);
    }

    /**
     * 删除微信会员卡
     *
     * @return
     */
    @RequestMapping(value = "/listWeiXinMemberCards")
    @ResponseBody
    public String listWeiXinMemberCards() {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        MethodCaller methodCaller = () -> {
            ListWeiXinMemberCardsModel listWeiXinMemberCardsModel = ApplicationHandler.instantiateObject(ListWeiXinMemberCardsModel.class, requestParameters);
            listWeiXinMemberCardsModel.validateAndThrow();
            return weiXinService.listWeiXinMemberCards(listWeiXinMemberCardsModel);
        };
        return ApplicationHandler.callMethod(methodCaller, "查询会员卡列表失败", requestParameters);
    }
}
