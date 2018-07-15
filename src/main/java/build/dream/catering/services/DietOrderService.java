package build.dream.catering.services;

import build.dream.catering.constants.Constants;
import build.dream.catering.mappers.ActivityMapper;
import build.dream.catering.mappers.GoodsMapper;
import build.dream.catering.mappers.SequenceMapper;
import build.dream.catering.models.dietorder.CancelOrderModel;
import build.dream.catering.models.dietorder.ConfirmOrderModel;
import build.dream.catering.models.dietorder.ObtainDietOrderInfoModel;
import build.dream.catering.models.dietorder.SaveDietOrderModel;
import build.dream.common.api.ApiRest;
import build.dream.common.constants.DietOrderConstants;
import build.dream.common.erp.catering.domains.*;
import build.dream.common.utils.DatabaseHelper;
import build.dream.common.utils.SearchModel;
import build.dream.common.utils.SerialNumberGenerator;
import build.dream.common.utils.ValidateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class DietOrderService {
    @Autowired
    private SequenceMapper sequenceMapper;
    @Autowired
    private ActivityMapper activityMapper;
    @Autowired
    private GoodsMapper goodsMapper;

    /**
     * 获取订单明细
     *
     * @param obtainDietOrderInfoModel
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest obtainDietOrderInfo(ObtainDietOrderInfoModel obtainDietOrderInfoModel) {
        // 查询出订单信息
        BigInteger tenantId = obtainDietOrderInfoModel.getTenantId();
        BigInteger branchId = obtainDietOrderInfoModel.getBranchId();
        BigInteger dietOrderId = obtainDietOrderInfoModel.getDietOrderId();
        SearchModel dietOrderSearchModel = new SearchModel(true);
        dietOrderSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUAL, dietOrderId);
        dietOrderSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        dietOrderSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        DietOrder dietOrder = DatabaseHelper.find(DietOrder.class, dietOrderSearchModel);
        ValidateUtils.notNull(dietOrder, "订单不存在！");

        // 查询出订单组信息
        SearchModel dietOrderGroupSearchModel = new SearchModel(true);
        dietOrderGroupSearchModel.addSearchCondition("diet_order_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, dietOrderId);
        dietOrderGroupSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        dietOrderGroupSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        List<DietOrderGroup> dietOrderGroups = DatabaseHelper.findAll(DietOrderGroup.class, dietOrderGroupSearchModel);

        // 查询出订单详情信息
        SearchModel dietOrderDetailSearchModel = new SearchModel(true);
        dietOrderDetailSearchModel.addSearchCondition("diet_order_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, dietOrderId);
        dietOrderDetailSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        dietOrderDetailSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        List<DietOrderDetail> dietOrderDetails = DatabaseHelper.findAll(DietOrderDetail.class, dietOrderDetailSearchModel);

        // 查询出订单口味信息
        SearchModel dietOrderDetailGoodsAttributeSearchModel = new SearchModel(true);
        dietOrderDetailGoodsAttributeSearchModel.addSearchCondition("diet_order_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, dietOrderId);
        dietOrderDetailGoodsAttributeSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        dietOrderDetailGoodsAttributeSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        List<DietOrderDetailGoodsAttribute> dietOrderDetailGoodsAttributes = DatabaseHelper.findAll(DietOrderDetailGoodsAttribute.class, dietOrderDetailGoodsAttributeSearchModel);

        SearchModel dietOrderActivitySearchModel = new SearchModel(true);
        dietOrderActivitySearchModel.addSearchCondition("diet_order_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, dietOrderId);
        dietOrderActivitySearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        dietOrderActivitySearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        List<DietOrderActivity> dietOrderActivities = DatabaseHelper.findAll(DietOrderActivity.class, dietOrderActivitySearchModel);

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

        ApiRest apiRest = new ApiRest(buildDietOrderInfo(dietOrder, dietOrderGroups, dietOrderDetailMap, dietOrderDetailGoodsAttributeMap, dietOrderActivities), "获取订单信息成功！");
        return apiRest;
    }

    /**
     * 构建订单信息
     *
     * @param dietOrder
     * @param dietOrderGroups
     * @param dietOrderDetailMap
     * @param dietOrderDetailGoodsAttributeMap
     * @return
     */
    private Map<String, Object> buildDietOrderInfo(DietOrder dietOrder, List<DietOrderGroup> dietOrderGroups, Map<BigInteger, List<DietOrderDetail>> dietOrderDetailMap, Map<BigInteger, List<DietOrderDetailGoodsAttribute>> dietOrderDetailGoodsAttributeMap, List<DietOrderActivity> dietOrderActivities) {
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
        dietOrderInfo.put("groups", buildGroups(dietOrderGroups, dietOrderDetailMap, dietOrderDetailGoodsAttributeMap));
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
    private List<Map<String, Object>> buildGroups(List<DietOrderGroup> dietOrderGroups, Map<BigInteger, List<DietOrderDetail>> dietOrderDetailMap, Map<BigInteger, List<DietOrderDetailGoodsAttribute>> dietOrderDetailGoodsAttributeMap) {
        List<Map<String, Object>> groups = new ArrayList<Map<String, Object>>();
        for (DietOrderGroup dietOrderGroup : dietOrderGroups) {
            Map<String, Object> group = new HashMap<String, Object>();
            group.put("id", dietOrderGroup.getId());
            group.put("name", dietOrderGroup.getName());
            group.put("type", dietOrderGroup.getType());
            group.put("details", buildDetails(dietOrderDetailMap.get(dietOrderGroup.getId()), dietOrderDetailGoodsAttributeMap));
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
    private List<Map<String, Object>> buildDetails(List<DietOrderDetail> dietOrderDetails, Map<BigInteger, List<DietOrderDetailGoodsAttribute>> dietOrderDetailGoodsAttributeMap) {
        List<Map<String, Object>> dietOrderDetailInfos = new ArrayList<Map<String, Object>>();
        for (DietOrderDetail dietOrderDetail : dietOrderDetails) {
            Map<String, Object> dietOrderDetailInfo = new HashMap<String, Object>();
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
        }
        return dietOrderDetailInfos;
    }

    /**
     * 保存订单信息
     *
     * @param saveDietOrderModel
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiRest saveDietOrder(SaveDietOrderModel saveDietOrderModel) {
        BigInteger tenantId = saveDietOrderModel.getTenantId();
        String tenantCode = saveDietOrderModel.getTenantCode();
        BigInteger branchId = saveDietOrderModel.getBranchId();
        BigInteger userId = saveDietOrderModel.getUserId();

        // 查询出门店信息
        Branch branch = DatabaseHelper.find(Branch.class, branchId);
        ValidateUtils.notNull(branch, "门店不存在！");

        List<BigInteger> goodsIds = new ArrayList<BigInteger>();
        List<BigInteger> goodsSpecificationIds = new ArrayList<BigInteger>();
        List<BigInteger> goodsAttributeGroupIds = new ArrayList<BigInteger>();
        List<BigInteger> goodsAttributeIds = new ArrayList<BigInteger>();
        List<SaveDietOrderModel.GoodsInfo> goodsInfos = saveDietOrderModel.getGoodsInfos();
        for (SaveDietOrderModel.GoodsInfo goodsInfo : goodsInfos) {
            goodsIds.add(goodsInfo.getGoodsId());
            goodsSpecificationIds.add(goodsInfo.getGoodsSpecificationId());

            List<SaveDietOrderModel.AttributeInfo> attributeInfos = goodsInfo.getAttributeInfos();
            if (CollectionUtils.isNotEmpty(attributeInfos)) {
                for (SaveDietOrderModel.AttributeInfo attributeInfo : attributeInfos) {
                    goodsAttributeGroupIds.add(attributeInfo.getAttributeGroupId());
                    goodsAttributeIds.add(attributeInfo.getAttributeId());
                }
            }
        }

        // 查询出订单中包含的所有商品
        List<Goods> goodses = goodsMapper.findAllGoodsInfos(tenantId, branchId, goodsIds);

        // 查询出订单中包含的所有商品规格
        SearchModel goodsSpecificationSearchModel = new SearchModel(true);
        goodsSpecificationSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        goodsSpecificationSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        goodsSpecificationSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_IN, goodsSpecificationIds);
        List<GoodsSpecification> goodsSpecifications = DatabaseHelper.findAll(GoodsSpecification.class, goodsSpecificationSearchModel);

        // 查询出订单中包含的所有口味组
        List<GoodsAttributeGroup> goodsAttributeGroups = new ArrayList<GoodsAttributeGroup>();
        if (CollectionUtils.isNotEmpty(goodsAttributeGroupIds)) {
            SearchModel goodsAttributeGroupSearchModel = new SearchModel(true);
            goodsAttributeGroupSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
            goodsAttributeGroupSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
            goodsAttributeGroupSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_IN, goodsAttributeGroupIds);
            goodsAttributeGroups = DatabaseHelper.findAll(GoodsAttributeGroup.class, goodsAttributeGroupSearchModel);
        }

        // 查询出订单中包含的所有口味
        List<GoodsAttribute> goodsAttributes = new ArrayList<GoodsAttribute>();
        if (CollectionUtils.isNotEmpty(goodsAttributeIds)) {
            SearchModel goodsAttributeSearchModel = new SearchModel(true);
            goodsAttributeSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
            goodsAttributeSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
            goodsAttributeSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_IN, goodsAttributeIds);
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
        Integer daySerialNumber = sequenceMapper.nextValue(SerialNumberGenerator.generatorTodaySequenceName(tenantId, branchId, "diet_order_number"));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        String orderNumber = orderNumberPrefix + simpleDateFormat.format(new Date()) + SerialNumberGenerator.nextSerialNumber(8, daySerialNumber);

        DietOrder.Builder builder = DietOrder.builder();
        builder.tenantId(tenantId).tenantCode(tenantCode).branchId(branchId).orderNumber(orderNumber).orderType(orderType).orderStatus(DietOrderConstants.ORDER_STATUS_PENDING).payStatus(DietOrderConstants.PAY_STATUS_UNPAID).refundStatus(DietOrderConstants.REFUND_STATUS_NO_REFUND).daySerialNumber(daySerialNumber.toString());

        boolean invoiced = saveDietOrderModel.getInvoiced();
        builder.invoiced(invoiced);
        if (invoiced) {
            builder.invoiceType(saveDietOrderModel.getInvoiceType()).invoice(saveDietOrderModel.getInvoice());
        }
        builder.createUserId(userId).lastUpdateUserId(userId).lastUpdateRemark("保存订单信息！");

        DietOrder dietOrder = builder.build();
        DatabaseHelper.insert(dietOrder);

        BigDecimal dietOrderTotalAmount = BigDecimal.ZERO;
        BigDecimal deliverFee = BigDecimal.TEN;
        BigDecimal packageFee = BigDecimal.TEN;
        BigInteger dietOrderId = dietOrder.getId();

        // 存放订单优惠金额
        BigDecimal dietOrderDiscountAmount = BigDecimal.ZERO;
        // 用来保存订单活动

        List<EffectiveActivity> effectiveActivities = activityMapper.callProcedureEffectiveActivity(tenantId, branchId);
        List<EffectiveActivity> fullReductionActivities = new ArrayList<EffectiveActivity>();
        List<EffectiveActivity> paymentActivities = new ArrayList<EffectiveActivity>();
        Map<String, EffectiveActivity> effectiveActivityMap = new HashMap<String, EffectiveActivity>();
        for (EffectiveActivity effectiveActivity : effectiveActivities) {
            int type = effectiveActivity.getType();
            if (type == 1 || type == 3) {
                effectiveActivityMap.put(effectiveActivity.getGoodsId() + "_" + effectiveActivity.getGoodsSpecificationId(), effectiveActivity);
            } else if (type == 2) {
                fullReductionActivities.add(effectiveActivity);
            } else if (type == 4) {
                paymentActivities.add(effectiveActivity);
            }
        }

        List<DietOrderDetail> dietOrderDetails = new ArrayList<DietOrderDetail>();
        Map<String, DietOrderDetail> dietOrderDetailMap = new HashMap<String, DietOrderDetail>();
        Map<String, List<DietOrderDetailGoodsAttribute>> dietOrderDetailGoodsAttributeMap = new HashMap<String, List<DietOrderDetailGoodsAttribute>>();
        Map<BigInteger, DietOrderActivity> dietOrderActivityMap = new HashMap<BigInteger, DietOrderActivity>();

        DietOrderGroup discountDietOrderGroup = null;
        DietOrderGroup normalDietOrderGroup = DietOrderGroup.builder().tenantId(tenantId).tenantCode(tenantCode).branchId(branchId).dietOrderId(dietOrderId).name("正常的菜品").type(DietOrderConstants.GROUP_TYPE_NORMAL).createUserId(userId).lastUpdateUserId(userId).lastUpdateRemark("保存订单分组信息！").build();
        DatabaseHelper.insert(normalDietOrderGroup);

        for (SaveDietOrderModel.GoodsInfo goodsInfo : goodsInfos) {

            Goods goods = goodsMap.get(goodsInfo.getGoodsId());
            ValidateUtils.notNull(goods, "商品不存在！");

            GoodsSpecification goodsSpecification = goodsSpecificationMap.get(goodsInfo.getGoodsSpecificationId());
            ValidateUtils.notNull(goodsSpecification, "商品规格不存在！");

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
                            .createUserId(userId)
                            .lastUpdateUserId(userId)
                            .build();
                    dietOrderDetailGoodsAttributes.add(dietOrderDetailGoodsAttribute);
                    dietOrderDetailGoodsAttributeMap.put(uuid, dietOrderDetailGoodsAttributes);
                }
            }

            BigDecimal quantity = goodsInfo.getQuantity();
            BigDecimal price = goodsSpecification.getPrice();

            DietOrderDetail dietOrderDetail = null;
            // 开始处理促销活动
            EffectiveActivity effectiveActivity = effectiveActivityMap.get(goods.getId() + "_" + goodsSpecification.getId());
            if (effectiveActivity != null) {
                int type = effectiveActivity.getType();
                // 买A赠B活动
                if (type == 1) {
                    if (discountDietOrderGroup == null) {
                        discountDietOrderGroup = DietOrderGroup.builder()
                                .tenantId(tenantId)
                                .tenantCode(tenantCode)
                                .branchId(branchId)
                                .dietOrderId(dietOrderId)
                                .name("赠送的菜品")
                                .type(DietOrderConstants.GROUP_TYPE_DISCOUNT)
                                .createUserId(userId)
                                .lastUpdateUserId(userId)
                                .lastUpdateRemark("保存订单分组信息！")
                                .build();
                        DatabaseHelper.insert(discountDietOrderGroup);
                    }

                    if (quantity.compareTo(effectiveActivity.getBuyQuantity()) >= 0) {
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
                                .goodsName(Constants.VARCHAR_DEFAULT_VALUE)
                                .goodsSpecificationId(effectiveActivity.getGoodsSpecificationId())
                                .goodsSpecificationName(effectiveActivity.getGoodsSpecificationName())
                                .categoryId(BigInteger.ZERO).categoryName("")
                                .price(BigDecimal.ZERO)
                                .attributeIncrease(BigDecimal.ZERO)
                                .quantity(effectiveActivity.getGiveQuantity())
                                .totalAmount(giveTotalAmount)
                                .discountAmount(giveTotalAmount)
                                .payableAmount(BigDecimal.ZERO)
                                .createUserId(userId)
                                .lastUpdateUserId(userId)
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
                                    .createUserId(userId)
                                    .lastUpdateUserId(userId)
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
                            .goodsId(goods.getId())
                            .goodsName(goods.getName())
                            .goodsSpecificationId(goodsSpecification.getId())
                            .goodsSpecificationName(goodsSpecification.getName())
                            .categoryId(goods.getCategoryId())
                            .categoryName(goods.getCategoryName())
                            .price(price)
                            .attributeIncrease(attributeIncrease)
                            .quantity(quantity)
                            .totalAmount(dietOrderDetailTotalAmount)
                            .discountAmount(BigDecimal.ZERO)
                            .payableAmount(dietOrderDetailTotalAmount)
                            .createUserId(userId)
                            .lastUpdateUserId(userId)
                            .build();
                }

                if (type == 3) {
                    Integer discountType = effectiveActivity.getDiscountType();
                    BigDecimal dietOrderDetailTotalAmount = price.add(attributeIncrease).multiply(quantity);
                    BigDecimal dietOrderDetailPayableAmount = null;
                    if (discountType == 1) {
                        dietOrderDetailPayableAmount = effectiveActivity.getSpecialPrice().add(attributeIncrease).multiply(quantity);
                    } else {
                        dietOrderDetailPayableAmount = price.subtract(price.multiply(effectiveActivity.getDiscountRate()).add(attributeIncrease).divide(Constants.BIG_DECIMAL_HUNDRED));
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
                            .goodsId(goods.getId())
                            .goodsName(goods.getName())
                            .goodsSpecificationId(goodsSpecification.getId())
                            .goodsSpecificationName(goodsSpecification.getName())
                            .categoryId(goods.getCategoryId())
                            .categoryName(goods.getCategoryName())
                            .price(price)
                            .attributeIncrease(attributeIncrease)
                            .quantity(quantity)
                            .totalAmount(dietOrderDetailTotalAmount)
                            .discountAmount(dietOrderDetailDiscountAmount)
                            .payableAmount(dietOrderDetailPayableAmount)
                            .createUserId(userId)
                            .lastUpdateUserId(userId)
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
                                .createUserId(userId)
                                .lastUpdateUserId(userId)
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
                        .goodsId(goods.getId())
                        .goodsName(goods.getName())
                        .goodsSpecificationId(goodsSpecification.getId())
                        .goodsSpecificationName(goodsSpecification.getName())
                        .categoryId(goods.getCategoryId())
                        .categoryName(goods.getCategoryName())
                        .price(price)
                        .attributeIncrease(attributeIncrease)
                        .quantity(quantity)
                        .totalAmount(dietOrderDetailTotalAmount)
                        .discountAmount(BigDecimal.ZERO)
                        .payableAmount(dietOrderDetailTotalAmount)
                        .createUserId(userId)
                        .lastUpdateUserId(userId)
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
                        .createUserId(userId)
                        .lastUpdateUserId(userId)
                        .lastUpdateRemark("保存订单分组信息！")
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
                    .createUserId(userId)
                    .lastUpdateUserId(userId)
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
                        .createUserId(userId)
                        .lastUpdateUserId(userId)
                        .lastUpdateRemark("保存订单分组信息！")
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
                    .createUserId(userId)
                    .lastUpdateUserId(userId)
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
                    amount = dietOrderPayableAmount.subtract(dietOrderPayableAmount.multiply(fullReductionActivity.getDiscountRate()).divide(Constants.BIG_DECIMAL_HUNDRED));
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
                        .createUserId(userId)
                        .lastUpdateUserId(userId)
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
        DatabaseHelper.update(dietOrder);

        return new ApiRest(dietOrder, "保存订单成功！");
    }

    @Transactional(rollbackFor = Exception.class)
    public ApiRest confirmOrder(ConfirmOrderModel confirmOrderModel) {
        BigInteger tenantId = confirmOrderModel.getTenantId();
        BigInteger branchId = confirmOrderModel.getBranchId();
        BigInteger orderId = confirmOrderModel.getOrderId();

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        searchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        searchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUAL, orderId);
        DietOrder dietOrder = DatabaseHelper.find(DietOrder.class, searchModel);
        ValidateUtils.notNull(dietOrder, "订单不存在！");
        ValidateUtils.isTrue(dietOrder.getOrderStatus() == DietOrderConstants.ORDER_STATUS_UNPROCESSED, "只有未处理的订单才能进行接单操作！");

        dietOrder.setOrderStatus(DietOrderConstants.ORDER_STATUS_VALID);
        DatabaseHelper.update(dietOrder);

        ApiRest apiRest = new ApiRest();
        apiRest.setMessage("接单成功！");
        apiRest.setSuccessful(true);
        return apiRest;
    }

    @Transactional(rollbackFor = Exception.class)
    public ApiRest cancelOrder(CancelOrderModel cancelOrderModel) {
        BigInteger tenantId = cancelOrderModel.getTenantId();
        BigInteger branchId = cancelOrderModel.getBranchId();
        BigInteger orderId = cancelOrderModel.getOrderId();

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        searchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        searchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUAL, orderId);
        DietOrder dietOrder = DatabaseHelper.find(DietOrder.class, searchModel);
        ValidateUtils.notNull(dietOrder, "订单不存在！");
        ValidateUtils.isTrue(dietOrder.getOrderStatus() == DietOrderConstants.ORDER_STATUS_UNPROCESSED, "只有未处理的订单才能进取消订单操作！");

        dietOrder.setOrderStatus(DietOrderConstants.ORDER_STATUS_INVALID);
        DatabaseHelper.update(dietOrder);

        ApiRest apiRest = new ApiRest();
        apiRest.setMessage("取消订单成功！");
        apiRest.setSuccessful(true);
        return apiRest;
    }
}
