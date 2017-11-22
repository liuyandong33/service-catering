package build.dream.erp.services;

import build.dream.common.api.ApiRest;
import build.dream.common.erp.domains.*;
import build.dream.common.utils.*;
import build.dream.erp.constants.Constants;
import build.dream.erp.mappers.*;
import build.dream.erp.models.meituan.PullMeiTuanOrderModel;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

@Service
public class MeiTuanService {
    @Autowired
    private BranchMapper branchMapper;
    @Autowired
    private MeiTuanOrderMapper meiTuanOrderMapper;
    @Autowired
    private MeiTuanOrderItemMapper meiTuanOrderItemMapper;
    @Autowired
    private MeiTuanOrderExtraMapper meiTuanOrderExtraMapper;
    @Autowired
    private MeiTuanOrderPoiReceiveDetailMapper meiTuanOrderPoiReceiveDetailMapper;
    @Autowired
    private ActOrderChargeByMtMapper actOrderChargeByMtMapper;
    @Autowired
    private ActOrderChargeByPoiMapper actOrderChargeByPoiMapper;
    @Autowired
    private MeiTuanOrderCancelMessageMapper meiTuanOrderCancelMessageMapper;

    @Transactional(readOnly = true)
    public ApiRest generateBindingStoreLink(BigInteger tenantId, BigInteger branchId, String businessId) throws IOException {
        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        Branch branch = branchMapper.find(searchModel);
        Validate.notNull(branch, "门店不存在！");
        String meiTuanErpServiceUrl = ConfigurationUtils.getConfiguration(Constants.MEI_TUAN_ERP_SERVICE_URL);
        String meiTuanDeveloperId = ConfigurationUtils.getConfiguration(Constants.MEI_TUAN_DEVELOPER_ID);
        String meiTuanSignKey = ConfigurationUtils.getConfiguration(Constants.MEI_TUAN_SIGN_KEY);
        StringBuffer bindingStoreLink = new StringBuffer(meiTuanErpServiceUrl);
        bindingStoreLink.append(Constants.MEI_TUAN_PATH_STORE_MAP);
        bindingStoreLink.append("?developerId=").append(meiTuanDeveloperId);
        bindingStoreLink.append("&businessId=").append(businessId);
        bindingStoreLink.append("&ePoiId=").append(tenantId).append("Z").append(branchId);
        bindingStoreLink.append("&signKey=").append(meiTuanSignKey);
        bindingStoreLink.append("&ePoiName=").append(branch.getName());
        ApiRest apiRest = new ApiRest();
        apiRest.setData(bindingStoreLink.toString());
        apiRest.setMessage("生成门店绑定链接成功！");
        apiRest.setSuccessful(true);
        return apiRest;
    }

