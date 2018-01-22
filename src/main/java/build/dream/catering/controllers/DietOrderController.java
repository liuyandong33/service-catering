package build.dream.catering.controllers;

import build.dream.catering.models.dietorder.ObtainDietOrderInfoModel;
import build.dream.catering.models.dietorder.SaveDietOrderModel;
import build.dream.catering.services.DietOrderService;
import build.dream.common.api.ApiRest;
import build.dream.common.controllers.BasicController;
import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.GsonUtils;
import build.dream.common.utils.LogUtils;
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

    @RequestMapping(value = "/obtainDietOrderInfo")
    @ResponseBody
    public String obtainDietOrderInfo() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            ObtainDietOrderInfoModel obtainDietOrderInfoModel = ApplicationHandler.instantiateObject(ObtainDietOrderInfoModel.class, requestParameters);
            obtainDietOrderInfoModel.validateAndThrow();
            apiRest = dietOrderService.obtainDietOrderInfo(obtainDietOrderInfoModel);
        } catch (Exception e) {
            LogUtils.error("获取订单信息失败", controllerSimpleName, "obtainDietOrderInfo", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    @RequestMapping(value = "/saveDietOrder")
    @ResponseBody
    public String saveDietOrder() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            String groups = requestParameters.get("groups");
            ApplicationHandler.notEmpty(groups, "groups");

            SaveDietOrderModel saveDietOrderModel = ApplicationHandler.instantiateObject(SaveDietOrderModel.class, requestParameters);

            List<SaveDietOrderModel.GroupModel> groupModels = GsonUtils.jsonToList(groups, SaveDietOrderModel.GroupModel.class);
            saveDietOrderModel.setGroupModels(groupModels);
            saveDietOrderModel.validateAndThrow();

            apiRest = dietOrderService.saveDietOrder(saveDietOrderModel);
        } catch (Exception e) {
            LogUtils.error("保存订单失败", controllerSimpleName, "saveDietOrder", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }
}
