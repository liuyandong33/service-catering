package build.dream.catering.controllers;

import build.dream.catering.constants.Constants;
import build.dream.catering.models.anubis.*;
import build.dream.catering.services.AnubisService;
import build.dream.catering.utils.AnubisUtils;
import build.dream.common.controllers.BasicController;
import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.ConfigurationUtils;
import build.dream.common.utils.MethodCaller;
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
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        MethodCaller methodCaller = () -> {
            ChainStoreModel chainStoreModel = ApplicationHandler.instantiateObject(ChainStoreModel.class, requestParameters);
            chainStoreModel.validateAndThrow();

            return anubisService.chainStore(chainStoreModel);
        };
        return ApplicationHandler.callMethod(methodCaller, "添加门店失败", requestParameters);
    }

    /**
     * 查询门店信息
     *
     * @return
     */
    @RequestMapping(value = "/chainStoreQuery")
    @ResponseBody
    public String chainStoreQuery() {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        MethodCaller methodCaller = () -> {
            List<String> chainStoreCodes = new ArrayList<String>();
            chainStoreCodes.add("A001");
            chainStoreCodes.add("A002");

            Map<String, Object> data = new HashMap<String, Object>();
            data.put("chain_store_code", chainStoreCodes);

            String url = ConfigurationUtils.getConfiguration(Constants.ANUBIS_SERVICE_URL) + Constants.ANUBIS_CHAIN_STORE_QUERY_URI;
            String appId = ConfigurationUtils.getConfiguration(Constants.ANUBIS_APP_ID);
            return AnubisUtils.callAnubisSystem(url, appId, data);
        };
        return ApplicationHandler.callMethod(methodCaller, "查询门店信息失败", requestParameters);
    }

    /**
     * 更新门店信息
     *
     * @return
     */
    @RequestMapping(value = "/chainStoreUpdate")
    @ResponseBody
    public String chainStoreUpdate() {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        MethodCaller methodCaller = () -> {
            ChainStoreUpdateModel chainStoreUpdateModel = ApplicationHandler.instantiateObject(ChainStoreUpdateModel.class, requestParameters);
            chainStoreUpdateModel.validateAndThrow();

            return anubisService.chainStoreUpdate(chainStoreUpdateModel);
        };
        return ApplicationHandler.callMethod(methodCaller, "更新门店信息失败", requestParameters);
    }

    /**
     * 查询配送服务
     *
     * @return
     */
    @RequestMapping(value = "/chainStoreDeliveryQuery")
    @ResponseBody
    public String chainStoreDeliveryQuery() {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        MethodCaller methodCaller = () -> {
            ChainStoreDeliveryQueryModel chainStoreDeliveryQueryModel = ApplicationHandler.instantiateObject(ChainStoreDeliveryQueryModel.class, requestParameters);
            chainStoreDeliveryQueryModel.validateAndThrow();

            return anubisService.chainStoreDeliveryQuery(chainStoreDeliveryQueryModel);
        };
        return ApplicationHandler.callMethod(methodCaller, "查询配送服务失败", requestParameters);
    }

    /**
     * 蜂鸟配送
     *
     * @return
     */
    @RequestMapping(value = "/order")
    @ResponseBody
    public String order() {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        MethodCaller methodCaller = () -> {
            OrderModel orderModel = ApplicationHandler.instantiateObject(OrderModel.class, requestParameters);
            orderModel.validateAndThrow();

            return anubisService.order(orderModel);
        };
        return ApplicationHandler.callMethod(methodCaller, "蜂鸟配送失败", requestParameters);
    }

    /**
     * 同步取消订单
     *
     * @return
     */
    @RequestMapping(value = "/orderCancel")
    @ResponseBody
    public String orderCancel() {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        MethodCaller methodCaller = () -> {
            OrderCancelModel orderCancelModel = ApplicationHandler.instantiateObject(OrderCancelModel.class, requestParameters);
            orderCancelModel.validateAndThrow();

            return anubisService.orderCancel(orderCancelModel);
        };
        return ApplicationHandler.callMethod(methodCaller, "同步取消订单失败", requestParameters);
    }

    /**
     * 订单查询
     *
     * @return
     */
    @RequestMapping(value = "/orderQuery")
    @ResponseBody
    public String orderQuery() {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        MethodCaller methodCaller = () -> {
            OrderQueryModel orderQueryModel = ApplicationHandler.instantiateObject(OrderQueryModel.class, requestParameters);
            orderQueryModel.validateAndThrow();

            return anubisService.orderQuery(orderQueryModel);
        };
        return ApplicationHandler.callMethod(methodCaller, "订单查询失败", requestParameters);
    }

    /**
     * 订单投诉
     *
     * @return
     */
    @RequestMapping(value = "/orderComplaint")
    @ResponseBody
    public String orderComplaint() {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        MethodCaller methodCaller = () -> {
            OrderComplaintModel orderComplaintModel = ApplicationHandler.instantiateObject(OrderComplaintModel.class, requestParameters);
            orderComplaintModel.validateAndThrow();

            return anubisService.orderComplaint(orderComplaintModel);
        };
        return ApplicationHandler.callMethod(methodCaller, "订单查询失败", requestParameters);
    }

    /**
     * 处理蜂鸟配送系统回调
     *
     * @return
     */
    @RequestMapping(value = "/anubisCallback")
    @ResponseBody
    public String anubisCallback() {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        MethodCaller methodCaller = () -> {
            String callbackRequestBody = requestParameters.get("callbackRequestBody");
            ApplicationHandler.notBlank(callbackRequestBody, "callbackRequestBody");

            return anubisService.handleAnubisCallback(callbackRequestBody);
        };
        return ApplicationHandler.callMethod(methodCaller, "处理蜂鸟系统回调失败", requestParameters);
    }

    /**
     * 获取配送记录
     *
     * @return
     */
    @RequestMapping(value = "/obtainDeliveryStates")
    @ResponseBody
    public String obtainDeliveryStates() {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        MethodCaller methodCaller = () -> {
            ObtainDeliveryStatesModel obtainDeliveryStatesModel = ApplicationHandler.instantiateObject(ObtainDeliveryStatesModel.class, requestParameters);
            obtainDeliveryStatesModel.validateAndThrow();

            return anubisService.obtainDeliveryStates(obtainDeliveryStatesModel);
        };
        return ApplicationHandler.callMethod(methodCaller, "获取配送记录失败", requestParameters);
    }

    /**
     * 订单骑手位置查询
     *
     * @return
     */
    @RequestMapping(value = "/orderCarrier")
    @ResponseBody
    public String orderCarrier() {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        MethodCaller methodCaller = () -> {
            OrderCarrierModel orderCarrierModel = ApplicationHandler.instantiateObject(OrderCarrierModel.class, requestParameters);
            orderCarrierModel.validateAndThrow();

            return anubisService.orderCarrier(orderCarrierModel);
        };
        return ApplicationHandler.callMethod(methodCaller, "订单骑手位置查询失败", requestParameters);
    }
}
