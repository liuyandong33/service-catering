package build.dream.catering.utils;

import build.dream.catering.beans.PackageGroupDietOrderDetail;
import build.dream.catering.constants.Constants;
import build.dream.catering.tools.PushMessageThread;
import build.dream.common.catering.domains.*;
import build.dream.common.constants.DietOrderConstants;
import build.dream.common.models.alipay.AlipayTradeRefundModel;
import build.dream.common.models.jpush.PushModel;
import build.dream.common.models.weixinpay.RefundModel;
import build.dream.common.utils.*;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.dom4j.DocumentException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DietOrderUtils {
    public static Map<BigInteger, List<DietOrderDetail>> splitDietOrderDetails(List<DietOrderDetail> dietOrderDetails) {
        Map<BigInteger, List<DietOrderDetail>> dietOrderDetailMap = new HashMap<BigInteger, List<DietOrderDetail>>();
        for (DietOrderDetail dietOrderDetail : dietOrderDetails) {
            BigInteger dietOrderGroupId = dietOrderDetail.getDietOrderGroupId();
            List<DietOrderDetail> dietOrderDetailList = dietOrderDetailMap.get(dietOrderGroupId);
            if (CollectionUtils.isEmpty(dietOrderDetailList)) {
                dietOrderDetailList = new ArrayList<DietOrderDetail>();
                dietOrderDetailMap.put(dietOrderGroupId, dietOrderDetailList);
            }
            dietOrderDetailList.add(dietOrderDetail);
        }
        return dietOrderDetailMap;
    }

    public static void recoveryStock(DietOrder dietOrder) {
        BigInteger dietOrderId = dietOrder.getId();

        List<SearchCondition> searchConditions = new ArrayList<SearchCondition>();
        searchConditions.add(new SearchCondition("deleted", Constants.SQL_OPERATION_SYMBOL_EQUAL, 0));
        searchConditions.add(new SearchCondition("diet_order_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, dietOrderId));
        SearchModel dietOrderGroupSearchModel = new SearchModel();
        dietOrderGroupSearchModel.setSearchConditions(searchConditions);
        List<DietOrderGroup> dietOrderGroups = DatabaseHelper.findAll(DietOrderGroup.class, dietOrderGroupSearchModel);

        SearchModel dietOrderDetailSearchModel = new SearchModel();
        dietOrderDetailSearchModel.setSearchConditions(searchConditions);
        List<DietOrderDetail> dietOrderDetails = DatabaseHelper.findAll(DietOrderDetail.class, dietOrderDetailSearchModel);

        Map<BigInteger, List<DietOrderDetail>> dietOrderDetailMap = DietOrderUtils.splitDietOrderDetails(dietOrderDetails);

        List<DietOrderDetail> normalDietOrderDetails = new ArrayList<DietOrderDetail>();
        for (DietOrderGroup dietOrderGroup : dietOrderGroups) {
            String type = dietOrderGroup.getType();
            BigInteger dietOrderGroupId = dietOrderGroup.getId();
            if (DietOrderConstants.GROUP_TYPE_NORMAL.equals(type)) {
                normalDietOrderDetails.addAll(dietOrderDetailMap.get(dietOrderGroupId));
            }
        }

        List<BigInteger> goodsIds = new ArrayList<BigInteger>();
        for (DietOrderDetail normalDietOrderDetail : normalDietOrderDetails) {
            goodsIds.add(normalDietOrderDetail.getGoodsId());
        }

        SearchModel goodsSearchModel = new SearchModel(true);
        goodsSearchModel.addSearchCondition(Goods.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_IN, goodsIds);
        List<Goods> goodsList = DatabaseHelper.findAll(Goods.class, goodsSearchModel);
        Map<BigInteger, Goods> goodsMap = new HashMap<BigInteger, Goods>();
        for (Goods goods : goodsList) {
            goodsMap.put(goods.getId(), goods);
        }

        for (DietOrderDetail normalDietOrderDetail : normalDietOrderDetails) {
            BigInteger goodsId = normalDietOrderDetail.getGoodsId();
            Goods goods = goodsMap.get(goodsId);
            if (goods.isStocked()) {
                GoodsUtils.addGoodsStock(goodsId, normalDietOrderDetail.getGoodsSpecificationId(), normalDietOrderDetail.getQuantity());
            }
        }
    }

    public static void refund(DietOrder dietOrder) throws DocumentException {
        BigInteger tenantId = dietOrder.getTenantId();
        BigInteger branchId = dietOrder.getBranchId();
        BigInteger dietOrderId = dietOrder.getId();
        BigInteger vipId = dietOrder.getVipId();
        Vip vip = null;
        if (vipId.compareTo(Constants.BIGINT_DEFAULT_VALUE) != 0) {
            vip = VipUtils.find(vipId);
        }

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition(DietOrderPayment.ColumnName.DIET_ORDER_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, dietOrderId);
        List<DietOrderPayment> dietOrderPayments = DatabaseHelper.findAll(DietOrderPayment.class, searchModel);

        for (DietOrderPayment dietOrderPayment : dietOrderPayments) {
            String paymentCode = dietOrderPayment.getPaymentCode();
            String extraInfo = dietOrderPayment.getExtraInfo();
            if (Constants.PAYMENT_CODE_HYJF.equals(paymentCode)) {
                VipUtils.addVipPoint(vip.getTenantId(), vip.getBranchId(), vipId, dietOrderPayment.getPaidAmount().multiply(BigDecimal.valueOf(Double.valueOf(extraInfo))));
            } else if (Constants.PAYMENT_CODE_HYQB.equals(paymentCode)) {
                VipUtils.addVipBalance(vip.getTenantId(), vip.getBranchId(), vipId, dietOrderPayment.getPaidAmount());
            } else if (Constants.PAYMENT_CODE_WX.equals(paymentCode)) {
                RefundModel refundModel = new RefundModel();
                refundModel.setOutTradeNo(dietOrder.getOrderNumber());
                refundModel.setOutRefundNo(dietOrder.getOrderNumber());
                refundModel.setTotalFee(dietOrder.getPayableAmount().multiply(Constants.BIG_DECIMAL_ONE_HUNDRED).intValue());
                refundModel.setRefundFee(dietOrder.getPayableAmount().multiply(Constants.BIG_DECIMAL_ONE_HUNDRED).intValue());
                JSONObject extraInfoJsonObject = JSONObject.fromObject(extraInfo);
                JSONObject attachJsonObject = JSONObject.fromObject(extraInfoJsonObject.getString("attach"));

                refundModel.setTradeType(WeiXinPayUtils.obtainTradeType(attachJsonObject.getInt("paidScene")));
                WeiXinPayUtils.refund(tenantId.toString(), branchId.toString(), refundModel);
            } else if (Constants.PAYMENT_CODE_ALIPAY.equals(paymentCode)) {
                AlipayTradeRefundModel alipayTradeRefundModel = new AlipayTradeRefundModel();
                AlipayUtils.alipayTradeRefund(tenantId.toString(), branchId.toString(), null, alipayTradeRefundModel);
            }
        }
    }

    /**
     * 构建订单信息
     *
     * @param dietOrder
     * @param dietOrderGroups
     * @param dietOrderDetails
     * @param dietOrderDetailGoodsAttributes
     * @param dietOrderActivities
     * @return
     */
    public static Map<String, Object> buildDietOrderInfo(DietOrder dietOrder, List<DietOrderGroup> dietOrderGroups, List<DietOrderDetail> dietOrderDetails, List<DietOrderDetailGoodsAttribute> dietOrderDetailGoodsAttributes, List<DietOrderActivity> dietOrderActivities) {
        // 封装订单分组与订单详情之间的map
        Map<BigInteger, List<DietOrderDetail>> dietOrderDetailMap = new HashMap<BigInteger, List<DietOrderDetail>>();
        for (DietOrderDetail dietOrderDetail : dietOrderDetails) {
            List<DietOrderDetail> dietOrderDetailList = dietOrderDetailMap.get(dietOrderDetail.getDietOrderGroupId());
            if (dietOrderDetailList == null) {
                dietOrderDetailList = new ArrayList<DietOrderDetail>();
                dietOrderDetailMap.put(dietOrderDetail.getDietOrderGroupId(), dietOrderDetailList);
            }
            dietOrderDetailList.add(dietOrderDetail);
        }

        // 封装订单详情与订单口味之间的map
        Map<BigInteger, List<DietOrderDetailGoodsAttribute>> dietOrderDetailGoodsAttributeMap = new HashMap<BigInteger, List<DietOrderDetailGoodsAttribute>>();
        for (DietOrderDetailGoodsAttribute dietOrderDetailGoodsAttribute : dietOrderDetailGoodsAttributes) {
            List<DietOrderDetailGoodsAttribute> dietOrderDetailGoodsAttributeList = dietOrderDetailGoodsAttributeMap.get(dietOrderDetailGoodsAttribute.getDietOrderDetailId());
            if (dietOrderDetailGoodsAttributeList == null) {
                dietOrderDetailGoodsAttributeList = new ArrayList<DietOrderDetailGoodsAttribute>();
                dietOrderDetailGoodsAttributeMap.put(dietOrderDetailGoodsAttribute.getDietOrderDetailId(), dietOrderDetailGoodsAttributeList);
            }
            dietOrderDetailGoodsAttributeList.add(dietOrderDetailGoodsAttribute);
        }

        Map<String, Object> dietOrderInfo = new HashMap<String, Object>();
        dietOrderInfo.put("id", dietOrder.getId());
        dietOrderInfo.put("orderNumber", dietOrder.getOrderNumber());
        dietOrderInfo.put("tenantId", dietOrder.getTenantId());
        dietOrderInfo.put("tenantCode", dietOrder.getTenantCode());
        dietOrderInfo.put("branchId", dietOrder.getBranchId());
        dietOrderInfo.put("orderType", dietOrder.getOrderType());
        dietOrderInfo.put("orderStatus", dietOrder.getOrderStatus());
        dietOrderInfo.put("payStatus", dietOrder.getPayStatus());
        dietOrderInfo.put("refundStatus", dietOrder.getRefundStatus());
        dietOrderInfo.put("totalAmount", dietOrder.getTotalAmount());
        dietOrderInfo.put("discountAmount", dietOrder.getDiscountAmount());
        dietOrderInfo.put("payableAmount", dietOrder.getPayableAmount());
        dietOrderInfo.put("paidAmount", dietOrder.getPaidAmount());
        dietOrderInfo.put("paidType", dietOrder.getPaidType());
        dietOrderInfo.put("remark", dietOrder.getRemark());
        dietOrderInfo.put("deliveryAddress", dietOrder.getDeliveryAddress());
        dietOrderInfo.put("deliveryLongitude", dietOrder.getDeliveryLongitude());
        dietOrderInfo.put("deliveryLatitude", dietOrder.getDeliveryLatitude());
        dietOrderInfo.put("deliverTime", dietOrder.getDeliverTime());
        dietOrderInfo.put("activeTime", dietOrder.getActiveTime());
        dietOrderInfo.put("deliverFee", dietOrder.getDeliverFee());
        dietOrderInfo.put("telephoneNumber", dietOrder.getTelephoneNumber());
        dietOrderInfo.put("daySerialNumber", dietOrder.getDaySerialNumber());
        dietOrderInfo.put("consignee", dietOrder.getConsignee());
        dietOrderInfo.put("invoiced", dietOrder.isInvoiced());
        dietOrderInfo.put("invoiceType", dietOrder.getInvoiceType());
        dietOrderInfo.put("invoice", dietOrder.getInvoice());
        dietOrderInfo.put("vipId", dietOrder.getVipId());
        dietOrderInfo.put("createTime", dietOrder.getCreateTime());
        dietOrderInfo.put("lastUpdateTime", dietOrder.getLastUpdateTime());
        dietOrderInfo.put("lastUpdateRemark", dietOrder.getLastUpdateRemark());
        dietOrderInfo.put("groups", buildDietOrderGroupInfos(dietOrderGroups, dietOrderDetailMap, dietOrderDetailGoodsAttributeMap));
        List<Map<String, Object>> orderActivities = new ArrayList<Map<String, Object>>();
        for (DietOrderActivity dietOrderActivity : dietOrderActivities) {
            Map<String, Object> orderActivity = new HashMap<String, Object>();
            orderActivity.put("activityId", dietOrderActivity.getActivityId());
            orderActivity.put("activityName", dietOrderActivity.getActivityName());
            orderActivity.put("activityType", dietOrderActivity.getActivityType());
            orderActivity.put("amount", dietOrderActivity.getAmount());
            orderActivities.add(orderActivity);
        }
        dietOrderInfo.put("orderActivities", orderActivities);
        return dietOrderInfo;
    }

    /**
     * 构建订单组信息
     *
     * @param dietOrderGroups
     * @param dietOrderDetailMap
     * @param dietOrderDetailGoodsAttributeMap
     * @return
     */
    private static List<Map<String, Object>> buildDietOrderGroupInfos(List<DietOrderGroup> dietOrderGroups, Map<BigInteger, List<DietOrderDetail>> dietOrderDetailMap, Map<BigInteger, List<DietOrderDetailGoodsAttribute>> dietOrderDetailGoodsAttributeMap) {
        List<Map<String, Object>> groups = new ArrayList<Map<String, Object>>();
        for (DietOrderGroup dietOrderGroup : dietOrderGroups) {
            Map<String, Object> group = new HashMap<String, Object>();
            group.put("id", dietOrderGroup.getId());
            group.put("name", dietOrderGroup.getName());
            group.put("type", dietOrderGroup.getType());
            group.put("details", buildDietOrderDetailInfos(dietOrderDetailMap.get(dietOrderGroup.getId()), dietOrderDetailGoodsAttributeMap));
            groups.add(group);
        }
        return groups;
    }

    /**
     * 构建订单详情信息
     *
     * @param dietOrderDetails
     * @param dietOrderDetailGoodsAttributeMap
     * @return
     */
    private static List<Map<String, Object>> buildDietOrderDetailInfos(List<DietOrderDetail> dietOrderDetails, Map<BigInteger, List<DietOrderDetailGoodsAttribute>> dietOrderDetailGoodsAttributeMap) {
        List<DietOrderDetail> ordinaryGoodsDietOrderDetail = new ArrayList<DietOrderDetail>();
        Map<BigInteger, List<DietOrderDetail>> dietOrderDetailMap = new HashMap<BigInteger, List<DietOrderDetail>>();
        for (DietOrderDetail dietOrderDetail : dietOrderDetails) {
            if (dietOrderDetail.getGoodsType() == Constants.GOODS_TYPE_PACKAGE_DETAIL) {
                BigInteger packageGroupId = dietOrderDetail.getPackageGroupId();
                List<DietOrderDetail> dietOrderDetailList = dietOrderDetailMap.get(packageGroupId);
                if (CollectionUtils.isEmpty(dietOrderDetailList)) {
                    dietOrderDetailList = new ArrayList<DietOrderDetail>();
                    dietOrderDetailMap.put(packageGroupId, dietOrderDetailList);
                }
                dietOrderDetailList.add(dietOrderDetail);
            } else {
                ordinaryGoodsDietOrderDetail.add(dietOrderDetail);
            }
        }
        List<PackageGroupDietOrderDetail> packageGroupDietOrderDetails = new ArrayList<PackageGroupDietOrderDetail>();
        for (Map.Entry<BigInteger, List<DietOrderDetail>> entry : dietOrderDetailMap.entrySet()) {
            PackageGroupDietOrderDetail packageGroupDietOrderDetail = new PackageGroupDietOrderDetail();
            List<DietOrderDetail> value = entry.getValue();
            DietOrderDetail dietOrderDetail = value.get(0);
            packageGroupDietOrderDetail.setPackageId(dietOrderDetail.getPackageId());
            packageGroupDietOrderDetail.setPackageGroupId(dietOrderDetail.getPackageGroupId());
            packageGroupDietOrderDetail.setPackageGroupName(dietOrderDetail.getPackageGroupName());
            packageGroupDietOrderDetail.setDietOrderDetails(value);
            packageGroupDietOrderDetails.add(packageGroupDietOrderDetail);
        }

        Map<BigInteger, List<PackageGroupDietOrderDetail>> packageGroupDietOrderDetailMap = new HashMap<BigInteger, List<PackageGroupDietOrderDetail>>();
        for (PackageGroupDietOrderDetail packageGroupDietOrderDetail : packageGroupDietOrderDetails) {
            BigInteger packageId = packageGroupDietOrderDetail.getPackageId();
            List<PackageGroupDietOrderDetail> packageGroupDietOrderDetailList = packageGroupDietOrderDetailMap.get(packageId);
            if (CollectionUtils.isEmpty(packageGroupDietOrderDetailList)) {
                packageGroupDietOrderDetailList = new ArrayList<PackageGroupDietOrderDetail>();
                packageGroupDietOrderDetailMap.put(packageId, packageGroupDietOrderDetailList);
            }
            packageGroupDietOrderDetailList.add(packageGroupDietOrderDetail);
        }

        List<Map<String, Object>> dietOrderDetailInfos = new ArrayList<Map<String, Object>>();
        for (DietOrderDetail dietOrderDetail : ordinaryGoodsDietOrderDetail) {
            Map<String, Object> dietOrderDetailInfo = new HashMap<String, Object>();
            int goodsType = dietOrderDetail.getGoodsType();
            dietOrderDetailInfo.put("goodsType", goodsType);
            dietOrderDetailInfo.put("goodsId", dietOrderDetail.getGoodsId());
            dietOrderDetailInfo.put("goodsName", dietOrderDetail.getGoodsName());
            dietOrderDetailInfo.put("goodsSpecificationId", dietOrderDetail.getGoodsSpecificationId());
            dietOrderDetailInfo.put("goodsSpecificationName", dietOrderDetail.getGoodsSpecificationName());
            dietOrderDetailInfo.put("price", dietOrderDetail.getPrice());
            dietOrderDetailInfo.put("attributeIncrease", dietOrderDetail.getAttributeIncrease());
            dietOrderDetailInfo.put("quantity", dietOrderDetail.getQuantity());
            dietOrderDetailInfo.put("totalAmount", dietOrderDetail.getTotalAmount());
            dietOrderDetailInfo.put("discountAmount", dietOrderDetail.getDiscountAmount());
            dietOrderDetailInfo.put("payableAmount", dietOrderDetail.getPayableAmount());

            List<DietOrderDetailGoodsAttribute> dietOrderDetailGoodsAttributes = dietOrderDetailGoodsAttributeMap.get(dietOrderDetail.getId());
            if (CollectionUtils.isNotEmpty(dietOrderDetailGoodsAttributes)) {
                List<Map<String, Object>> attributes = new ArrayList<Map<String, Object>>();
                for (DietOrderDetailGoodsAttribute dietOrderDetailGoodsAttribute : dietOrderDetailGoodsAttributes) {
                    Map<String, Object> attribute = new HashMap<String, Object>();
                    attribute.put("attributeGroupId", dietOrderDetailGoodsAttribute.getGoodsAttributeGroupId());
                    attribute.put("attributeGroupName", dietOrderDetailGoodsAttribute.getGoodsAttributeGroupName());
                    attribute.put("attributeId", dietOrderDetailGoodsAttribute.getGoodsAttributeId());
                    attribute.put("attributeName", dietOrderDetailGoodsAttribute.getGoodsAttributeName());
                    attribute.put("price", dietOrderDetailGoodsAttribute.getPrice());
                    attributes.add(attribute);
                }
                dietOrderDetailInfo.put("attributes", attributes);
            }
            dietOrderDetailInfos.add(dietOrderDetailInfo);
            if (goodsType == Constants.GOODS_TYPE_PACKAGE) {
                dietOrderDetailInfo.put("packageGroups", buildPackageGroupInfos(packageGroupDietOrderDetailMap.get(dietOrderDetail.getGoodsId())));
            }
        }
        return dietOrderDetailInfos;
    }

    /**
     * 构建套餐分组信息
     *
     * @param packageGroupDietOrderDetails
     * @return
     */
    public static List<Map<String, Object>> buildPackageGroupInfos(List<PackageGroupDietOrderDetail> packageGroupDietOrderDetails) {
        List<Map<String, Object>> packageGroups = new ArrayList<Map<String, Object>>();
        for (PackageGroupDietOrderDetail packageGroupDietOrderDetail : packageGroupDietOrderDetails) {
            Map<String, Object> packageGroup = new HashMap<String, Object>();
            packageGroup.put("id", packageGroupDietOrderDetail.getPackageGroupId());
            packageGroup.put("name", packageGroupDietOrderDetail.getPackageGroupName());
            List<Map<String, Object>> details = new ArrayList<Map<String, Object>>();
            List<DietOrderDetail> dietOrderDetails = packageGroupDietOrderDetail.getDietOrderDetails();
            for (DietOrderDetail dietOrderDetail : dietOrderDetails) {
                Map<String, Object> detail = new HashMap<String, Object>();
                detail.put("goodsType", dietOrderDetail.getGoodsType());
                detail.put("goodsId", dietOrderDetail.getGoodsId());
                detail.put("goodsName", dietOrderDetail.getGoodsName());
                detail.put("goodsSpecificationId", dietOrderDetail.getGoodsSpecificationId());
                detail.put("goodsSpecificationName", dietOrderDetail.getGoodsSpecificationName());
                detail.put("price", dietOrderDetail.getPrice());
                detail.put("attributeIncrease", dietOrderDetail.getAttributeIncrease());
                detail.put("quantity", dietOrderDetail.getQuantity());
                detail.put("totalAmount", dietOrderDetail.getTotalAmount());
                detail.put("discountAmount", dietOrderDetail.getDiscountAmount());
                detail.put("payableAmount", dietOrderDetail.getPayableAmount());
                details.add(detail);
            }
            packageGroup.put("details", details);
            packageGroups.add(packageGroup);
        }
        return packageGroups;
    }

    public static void pushMessage(BigInteger tenantId, BigInteger branchId, BigInteger dietOrderId, String uuid, int count, int interval) {
        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        searchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        searchModel.addSearchCondition("online", Constants.SQL_OPERATION_SYMBOL_EQUAL, 1);
        List<Pos> poses = DatabaseHelper.findAll(Pos.class, searchModel);
        if (CollectionUtils.isNotEmpty(poses)) {
            List<String> deviceIds = new ArrayList<String>();
            for (Pos pos : poses) {
                deviceIds.add(pos.getDeviceId());
            }
            PushModel pushModel = new PushModel();
            PushMessageThread pushMessageThread = new PushMessageThread(pushModel, uuid, count, interval);
            new Thread(pushMessageThread).start();
        }
    }
}
