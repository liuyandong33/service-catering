package build.dream.catering.services;

import build.dream.catering.constants.Constants;
import build.dream.catering.mappers.BranchMapper;
import build.dream.catering.mappers.DietOrderDetailMapper;
import build.dream.catering.mappers.DietOrderGroupMapper;
import build.dream.catering.mappers.DietOrderMapper;
import build.dream.catering.models.anubis.*;
import build.dream.catering.utils.AnubisUtils;
import build.dream.common.api.ApiRest;
import build.dream.common.erp.catering.domains.Branch;
import build.dream.common.erp.catering.domains.DietOrder;
import build.dream.common.erp.catering.domains.DietOrderDetail;
import build.dream.common.erp.catering.domains.DietOrderGroup;
import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.ConfigurationUtils;
import build.dream.common.utils.SearchModel;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnubisService {
    @Autowired
    private BranchMapper branchMapper;
    @Autowired
    private DietOrderMapper dietOrderMapper;
    @Autowired
    private DietOrderGroupMapper dietOrderGroupMapper;
    @Autowired
    private DietOrderDetailMapper dietOrderDetailMapper;

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
        searchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        Branch branch = branchMapper.find(searchModel);
        Validate.notNull(branch, "门店不存在！");

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("chain_store_code", branch.getTenantCode() + "Z" + branch.getCode());
        data.put("chain_store_name", branch.getTenantCode() + "Z" + branch.getCode() + "Z" + branch.getName());
        data.put("contact_phone", "13789871965");
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
        branchSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        branchSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        Branch branch = branchMapper.find(branchSearchModel);
        Validate.notNull(branch, "门店不存在！");

        // 查询订单信息
        SearchModel dietOrderSearchModel = new SearchModel(true);
        dietOrderSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        dietOrderSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        dietOrderSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUALS, dietOrderId);
        DietOrder dietOrder = dietOrderMapper.find(dietOrderSearchModel);
        Validate.notNull(dietOrder, "订单不存在！");

        SearchModel dietOrderGroupSearchModel = new SearchModel(true);
        dietOrderGroupSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        dietOrderGroupSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        dietOrderGroupSearchModel.addSearchCondition("diet_order_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, dietOrderId);
        List<DietOrderGroup> dietOrderGroups = dietOrderGroupMapper.findAll(dietOrderGroupSearchModel);

        // 查询出订单详情信息
        SearchModel dietOrderDetailSearchModel = new SearchModel(true);
        dietOrderDetailSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        dietOrderDetailSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        dietOrderDetailSearchModel.addSearchCondition("diet_order_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, dietOrderId);
        List<DietOrderDetail> dietOrderDetails = dietOrderDetailMapper.findAll(dietOrderDetailSearchModel);

        Map<String, Object> data = new HashMap<String, Object>();
        ApplicationHandler.ifNotNullPut(data, "partner_remark", orderModel.getPartnerRemark());
        data.put("partner_order_code", dietOrder.getOrderNumber());
        data.put("notify_url", "");
        data.put("order_type", 1);
        data.put("chain_store_code", branch.getTenantCode() + "Z" + branch.getCode());

        Map<String, Object> transportInfo = new HashMap<String, Object>();
        transportInfo.put("transport_name", branch.getTenantCode() + "Z" + branch.getCode() + "Z" + branch.getName());
        transportInfo.put("transport_address", branch.getProvinceName() + branch.getCityName() + branch.getDistrictName() + branch.getAddress());
        transportInfo.put("transport_longitude", branch.getLongitude());
        transportInfo.put("transport_latitude", branch.getLatitude());
        transportInfo.put("position_source", Constants.POSITION_SOURCE_BAIDU_MAP);
        transportInfo.put("transport_tel", "13789871965");
        ApplicationHandler.ifNotNullPut(transportInfo, "transport_remark", orderModel.getTransportRemark());
        data.put("transport_info", transportInfo);

        data.put("order_add_time", dietOrder.getActiveTime().getTime());
        data.put("order_total_amount", dietOrder.getTotalAmount());
        data.put("order_actual_amount", dietOrder.getPayableAmount());
        data.put("order_weight", 1);
        data.put("order_remark", dietOrder.getRemark());
        data.put("is_invoiced", 0);
        data.put("invoice", "发票抬头");
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

        DietOrder dietOrder = findDietOrder(tenantId, branchId, dietOrderId);

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

    @Transactional(readOnly = true)
    public ApiRest orderQuery(OrderQueryModel orderQueryModel) throws IOException {
        BigInteger tenantId = orderQueryModel.getTenantId();
        BigInteger branchId = orderQueryModel.getBranchId();
        BigInteger dietOrderId = orderQueryModel.getDietOrderId();

        DietOrder dietOrder = findDietOrder(tenantId, branchId, dietOrderId);

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
     * @param dietOrderId
     * @return
     */
    private DietOrder findDietOrder(BigInteger tenantId, BigInteger branchId, BigInteger dietOrderId) {
        SearchModel dietOrderSearchModel = new SearchModel(true);
        dietOrderSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        dietOrderSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        dietOrderSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUALS, dietOrderId);
        DietOrder dietOrder = dietOrderMapper.find(dietOrderSearchModel);
        Validate.notNull(dietOrder, "订单不存在！");
        return dietOrder;
    }

    @Transactional(readOnly = true)
    public ApiRest orderComplaint(OrderComplaintModel orderComplaintModel) throws IOException {
        BigInteger tenantId = orderComplaintModel.getTenantId();
        BigInteger branchId = orderComplaintModel.getBranchId();
        BigInteger dietOrderId = orderComplaintModel.getDietOrderId();

        DietOrder dietOrder = findDietOrder(tenantId, branchId, dietOrderId);

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("partner_order_code", dietOrder.getOrderNumber());
        data.put("order_complaint_code", orderComplaintModel.getOrderComplaintCode());
        ApplicationHandler.ifNotNullPut(data, "order_complaint_desc", orderComplaintModel.getOrderComplaintDesc());
        data.put("order_complaint_time", System.currentTimeMillis());

        String url = ConfigurationUtils.getConfiguration(Constants.ANUBIS_SERVICE_URL) + Constants.ANUBIS_ORDER_COMPLAINT_URI;
        String appId = ConfigurationUtils.getConfiguration(Constants.ANUBIS_APP_ID);
        return AnubisUtils.callAnubisSystem(url, appId, data);
    }
}
