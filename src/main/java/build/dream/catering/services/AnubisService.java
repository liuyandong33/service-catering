package build.dream.catering.services;

import build.dream.catering.constants.Constants;
import build.dream.catering.models.anubis.*;
import build.dream.catering.utils.AnubisUtils;
import build.dream.common.api.ApiRest;
import build.dream.common.domains.catering.*;
import build.dream.common.utils.*;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.util.*;

@Service
public class AnubisService {
    /**
     * 添加门店
     *
     * @param chainStoreModel
     * @return
     * @throws IOException
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiRest chainStore(ChainStoreModel chainStoreModel) throws IOException {
        BigInteger tenantId = chainStoreModel.getTenantId();
        BigInteger branchId = chainStoreModel.getBranchId();
        BigInteger userId = chainStoreModel.getUserId();

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        Branch branch = DatabaseHelper.find(Branch.class, searchModel);
        ValidateUtils.notNull(branch, "门店不存在！");

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("chain_store_code", branch.getTenantCode() + "Z" + branch.getCode());
        data.put("chain_store_name", branch.getTenantCode() + "Z" + branch.getCode() + "Z" + branch.getName());
        data.put("contact_phone", branch.getContactPhone());
        data.put("address", branch.getProvinceName() + branch.getCityName() + branch.getDistrictName() + branch.getAddress());
        data.put("position_source", Constants.POSITION_SOURCE_BAIDU_MAP);
        data.put("longitude", branch.getLongitude());
        data.put("latitude", branch.getLatitude());
        data.put("service_code", 1);

        String url = ConfigurationUtils.getConfiguration(Constants.ANUBIS_SERVICE_URL) + Constants.ANUBIS_CHAIN_STORE_URI;
        String appId = ConfigurationUtils.getConfiguration(Constants.ANUBIS_APP_ID);
        ApiRest apiRest = AnubisUtils.callAnubisSystem(url, appId, data);
        return apiRest;
    }

    /**
     * 更新门店信息
     *
     * @param chainStoreUpdateModel
     * @return
     * @throws IOException
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiRest chainStoreUpdate(ChainStoreUpdateModel chainStoreUpdateModel) throws IOException {
        BigInteger tenantId = chainStoreUpdateModel.getTenantId();
        BigInteger branchId = chainStoreUpdateModel.getBranchId();
        BigInteger userId = chainStoreUpdateModel.getUserId();

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        Branch branch = DatabaseHelper.find(Branch.class, searchModel);
        ValidateUtils.notNull(branch, "门店不存在！");

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("chain_store_code", branch.getTenantCode() + "Z" + branch.getCode());
        data.put("chain_store_name", branch.getTenantCode() + "Z" + branch.getCode() + "Z" + branch.getName());
        data.put("contact_phone", branch.getContactPhone());
        data.put("address", branch.getProvinceName() + branch.getCityName() + branch.getDistrictName() + branch.getAddress());
        data.put("position_source", Constants.POSITION_SOURCE_BAIDU_MAP);
        data.put("longitude", branch.getLongitude());
        data.put("latitude", branch.getLatitude());
        data.put("service_code", 1);

        String url = ConfigurationUtils.getConfiguration(Constants.ANUBIS_SERVICE_URL) + Constants.ANUBIS_CHAIN_STORE_UPDATE_URI;
        String appId = ConfigurationUtils.getConfiguration(Constants.ANUBIS_APP_ID);
        ApiRest apiRest = AnubisUtils.callAnubisSystem(url, appId, data);
        return apiRest;
    }

    /**
     * 查询配送服务
     *
     * @param chainStoreDeliveryQueryModel
     * @return
     * @throws IOException
     */
    @Transactional(readOnly = true)
    public ApiRest chainStoreDeliveryQuery(ChainStoreDeliveryQueryModel chainStoreDeliveryQueryModel) throws IOException {
        BigInteger tenantId = chainStoreDeliveryQueryModel.getTenantId();
        BigInteger branchId = chainStoreDeliveryQueryModel.getBranchId();

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        Branch branch = DatabaseHelper.find(Branch.class, searchModel);
        ValidateUtils.notNull(branch, "门店不存在！");

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("chain_store_code", branch.getTenantCode() + "Z" + branch.getCode());
        data.put("position_source", Constants.POSITION_SOURCE_BAIDU_MAP);
        data.put("receiver_longitude", chainStoreDeliveryQueryModel.getReceiverLongitude());
        data.put("receiver_latitude", chainStoreDeliveryQueryModel.getReceiverLatitude());

        String url = ConfigurationUtils.getConfiguration(Constants.ANUBIS_SERVICE_URL) + Constants.ANUBIS_CHAIN_STORE_DELIVERY_QUERY_URI;
        String appId = ConfigurationUtils.getConfiguration(Constants.ANUBIS_APP_ID);
        ApiRest apiRest = AnubisUtils.callAnubisSystem(url, appId, data);
        return apiRest;
    }

