package build.dream.erp.services;

import build.dream.common.api.ApiRest;
import build.dream.common.erp.domains.*;
import build.dream.common.utils.ConfigurationUtils;
import build.dream.common.utils.GsonUtils;
import build.dream.common.utils.SearchModel;
import build.dream.erp.constants.Constants;
import build.dream.erp.mappers.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Map;

@Service
public class MeiTuanService {
    @Autowired
    private BranchMapper branchMapper;
    @Autowired
    private MeiTuanOrderMapper meiTuanOrderMapper;
    @Autowired
    private MeiTuanItemMapper meiTuanItemMapper;
    @Autowired
    private MeiTuanOrderExtraMapper meiTuanOrderExtraMapper;
    @Autowired
    private MeiTuanOrderPoiReceiveDetailMapper meiTuanOrderPoiReceiveDetailMapper;
    @Autowired
    private ActOrderChargeByMtMapper actOrderChargeByMtMapper;
    @Autowired
    private ActOrderChargeByPoiMapper actOrderChargeByPoiMapper;

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
    public ApiRest handleOrderEffectiveCallback(Map<String, String> parameters) {
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
            MeiTuanItem meiTuanItem = new MeiTuanItem();
            meiTuanItem.setMeiTuanOrderId(meiTuanOrder.getId());
            meiTuanItem.setAppFoodCode(detailJsonObject.optString("app_food_code"));
            meiTuanItem.setBoxNum(detailJsonObject.optInt("box_num"));
            meiTuanItem.setBoxPrice(BigDecimal.valueOf(detailJsonObject.optDouble("box_price")));
            meiTuanItem.setFoodName(detailJsonObject.optString("food_name"));
            meiTuanItem.setPrice(BigDecimal.valueOf(detailJsonObject.optDouble("price")));
            meiTuanItem.setSkuId(detailJsonObject.optString("sku_id"));
            meiTuanItem.setQuantity(detailJsonObject.optInt("quantity"));
            meiTuanItem.setUnit(detailJsonObject.optString("unit"));
            meiTuanItem.setFoodDiscount(BigDecimal.valueOf(detailJsonObject.optDouble("food_discount")));
            meiTuanItem.setFoodProperty(detailJsonObject.optString("food_property"));
            if (detailJsonObject.has("foodShareFeeChargeByPoi")) {
                meiTuanItem.setFoodShareFeeChargeByPoi(BigDecimal.valueOf(detailJsonObject.optDouble("foodShareFeeChargeByPoi")).divide(hundred));
            }
            meiTuanItem.setCartId(detailJsonObject.optInt("cart_id"));
            meiTuanItem.setCreateUserId(userId);
            meiTuanItem.setLastUpdateUserId(userId);
            meiTuanItem.setLastUpdateRemark("处理美团订单生效回调，保存订单明细！");
            meiTuanItemMapper.insert(meiTuanItem);
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

        ApiRest apiRest = new ApiRest();
        apiRest.setMessage("美团订单生效回调处理成功！");
        apiRest.setSuccessful(true);
        return apiRest;
    }
}
