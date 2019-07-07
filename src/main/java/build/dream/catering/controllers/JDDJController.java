package build.dream.catering.controllers;

import build.dream.catering.constants.Constants;
import build.dream.catering.models.jddj.CancelOrderModel;
import build.dream.catering.models.jddj.ConfirmOrderModel;
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

    /**
     * 确认订单
     *
     * @return
     */
    @RequestMapping(value = "/confirmOrder", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = ConfirmOrderModel.class, serviceClass = JDDJService.class, serviceMethodName = "confirmOrder", error = "确认订单失败")
    public String confirmOrder() {
        return null;
    }

    /**
     * 取消订单
     *
     * @return
     */
    @RequestMapping(value = "/cancelOrder", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = CancelOrderModel.class, serviceClass = JDDJService.class, serviceMethodName = "cancelOrder", error = "取消订单失败")
    public String cancelOrder() {
        return null;
    }
}