    @Transactional(rollbackFor = Exception.class)
    public ApiRest handleOrderEffectiveCallback(Map<String, String> parameters) throws IOException {
        String developerId = parameters.get("developerId");
        String ePoiId = parameters.get("ePoiId");
        String sign = parameters.get("sign");
        String orderJson = parameters.get("order");
        JSONObject orderJsonObject = JSONObject.fromObject(orderJson);
        JSONArray detailJsonArray = orderJsonObject.optJSONArray("detail");
        JSONArray extrasJsonArray = orderJsonObject.optJSONArray("extras");
        JSONObject poiReceiveDetailJsonObject = orderJsonObject.optJSONObject("poiReceiveDetail");
        long ctime = orderJsonObject.optLong("ctime");
        long utime = orderJsonObject.optLong("utime");
        long deliveryTime = orderJsonObject.optLong("deliveryTime");
        int hasInvoiced = orderJsonObject.optInt("hasInvoiced");
        int isThirdShipping = orderJsonObject.optInt("isThirdShipping");
        orderJsonObject.remove("detail");
        orderJsonObject.remove("ePoiId");
        orderJsonObject.remove("extras");
        orderJsonObject.remove("poiReceiveDetail");
        orderJsonObject.remove("ctime");
        orderJsonObject.remove("utime");
        orderJsonObject.remove("deliveryTime");
        orderJsonObject.remove("hasInvoiced");
        orderJsonObject.remove("isThirdShipping");

        String[] tenantIdAndBranchIdArray = ePoiId.split("Z");
        BigInteger tenantId = NumberUtils.createBigInteger(tenantIdAndBranchIdArray[0]);
        BigInteger branchId = NumberUtils.createBigInteger(tenantIdAndBranchIdArray[1]);
        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        Branch branch = branchMapper.find(searchModel);
        Validate.notNull(branch, "门店不存在！");

        MeiTuanOrder meiTuanOrder = GsonUtils.fromJson(orderJsonObject.toString(), MeiTuanOrder.class);
        Calendar ctimeCalendar = Calendar.getInstance();
        ctimeCalendar.setTimeInMillis(ctime);
        meiTuanOrder.setCtime(ctimeCalendar.getTime());

        Calendar utimeCalendar = Calendar.getInstance();
        utimeCalendar.setTimeInMillis(utime);
        meiTuanOrder.setUtime(utimeCalendar.getTime());

        Calendar deliveryTimeCalendar = Calendar.getInstance();
        deliveryTimeCalendar.setTimeInMillis(deliveryTime);
        meiTuanOrder.setDeliveryTime(deliveryTimeCalendar.getTime());
        meiTuanOrder.setHasInvoiced(hasInvoiced == 1);
        meiTuanOrder.setThirdShipping(isThirdShipping == 1);
        meiTuanOrder.setTenantId(tenantId);
        meiTuanOrder.setTenantCode(branch.getTenantCode());
        meiTuanOrder.setBranchId(branchId);
        meiTuanOrder.setBranchCode(branch.getCode());

        BigInteger userId = BigInteger.TEN;
        meiTuanOrder.setCreateUserId(userId);
        meiTuanOrder.setLastUpdateUserId(userId);
        meiTuanOrder.setLastUpdateRemark("处理美团订单生效回调，保存新订单！");
        meiTuanOrderMapper.insert(meiTuanOrder);

        BigDecimal hundred = NumberUtils.createBigDecimal("100");
        int detailJsonArraySize = detailJsonArray.size();
        for (int index = 0; index < detailJsonArraySize; index++) {
            JSONObject detailJsonObject = detailJsonArray.getJSONObject(index);
            MeiTuanOrderItem meiTuanOrderItem = new MeiTuanOrderItem();
            meiTuanOrderItem.setMeiTuanOrderId(meiTuanOrder.getId());
            meiTuanOrderItem.setAppFoodCode(detailJsonObject.optString("app_food_code"));
            meiTuanOrderItem.setBoxNum(detailJsonObject.optInt("box_num"));
            meiTuanOrderItem.setBoxPrice(BigDecimal.valueOf(detailJsonObject.optDouble("box_price")));
            meiTuanOrderItem.setFoodName(detailJsonObject.optString("food_name"));
            meiTuanOrderItem.setPrice(BigDecimal.valueOf(detailJsonObject.optDouble("price")));
            meiTuanOrderItem.setSkuId(detailJsonObject.optString("sku_id"));
            meiTuanOrderItem.setQuantity(detailJsonObject.optInt("quantity"));
            meiTuanOrderItem.setUnit(detailJsonObject.optString("unit"));
            meiTuanOrderItem.setFoodDiscount(BigDecimal.valueOf(detailJsonObject.optDouble("food_discount")));
            meiTuanOrderItem.setFoodProperty(detailJsonObject.optString("food_property"));
            if (detailJsonObject.has("foodShareFeeChargeByPoi")) {
                meiTuanOrderItem.setFoodShareFeeChargeByPoi(BigDecimal.valueOf(detailJsonObject.optDouble("foodShareFeeChargeByPoi")).divide(hundred));
            }
            meiTuanOrderItem.setCartId(detailJsonObject.optInt("cart_id"));
            meiTuanOrderItem.setCreateUserId(userId);
            meiTuanOrderItem.setLastUpdateUserId(userId);
            meiTuanOrderItem.setLastUpdateRemark("处理美团订单生效回调，保存订单明细！");
            meiTuanOrderItemMapper.insert(meiTuanOrderItem);
        }

        int extrasJsonArraySize = extrasJsonArray.size();
        for (int index = 0; index < extrasJsonArraySize; index++) {
            JSONObject extraJsonObject = extrasJsonArray.getJSONObject(index);
            MeiTuanOrderExtra meiTuanOrderExtra = new MeiTuanOrderExtra();
            meiTuanOrderExtra.setMeiTuanOrderId(meiTuanOrder.getId());
            if (extraJsonObject.has("mt_charge")) {
                meiTuanOrderExtra.setMtCharge(BigDecimal.valueOf(extraJsonObject.optDouble("mt_charge")));
            }

            if (extraJsonObject.has("poi_charge")) {
                meiTuanOrderExtra.setPoiCharge(BigDecimal.valueOf(extraJsonObject.optDouble("poi_charge")));
            }

            if (extraJsonObject.has("reduce_fee")) {
                meiTuanOrderExtra.setReduceFee(BigDecimal.valueOf(extraJsonObject.optDouble("reduce_fee")));
            }
            meiTuanOrderExtra.setRemark(extraJsonObject.optString("remark"));
            meiTuanOrderExtra.setType(extraJsonObject.optInt("type"));
            meiTuanOrderExtra.setCreateUserId(userId);
            meiTuanOrderExtra.setLastUpdateUserId(userId);
            meiTuanOrderExtra.setLastUpdateRemark("处理美团订单生效回调，保存订单扩展信息！");
            meiTuanOrderExtraMapper.insert(meiTuanOrderExtra);
        }

        MeiTuanOrderPoiReceiveDetail meiTuanOrderPoiReceiveDetail = new MeiTuanOrderPoiReceiveDetail();
        meiTuanOrderPoiReceiveDetail.setMeiTuanOrderId(meiTuanOrder.getId());
        meiTuanOrderPoiReceiveDetail.setFoodShareFeeChargeByPoi(BigDecimal.valueOf(poiReceiveDetailJsonObject.optDouble("foodShareFeeChargeByPoi")).divide(hundred));
        meiTuanOrderPoiReceiveDetail.setLogisticsFee(BigDecimal.valueOf(poiReceiveDetailJsonObject.optDouble("logisticsFee")).divide(hundred));
        meiTuanOrderPoiReceiveDetail.setOnlinePayment(BigDecimal.valueOf(poiReceiveDetailJsonObject.optDouble("onlinePayment")).divide(hundred));
        meiTuanOrderPoiReceiveDetail.setWmPoiReceiveCent(BigDecimal.valueOf(poiReceiveDetailJsonObject.optDouble("wmPoiReceiveCent")).divide(hundred));
        meiTuanOrderPoiReceiveDetail.setCreateUserId(userId);
        meiTuanOrderPoiReceiveDetail.setLastUpdateUserId(userId);
        meiTuanOrderPoiReceiveDetail.setLastUpdateRemark("处理美团订单生效回调，保存商家对账信息！");
        meiTuanOrderPoiReceiveDetailMapper.insert(meiTuanOrderPoiReceiveDetail);

        JSONArray actOrderChargeByMtJsonArray = poiReceiveDetailJsonObject.optJSONArray("actOrderChargeByMt");
        if (actOrderChargeByMtJsonArray != null) {
            int size = actOrderChargeByMtJsonArray.size();
            for (int index = 0; index < size; index++) {
                JSONObject actOrderChargeByMtJsonObject = actOrderChargeByMtJsonArray.getJSONObject(index);
                ActOrderChargeByMt actOrderChargeByMt = new ActOrderChargeByMt();
                actOrderChargeByMt.setMeiTuanOrderPoiReceiveDetailId(meiTuanOrderPoiReceiveDetail.getId());
                actOrderChargeByMt.setComment(actOrderChargeByMtJsonObject.optString("comment"));
                actOrderChargeByMt.setFeeTypeDesc(actOrderChargeByMtJsonObject.optString("feeTypeDesc"));
                actOrderChargeByMt.setFeeTypeId(BigInteger.valueOf(actOrderChargeByMtJsonObject.optLong("feeTypeId")));
                actOrderChargeByMt.setMoneyCent(BigDecimal.valueOf(actOrderChargeByMtJsonObject.optDouble("moneyCent")).divide(hundred));
                actOrderChargeByMt.setCreateUserId(userId);
                actOrderChargeByMt.setLastUpdateUserId(userId);
                actOrderChargeByMt.setLastUpdateRemark("处理美团订单生效回调，保存美团承担明细！");
                actOrderChargeByMtMapper.insert(actOrderChargeByMt);
            }
        }

        JSONArray actOrderChargeByPoiJsonArray = poiReceiveDetailJsonObject.optJSONArray("actOrderChargeByPoi");
        if (actOrderChargeByPoiJsonArray != null) {
            int size = actOrderChargeByPoiJsonArray.size();
            for (int index = 0; index < size; index++) {
                JSONObject actOrderChargeByPoiJsonObject = actOrderChargeByPoiJsonArray.getJSONObject(index);
                ActOrderChargeByPoi actOrderChargeByPoi = new ActOrderChargeByPoi();
                actOrderChargeByPoi.setMeiTuanOrderPoiReceiveDetailId(meiTuanOrderPoiReceiveDetail.getId());
                actOrderChargeByPoi.setComment(actOrderChargeByPoiJsonObject.optString("comment"));
                actOrderChargeByPoi.setFeeTypeDesc(actOrderChargeByPoiJsonObject.optString("feeTypeDesc"));
                actOrderChargeByPoi.setFeeTypeId(BigInteger.valueOf(actOrderChargeByPoiJsonObject.optLong("feeTypeId")));
                actOrderChargeByPoi.setMoneyCent(BigDecimal.valueOf(actOrderChargeByPoiJsonObject.optDouble("moneyCent")).divide(hundred));
                actOrderChargeByPoi.setCreateUserId(userId);
                actOrderChargeByPoi.setLastUpdateUserId(userId);
                actOrderChargeByPoi.setLastUpdateRemark("处理美团订单生效回调，保存美团承担明细！");
                actOrderChargeByPoiMapper.insert(actOrderChargeByPoi);
            }
        }
        publishMeiTuanOrderMessage(meiTuanOrder.getTenantCode(), meiTuanOrder.getBranchCode(), meiTuanOrder.getId(), 1);

        ApiRest apiRest = new ApiRest();
        apiRest.setMessage("美团订单生效回调处理成功！");
        apiRest.setSuccessful(true);
        return apiRest;
    }

