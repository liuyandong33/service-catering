package build.dream.catering.controllers;

import build.dream.catering.models.alipay.CreateMemberCardTemplateModel;
import build.dream.catering.models.alipay.GenerateAppToAppAuthorizeUrlModel;
import build.dream.catering.services.AlipayService;
import build.dream.common.annotations.ApiRestAction;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/alipay")
public class AlipayController {
    /**
     * 创建会员卡模板
     *
     * @return
     */
    @RequestMapping(value = "/createMemberCardTemplate", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = CreateMemberCardTemplateModel.class, serviceClass = AlipayService.class, serviceMethodName = "createMemberCardTemplate", error = "创建会员卡模板失败")
    public String createMemberCardTemplate() {
        return null;
    }

    /**
     * 生成应用授权连接
     *
     * @return
     */
    @RequestMapping(value = "/generateAppToAppAuthorizeUrl", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = GenerateAppToAppAuthorizeUrlModel.class, serviceClass = AlipayService.class, serviceMethodName = "generateAppToAppAuthorizeUrl", error = "生成应用授权连接失败")
    public String generateAppToAppAuthorizeUrl() {
        return null;
    }
}
