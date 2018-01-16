package build.dream.catering.controllers;

import build.dream.common.api.ApiRest;
import build.dream.common.controllers.BasicController;
import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.GsonUtils;
import build.dream.common.utils.LogUtils;
import build.dream.catering.models.dietorder.DoPayModel;
import build.dream.catering.models.dietorder.DoPayOfflineModel;
import build.dream.catering.models.dietorder.SaveDietOrderModel;
import build.dream.catering.services.DietOrderService;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
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
            String orderInfo = requestParameters.get("orderInfo");
            Validate.notNull(orderInfo, ApplicationHandler.obtainParameterErrorMessage("orderInfo"));
            List<SaveDietOrderModel.DietOrderModel> dietOrderModels = GsonUtils.jsonToList(orderInfo, SaveDietOrderModel.DietOrderModel.class);

            SaveDietOrderModel saveDietOrderModel = ApplicationHandler.instantiateObject(SaveDietOrderModel.class, requestParameters);
            saveDietOrderModel.setDietOrderModels(dietOrderModels);
            saveDietOrderModel.validateAndThrow();

            apiRest = dietOrderService.saveDietOrder(saveDietOrderModel);
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

    @RequestMapping(value = "/doPayOffline")
    @ResponseBody
    public String doPayOffline() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            String bizContent = requestParameters.get("bizContent");
            Validate.notNull(bizContent, ApplicationHandler.obtainParameterErrorMessage("bizContent"));

            String signature = requestParameters.get("signature");
            Validate.notNull(signature, ApplicationHandler.obtainParameterErrorMessage("signature"));

            DoPayOfflineModel doPayOfflineModel = GsonUtils.fromJson(bizContent, DoPayOfflineModel.class);
            doPayOfflineModel.validateAndThrow();
            apiRest = dietOrderService.doPayOffline(doPayOfflineModel, bizContent, signature);
        } catch (Exception e) {
            LogUtils.error("提交线下支付请求失败", controllerSimpleName, "doPayOffline", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }
}
