package build.dream.catering.controllers;

import build.dream.catering.constants.Constants;
import build.dream.catering.models.dietorder.*;
import build.dream.catering.services.DietOrderService;
import build.dream.common.annotations.ApiRestAction;
import build.dream.common.controllers.BasicController;
import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.LogUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping(value = "/dietOrder")
public class DietOrderController extends BasicController {
    @Autowired
    private DietOrderService dietOrderService;

    /**
     * 获取订单明细
     *
     * @return
     */
    @RequestMapping(value = "/obtainDietOrderInfo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = ObtainDietOrderInfoModel.class, serviceClass = DietOrderService.class, serviceMethodName = "obtainDietOrderInfo", error = "获取订单信息失败")
    public String obtainDietOrderInfo() {
        return null;
    }

    /**
     * 保存订单
     *
     * @return
     */
    @RequestMapping(value = "/saveDietOrder", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = SaveDietOrderModel.class, serviceClass = DietOrderService.class, serviceMethodName = "saveDietOrder", error = "保存订单失败")
    public String saveDietOrder() {
        return null;
    }

    /**
     * 确认订单
     *
     * @return
     */
    @RequestMapping(value = "/confirmOrder", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = ConfirmOrderModel.class, serviceClass = DietOrderService.class, serviceMethodName = "confirmOrder", error = "确认订单失败")
    public String confirmOrder() {
        return null;
    }

    /**
     * 取消订单
     *
     * @return
     */
    @RequestMapping(value = "/cancelOrder", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = CancelOrderModel.class, serviceClass = DietOrderService.class, serviceMethodName = "cancelOrder", error = "取消订单失败")
    public String cancelOrder() {
        return null;
    }

    /**
     * 取消订单
     *
     * @return
     */
    @RequestMapping(value = "/doPay", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = DoPayModel.class, serviceClass = DietOrderService.class, serviceMethodName = "doPay", error = "发起支付失败")
    public String doPay() {
        return null;
    }

    /**
     * 支付宝支付回调
     *
     * @return
     */
    @RequestMapping(value = "/alipayCallback", method = RequestMethod.POST)
    @ResponseBody
    public String alipayCallback() {
        String returnValue = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            dietOrderService.handleCallback(requestParameters, Constants.PAYMENT_CODE_ALIPAY);
            returnValue = Constants.SUCCESS;
        } catch (Exception e) {
            LogUtils.error("支付宝支付回调处理失败", className, "alipayCallback", e, requestParameters);
            returnValue = Constants.FAILURE;
        }
        return returnValue;
    }

    /**
     * 微信支付回调
     *
     * @return
     */
    @RequestMapping(value = "/weiXinPayCallback", method = RequestMethod.POST)
    @ResponseBody
    public String weiXinPayCallback() {
        String returnValue = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            dietOrderService.handleCallback(requestParameters, Constants.PAYMENT_CODE_WX);
            returnValue = Constants.SUCCESS;
        } catch (Exception e) {
            LogUtils.error("微信支付回调处理失败", className, "weiXinPayCallback", e, requestParameters);
            returnValue = Constants.FAILURE;
        }
        return returnValue;
    }

    /**
     * 获取POS订单
     *
     * @return
     */
    @RequestMapping(value = "/obtainPosOrder", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = ObtainPosOrderModel.class, serviceClass = DietOrderService.class, serviceMethodName = "obtainPosOrder", error = "获取POS订单失败")
    public String obtainPosOrder() {
        return null;
    }

    /**
     * 组合付款
     *
     * @return
     */
    @RequestMapping(value = "/doPayCombined", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = DoPayCombinedModel.class, serviceClass = DietOrderService.class, serviceMethodName = "doPayCombined", error = "发起支付失败")
    public String doPayCombined() {
        return null;
    }
}
