package build.dream.catering.controllers;

import build.dream.catering.models.alipay.CreateMemberCardTemplateModel;
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
    @RequestMapping(value = "/createMemberCardTemplate", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = CreateMemberCardTemplateModel.class, serviceClass = AlipayService.class, serviceMethodName = "createMemberCardTemplate", error = "创建会员卡模板失败")
    public String createMemberCardTemplate() {
        return null;
    }
}