    @Transactional(rollbackFor = Exception.class)
    public ApiRest handleOrderCancelCallback(Map<String, String> parameters) throws IOException {
        String developerId = parameters.get("developerId");
        String ePoiId = parameters.get("ePoiId");
        String sign = parameters.get("sign");
        String orderCancelJson = parameters.get("orderCancel");
        JSONObject orderCancelJsonObject = JSONObject.fromObject(orderCancelJson);
        BigInteger orderId = BigInteger.valueOf(orderCancelJsonObject.getLong("orderId"));

        MeiTuanOrder meiTuanOrder = findMeiTuanOrder(ePoiId, orderId);
        MeiTuanOrderCancelMessage meiTuanOrderCancelMessage = new MeiTuanOrderCancelMessage();
        meiTuanOrderCancelMessage.setMeiOrderId(meiTuanOrder.getId());
        meiTuanOrderCancelMessage.setDeveloperId(NumberUtils.createBigInteger(developerId));
        meiTuanOrderCancelMessage.setePoiId(ePoiId);
        meiTuanOrderCancelMessage.setSign(sign);
        meiTuanOrderCancelMessage.setOrderId(orderId);
        meiTuanOrderCancelMessage.setReasonCode(orderCancelJsonObject.optString("reasonCode"));
        meiTuanOrderCancelMessage.setReason(orderCancelJsonObject.optString("reason"));

        BigInteger userId = CommonUtils.getServiceSystemUserId();
        // TODO 删除
        userId = BigInteger.ZERO;
        meiTuanOrderCancelMessage.setCreateUserId(userId);
        meiTuanOrderCancelMessage.setLastUpdateUserId(userId);
        meiTuanOrderCancelMessage.setLastUpdateRemark("处理美团订单取消回调，保存美团订单取消消息！");
        meiTuanOrderCancelMessageMapper.insert(meiTuanOrderCancelMessage);

        meiTuanOrder.setStatus(9);
        meiTuanOrderMapper.update(meiTuanOrder);
        publishMeiTuanOrderMessage(meiTuanOrder.getTenantCode(), meiTuanOrder.getBranchCode(), meiTuanOrder.getId(), 2);

        ApiRest apiRest = new ApiRest();
        apiRest.setMessage("美团订单取消回调处理成功！");
        apiRest.setSuccessful(true);
        return apiRest;
    }

