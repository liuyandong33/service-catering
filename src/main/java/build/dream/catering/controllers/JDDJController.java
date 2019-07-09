package build.dream.catering.controllers;

import build.dream.catering.constants.Constants;
import build.dream.catering.models.jddj.CancelAndRefundModel;
import build.dream.catering.models.jddj.CancelOrderModel;
import build.dream.catering.models.jddj.ConfirmOrderModel;
import build.dream.catering.models.jddj.PrintOrderModel;
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

    /**
     * 订单取消且退款接口
     * 1、商家自送订单在配送流程中，若用户拒收，商家可调用接口进行取消；
     * 2、非商家自送订单，调用接口取消失败，仅可用户进行取消；
     * 3、达达配送转商家自送的订单，若用户拒收，商家可调用接口进行取消；
     *
     * @return
     */
    @RequestMapping(value = "/cancelAndRefund", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = CancelAndRefundModel.class, serviceClass = JDDJService.class, serviceMethodName = "cancelAndRefund", error = "订单取消且退款失败")
    public String cancelAndRefund() {
        return null;
    }

    /**
     * 订单已打印接口
     *
     * @return
     */
    @RequestMapping(value = "/printOrder", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = PrintOrderModel.class, serviceClass = JDDJService.class, serviceMethodName = "printOrder", error = "订单已打印失败")
    public String printOrder() {
        return null;
    }
}
