package build.dream.catering.controllers;

import build.dream.catering.constants.Constants;
import build.dream.catering.models.meituan.GenerateBindingStoreLinkModel;
import build.dream.catering.models.meituan.PullMeiTuanOrderModel;
import build.dream.catering.services.MeiTuanService;
import build.dream.common.api.ApiRest;
import build.dream.common.controllers.BasicController;
import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.GsonUtils;
import build.dream.common.utils.LogUtils;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping(value = "/meiTuan")
public class MeiTuanController extends BasicController {
    @Autowired
    private MeiTuanService meiTuanService;

    /**
     * 生成门店绑定链接
     *
     * @return
     */
    @RequestMapping(value = "/generateBindingStoreLink")
    @ResponseBody
    public String generateBindingStoreLink() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            GenerateBindingStoreLinkModel generateBindingStoreLinkModel = ApplicationHandler.instantiateObject(GenerateBindingStoreLinkModel.class, requestParameters);
            generateBindingStoreLinkModel.validateAndThrow();
            apiRest = meiTuanService.generateBindingStoreLink(generateBindingStoreLinkModel);
        } catch (Exception e) {
            LogUtils.error("生成门店绑定链接失败", controllerSimpleName, "createCategoryWithChildren", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    /**
     * 门店绑定回调
     *
     * @return
     */
    @RequestMapping(value = "/storeBindingCallback")
    @ResponseBody
    public String storeBindingCallback() {
        return null;
    }

    /**
     * 订单生效回调
     *
     * @return
     */
    @RequestMapping(value = "/orderEffectiveCallback")
    @ResponseBody
    public String orderEffectiveCallback() {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        String returnValue = null;
        try {
            requestParameters.put("developerId", "100120");
            requestParameters.put("ePoiId", "1Z1");
            requestParameters.put("sign", "52b379754c40c7865a48b84a24fb99c1ebb49f11");
            requestParameters.put("order", "{\"caution\": \"哈哈\",\"cityId\": 132,\"ctime\": 12341234,\"daySeq\": \"1\",\"deliveryTime\": 124124123,\"detail\": [{\"app_food_code\": \"1\",\"food_name\": \"狗不理\",\"sku_id\": \"1\",\"quantity\": 6,\"price\": 100,\"box_num\": 2,\"box_price\": 1,\"unit\": \"份\",\"food_discount\": 0.8,\"spec\": \"大份\",\"food_property\": \"中辣,微甜\",\"cart_id\": 0}],\"ePoiId\": \"erp-poi\",\"extras\": [{\"act_detail_id\": 10,\"reduce_fee\": 2.5,\"mt_charge\": 1.5,\"poi_charge\": 1.5,\"remark\": \"满10元减2.5元\",\"type\": 2,\"avg_send_time\": 5.5},{\"reduce_fee\": 5,\"remark\": \"新用户立减5元\",\"type\": 1,\"avg_send_time\": 1.0},{\"rider_fee\": 10}],\"favorites\": false,\"hasInvoiced\": 1,\"invoiceTitle\": \"XXX公司\",\"taxpayerId\": \"91110108562144110X\",\"isFavorites\": false,\"isPoiFirstOrder\": false,\"isThirdShipping\": 0,\"latitude\": 234.12341234,\"longitude\": 534.12341234,\"logisticsCode\": 1002,\"orderId\": 12341234,\"orderIdView\": 12343412344,\"originalPrice\": 25,\"payType\": 2,\"pickType\": 0,\"poiAddress\": \"望京-研发园\",\"poiFirstOrder\": false,\"poiName\": \"门店名称\",\"poiPhone\": \"18610543723\",\"poiReceiveDetail\": {\"actOrderChargeByMt\":[{\"comment\":\"美团配送减3.0元\",\"feeTypeDesc\":\"活动款\",\"feeTypeId\":10019 ,\"moneyCent\":300}],\"actOrderChargeByPoi\" :[{\"comment\":\"美团配送减3.0元\",\"feeTypeDesc\":\"活动款\",\"feeTypeId\":10019,\"moneyCent\":0}],\"foodShareFeeChargeByPoi\":390,\"logisticsFee\":300 ,\"onlinePayment\":2000 ,\"wmPoiReceiveCent\":1610 },\"recipientAddress\": \"望京-西小区-8号楼5层\",\"recipientName\": \"X先生\",\"recipientPhone\": \"18610543723\",\"shipperPhone\": \"18610543723\",\"shippingFee\": 5,\"status\": 1,\"total\": 20,\"utime\": 235131234}");
            ApiRest apiRest = meiTuanService.handleOrderEffectiveCallback(requestParameters);
            Validate.isTrue(apiRest.isSuccessful(), apiRest.getError());
            returnValue = Constants.ELEME_ORDER_CALLBACK_SUCCESS_RETURN_VALUE;
        } catch (Exception e) {
            LogUtils.error("订单生效回调处理失败", controllerSimpleName, "orderEffectiveCallback", e, requestParameters);
            returnValue = e.getMessage();
        }
        return returnValue;
    }

    /**
     * 订单取消回调
     *
     * @return
     */
    @RequestMapping(value = "/orderCancelCallback")
    @ResponseBody
    public String orderCancelCallback() {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        String returnValue = null;
        try {
            requestParameters.put("developerId", "100120");
            requestParameters.put("ePoiId", "1Z1");
            requestParameters.put("sign", "52b379754c40c7865a48b84a24fb99c1ebb49f11");
            requestParameters.put("orderCancel", "{\"orderId\":12341234,\"reason\":\"超时取消\",\"reasonCode\":\"1002\"}");
            ApiRest apiRest = meiTuanService.handleOrderCancelCallback(requestParameters);
            Validate.isTrue(apiRest.isSuccessful(), apiRest.getError());
            returnValue = Constants.ELEME_ORDER_CALLBACK_SUCCESS_RETURN_VALUE;
        } catch (Exception e) {
            LogUtils.error("订单取消回调处理失败", controllerSimpleName, "orderCancelCallback", e, requestParameters);
            returnValue = e.getMessage();
        }
        return returnValue;
    }

    /**
     * 订单退款回调
     *
     * @return
     */
    @RequestMapping(value = "/orderRefundCallback")
    @ResponseBody
    public String orderRefundCallback() {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        String returnValue = null;
        try {
            requestParameters.put("developerId", "100120");
            requestParameters.put("ePoiId", "1Z1");
            requestParameters.put("sign", "52b379754c40c7865a48b84a24fb99c1ebb49f11");
            requestParameters.put("orderRefund", "{\"notifyType\":\"agree\",\"orderId\":12341234,\"reason\":\"同意\"}");
            ApiRest apiRest = meiTuanService.handleOrderRefundCallback(requestParameters);
            Validate.isTrue(apiRest.isSuccessful(), apiRest.getError());
            returnValue = Constants.ELEME_ORDER_CALLBACK_SUCCESS_RETURN_VALUE;
        } catch (Exception e) {
            LogUtils.error("订单取消回调处理失败", controllerSimpleName, "orderCancelCallback", e, requestParameters);
            returnValue = e.getMessage();
        }
        return returnValue;
    }

    /**
     * 拉取美团订单
     *
     * @return
     */
    @RequestMapping(value = "/pullMeiTuanOrder")
    @ResponseBody
    public String pullMeiTuanOrder() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            PullMeiTuanOrderModel pullMeiTuanOrderModel = ApplicationHandler.instantiateObject(PullMeiTuanOrderModel.class, requestParameters);
            pullMeiTuanOrderModel.validateAndThrow();

            apiRest = meiTuanService.pullMeiTuanOrder(pullMeiTuanOrderModel);
        } catch (Exception e) {
            LogUtils.error("拉取美团订单失败！", controllerSimpleName, "pullMeiTuanOrder", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }
}
