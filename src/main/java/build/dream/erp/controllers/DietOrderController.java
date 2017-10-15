package build.dream.erp.controllers;

import build.dream.common.api.ApiRest;
import build.dream.common.controllers.BasicController;
import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.GsonUtils;
import build.dream.common.utils.LogUtils;
import build.dream.erp.models.dietorder.DoPayModel;
import build.dream.erp.services.DietOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping(value = "/dietOrder")
public class DietOrderController extends BasicController {
    @Autowired
    private DietOrderService dietOrderService;

    @RequestMapping(value = "/saveDietOrder")
    @ResponseBody
    public String saveDietOrder() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            apiRest = new ApiRest();
            apiRest.setData("100");
            apiRest.setMessage("保存订单成功！");
            apiRest.setSuccessful(true);
        } catch (Exception e) {
            LogUtils.error("保存订单失败", controllerSimpleName, "saveDietOrder", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    @RequestMapping(value = "/doPay")
    @ResponseBody
    public String doPay() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            DoPayModel doPayModel = ApplicationHandler.instantiateObject(DoPayModel.class, requestParameters);
            doPayModel.validateAndThrow();

            apiRest = dietOrderService.doPay(doPayModel);
        } catch (Exception e) {
            LogUtils.error("提交支付请求失败", controllerSimpleName, "doPay", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }
}