    @Transactional(rollbackFor = Exception.class)
    public ApiRest handleOrderRefundCallback(Map<String, String> parameters) throws IOException {
        String developerId = parameters.get("developerId");
        String ePoiId = parameters.get("ePoiId");
        String sign = parameters.get("sign");
        String orderRefundJson = parameters.get("orderCancel");
        JSONObject orderRefundJsonObject = JSONObject.fromObject(orderRefundJson);
        BigInteger orderId = BigInteger.valueOf(orderRefundJsonObject.getLong("orderId"));

        MeiTuanOrder meiTuanOrder = findMeiTuanOrder(ePoiId, orderId);

        String notifyType = orderRefundJsonObject.getString("notifyType");
        int status = 0;
        if ("agree".equals(notifyType)) {

        } else if ("".equals(notifyType)) {

        }
        meiTuanOrder.setStatus(status);
        meiTuanOrderMapper.update(meiTuanOrder);
        publishMeiTuanOrderMessage(meiTuanOrder.getTenantCode(), meiTuanOrder.getBranchCode(), meiTuanOrder.getId(), 2);

        ApiRest apiRest = new ApiRest();
        apiRest.setMessage("美团订单退款回调处理成功！");
        apiRest.setSuccessful(true);
        return apiRest;
    }

    /**
     * 查询美团订单
     * @param ePoiId
     * @param orderId
     * @return
     */
    private MeiTuanOrder findMeiTuanOrder(String ePoiId, BigInteger orderId) {
        String[] tenantIdAndBranchIdArray = ePoiId.split("Z");
        BigInteger tenantId = NumberUtils.createBigInteger(tenantIdAndBranchIdArray[0]);
        BigInteger branchId = NumberUtils.createBigInteger(tenantIdAndBranchIdArray[1]);
        SearchModel branchSearchModel = new SearchModel(true);
        branchSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        branchSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        Branch branch = branchMapper.find(branchSearchModel);
        Validate.notNull(branch, "门店不存在！");

        SearchModel meiTuanOrderSearchModel = new SearchModel(true);
        meiTuanOrderSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        meiTuanOrderSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        meiTuanOrderSearchModel.addSearchCondition("order_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, orderId);
        MeiTuanOrder meiTuanOrder = meiTuanOrderMapper.find(meiTuanOrderSearchModel);
        Validate.notNull(meiTuanOrder, "订单不存在！");
        return meiTuanOrder;
    }

    /**
     * 发布饿了么订单消息
     * @param tenantCode：商户编码
     * @param branchCode：门店编码
     * @param meiTuanOrderId：订单ID
     * @param type：消息类型
     * @return
     */
    private void publishMeiTuanOrderMessage(String tenantCode, String branchCode, BigInteger meiTuanOrderId, Integer type) throws IOException {
        String meiTuanMessageChannelTopic = ConfigurationUtils.getConfiguration(Constants.MEI_TUAN_MESSAGE_CHANNEL_TOPIC);
        JSONObject messageJsonObject = new JSONObject();
        messageJsonObject.put("tenantCodeAndBranchCode", tenantCode + "_" + branchCode);
        messageJsonObject.put("type", type);
        messageJsonObject.put("elemeOrderId", meiTuanOrderId);
        QueueUtils.convertAndSend(meiTuanMessageChannelTopic, messageJsonObject.toString());
    }

    @Transactional(readOnly = true)
    public ApiRest pullMeiTuanOrder(PullMeiTuanOrderModel pullMeiTuanOrderModel) {
        SearchModel meiTuanOrderSearchModel = new SearchModel(true);
        meiTuanOrderSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, pullMeiTuanOrderModel.getTenantId());
        meiTuanOrderSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, pullMeiTuanOrderModel.getBranchId());
        meiTuanOrderSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUALS, pullMeiTuanOrderModel.getMeiTuanOrderId());
        MeiTuanOrder meiTuanOrder = meiTuanOrderMapper.find(meiTuanOrderSearchModel);
        Validate.notNull(meiTuanOrder, "订单不存在！");

        SearchModel meiTuanItemSearchModel = new SearchModel(true);
        meiTuanItemSearchModel.addSearchCondition("mei_tuan_order_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, meiTuanOrder.getId());
        List<MeiTuanOrderItem> meiTuanOrderItems = meiTuanOrderItemMapper.findAll(meiTuanItemSearchModel);

        SearchModel meiTuanOrderExtraSearchModel = new SearchModel(true);
        meiTuanOrderExtraSearchModel.addSearchCondition("mei_tuan_order_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, meiTuanOrder.getId());
        List<MeiTuanOrderExtra> meiTuanOrderExtras = meiTuanOrderExtraMapper.findAll(meiTuanOrderExtraSearchModel);

        SearchModel meiTuanOrderPoiReceiveDetailSearchModel = new SearchModel(true);
        meiTuanOrderPoiReceiveDetailSearchModel.addSearchCondition("mei_tuan_order_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, meiTuanOrder.getId());
        MeiTuanOrderPoiReceiveDetail meiTuanOrderPoiReceiveDetail = meiTuanOrderPoiReceiveDetailMapper.find(meiTuanOrderPoiReceiveDetailSearchModel);

        Map<String, Object> poiReceiveDetail = new HashMap<String, Object>();
        if (meiTuanOrderPoiReceiveDetail != null) {
            SearchModel actOrderChargeByMtSearchModel = new SearchModel(true);
            actOrderChargeByMtSearchModel.addSearchCondition("mei_tuan_order_poi_receive_detail_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, meiTuanOrderPoiReceiveDetail.getId());
            List<ActOrderChargeByMt> actOrderChargeByMts = actOrderChargeByMtMapper.findAll(actOrderChargeByMtSearchModel);

            SearchModel actOrderChargeByPoiSearchModel = new SearchModel(true);
            actOrderChargeByPoiSearchModel.addSearchCondition("mei_tuan_order_poi_receive_detail_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, meiTuanOrderPoiReceiveDetail.getId());
            List<ActOrderChargeByPoi> actOrderChargeByPois = actOrderChargeByPoiMapper.findAll(actOrderChargeByPoiSearchModel);

            poiReceiveDetail.put("foodShareFeeChargeByPoi", meiTuanOrderPoiReceiveDetail.getFoodShareFeeChargeByPoi());
            poiReceiveDetail.put("logisticsFee", meiTuanOrderPoiReceiveDetail.getLogisticsFee());
            poiReceiveDetail.put("onlinePayment", meiTuanOrderPoiReceiveDetail.getOnlinePayment());
            poiReceiveDetail.put("wmPoiReceiveCent", meiTuanOrderPoiReceiveDetail.getWmPoiReceiveCent());

            List<Map<String, Object>> actOrderChargeByMtData = new ArrayList<Map<String, Object>>();
            if (CollectionUtils.isNotEmpty(actOrderChargeByMts)) {
                for (ActOrderChargeByMt actOrderChargeByMt : actOrderChargeByMts) {
                    Map<String, Object> actOrderChargeByMtMap = new HashMap<String, Object>();
                    actOrderChargeByMtMap.put("comment", actOrderChargeByMt.getComment());
                    actOrderChargeByMtMap.put("feeTypeDesc", actOrderChargeByMt.getFeeTypeDesc());
                    actOrderChargeByMtMap.put("feeTypeId", actOrderChargeByMt.getFeeTypeId());
                    actOrderChargeByMtMap.put("moneyCent", actOrderChargeByMt.getMoneyCent());
                    actOrderChargeByMtData.add(actOrderChargeByMtMap);
                }
                poiReceiveDetail.put("actOrderChargeByMt", actOrderChargeByMtData);
            }

            List<Map<String, Object>> actOrderChargeByPoiData = new ArrayList<Map<String, Object>>();
            if (CollectionUtils.isNotEmpty(actOrderChargeByPois)) {
                for (ActOrderChargeByPoi actOrderChargeByPoi : actOrderChargeByPois) {
                    Map<String, Object> actOrderChargeByPoiMap = new HashMap<String, Object>();
                    actOrderChargeByPoiMap.put("comment", actOrderChargeByPoi.getComment());
                    actOrderChargeByPoiMap.put("feeTypeDesc", actOrderChargeByPoi.getFeeTypeDesc());
                    actOrderChargeByPoiMap.put("feeTypeId", actOrderChargeByPoi.getFeeTypeId());
                    actOrderChargeByPoiMap.put("moneyCent", actOrderChargeByPoi.getMoneyCent());
                    actOrderChargeByPoiData.add(actOrderChargeByPoiMap);
                }
                poiReceiveDetail.put("actOrderChargeByPoi", actOrderChargeByPoiData);
            }
        }

        Map<String, Object> meiTuanOrderMap = BeanUtils.beanToMap(meiTuanOrder);
        meiTuanOrderMap.put("poiReceiveDetail", poiReceiveDetail);

        List<Map<String, Object>> detail = new ArrayList<Map<String, Object>>();
        for (MeiTuanOrderItem meiTuanOrderItem : meiTuanOrderItems) {
            Map<String, Object> meiTuanItemMap = new HashMap<String, Object>();
            meiTuanItemMap.put("app_food_code", meiTuanOrderItem.getAppFoodCode());
            meiTuanItemMap.put("box_num", meiTuanOrderItem.getBoxNum());
            meiTuanItemMap.put("box_price", meiTuanOrderItem.getBoxPrice());
            meiTuanItemMap.put("food_name", meiTuanOrderItem.getFoodName());
            meiTuanItemMap.put("price", meiTuanOrderItem.getPrice());
            meiTuanItemMap.put("sku_id", meiTuanOrderItem.getSkuId());
            meiTuanItemMap.put("quantity", meiTuanOrderItem.getQuantity());
            meiTuanItemMap.put("unit", meiTuanOrderItem.getUnit());
            meiTuanItemMap.put("food_discount", meiTuanOrderItem.getFoodDiscount());
            meiTuanItemMap.put("food_property", meiTuanOrderItem.getFoodProperty());
            meiTuanItemMap.put("foodShareFeeChargeByPoi", meiTuanOrderItem.getFoodShareFeeChargeByPoi());
            meiTuanItemMap.put("cart_id", meiTuanOrderItem.getCartId());
            detail.add(meiTuanItemMap);
        }
        meiTuanOrderMap.put("detail", detail);

        List<Map<String, Object>> extras = new ArrayList<Map<String, Object>>();
        for (MeiTuanOrderExtra meiTuanOrderExtra : meiTuanOrderExtras) {
            Map<String, Object> meiTuanOrderExtraMap = new HashMap<String, Object>();
            meiTuanOrderExtraMap.put("mt_charge", meiTuanOrderExtra.getMtCharge());
            meiTuanOrderExtraMap.put("poi_charge", meiTuanOrderExtra.getPoiCharge());
            meiTuanOrderExtraMap.put("reduce_fee", meiTuanOrderExtra.getReduceFee());
            meiTuanOrderExtraMap.put("remark", meiTuanOrderExtra.getRemark());
            meiTuanOrderExtraMap.put("type", meiTuanOrderExtra.getType());
            extras.add(meiTuanOrderExtraMap);
        }
        meiTuanOrderMap.put("extras", extras);

        ApiRest apiRest = new ApiRest();
        apiRest.setData(meiTuanOrderMap);
        apiRest.setMessage("拉取美团订单成功！");
        apiRest.setSuccessful(true);
        return apiRest;
    }
}