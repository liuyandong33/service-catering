package build.dream.catering.controllers;

import build.dream.catering.constants.Constants;
import build.dream.catering.models.anubis.*;
import build.dream.catering.services.AnubisService;
import build.dream.catering.utils.AnubisUtils;
import build.dream.common.api.ApiRest;
import build.dream.common.controllers.BasicController;
import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.ConfigurationUtils;
import build.dream.common.utils.GsonUtils;
import build.dream.common.utils.LogUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    public String chainStore() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            ChainStoreModel chainStoreModel = ApplicationHandler.instantiateObject(ChainStoreModel.class, requestParameters);
            chainStoreModel.validateAndThrow();

            apiRest = anubisService.chainStore(chainStoreModel);
        } catch (Exception e) {
            LogUtils.error("添加门店失败", controllerSimpleName, "getAccessToken", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    /**
     * 查询门店信息
     *
     * @return
     */
    @RequestMapping(value = "/chainStoreQuery")
    @ResponseBody
    public String chainStoreQuery() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            List<String> chainStoreCodes = new ArrayList<String>();
            chainStoreCodes.add("A001");
            chainStoreCodes.add("A002");

            Map<String, Object> data = new HashMap<String, Object>();
            data.put("chain_store_code", chainStoreCodes);

            String url = ConfigurationUtils.getConfiguration(Constants.ANUBIS_SERVICE_URL) + Constants.ANUBIS_CHAIN_STORE_QUERY_URI;
            String appId = ConfigurationUtils.getConfiguration(Constants.ANUBIS_APP_ID);
            apiRest = AnubisUtils.callAnubisSystem(url, appId, data);
        } catch (Exception e) {
            LogUtils.error("查询门店信息失败", controllerSimpleName, "chainStoreQuery", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    /**
     * 蜂鸟配送
     *
     * @return
     */
    @RequestMapping(value = "/order")
    @ResponseBody
    public String order() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            OrderModel orderModel = ApplicationHandler.instantiateObject(OrderModel.class, requestParameters);
            orderModel.validateAndThrow();

        } catch (Exception e) {
            LogUtils.error("蜂鸟配送失败", controllerSimpleName, "order", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    /**
     * 同步取消订单
     *
     * @return
     */
    @RequestMapping(value = "/orderCancel")
    @ResponseBody
    public String orderCancel() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            OrderCancelModel orderCancelModel = ApplicationHandler.instantiateObject(OrderCancelModel.class, requestParameters);
            orderCancelModel.validateAndThrow();

            apiRest = anubisService.orderCancel(orderCancelModel);
        } catch (Exception e) {
            LogUtils.error("同步取消订单失败", controllerSimpleName, "orderCancel", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    /**
     * 订单查询
     *
     * @return
     */
    @RequestMapping(value = "/orderQuery")
    @ResponseBody
    public String orderQuery() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            OrderQueryModel orderQueryModel = ApplicationHandler.instantiateObject(OrderQueryModel.class, requestParameters);
            orderQueryModel.validateAndThrow();
            apiRest = anubisService.orderQuery(orderQueryModel);
        } catch (Exception e) {
            LogUtils.error("订单查询失败", controllerSimpleName, "orderQuery", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    /**
     * 订单投诉
     *
     * @return
     */
    @RequestMapping(value = "/orderComplaint")
    @ResponseBody
    public String orderComplaint() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            OrderComplaintModel orderComplaintModel = ApplicationHandler.instantiateObject(OrderComplaintModel.class, requestParameters);
            orderComplaintModel.validateAndThrow();

            apiRest = anubisService.orderComplaint(orderComplaintModel);
        } catch (Exception e) {
            LogUtils.error("订单查询失败", controllerSimpleName, "orderComplaint", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    /**
     * 处理蜂鸟配送系统回调
     *
     * @return
     */
    @RequestMapping(value = "/anubisCallback")
    @ResponseBody
    public String anubisCallback() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            String callbackRequestBody = requestParameters.get("callbackRequestBody");
            ApplicationHandler.notBlank(callbackRequestBody, "callbackRequestBody");
            apiRest = anubisService.handleAnubisCallback(callbackRequestBody);
        } catch (Exception e) {
            LogUtils.error("处理蜂鸟系统回调失败", controllerSimpleName, "anubisCallback", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    /**
     * 获取配送记录
     *
     * @return
     */
    @RequestMapping(value = "/obtainDeliveryStates")
    @ResponseBody
    public String obtainDeliveryStates() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            ObtainDeliveryStatesModel obtainDeliveryStatesModel = ApplicationHandler.instantiateObject(ObtainDeliveryStatesModel.class, requestParameters);
            obtainDeliveryStatesModel.validateAndThrow();

            apiRest = anubisService.obtainDeliveryStates(obtainDeliveryStatesModel);
        } catch (Exception e) {
            LogUtils.error("获取配送记录失败", controllerSimpleName, "obtainDeliveryStates", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }
}