    /**
     * 蜂鸟配送
     *
     * @param orderModel
     * @return
     * @throws IOException
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiRest order(OrderModel orderModel) throws IOException {
        BigInteger tenantId = orderModel.getTenantId();
        BigInteger branchId = orderModel.getBranchId();
        BigInteger userId = orderModel.getUserId();
        BigInteger dietOrderId = orderModel.getDietOrderId();

        // 查询门店信息
        SearchModel branchSearchModel = new SearchModel(true);
        branchSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        branchSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        Branch branch = DatabaseHelper.find(Branch.class, branchSearchModel);
        ValidateUtils.notNull(branch, "门店不存在！");

        // 查询订单信息
        SearchModel dietOrderSearchModel = new SearchModel(true);
        dietOrderSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        dietOrderSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        dietOrderSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUAL, dietOrderId);
        DietOrder dietOrder = DatabaseHelper.find(DietOrder.class, dietOrderSearchModel);
        ValidateUtils.notNull(dietOrder, "订单不存在！");

        SearchModel dietOrderGroupSearchModel = new SearchModel(true);
        dietOrderGroupSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        dietOrderGroupSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        dietOrderGroupSearchModel.addSearchCondition("diet_order_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, dietOrderId);
        List<DietOrderGroup> dietOrderGroups = DatabaseHelper.findAll(DietOrderGroup.class, dietOrderGroupSearchModel);

        // 查询出订单详情信息
        SearchModel dietOrderDetailSearchModel = new SearchModel(true);
        dietOrderDetailSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        dietOrderDetailSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        dietOrderDetailSearchModel.addSearchCondition("diet_order_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, dietOrderId);
        List<DietOrderDetail> dietOrderDetails = DatabaseHelper.findAll(DietOrderDetail.class, dietOrderDetailSearchModel);

        Map<String, Object> data = new HashMap<String, Object>();
        ApplicationHandler.ifNotNullPut(data, "partner_remark", orderModel.getPartnerRemark());
        data.put("partner_order_code", dietOrder.getOrderNumber());

        String notifyUrl = CommonUtils.getUrl(ConfigurationUtils.getConfiguration(Constants.PARTITION_CODE), Constants.SERVICE_NAME_CATERING, "anubis", "anubisCallback");

        data.put("notify_url", notifyUrl);
        data.put("order_type", 1);
        data.put("chain_store_code", branch.getTenantCode() + "Z" + branch.getCode());

        Map<String, Object> transportInfo = new HashMap<String, Object>();
        transportInfo.put("transport_name", branch.getTenantCode() + "Z" + branch.getCode() + "Z" + branch.getName());
        transportInfo.put("transport_address", branch.getProvinceName() + branch.getCityName() + branch.getDistrictName() + branch.getAddress());
        transportInfo.put("transport_longitude", branch.getLongitude());
        transportInfo.put("transport_latitude", branch.getLatitude());
        transportInfo.put("position_source", Constants.POSITION_SOURCE_BAIDU_MAP);
        transportInfo.put("transport_tel", branch.getContactPhone());
        ApplicationHandler.ifNotNullPut(transportInfo, "transport_remark", orderModel.getTransportRemark());
        data.put("transport_info", transportInfo);

        data.put("order_add_time", dietOrder.getActiveTime().getTime());
        data.put("order_total_amount", dietOrder.getTotalAmount());
        data.put("order_actual_amount", dietOrder.getPayableAmount());
        data.put("order_weight", 1);
        data.put("order_remark", dietOrder.getRemark());

        boolean invoiced = dietOrder.isInvoiced();

        data.put("is_invoiced", invoiced ? 1 : 0);
        if (invoiced) {
            data.put("invoice", dietOrder.getInvoice());
        }
        data.put("order_payment_status", 1);
        data.put("order_payment_method", 1);
        data.put("is_agent_payment", 0);
        // 需要代收时客户应付金额
//        data.put("require_payment_pay", 10);
        data.put("goods_count", dietOrderGroups.size());
        data.put("require_receive_time", dietOrder.getDeliverTime().getTime());

        Map<String, Object> receiverInfo = new HashMap<String, Object>();
        receiverInfo.put("receiver_name", dietOrder.getConsignee());
        receiverInfo.put("receiver_primary_phone", dietOrder.getTelephoneNumber());
        // 收货人备用联系方式
//        receiverInfo.put("receiver_second_phone", dietOrder.getTelephoneNumber());
        receiverInfo.put("receiver_address", dietOrder.getDeliveryAddress());
        receiverInfo.put("receiver_longitude", dietOrder.getDeliveryLongitude());
        receiverInfo.put("receiver_latitude", dietOrder.getDeliveryLatitude());
        receiverInfo.put("position_source", Constants.POSITION_SOURCE_BAIDU_MAP);
        data.put("receiver_info", receiverInfo);

        List<Map<String, Object>> itemInfos = new ArrayList<Map<String, Object>>();
        for (DietOrderDetail dietOrderDetail : dietOrderDetails) {
            Map<String, Object> itemInfo = new HashMap<String, Object>();
            itemInfo.put("item_id", dietOrderDetail.getGoodsId() + "_" + dietOrderDetail.getGoodsSpecificationId());
            String itemName = dietOrderDetail.getGoodsName();
            if (StringUtils.isNotBlank(dietOrderDetail.getGoodsSpecificationName())) {
                itemName = itemName + "_" + dietOrderDetail.getGoodsSpecificationName();
            }
            itemInfo.put("item_name", itemName);
            itemInfo.put("item_quantity", dietOrderDetail.getQuantity());
            itemInfo.put("item_price", dietOrderDetail.getTotalAmount());
            itemInfo.put("item_actual_price", dietOrderDetail.getPayableAmount());
            // 商品尺寸
//            itemInfo.put("item_size", 10);
            // 商品备注
//            itemInfo.put("item_remark", "商品备注");
            itemInfo.put("is_need_package", 0);
            itemInfo.put("is_agent_purchase", 0);
            // 代购进价, 如果需要代购 此项必填
//            itemInfo.put("agent_purchase_price", 10);
            itemInfos.add(itemInfo);
        }
        data.put("items_json", itemInfos);
        data.put("serial_number", dietOrder.getDaySerialNumber());

        String url = ConfigurationUtils.getConfiguration(Constants.ANUBIS_SERVICE_URL) + Constants.ANUBIS_ORDER_URI;
        String appId = ConfigurationUtils.getConfiguration(Constants.ANUBIS_APP_ID);
        ApiRest apiRest = AnubisUtils.callAnubisSystem(url, appId, data);
        return apiRest;
    }

    /**
     * 同步取消订单
     *
     * @param orderCancelModel
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest orderCancel(OrderCancelModel orderCancelModel) throws IOException {
        BigInteger tenantId = orderCancelModel.getTenantId();
        BigInteger branchId = orderCancelModel.getBranchId();
        BigInteger userId = orderCancelModel.getUserId();
        BigInteger dietOrderId = orderCancelModel.getDietOrderId();

        DietOrder dietOrder = obtainDietOrder(tenantId, branchId, dietOrderId);

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("partner_order_code", dietOrder.getOrderNumber());
        data.put("order_cancel_reason_code", 2);
        data.put("order_cancel_code", orderCancelModel.getOrderCancelCode());
        ApplicationHandler.ifNotNullPut(data, "order_cancel_description", orderCancelModel.getOrderCancelDescription());
        data.put("order_cancel_time", System.currentTimeMillis());

        String url = ConfigurationUtils.getConfiguration(Constants.ANUBIS_SERVICE_URL) + Constants.ANUBIS_ORDER_CANCEL_URI;
        String appId = ConfigurationUtils.getConfiguration(Constants.ANUBIS_APP_ID);
        ApiRest apiRest = AnubisUtils.callAnubisSystem(url, appId, data);
        return apiRest;
    }

    /**
     * 订单查询
     *
     * @param orderQueryModel
     * @return
     * @throws IOException
     */
    @Transactional(readOnly = true)
    public ApiRest orderQuery(OrderQueryModel orderQueryModel) throws IOException {
        BigInteger tenantId = orderQueryModel.getTenantId();
        BigInteger branchId = orderQueryModel.getBranchId();
        BigInteger dietOrderId = orderQueryModel.getDietOrderId();

        DietOrder dietOrder = obtainDietOrder(tenantId, branchId, dietOrderId);

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("partner_order_code", dietOrder.getOrderNumber());

        String url = ConfigurationUtils.getConfiguration(Constants.ANUBIS_SERVICE_URL) + Constants.ANUBIS_ORDER_QUERY_URI;
        String appId = ConfigurationUtils.getConfiguration(Constants.ANUBIS_APP_ID);
        return AnubisUtils.callAnubisSystem(url, appId, data);
    }

