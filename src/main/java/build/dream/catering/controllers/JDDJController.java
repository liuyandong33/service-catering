package build.dream.catering.controllers;

import build.dream.catering.constants.Constants;
import build.dream.catering.models.jddj.OrderAcceptOperateModel;
import build.dream.catering.services.JDDJService;
import build.dream.common.annotations.ApiRestAction;
import build.dream.common.annotations.PermitAll;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/jddj")
public class JDDJController {
    @PermitAll
    @RequestMapping(value = "/test")
    @ResponseBody
    public String test() {
        return Constants.SUCCESS;
    }

    @RequestMapping(value = "/orderAcceptOperate", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = OrderAcceptOperateModel.class, serviceClass = JDDJService.class, serviceMethodName = "orderAcceptOperate", error = "确认接单失败")
    public String orderAcceptOperate() {
        return null;
    }
}
