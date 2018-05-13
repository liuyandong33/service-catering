package build.dream.catering.controllers;

import build.dream.catering.models.dietorder.ObtainDietOrderInfoModel;
import build.dream.catering.models.dietorder.SaveDietOrderModel;
import build.dream.catering.services.DietOrderService;
import build.dream.common.controllers.BasicController;
import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.MethodCaller;
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

    /**
     * 获取订单明细
     *
     * @return
     */
    @RequestMapping(value = "/obtainDietOrderInfo")
    @ResponseBody
    public String obtainDietOrderInfo() {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        MethodCaller methodCaller = () -> {
            ObtainDietOrderInfoModel obtainDietOrderInfoModel = ApplicationHandler.instantiateObject(ObtainDietOrderInfoModel.class, requestParameters);
            obtainDietOrderInfoModel.validateAndThrow();
            return dietOrderService.obtainDietOrderInfo(obtainDietOrderInfoModel);
        };
        return ApplicationHandler.callMethod(methodCaller, "获取订单信息失败", requestParameters);
    }

    /**
     * 保存订单
     *
     * @return
     */
    @RequestMapping(value = "/saveDietOrder")
    @ResponseBody
    public String saveDietOrder() {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        MethodCaller methodCaller = () -> {
            SaveDietOrderModel saveDietOrderModel = ApplicationHandler.instantiateObject(SaveDietOrderModel.class, requestParameters);
            String groups = requestParameters.get("groups");
            saveDietOrderModel.setGroupInfos(groups);
            saveDietOrderModel.validateAndThrow();

            return dietOrderService.saveDietOrder(saveDietOrderModel);
        };
        return ApplicationHandler.callMethod(methodCaller, "保存订单失败", requestParameters);
    }
}
