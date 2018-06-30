package build.dream.catering.controllers;

import build.dream.catering.models.anubis.*;
import build.dream.catering.services.AnubisService;
import build.dream.common.annotations.ApiRestAction;
import build.dream.common.controllers.BasicController;
import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.GsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.Map;

@Controller
@RequestMapping(value = "/anubis")
public class AnubisController extends BasicController {
    @Autowired
    private AnubisService anubisService;

    /**
     * 添加门店
     *
     * @return
     */
    @RequestMapping(value = "/chainStore")
    @ResponseBody
    @ApiRestAction(modelClass = ChainStoreModel.class, serviceClass = AnubisService.class, serviceMethodName = "chainStore", error = "添加门店失败")
    public String chainStore() {
        return null;
    }

    /**
     * 查询门店信息
     *
     * @return
     */
    @RequestMapping(value = "/chainStoreQuery")
    @ResponseBody
    @ApiRestAction(modelClass = ChainStoreQueryModel.class, serviceClass = AnubisService.class, serviceMethodName = "chainStoreQuery", error = "查询门店信息失败")
    public String chainStoreQuery() {
        return null;
    }

    /**
     * 更新门店信息
     *
     * @return
     */
    @RequestMapping(value = "/chainStoreUpdate")
    @ResponseBody
    @ApiRestAction(modelClass = ChainStoreUpdateModel.class, serviceClass = AnubisService.class, serviceMethodName = "chainStoreUpdate", error = "更新门店信息失败")
    public String chainStoreUpdate() {
        return null;
    }

    /**
     * 查询配送服务
     *
     * @return
     */
    @RequestMapping(value = "/chainStoreDeliveryQuery")
    @ResponseBody
    @ApiRestAction(modelClass = ChainStoreDeliveryQueryModel.class, serviceClass = AnubisService.class, serviceMethodName = "chainStoreDeliveryQuery", error = "查询配送服务失败")
    public String chainStoreDeliveryQuery() {
        return null;
    }

    /**
     * 蜂鸟配送
     *
     * @return
     */
    @RequestMapping(value = "/order")
    @ResponseBody
    @ApiRestAction(modelClass = OrderModel.class, serviceClass = AnubisService.class, serviceMethodName = "order", error = "蜂鸟配送失败")
    public String order() {
        return null;
    }

    /**
     * 同步取消订单
     *
     * @return
     */
    @RequestMapping(value = "/orderCancel")
    @ResponseBody
    @ApiRestAction(modelClass = OrderCancelModel.class, serviceClass = AnubisService.class, serviceMethodName = "orderCancel", error = "同步取消订单失败")
    public String orderCancel() {
        return null;
    }

    /**
     * 订单查询
     *
     * @return
     */
    @RequestMapping(value = "/orderQuery")
    @ResponseBody
    @ApiRestAction(modelClass = OrderQueryModel.class, serviceClass = AnubisService.class, serviceMethodName = "orderQuery", error = "订单查询失败")
    public String orderQuery() {
        return null;
    }

    /**
     * 订单投诉
     *
     * @return
     */
    @RequestMapping(value = "/orderComplaint")
    @ResponseBody
    @ApiRestAction(modelClass = OrderComplaintModel.class, serviceClass = AnubisService.class, serviceMethodName = "orderComplaint", error = "订单查询失败")
    public String orderComplaint() {
        return null;
    }

    /**
     * 处理蜂鸟配送系统回调
     *
     * @return
     */
    @RequestMapping(value = "/anubisCallback")
    @ResponseBody
    @ApiRestAction(error = "处理蜂鸟系统回调失败")
    public String anubisCallback() throws IOException {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        String callbackRequestBody = requestParameters.get("callbackRequestBody");
        ApplicationHandler.notBlank(callbackRequestBody, "callbackRequestBody");

        return GsonUtils.toJson(anubisService.handleAnubisCallback(callbackRequestBody));
    }

    /**
     * 获取配送记录
     *
     * @return
     */
    @RequestMapping(value = "/obtainDeliveryStates")
    @ResponseBody
    @ApiRestAction(modelClass = ObtainDeliveryStatesModel.class, serviceClass = AnubisService.class, serviceMethodName = "obtainDeliveryStates", error = "获取配送记录失败")
    public String obtainDeliveryStates() {
        return null;
    }

    /**
     * 订单骑手位置查询
     *
     * @return
     */
    @RequestMapping(value = "/orderCarrier")
    @ResponseBody
    @ApiRestAction(modelClass = OrderCarrierModel.class, serviceClass = AnubisService.class, serviceMethodName = "orderCarrier", error = "订单骑手位置查询失败")
    public String orderCarrier() {
        return null;
    }
}
