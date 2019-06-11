package build.dream.catering.utils;

import build.dream.catering.beans.PackageDetail;
import build.dream.catering.beans.PackageGroupDietOrderDetail;
import build.dream.catering.constants.Constants;
import build.dream.catering.models.dietorder.SaveDietOrderModel;
import build.dream.catering.tools.PushMessageThread;
import build.dream.common.beans.KafkaFixedTimeSendResult;
import build.dream.common.catering.domains.*;
import build.dream.common.constants.DietOrderConstants;
import build.dream.common.models.alipay.AlipayTradeRefundModel;
import build.dream.common.models.jpush.PushModel;
import build.dream.common.models.weixinpay.RefundModel;
import build.dream.common.saas.domains.Tenant;
import build.dream.common.saas.domains.WeiXinPayAccount;
import build.dream.common.utils.*;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

public class DietOrderUtils {
    /**
     * 恢复库存
     *
     * @param dietOrder
     */
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

        Map<BigInteger, List<DietOrderDetail>> dietOrderDetailMap = dietOrderDetails.stream().collect(Collectors.groupingBy(DietOrderDetail::getDietOrderGroupId));

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

    /**
     * 退款
     *
     * @param dietOrder
     */
    public static void refund(DietOrder dietOrder) {
        BigInteger tenantId = dietOrder.getTenantId();
        BigInteger branchId = dietOrder.getBranchId();
        BigInteger dietOrderId = dietOrder.getId();
        BigInteger vipId = dietOrder.getVipId();
        Vip vip = null;
        if (vipId.compareTo(Constants.BIGINT_DEFAULT_VALUE) != 0) {
            vip = VipUtils.find(tenantId, vipId);
        }

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition(DietOrderPayment.ColumnName.DIET_ORDER_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, dietOrderId);
        List<DietOrderPayment> dietOrderPayments = DatabaseHelper.findAll(DietOrderPayment.class, searchModel);

        for (DietOrderPayment dietOrderPayment : dietOrderPayments) {
            String paymentCode = dietOrderPayment.getPaymentCode();
            String extraInfo = dietOrderPayment.getExtraInfo();
            if (Constants.PAYMENT_CODE_HYJF.equals(paymentCode)) {
                Tenant tenant = TenantUtils.obtainTenantInfo(tenantId);
                VipAccount vipAccount = VipUtils.obtainVipAccount(tenantId, branchId, vipId, tenant.getVipSharedType());
                VipUtils.addVipPoint(vip.getTenantId(), vipId, vipAccount.getId(), dietOrderPayment.getPaidAmount().multiply(BigDecimal.valueOf(Double.valueOf(extraInfo))));
            } else if (Constants.PAYMENT_CODE_HYQB.equals(paymentCode)) {
                Tenant tenant = TenantUtils.obtainTenantInfo(tenantId);
                VipAccount vipAccount = VipUtils.obtainVipAccount(tenantId, branchId, vipId, tenant.getVipSharedType());
                VipUtils.addVipBalance(vip.getTenantId(), vipId, vipAccount.getId(), dietOrderPayment.getPaidAmount());
            } else if (Constants.PAYMENT_CODE_WX.equals(paymentCode)) {
                String orderNumber = dietOrder.getOrderNumber();
                int payableAmount = dietOrder.getPayableAmount().intValue();

                JSONObject extraInfoJsonObject = JSONObject.fromObject(extraInfo);
                JSONObject attachJsonObject = JSONObject.fromObject(extraInfoJsonObject.getString("attach"));

                WeiXinPayAccount weiXinPayAccount = WeiXinPayUtils.obtainWeiXinPayAccount(tenantId.toString(), branchId.toString());
                RefundModel refundModel = RefundModel.builder()
                        .appId(weiXinPayAccount.getAppId())
                        .mchId(weiXinPayAccount.getMchId())
                        .apiSecretKey(weiXinPayAccount.getApiSecretKey())
                        .subAppId(weiXinPayAccount.getSubPublicAccountAppId())
                        .subMchId(weiXinPayAccount.getSubMchId())
                        .acceptanceModel(weiXinPayAccount.isAcceptanceModel())
                        .outTradeNo(orderNumber)
                        .outRefundNo(orderNumber)
                        .totalFee(payableAmount)
                        .refundFee(payableAmount)
                        .build();
                WeiXinPayUtils.refund(refundModel);
            } else if (Constants.PAYMENT_CODE_ALIPAY.equals(paymentCode)) {
                AlipayTradeRefundModel alipayTradeRefundModel = new AlipayTradeRefundModel();
                AlipayUtils.alipayTradeRefund(alipayTradeRefundModel);
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
        Map<BigInteger, List<DietOrderDetail>> dietOrderDetailMap = dietOrderDetails.stream().collect(Collectors.groupingBy(DietOrderDetail::getDietOrderGroupId));

        // 封装订单详情与订单口味之间的map
        Map<BigInteger, List<DietOrderDetailGoodsAttribute>> dietOrderDetailGoodsAttributeMap = dietOrderDetailGoodsAttributes.stream().collect(Collectors.groupingBy(DietOrderDetailGoodsAttribute::getDietOrderDetailId));

        Map<String, Object> dietOrderInfo = new HashMap<String, Object>();
        dietOrderInfo.put(DietOrder.FieldName.ID, dietOrder.getId());
        dietOrderInfo.put(DietOrder.FieldName.ORDER_NUMBER, dietOrder.getOrderNumber());
        dietOrderInfo.put(DietOrder.FieldName.TENANT_ID, dietOrder.getTenantId());
        dietOrderInfo.put(DietOrder.FieldName.TENANT_CODE, dietOrder.getTenantCode());
        dietOrderInfo.put(DietOrder.FieldName.BRANCH_ID, dietOrder.getBranchId());
        dietOrderInfo.put(DietOrder.FieldName.ORDER_TYPE, dietOrder.getOrderType());
        dietOrderInfo.put(DietOrder.FieldName.ORDER_STATUS, dietOrder.getOrderStatus());
        dietOrderInfo.put(DietOrder.FieldName.PAY_STATUS, dietOrder.getPayStatus());
        dietOrderInfo.put(DietOrder.FieldName.REFUND_STATUS, dietOrder.getRefundStatus());
        dietOrderInfo.put(DietOrder.FieldName.TOTAL_AMOUNT, dietOrder.getTotalAmount());
        dietOrderInfo.put(DietOrder.FieldName.DISCOUNT_AMOUNT, dietOrder.getDiscountAmount());
        dietOrderInfo.put(DietOrder.FieldName.PAYABLE_AMOUNT, dietOrder.getPayableAmount());
        dietOrderInfo.put(DietOrder.FieldName.PAID_AMOUNT, dietOrder.getPaidAmount());
        dietOrderInfo.put(DietOrder.FieldName.PAID_TYPE, dietOrder.getPaidType());
        dietOrderInfo.put(DietOrder.FieldName.REMARK, dietOrder.getRemark());
        dietOrderInfo.put(DietOrder.FieldName.DELIVERY_ADDRESS, dietOrder.getDeliveryAddress());
        dietOrderInfo.put(DietOrder.FieldName.DELIVERY_LONGITUDE, dietOrder.getDeliveryLongitude());
        dietOrderInfo.put(DietOrder.FieldName.DELIVERY_LATITUDE, dietOrder.getDeliveryLatitude());
        dietOrderInfo.put(DietOrder.FieldName.DELIVER_TIME, dietOrder.getDeliverTime());
        dietOrderInfo.put(DietOrder.FieldName.ACTIVE_TIME, dietOrder.getActiveTime());
        dietOrderInfo.put(DietOrder.FieldName.DELIVER_FEE, dietOrder.getDeliverFee());
        dietOrderInfo.put(DietOrder.FieldName.TELEPHONE_NUMBER, dietOrder.getTelephoneNumber());
        dietOrderInfo.put(DietOrder.FieldName.DAY_SERIAL_NUMBER, dietOrder.getDaySerialNumber());
        dietOrderInfo.put(DietOrder.FieldName.CONSIGNEE, dietOrder.getConsignee());
        dietOrderInfo.put(DietOrder.FieldName.INVOICED, dietOrder.isInvoiced());
        dietOrderInfo.put(DietOrder.FieldName.INVOICE_TYPE, dietOrder.getInvoiceType());
        dietOrderInfo.put(DietOrder.FieldName.INVOICE, dietOrder.getInvoice());
        dietOrderInfo.put(DietOrder.FieldName.VIP_ID, dietOrder.getVipId());
        dietOrderInfo.put(DietOrder.FieldName.CREATED_TIME, dietOrder.getCreatedTime());
        dietOrderInfo.put(DietOrder.FieldName.UPDATED_TIME, dietOrder.getUpdatedTime());
        dietOrderInfo.put(DietOrder.FieldName.UPDATED_REMARK, dietOrder.getUpdatedRemark());
        dietOrderInfo.put("groups", buildDietOrderGroupInfos(dietOrderGroups, dietOrderDetailMap, dietOrderDetailGoodsAttributeMap));
        List<Map<String, Object>> orderActivities = new ArrayList<Map<String, Object>>();
        for (DietOrderActivity dietOrderActivity : dietOrderActivities) {
            Map<String, Object> orderActivity = new HashMap<String, Object>();
            orderActivity.put(DietOrderActivity.FieldName.ACTIVITY_ID, dietOrderActivity.getActivityId());
            orderActivity.put(DietOrderActivity.FieldName.ACTIVITY_NAME, dietOrderActivity.getActivityName());
            orderActivity.put(DietOrderActivity.FieldName.ACTIVITY_TYPE, dietOrderActivity.getActivityType());
            orderActivity.put(DietOrderActivity.FieldName.AMOUNT, dietOrderActivity.getAmount());
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
            group.put(DietOrderGroup.FieldName.ID, dietOrderGroup.getId());
            group.put(DietOrderGroup.FieldName.NAME, dietOrderGroup.getName());
            group.put(DietOrderGroup.FieldName.TYPE, dietOrderGroup.getType());
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

        Map<BigInteger, List<PackageGroupDietOrderDetail>> packageGroupDietOrderDetailMap = packageGroupDietOrderDetails.stream().collect(Collectors.groupingBy(PackageGroupDietOrderDetail::getPackageId));

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

    /**
     * 推送消息
     *
     * @param tenantId:    商户ID
     * @param branchId:    门店ID
     * @param dietOrderId: 订单ID
     * @param uuid:        唯一ID
     * @param count:       最大推送次数
     * @param interval:    推送间隔
     */
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

    /**
     * 保存订单
     *
     * @param saveDietOrderModel
     * @return
     */
    public static DietOrder saveDietOrder(SaveDietOrderModel saveDietOrderModel) {
        BigInteger tenantId = saveDietOrderModel.getTenantId();
        String tenantCode = saveDietOrderModel.getTenantCode();
        BigInteger branchId = saveDietOrderModel.getBranchId();
        BigInteger userId = saveDietOrderModel.getUserId();
        BigInteger vipId = saveDietOrderModel.getVipId();

        // 查询出门店信息
        Branch branch = DatabaseHelper.find(Branch.class, branchId);
        ValidateUtils.notNull(branch, "门店不存在！");

        List<BigInteger> goodsIds = new ArrayList<BigInteger>();
        List<BigInteger> packageIds = new ArrayList<BigInteger>();
        List<BigInteger> goodsSpecificationIds = new ArrayList<BigInteger>();
        List<BigInteger> goodsAttributeGroupIds = new ArrayList<BigInteger>();
        List<BigInteger> goodsAttributeIds = new ArrayList<BigInteger>();
        List<SaveDietOrderModel.GoodsInfo> goodsInfos = saveDietOrderModel.getGoodsInfos();
        for (SaveDietOrderModel.GoodsInfo goodsInfo : goodsInfos) {
            BigInteger goodsId = goodsInfo.getGoodsId();
            goodsIds.add(goodsInfo.getGoodsId());
            goodsSpecificationIds.add(goodsInfo.getGoodsSpecificationId());
            if (goodsInfo.isPackage()) {
                packageIds.add(goodsId);
            }

            List<SaveDietOrderModel.AttributeInfo> attributeInfos = goodsInfo.getAttributeInfos();
            if (CollectionUtils.isNotEmpty(attributeInfos)) {
                for (SaveDietOrderModel.AttributeInfo attributeInfo : attributeInfos) {
                    goodsAttributeGroupIds.add(attributeInfo.getAttributeGroupId());
                    goodsAttributeIds.add(attributeInfo.getAttributeId());
                }
            }
        }

        List<PackageDetail> optionalGroupPackageDetails = new ArrayList<PackageDetail>();
        List<PackageDetail> requiredGroupPackageDetails = new ArrayList<PackageDetail>();
        if (CollectionUtils.isNotEmpty(packageIds)) {
            optionalGroupPackageDetails = GoodsUtils.listPackageInfos(tenantId, branchId, packageIds, Constants.PACKAGE_GROUP_TYPE_OPTIONAL_GROUP);
            requiredGroupPackageDetails = GoodsUtils.listPackageInfos(tenantId, branchId, packageIds, Constants.PACKAGE_GROUP_TYPE_REQUIRED_GROUP);
        }

        Map<String, PackageDetail> optionalGroupPackageDetailMap = new HashMap<String, PackageDetail>();
        for (PackageDetail packageDetail : optionalGroupPackageDetails) {
            String key = packageDetail.getPackageId() + "_" + packageDetail.getPackageGroupId() + "_" + packageDetail.getGoodsId() + "_" + packageDetail.getGoodsSpecificationId();
            optionalGroupPackageDetailMap.put(key, packageDetail);
        }

        Map<BigInteger, List<PackageDetail>> requiredGroupPackageDetailMap = new HashMap<BigInteger, List<PackageDetail>>();
        for (PackageDetail packageDetail : requiredGroupPackageDetails) {
            BigInteger packageId = packageDetail.getPackageId();
            List<PackageDetail> packageDetails = requiredGroupPackageDetailMap.get(packageId);
            if (CollectionUtils.isEmpty(packageDetails)) {
                packageDetails = new ArrayList<PackageDetail>();
                requiredGroupPackageDetailMap.put(packageId, packageDetails);
            }
            packageDetails.add(packageDetail);
        }

        // 查询出订单中包含的所有商品
        List<Goods> goodses = new ArrayList<Goods>();
        if (CollectionUtils.isNotEmpty(goodsIds)) {
            goodses = GoodsUtils.findAllByIdInList(tenantId, branchId, goodsIds);
        }

        // 查询出订单中包含的所有商品规格
        List<GoodsSpecification> goodsSpecifications = new ArrayList<GoodsSpecification>();
        if (CollectionUtils.isNotEmpty(goodsSpecificationIds)) {
            SearchModel goodsSpecificationSearchModel = new SearchModel(true);
            goodsSpecificationSearchModel.addSearchCondition(GoodsSpecification.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
            goodsSpecificationSearchModel.addSearchCondition(GoodsSpecification.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
            goodsSpecificationSearchModel.addSearchCondition(GoodsSpecification.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_IN, goodsSpecificationIds);
            goodsSpecifications = DatabaseHelper.findAll(GoodsSpecification.class, goodsSpecificationSearchModel);
        }

        // 查询出订单中包含的所有口味组
        List<GoodsAttributeGroup> goodsAttributeGroups = new ArrayList<GoodsAttributeGroup>();
        if (CollectionUtils.isNotEmpty(goodsAttributeGroupIds)) {
            SearchModel goodsAttributeGroupSearchModel = new SearchModel(true);
            goodsAttributeGroupSearchModel.addSearchCondition(GoodsAttributeGroup.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
            goodsAttributeGroupSearchModel.addSearchCondition(GoodsAttributeGroup.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
            goodsAttributeGroupSearchModel.addSearchCondition(GoodsAttributeGroup.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_IN, goodsAttributeGroupIds);
            goodsAttributeGroups = DatabaseHelper.findAll(GoodsAttributeGroup.class, goodsAttributeGroupSearchModel);
        }

        // 查询出订单中包含的所有口味
        List<GoodsAttribute> goodsAttributes = new ArrayList<GoodsAttribute>();
        if (CollectionUtils.isNotEmpty(goodsAttributeIds)) {
            SearchModel goodsAttributeSearchModel = new SearchModel(true);
            goodsAttributeSearchModel.addSearchCondition(GoodsAttribute.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
            goodsAttributeSearchModel.addSearchCondition(GoodsAttribute.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
            goodsAttributeSearchModel.addSearchCondition(GoodsAttribute.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_IN, goodsAttributeIds);
            goodsAttributes = DatabaseHelper.findAll(GoodsAttribute.class, goodsAttributeSearchModel);
        }

        // 封装商品id与商品之间的map
        Map<BigInteger, Goods> goodsMap = new HashMap<BigInteger, Goods>();
        for (Goods goods : goodses) {
            goodsMap.put(goods.getId(), goods);
        }

        // 封装商品规格id与商品规格之间的map
        Map<BigInteger, GoodsSpecification> goodsSpecificationMap = new HashMap<BigInteger, GoodsSpecification>();
        for (GoodsSpecification goodsSpecification : goodsSpecifications) {
            goodsSpecificationMap.put(goodsSpecification.getId(), goodsSpecification);
        }

        // 封装商品口味组id与商品口味组之间的map
        Map<BigInteger, GoodsAttributeGroup> goodsAttributeGroupMap = new HashMap<BigInteger, GoodsAttributeGroup>();
        for (GoodsAttributeGroup goodsAttributeGroup : goodsAttributeGroups) {
            goodsAttributeGroupMap.put(goodsAttributeGroup.getId(), goodsAttributeGroup);
        }

        // 封装商品口味id与商品口味之间的map
        Map<BigInteger, GoodsAttribute> goodsAttributeMap = new HashMap<BigInteger, GoodsAttribute>();
        for (GoodsAttribute goodsAttribute : goodsAttributes) {
            goodsAttributeMap.put(goodsAttribute.getId(), goodsAttribute);
        }

        String orderNumberPrefix = null;
        Integer orderType = saveDietOrderModel.getOrderType();
        if (orderType == DietOrderConstants.ORDER_TYPE_SCAN_CODE_ORDER) {
            orderNumberPrefix = "SC";
        }

        Integer daySerialNumber = SequenceUtils.nextValueToday(tenantId + "_" + branchId);
        String orderNumber = SerialNumberGenerator.generateSerialNumber(orderNumberPrefix);

        DietOrder.Builder builder = DietOrder.builder()
                .tenantId(tenantId)
                .tenantCode(tenantCode)
                .branchId(branchId)
                .orderNumber(orderNumber)
                .orderType(orderType)
                .orderStatus(DietOrderConstants.ORDER_STATUS_PENDING)
                .payStatus(DietOrderConstants.PAY_STATUS_UNPAID)
                .refundStatus(DietOrderConstants.REFUND_STATUS_NO_REFUND)
                .daySerialNumber(daySerialNumber.toString());

        boolean invoiced = saveDietOrderModel.getInvoiced();
        builder.invoiced(invoiced);
        if (invoiced) {
            builder.invoiceType(saveDietOrderModel.getInvoiceType()).invoice(saveDietOrderModel.getInvoice());
        }
        if (vipId != null) {
            builder.vipId(vipId);
        } else {
            builder.vipId(Constants.BIGINT_DEFAULT_VALUE);
        }
        builder.createdUserId(userId).updatedUserId(userId).updatedRemark("保存订单信息！");

        DietOrder dietOrder = builder.build();
        DatabaseHelper.insert(dietOrder);

        BigDecimal dietOrderTotalAmount = BigDecimal.ZERO;
        BigDecimal deliverFee = BigDecimal.TEN;
        BigDecimal packageFee = BigDecimal.TEN;
        BigInteger dietOrderId = dietOrder.getId();

        // 存放订单优惠金额
        BigDecimal dietOrderDiscountAmount = BigDecimal.ZERO;
        // 用来保存订单活动

        List<EffectiveActivity> effectiveActivities = ActivityUtils.listEffectiveActivities(tenantId, branchId);
        List<EffectiveActivity> fullReductionActivities = new ArrayList<EffectiveActivity>();
        List<EffectiveActivity> paymentActivities = new ArrayList<EffectiveActivity>();
        Map<String, EffectiveActivity> effectiveActivityMap = new HashMap<String, EffectiveActivity>();
        for (EffectiveActivity effectiveActivity : effectiveActivities) {
            int type = effectiveActivity.getType();
            if (type == Constants.ACTIVITY_TYPE_BUY_GIVE_ACTIVITY || type == Constants.ACTIVITY_TYPE_SPECIAL_GOODS_ACTIVITY) {
                effectiveActivityMap.put(effectiveActivity.getGoodsId() + "_" + effectiveActivity.getGoodsSpecificationId(), effectiveActivity);
            } else if (type == Constants.ACTIVITY_TYPE_FULL_REDUCTION_ACTIVITY) {
                fullReductionActivities.add(effectiveActivity);
            } else if (type == Constants.ACTIVITY_TYPE_PAYMENT_ACTIVITY) {
                paymentActivities.add(effectiveActivity);
            }
        }

        List<DietOrderDetail> dietOrderDetails = new ArrayList<DietOrderDetail>();
        Map<String, DietOrderDetail> dietOrderDetailMap = new HashMap<String, DietOrderDetail>();
        Map<String, List<DietOrderDetailGoodsAttribute>> dietOrderDetailGoodsAttributeMap = new HashMap<String, List<DietOrderDetailGoodsAttribute>>();
        Map<BigInteger, DietOrderActivity> dietOrderActivityMap = new HashMap<BigInteger, DietOrderActivity>();

        DietOrderGroup discountDietOrderGroup = null;
        DietOrderGroup normalDietOrderGroup = DietOrderGroup.builder()
                .tenantId(tenantId)
                .tenantCode(tenantCode)
                .branchId(branchId)
                .dietOrderId(dietOrderId)
                .name("正常的菜品")
                .type(DietOrderConstants.GROUP_TYPE_NORMAL)
                .createdUserId(userId)
                .updatedUserId(userId)
                .updatedRemark("保存订单分组信息！")
                .build();
        DatabaseHelper.insert(normalDietOrderGroup);

        for (SaveDietOrderModel.GoodsInfo goodsInfo : goodsInfos) {
            Goods goods = goodsMap.get(goodsInfo.getGoodsId());
            ValidateUtils.notNull(goods, "商品不存在！");

            GoodsSpecification goodsSpecification = goodsSpecificationMap.get(goodsInfo.getGoodsSpecificationId());
            ValidateUtils.notNull(goodsSpecification, "商品规格不存在！");

            BigInteger goodsId = goods.getId();
            BigInteger goodsSpecificationId = goodsSpecification.getId();
            String goodsSpecificationName = goodsSpecification.getName();
            BigDecimal quantity = goodsInfo.getQuantity();
            BigDecimal price = goodsSpecification.getPrice();
            if (goods.isStocked()) {
                GoodsUtils.deductingGoodsStock(goodsId, goodsSpecificationId, quantity);
            }

            if (goodsInfo.isPackage()) {
                List<SaveDietOrderModel.PackageInfo> infos = goodsInfo.getPackageInfos();
                for (SaveDietOrderModel.PackageInfo info : infos) {
                    for (SaveDietOrderModel.Detail detail : info.getDetails()) {
                        PackageDetail packageDetail = optionalGroupPackageDetailMap.get(goodsId + "_" + info.getGroupId() + "_" + detail.getGoodsId() + "_" + detail.getGoodsSpecificationId());
                        ValidateUtils.notNull(packageDetail, "套餐明细不存在！");
                        DietOrderDetail dietOrderDetail = DietOrderDetail.builder()
                                .tenantId(tenantId)
                                .tenantCode(tenantCode)
                                .branchId(branchId)
                                .dietOrderId(dietOrderId)
                                .dietOrderGroupId(normalDietOrderGroup.getId())
                                .goodsType(Constants.GOODS_TYPE_PACKAGE_DETAIL)
                                .goodsId(packageDetail.getGoodsId())
                                .goodsName(packageDetail.getGoodsName())
                                .goodsSpecificationId(packageDetail.getGoodsSpecificationId())
                                .goodsSpecificationName(packageDetail.getGoodsSpecificationName())
                                .packageId(packageDetail.getPackageId())
                                .packageGroupId(packageDetail.getPackageGroupId())
                                .packageGroupName(packageDetail.getPackageGroupName())
                                .categoryId(goods.getCategoryId())
                                .categoryName(goods.getCategoryName())
                                .price(price)
                                .attributeIncrease(BigDecimal.ZERO)
                                .quantity(detail.getQuantity().multiply(quantity))
                                .totalAmount(BigDecimal.ZERO)
                                .discountAmount(BigDecimal.ZERO)
                                .payableAmount(BigDecimal.ZERO)
                                .createdUserId(userId)
                                .updatedUserId(userId)
                                .build();
                        dietOrderDetails.add(dietOrderDetail);
                    }
                }
                List<PackageDetail> packageDetails = requiredGroupPackageDetailMap.get(goodsId);
                for (PackageDetail packageDetail : packageDetails) {
                    DietOrderDetail dietOrderDetail = DietOrderDetail.builder()
                            .tenantId(tenantId)
                            .tenantCode(tenantCode)
                            .branchId(branchId)
                            .dietOrderId(dietOrderId)
                            .dietOrderGroupId(normalDietOrderGroup.getId())
                            .goodsType(Constants.GOODS_TYPE_PACKAGE_DETAIL)
                            .goodsId(packageDetail.getGoodsId())
                            .goodsName(packageDetail.getGoodsName())
                            .goodsSpecificationId(packageDetail.getGoodsSpecificationId())
                            .goodsSpecificationName(packageDetail.getGoodsSpecificationName())
                            .packageId(packageDetail.getPackageId())
                            .packageGroupId(packageDetail.getPackageGroupId())
                            .packageGroupName(packageDetail.getPackageGroupName())
                            .categoryId(goods.getCategoryId())
                            .categoryName(goods.getCategoryName())
                            .price(price)
                            .attributeIncrease(BigDecimal.ZERO)
                            .quantity(quantity.multiply(packageDetail.getQuantity()))
                            .totalAmount(BigDecimal.ZERO)
                            .discountAmount(BigDecimal.ZERO)
                            .payableAmount(BigDecimal.ZERO)
                            .createdUserId(userId)
                            .updatedUserId(userId)
                            .build();
                    dietOrderDetails.add(dietOrderDetail);
                }
            }

            String uuid = UUID.randomUUID().toString();
            BigDecimal attributeIncrease = BigDecimal.ZERO;
            List<SaveDietOrderModel.AttributeInfo> attributeInfos = goodsInfo.getAttributeInfos();

            List<DietOrderDetailGoodsAttribute> dietOrderDetailGoodsAttributes = new ArrayList<DietOrderDetailGoodsAttribute>();
            if (CollectionUtils.isNotEmpty(attributeInfos)) {
                for (SaveDietOrderModel.AttributeInfo attributeInfo : attributeInfos) {
                    GoodsAttributeGroup goodsAttributeGroup = goodsAttributeGroupMap.get(attributeInfo.getAttributeGroupId());
                    ValidateUtils.notNull(goodsAttributeGroup, "口味组不存在！");

                    GoodsAttribute goodsAttribute = goodsAttributeMap.get(attributeInfo.getAttributeId());
                    ValidateUtils.notNull(goodsAttribute, "口味不存在！");
                    attributeIncrease = attributeIncrease.add(goodsAttribute.getPrice());

                    DietOrderDetailGoodsAttribute dietOrderDetailGoodsAttribute = DietOrderDetailGoodsAttribute.builder()
                            .tenantId(tenantId)
                            .tenantCode(tenantCode)
                            .branchId(branchId)
                            .dietOrderId(dietOrderId)
                            .dietOrderGroupId(normalDietOrderGroup.getId())
                            .dietOrderDetailId(null)
                            .goodsAttributeGroupId(goodsAttributeGroup.getId())
                            .goodsAttributeGroupName(goodsAttributeGroup.getName())
                            .goodsAttributeId(goodsAttribute.getId())
                            .goodsAttributeName(goodsAttribute.getName())
                            .price(goodsAttribute.getPrice())
                            .createdUserId(userId)
                            .updatedUserId(userId)
                            .build();
                    dietOrderDetailGoodsAttributes.add(dietOrderDetailGoodsAttribute);
                    dietOrderDetailGoodsAttributeMap.put(uuid, dietOrderDetailGoodsAttributes);
                }
            }

            DietOrderDetail dietOrderDetail = null;
            // 开始处理促销活动
            EffectiveActivity effectiveActivity = effectiveActivityMap.get(goodsId + "_" + goodsSpecificationId);
            if (effectiveActivity != null) {
                int type = effectiveActivity.getType();
                // 买A赠B活动
                if (type == Constants.ACTIVITY_TYPE_BUY_GIVE_ACTIVITY) {
                    if (quantity.compareTo(effectiveActivity.getBuyQuantity()) >= 0) {
                        if (discountDietOrderGroup == null) {
                            discountDietOrderGroup = DietOrderGroup.builder()
                                    .tenantId(tenantId)
                                    .tenantCode(tenantCode)
                                    .branchId(branchId)
                                    .dietOrderId(dietOrderId)
                                    .name("赠送的菜品")
                                    .type(DietOrderConstants.GROUP_TYPE_DISCOUNT)
                                    .createdUserId(userId)
                                    .updatedUserId(userId)
                                    .updatedRemark("保存订单分组信息！")
                                    .build();
                            DatabaseHelper.insert(discountDietOrderGroup);
                        }
                        BigDecimal giveQuantity = quantity.divide(effectiveActivity.getBuyQuantity(), 0, BigDecimal.ROUND_DOWN).multiply(effectiveActivity.getGiveQuantity());
                        BigDecimal giveTotalAmount = giveQuantity.multiply(effectiveActivity.getSpecialPrice());
                        DietOrderDetail giveDietOrderDetail = DietOrderDetail.builder()
                                .tenantId(tenantId)
                                .tenantCode(tenantCode)
                                .branchId(branchId)
                                .dietOrderId(dietOrderId)
                                .dietOrderGroupId(discountDietOrderGroup.getId())
                                .goodsType(Constants.GOODS_TYPE_ORDINARY_GOODS)
                                .goodsId(effectiveActivity.getGiveGoodsId())
                                .goodsName(effectiveActivity.getGiveGoodsName())
                                .goodsSpecificationId(effectiveActivity.getGoodsSpecificationId())
                                .goodsSpecificationName(effectiveActivity.getGoodsSpecificationName())
                                .categoryId(effectiveActivity.getCategoryId())
                                .categoryName(effectiveActivity.getCategoryName())
                                .price(BigDecimal.ZERO)
                                .attributeIncrease(BigDecimal.ZERO)
                                .quantity(effectiveActivity.getGiveQuantity())
                                .totalAmount(giveTotalAmount)
                                .discountAmount(giveTotalAmount)
                                .payableAmount(BigDecimal.ZERO)
                                .createdUserId(userId)
                                .updatedUserId(userId)
                                .build();
                        dietOrderDetails.add(giveDietOrderDetail);

                        BigInteger activityId = effectiveActivity.getActivityId();
                        DietOrderActivity dietOrderActivity = dietOrderActivityMap.get(activityId);
                        if (dietOrderActivity == null) {
                            dietOrderActivity = DietOrderActivity.builder()
                                    .tenantId(tenantId)
                                    .tenantCode(tenantCode)
                                    .branchId(branchId)
                                    .dietOrderId(dietOrderId)
                                    .activityId(activityId)
                                    .activityName(effectiveActivity.getName())
                                    .activityType(type)
                                    .amount(giveDietOrderDetail.getDiscountAmount())
                                    .createdUserId(userId)
                                    .updatedUserId(userId)
                                    .build();
                            dietOrderActivityMap.put(activityId, dietOrderActivity);
                        } else {
                            dietOrderActivity.setAmount(dietOrderActivity.getAmount().add(giveDietOrderDetail.getDiscountAmount()));
                        }
                    }

                    BigDecimal dietOrderDetailTotalAmount = price.add(attributeIncrease).multiply(quantity);
                    dietOrderDetail = DietOrderDetail.builder()
                            .tenantId(tenantId)
                            .tenantCode(tenantCode)
                            .branchId(branchId)
                            .dietOrderId(dietOrderId)
                            .dietOrderGroupId(normalDietOrderGroup.getId())
                            .goodsType(goods.getType())
                            .goodsId(goodsId)
                            .goodsName(goods.getName())
                            .goodsSpecificationId(goodsSpecificationId)
                            .goodsSpecificationName(goodsSpecificationName)
                            .categoryId(goods.getCategoryId())
                            .categoryName(goods.getCategoryName())
                            .price(price)
                            .attributeIncrease(attributeIncrease)
                            .quantity(quantity)
                            .totalAmount(dietOrderDetailTotalAmount)
                            .discountAmount(BigDecimal.ZERO)
                            .payableAmount(dietOrderDetailTotalAmount)
                            .createdUserId(userId)
                            .updatedUserId(userId)
                            .build();
                }

                if (type == Constants.ACTIVITY_TYPE_SPECIAL_GOODS_ACTIVITY) {
                    Integer discountType = effectiveActivity.getDiscountType();
                    BigDecimal dietOrderDetailTotalAmount = price.add(attributeIncrease).multiply(quantity);
                    BigDecimal dietOrderDetailPayableAmount = null;
                    if (discountType == 1) {
                        dietOrderDetailPayableAmount = effectiveActivity.getSpecialPrice().add(attributeIncrease).multiply(quantity);
                    } else {
                        dietOrderDetailPayableAmount = price.subtract(price.multiply(effectiveActivity.getDiscountRate()).add(attributeIncrease).divide(Constants.BIG_DECIMAL_ONE_HUNDRED));
                    }
                    BigDecimal dietOrderDetailDiscountAmount = dietOrderDetailTotalAmount.subtract(dietOrderDetailPayableAmount);
                    dietOrderTotalAmount = dietOrderTotalAmount.add(dietOrderDetailTotalAmount);
                    dietOrderDiscountAmount = dietOrderDiscountAmount.add(dietOrderDetailDiscountAmount);
                    dietOrderDetail = DietOrderDetail.builder()
                            .tenantId(tenantId)
                            .tenantCode(tenantCode)
                            .branchId(branchId)
                            .dietOrderId(dietOrderId)
                            .dietOrderGroupId(normalDietOrderGroup.getId())
                            .goodsType(goods.getType())
                            .goodsId(goodsId)
                            .goodsName(goods.getName())
                            .goodsSpecificationId(goodsSpecificationId)
                            .goodsSpecificationName(goodsSpecificationName)
                            .categoryId(goods.getCategoryId())
                            .categoryName(goods.getCategoryName())
                            .price(price)
                            .attributeIncrease(attributeIncrease)
                            .quantity(quantity)
                            .totalAmount(dietOrderDetailTotalAmount)
                            .discountAmount(dietOrderDetailDiscountAmount)
                            .payableAmount(dietOrderDetailPayableAmount)
                            .createdUserId(userId)
                            .updatedUserId(userId)
                            .build();

                    BigInteger activityId = effectiveActivity.getActivityId();
                    DietOrderActivity dietOrderActivity = dietOrderActivityMap.get(activityId);
                    if (dietOrderActivity == null) {
                        dietOrderActivity = DietOrderActivity.builder()
                                .tenantId(tenantId)
                                .tenantCode(tenantCode)
                                .branchId(branchId)
                                .dietOrderId(dietOrderId)
                                .activityId(activityId)
                                .activityName(effectiveActivity.getName())
                                .activityType(type)
                                .amount(dietOrderDetailDiscountAmount)
                                .createdUserId(userId)
                                .updatedUserId(userId)
                                .build();

                        dietOrderActivityMap.put(activityId, dietOrderActivity);
                    } else {
                        dietOrderActivity.setAmount(dietOrderActivity.getAmount().add(dietOrderDetailDiscountAmount));
                    }
                }
            } else {
                BigDecimal dietOrderDetailTotalAmount = price.add(attributeIncrease).multiply(quantity);
                dietOrderTotalAmount = dietOrderTotalAmount.add(dietOrderDetailTotalAmount);
                dietOrderDetail = DietOrderDetail.builder()
                        .tenantId(tenantId)
                        .tenantCode(tenantCode)
                        .branchId(branchId)
                        .dietOrderId(dietOrderId)
                        .dietOrderGroupId(normalDietOrderGroup.getId())
                        .goodsType(goods.getType())
                        .goodsId(goodsId)
                        .goodsName(goods.getName())
                        .goodsSpecificationId(goodsSpecificationId)
                        .goodsSpecificationName(goodsSpecificationName)
                        .categoryId(goods.getCategoryId())
                        .categoryName(goods.getCategoryName())
                        .price(price)
                        .attributeIncrease(attributeIncrease)
                        .quantity(quantity)
                        .totalAmount(dietOrderDetailTotalAmount)
                        .discountAmount(BigDecimal.ZERO)
                        .payableAmount(dietOrderDetailTotalAmount)
                        .createdUserId(userId)
                        .updatedUserId(userId)
                        .build();
            }

            dietOrderDetails.add(dietOrderDetail);
            dietOrderDetailMap.put(uuid, dietOrderDetail);
            packageFee = packageFee.add(BigDecimal.ZERO);
        }

        // 开始处理配送费与打包费
        DietOrderGroup extraDietOrderGroup = null;
        if (deliverFee.compareTo(BigDecimal.ZERO) > 0) {
            if (extraDietOrderGroup == null) {
                extraDietOrderGroup = DietOrderGroup.builder()
                        .tenantId(tenantId)
                        .tenantCode(tenantCode)
                        .branchId(branchId)
                        .dietOrderId(dietOrderId)
                        .name("其他费用")
                        .type(DietOrderConstants.GROUP_TYPE_EXTRA)
                        .createdUserId(userId)
                        .updatedUserId(userId)
                        .updatedRemark("保存订单分组信息！")
                        .build();
                DatabaseHelper.insert(extraDietOrderGroup);
            }
            dietOrderTotalAmount = dietOrderTotalAmount.add(deliverFee);
            DietOrderDetail dietOrderDetail = DietOrderDetail.builder()
                    .tenantId(tenantId)
                    .tenantCode(tenantCode)
                    .branchId(branchId)
                    .dietOrderId(dietOrderId)
                    .dietOrderGroupId(extraDietOrderGroup.getId())
                    .goodsType(Constants.GOODS_TYPE_DELIVER_FEE)
                    .goodsId(Constants.BIG_INTEGER_MINUS_ONE)
                    .goodsName("配送费")
                    .goodsSpecificationId(Constants.BIG_INTEGER_MINUS_ONE)
                    .goodsSpecificationName(Constants.VARCHAR_DEFAULT_VALUE)
                    .categoryId(Constants.FICTITIOUS_GOODS_CATEGORY_ID)
                    .categoryName(Constants.FICTITIOUS_GOODS_CATEGORY_NAME)
                    .price(deliverFee)
                    .attributeIncrease(BigDecimal.ZERO)
                    .quantity(BigDecimal.ONE)
                    .totalAmount(deliverFee)
                    .discountAmount(BigDecimal.ZERO)
                    .payableAmount(deliverFee)
                    .createdUserId(userId)
                    .updatedUserId(userId)
                    .build();
            dietOrderDetails.add(dietOrderDetail);
        }

        if (packageFee.compareTo(BigDecimal.ZERO) > 0) {
            if (extraDietOrderGroup == null) {
                extraDietOrderGroup = DietOrderGroup.builder()
                        .tenantId(tenantId)
                        .tenantCode(tenantCode)
                        .branchId(branchId)
                        .dietOrderId(dietOrderId)
                        .name("其他费用")
                        .type(DietOrderConstants.GROUP_TYPE_EXTRA)
                        .createdUserId(userId)
                        .updatedUserId(userId)
                        .updatedRemark("保存订单分组信息！")
                        .build();
                DatabaseHelper.insert(extraDietOrderGroup);
            }
            dietOrderTotalAmount = dietOrderTotalAmount.add(packageFee);
            DietOrderDetail dietOrderDetail = DietOrderDetail.builder()
                    .tenantId(tenantId)
                    .tenantCode(tenantCode)
                    .branchId(branchId)
                    .dietOrderId(dietOrderId)
                    .dietOrderGroupId(extraDietOrderGroup.getId())
                    .goodsType(Constants.GOODS_TYPE_PACKAGE_FEE)
                    .goodsId(Constants.BIG_INTEGER_MINUS_TWO)
                    .goodsName("打包费")
                    .goodsSpecificationId(Constants.BIG_INTEGER_MINUS_TWO)
                    .goodsSpecificationName(Constants.VARCHAR_DEFAULT_VALUE)
                    .categoryId(Constants.FICTITIOUS_GOODS_CATEGORY_ID)
                    .categoryName(Constants.FICTITIOUS_GOODS_CATEGORY_NAME)
                    .price(packageFee)
                    .attributeIncrease(BigDecimal.ZERO)
                    .quantity(BigDecimal.ONE)
                    .totalAmount(packageFee)
                    .discountAmount(BigDecimal.ZERO)
                    .payableAmount(packageFee)
                    .createdUserId(userId)
                    .updatedUserId(userId)
                    .build();
            dietOrderDetails.add(dietOrderDetail);
        }

        DatabaseHelper.insertAll(dietOrderDetails);

        if (MapUtils.isNotEmpty(dietOrderDetailGoodsAttributeMap)) {
            List<DietOrderDetailGoodsAttribute> dietOrderDetailGoodsAttributes = new ArrayList<DietOrderDetailGoodsAttribute>();
            for (Map.Entry<String, List<DietOrderDetailGoodsAttribute>> entry : dietOrderDetailGoodsAttributeMap.entrySet()) {
                String key = entry.getKey();
                DietOrderDetail dietOrderDetail = dietOrderDetailMap.get(key);
                BigInteger dietOrderDetailId = dietOrderDetail.getId();

                List<DietOrderDetailGoodsAttribute> value = entry.getValue();
                for (DietOrderDetailGoodsAttribute dietOrderDetailGoodsAttribute : value) {
                    dietOrderDetailGoodsAttribute.setDietOrderDetailId(dietOrderDetailId);
                    dietOrderDetailGoodsAttributes.add(dietOrderDetailGoodsAttribute);
                }
            }
            DatabaseHelper.insertAll(dietOrderDetailGoodsAttributes);
        }

        // 整单优惠活动

        BigDecimal dietOrderPayableAmount = dietOrderTotalAmount.subtract(dietOrderDiscountAmount);
        List<DietOrderActivity> dietOrderActivities = new ArrayList<DietOrderActivity>();
        if (CollectionUtils.isNotEmpty(fullReductionActivities)) {
            Collections.sort(fullReductionActivities, (o1, o2) -> o1.getTotalAmount().compareTo(o2.getTotalAmount()));

            EffectiveActivity fullReductionActivity = null;
            int size = fullReductionActivities.size();
            if (dietOrderPayableAmount.compareTo(fullReductionActivities.get(0).getTotalAmount()) < 0) {

            } else if (dietOrderPayableAmount.compareTo(fullReductionActivities.get(size - 1).getTotalAmount()) >= 0) {
                fullReductionActivity = fullReductionActivities.get(size - 1);
            } else {
                for (int index = 0; index < size - 1; index++) {
                    EffectiveActivity prevEffectiveActivity = fullReductionActivities.get(index);
                    EffectiveActivity nextEffectiveActivity = fullReductionActivities.get(index + 1);
                    if (dietOrderPayableAmount.compareTo(prevEffectiveActivity.getTotalAmount()) >= 0 && dietOrderPayableAmount.compareTo(nextEffectiveActivity.getTotalAmount()) < 0) {
                        fullReductionActivity = prevEffectiveActivity;
                    }
                }
            }
            if (fullReductionActivity != null) {
                BigDecimal amount = null;
                int discountType = fullReductionActivity.getDiscountType();
                if (discountType == 1) {
                    amount = fullReductionActivity.getDiscountAmount();
                } else {
                    amount = dietOrderPayableAmount.subtract(dietOrderPayableAmount.multiply(fullReductionActivity.getDiscountRate()).divide(Constants.BIG_DECIMAL_ONE_HUNDRED));
                }
                dietOrderDiscountAmount = dietOrderDiscountAmount.add(amount);
                DietOrderActivity dietOrderActivity = DietOrderActivity.builder()
                        .tenantId(tenantId)
                        .tenantCode(tenantCode)
                        .branchId(branchId)
                        .dietOrderId(dietOrderId)
                        .activityId(fullReductionActivity.getActivityId())
                        .activityName(fullReductionActivity.getName())
                        .activityType(fullReductionActivity.getType())
                        .amount(amount)
                        .createdUserId(userId)
                        .updatedUserId(userId)
                        .build();
                dietOrderActivities.add(dietOrderActivity);
            }
        }
        if (MapUtils.isNotEmpty(dietOrderActivityMap)) {
            dietOrderActivities.addAll(dietOrderActivityMap.values());
        }

        if (CollectionUtils.isNotEmpty(dietOrderActivities)) {
            DatabaseHelper.insertAll(dietOrderActivities);
        }

        dietOrder.setTotalAmount(dietOrderTotalAmount);
        dietOrder.setDiscountAmount(dietOrderDiscountAmount);
        dietOrder.setPayableAmount(dietOrderTotalAmount.subtract(dietOrderDiscountAmount));
        dietOrder.setPaidAmount(BigDecimal.ZERO);

        KafkaFixedTimeSendResult kafkaFixedTimeSendResult = startOrderInvalidJob(tenantId, branchId, dietOrderId, 1, DateUtils.addMinutes(dietOrder.getCreatedTime(), 15));
        dietOrder.setJobId(kafkaFixedTimeSendResult.getJobId());
        dietOrder.setTriggerId(kafkaFixedTimeSendResult.getTriggerId());

        DatabaseHelper.update(dietOrder);
        return dietOrder;
    }

    /**
     * 开始失效订单定时任务
     *
     * @param tenantId: 商户ID
     * @param branchId: 门店ID
     * @param orderId:  订单ID
     * @param type:     类型，1-超时未付款，3-超时未接单
     * @param startTime
     * @return
     */
    public static KafkaFixedTimeSendResult startOrderInvalidJob(BigInteger tenantId, BigInteger branchId, BigInteger orderId, int type, Date startTime) {
        String topic = ConfigurationUtils.getConfiguration(Constants.ORDER_INVALID_MESSAGE_TOPIC);
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("tenantId", tenantId);
        data.put("branchId", branchId);
        data.put("orderId", orderId);
        data.put("type", type);
        return KafkaUtils.fixedTimeSend(topic, GsonUtils.toJson(data), startTime);
    }

    /**
     * 停止失效订单定时任务
     *
     * @param jobId
     * @param triggerId
     */
    public static void stopOrderInvalidJob(String jobId, String triggerId) {
        KafkaUtils.cancelFixedTimeSend(jobId, triggerId);
    }

    /**
     * 取消订单
     *
     * @param tenantId: 商户ID
     * @param branchId: 门店ID
     * @param orderId:  订单ID
     * @param type:     类型，1-超时未付款自动取消，2-商户拒单取消订单，3-超时未接单自动取消
     */
    public static void cancelOrder(BigInteger tenantId, BigInteger branchId, BigInteger orderId, int type) {
        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition(DietOrder.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        searchModel.addSearchCondition(DietOrder.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        searchModel.addSearchCondition(DietOrder.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, orderId);
        DietOrder dietOrder = DatabaseHelper.find(DietOrder.class, searchModel);
        ValidateUtils.notNull(dietOrder, "订单不存在！");

        if (type == 1) {

        } else if (type == 2) {
            stopOrderInvalidJob(dietOrder.getJobId(), dietOrder.getTriggerId());
            DietOrderUtils.refund(dietOrder);
        } else if (type == 3) {
            DietOrderUtils.refund(dietOrder);
        }
        DietOrderUtils.recoveryStock(dietOrder);
        dietOrder.setOrderStatus(DietOrderConstants.ORDER_STATUS_INVALID);
        dietOrder.setJobId(Constants.VARCHAR_DEFAULT_VALUE);
        dietOrder.setTriggerId(Constants.VARCHAR_DEFAULT_VALUE);
        DatabaseHelper.update(dietOrder);
    }
}