    /**
     * 查询订单信息
     *
     * @param tenantId
     * @param branchId
     * @param orderId
     * @return
     */
    private DietOrder obtainDietOrder(BigInteger tenantId, BigInteger branchId, BigInteger orderId) {
        SearchModel dietOrderSearchModel = SearchModel.builder()
                .autoSetDeletedFalse()
                .addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId)
                .addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId)
                .addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUAL, orderId)
                .build();
        DietOrder dietOrder = DatabaseHelper.find(DietOrder.class, dietOrderSearchModel);
        ValidateUtils.notNull(dietOrder, "订单不存在！");
        return dietOrder;
    }

    /**
     * 订单投诉
     *
     * @param orderComplaintModel
     * @return
     * @throws IOException
     */
    @Transactional(readOnly = true)
    public ApiRest orderComplaint(OrderComplaintModel orderComplaintModel) throws IOException {
        BigInteger tenantId = orderComplaintModel.obtainTenantId();
        BigInteger branchId = orderComplaintModel.obtainBranchId();
        BigInteger orderId = orderComplaintModel.getOrderId();
        Integer orderComplaintCode = orderComplaintModel.getOrderComplaintCode();
        String orderComplaintDesc = orderComplaintModel.getOrderComplaintDesc();

        DietOrder dietOrder = obtainDietOrder(tenantId, branchId, orderId);

        build.dream.common.models.anubis.OrderComplaintModel model = build.dream.common.models.anubis.OrderComplaintModel.builder()
                .partnerOrderCode(dietOrder.getOrderNumber())
                .orderComplaintCode(orderComplaintCode)
                .orderComplaintTime(System.currentTimeMillis())
                .build();

        if (StringUtils.isNotBlank(orderComplaintDesc)) {
            model.setOrderComplaintDesc(orderComplaintDesc);
        }

        Map<String, Object> result = build.dream.common.utils.AnubisUtils.orderComplaint(model);
        return ApiRest.builder().data(result).message("订单投诉成功！").successful(true).build();
    }

    /**
     * 处理蜂鸟配送系统回调
     *
     * @param callbackRequestBody
     * @return
     * @throws UnsupportedEncodingException
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiRest handleAnubisCallback(String callbackRequestBody) throws IOException {
        JSONObject callbackRequestBodyJsonObject = JSONObject.fromObject(callbackRequestBody);
        String data = URLDecoder.decode(callbackRequestBodyJsonObject.getString("data"), Constants.CHARSET_NAME_UTF_8);

        String appId = ConfigurationUtils.getConfiguration(Constants.ANUBIS_APP_ID);
        String signature = callbackRequestBodyJsonObject.getString("signature");
        int salt = callbackRequestBodyJsonObject.getInt("salt");
        ValidateUtils.isTrue(AnubisUtils.verifySignature(appId, data, salt, signature), "签名验证未通过！");

        JSONObject dataJsonObject = JSONObject.fromObject(data);

        String orderNumber = dataJsonObject.getString("partner_order_code");
        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("order_number", Constants.SQL_OPERATION_SYMBOL_EQUAL, orderNumber);
        DietOrder dietOrder = DatabaseHelper.find(DietOrder.class, searchModel);
        ValidateUtils.notNull(dietOrder, "订单不存在！");

        DietOrderDeliveryState dietOrderDeliveryState = new DietOrderDeliveryState();
        dietOrderDeliveryState.setTenantId(dietOrder.getTenantId());
        dietOrderDeliveryState.setTenantCode(dietOrder.getTenantCode());
        dietOrderDeliveryState.setBranchId(dietOrder.getBranchId());
        dietOrderDeliveryState.setDietOrderId(dietOrder.getId());
        dietOrderDeliveryState.setDietOrderNumber(orderNumber);
        dietOrderDeliveryState.setStatus(dataJsonObject.getInt("order_status"));
        dietOrderDeliveryState.setCarrierDriverName(dataJsonObject.optString("carrier_driver_name", null));
        dietOrderDeliveryState.setCarrierDriverPhone(dataJsonObject.optString("carrier_driver_phone", null));
        dietOrderDeliveryState.setDescription(dataJsonObject.optString("description", null));
        dietOrderDeliveryState.setStationName(dataJsonObject.optString("station_name", null));
        dietOrderDeliveryState.setStationTel(dataJsonObject.optString("station_tel", null));

        int cancelReason = dataJsonObject.optInt("cancel_reason", -1);
        dietOrderDeliveryState.setCancelReason(cancelReason == -1 ? null : cancelReason);
        dietOrderDeliveryState.setErrorCode(dataJsonObject.optString("error_code", null));
        dietOrderDeliveryState.setAddress(dataJsonObject.optString("address", null));
        dietOrderDeliveryState.setLongitude(dataJsonObject.optString("longitude", null));
        dietOrderDeliveryState.setLatitude(dataJsonObject.optString("latitude", null));

        BigInteger userId = BigInteger.ZERO;
        dietOrderDeliveryState.setCreatedUserId(userId);
        dietOrderDeliveryState.setUpdatedUserId(userId);
        long pushTime = dataJsonObject.getLong("push_time");

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(pushTime);

        dietOrderDeliveryState.setPushTime(calendar.getTime());
        DatabaseHelper.insert(dietOrderDeliveryState);

        ApiRest apiRest = new ApiRest();
        apiRest.setMessage("处理成功！");
        apiRest.setSuccessful(true);
        return apiRest;
    }

    /**
     * 获取订单配送记录
     *
     * @param obtainDeliveryStatesModel
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest obtainDeliveryStates(ObtainDeliveryStatesModel obtainDeliveryStatesModel) {
        BigInteger tenantId = obtainDeliveryStatesModel.getTenantId();
        BigInteger branchId = obtainDeliveryStatesModel.getBranchId();
        BigInteger dietOrderId = obtainDeliveryStatesModel.getDietOrderId();

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        searchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        searchModel.addSearchCondition("diet_order_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, dietOrderId);

        List<DietOrderDeliveryState> dietOrderDeliveryStates = DatabaseHelper.findAll(DietOrderDeliveryState.class, searchModel);
        return new ApiRest(dietOrderDeliveryStates, "获取订单配送记录成功！");
    }

    /**
     * 订单骑手位置查询
     *
     * @param orderCarrierModel
     * @return
     * @throws IOException
     */
    @Transactional(readOnly = true)
    public ApiRest orderCarrier(OrderCarrierModel orderCarrierModel) {
        BigInteger tenantId = orderCarrierModel.obtainTenantId();
        BigInteger branchId = orderCarrierModel.obtainBranchId();
        BigInteger dietOrderId = orderCarrierModel.getOrderId();

        DietOrder dietOrder = obtainDietOrder(tenantId, branchId, dietOrderId);

        build.dream.common.models.anubis.OrderCarrierModel model = build.dream.common.models.anubis.OrderCarrierModel.builder()
                .partnerOrderCode(dietOrder.getOrderNumber())
                .build();
        Map<String, Object> result = build.dream.common.utils.AnubisUtils.orderCarrier(model);
        return ApiRest.builder().data(result).message("获取订单骑手位置成功！").successful(true).build();
    }
}
