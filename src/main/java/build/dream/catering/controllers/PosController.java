package build.dream.catering.controllers;

import build.dream.catering.models.pos.OfflinePayModel;
import build.dream.catering.models.pos.OfflinePosModel;
import build.dream.catering.models.pos.OnlinePosModel;
import build.dream.catering.models.pos.ReceiptModel;
import build.dream.catering.services.PosService;
import build.dream.common.annotations.ApiRestAction;
import build.dream.common.api.ApiRest;
import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.GsonUtils;
import build.dream.common.utils.RedisUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
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
    @RequestMapping(value = "/onlinePos")
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
    @RequestMapping(value = "/offlinePos")
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
    @RequestMapping(value = "/receipt")
    @ResponseBody
    @ApiRestAction(error = "回执失败")
    public String receipt() throws Exception {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        ReceiptModel receiptModel = ApplicationHandler.instantiateObject(ReceiptModel.class, requestParameters);
        receiptModel.validateAndThrow();
        String uuid = receiptModel.getUuid();

        RedisUtils.delete(uuid);
        return GsonUtils.toJson(ApiRest.builder().message("回执成功！").successful(true).build());
    }

    /**
     * 扫码支付
     *
     * @return
     */
    @RequestMapping(value = "/offlinePay")
    @ResponseBody
    @ApiRestAction(modelClass = OfflinePayModel.class, serviceClass = PosService.class, serviceMethodName = "scanCodePay", error = "扫码支付")
    public String offlinePay() {
        return null;
    }
}
