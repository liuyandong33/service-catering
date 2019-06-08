package build.dream.catering.controllers;

import build.dream.catering.models.pos.*;
import build.dream.catering.services.PosService;
import build.dream.common.annotations.ApiRestAction;
import build.dream.common.annotations.PermitAll;
import build.dream.common.api.ApiRest;
import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.CommonRedisUtils;
import build.dream.common.utils.GsonUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping(value = "/pos")
public class PosController {
    /**
     * 上线POS
     *
     * @return
     */
    @RequestMapping(value = "/onlinePos", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = OnlinePosModel.class, serviceClass = PosService.class, serviceMethodName = "onlinePos", error = "上线POS失败")
    public String onlinePos() {
        return null;
    }

    /**
     * 下线POS
     *
     * @return
     */
    @RequestMapping(value = "/offlinePos", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = OfflinePosModel.class, serviceClass = PosService.class, serviceMethodName = "offlinePos", error = "下线POS失败")
    public String offlinePos() {
        return null;
    }

    /**
     * 回执
     *
     * @return
     */
    @RequestMapping(value = "/receipt", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(error = "回执失败")
    public String receipt() throws Exception {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        ReceiptModel receiptModel = ApplicationHandler.instantiateObject(ReceiptModel.class, requestParameters);
        receiptModel.validateAndThrow();
        String uuid = receiptModel.getUuid();

        CommonRedisUtils.del(uuid);
        return GsonUtils.toJson(ApiRest.builder().message("回执成功！").successful(true).build());
    }

    /**
     * 扫码支付
     *
     * @return
     */
    @RequestMapping(value = "/offlinePay", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = OfflinePayModel.class, serviceClass = PosService.class, serviceMethodName = "offlinePay", error = "扫码支付失败")
    @PermitAll
    public String offlinePay() {
        return null;
    }

    /**
     * 订单查询
     *
     * @return
     */
    @RequestMapping(value = "/orderQuery", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = OrderQueryModel.class, serviceClass = PosService.class, serviceMethodName = "orderQuery", error = "查询订单失败")
    @PermitAll
    public String orderQuery() {
        return null;
    }

    /**
     * 退款
     *
     * @return
     */
    @RequestMapping(value = "/refund", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = RefundModel.class, serviceClass = PosService.class, serviceMethodName = "refund", error = "退款失败")
    @PermitAll
    public String refund() {
        return null;
    }
}
