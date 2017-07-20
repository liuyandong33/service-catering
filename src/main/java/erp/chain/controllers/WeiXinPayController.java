package erp.chain.controllers;

import erp.chain.api.ApiRest;
import erp.chain.services.WeiXinPayService;
import erp.chain.utils.ApplicationHandler;
import erp.chain.utils.GsonUtils;
import erp.chain.utils.LogUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * Created by liuyandong on 2017/7/19.
 */
@Controller
@RequestMapping(value = "/weiXinPay")
public class WeiXinPayController {
    private static final String WEI_XIN_PAY_CONTROLLER_SIMPLE_NAME = "WeiXinPayController";

    @Autowired
    private WeiXinPayService weiXinPayService;

    @RequestMapping(value = "/unifiedOrder", method = RequestMethod.GET)
    @ResponseBody
    public String unifiedOrder() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            ApplicationHandler.validateNotNull(requestParameters, "tenantId", "branchId", "body", "outTradeNo", "totalFee", "spbillCreateIp", "notifyUrl", "tradeType", "openid");
            apiRest = weiXinPayService.unifiedOrder(requestParameters);
        } catch (Exception e) {
            LogUtils.error("微信下单失败", WEI_XIN_PAY_CONTROLLER_SIMPLE_NAME, "unifiedOrder", e.getClass().getSimpleName(), e.getMessage(), requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }
}
