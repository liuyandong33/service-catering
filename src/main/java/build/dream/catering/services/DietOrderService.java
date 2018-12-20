package build.dream.catering.services;

import build.dream.catering.constants.Constants;
import build.dream.catering.mappers.ActivityMapper;
import build.dream.catering.mappers.GoodsMapper;
import build.dream.catering.mappers.SequenceMapper;
import build.dream.catering.models.dietorder.*;
import build.dream.catering.utils.DietOrderUtils;
import build.dream.catering.utils.GoodsUtils;
import build.dream.catering.utils.SequenceUtils;
import build.dream.catering.utils.ThreadUtils;
import build.dream.common.api.ApiRest;
import build.dream.common.catering.domains.*;
import build.dream.common.constants.DietOrderConstants;
import build.dream.common.models.alipay.AlipayTradePagePayModel;
import build.dream.common.models.alipay.AlipayTradeWapPayModel;
import build.dream.common.models.aliyunpush.PushMessageToAndroidModel;
import build.dream.common.models.weixinpay.MicroPayModel;
import build.dream.common.models.weixinpay.UnifiedOrderModel;
import build.dream.common.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.ParseException;
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
        BigInteger tenantId = obtainDietOrderInfoModel.obtainTenantId();
        BigInteger branchId = obtainDietOrderInfoModel.obtainBranchId();
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

        Map<String, Object> dietOrderInfo = DietOrderUtils.buildDietOrderInfo(dietOrder, dietOrderGroups, dietOrderDetails, dietOrderDetailGoodsAttributes, dietOrderActivities);
        return ApiRest.builder().data(dietOrderInfo).message("获取订单信息成功！").successful(true).build();
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

        List<Map<String, Object>> optionalGroupInfos = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> requiredGroupInfos = new ArrayList<Map<String, Object>>();
        if (CollectionUtils.isNotEmpty(packageIds)) {
            optionalGroupInfos = goodsMapper.listPackageInfos(packageIds, 1);
            requiredGroupInfos = goodsMapper.listPackageInfos(packageIds, 2);
        }

        Map<String, Map<String, Object>> optionalGroupInfoMap = new HashMap<String, Map<String, Object>>();
        for (Map<String, Object> packageInfo : optionalGroupInfos) {
            String key = MapUtils.getString(packageInfo, "packageId") + "_" + MapUtils.getString(packageInfo, "packageGroupId") + "_" + MapUtils.getString(packageInfo, "goodsId") + "_" + MapUtils.getString(packageInfo, "goodsSpecificationId");
            optionalGroupInfoMap.put(key, packageInfo);
        }

        Map<BigInteger, List<Map<String, Object>>> requiredGroupInfoMap = new HashMap<BigInteger, List<Map<String, Object>>>();
        for (Map<String, Object> packageInfo : requiredGroupInfos) {
            BigInteger packageId = BigInteger.valueOf(MapUtils.getLongValue(packageInfo, "packageId"));
            List<Map<String, Object>> packageInfos = requiredGroupInfoMap.get(packageId);
            if (CollectionUtils.isEmpty(packageInfos)) {
                packageInfos = new ArrayList<Map<String, Object>>();
                requiredGroupInfoMap.put(packageId, packageInfos);
            }
            packageInfos.add(packageInfo);
        }

        // 查询出订单中包含的所有商品
        List<Goods> goodses = new ArrayList<Goods>();
        if (CollectionUtils.isNotEmpty(goodsIds)) {
            goodses = goodsMapper.findAllGoodsInfos(tenantId, branchId, goodsIds);
        }

        // 查询出订单中包含的所有商品规格
        List<GoodsSpecification> goodsSpecifications = new ArrayList<GoodsSpecification>();
        if (CollectionUtils.isNotEmpty(goodsSpecificationIds)) {
            SearchModel goodsSpecificationSearchModel = new SearchModel(true);
            goodsSpecificationSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
            goodsSpecificationSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
            goodsSpecificationSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_IN, goodsSpecificationIds);
            goodsSpecifications = DatabaseHelper.findAll(GoodsSpecification.class, goodsSpecificationSearchModel);
        }

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

        Integer daySerialNumber = SequenceUtils.nextValue(SerialNumberGenerator.generatorTodaySequenceName(tenantId, branchId, "diet_order_number"));
        String orderNumber = SerialNumberGenerator.nextOrderNumber(orderNumberPrefix, 8, daySerialNumber);

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
                        Map<String, Object> packageInfo = optionalGroupInfoMap.get(goodsId + "_" + info.getGroupId() + "_" + detail.getGoodsId() + "_" + detail.getGoodsSpecificationId());
                        ValidateUtils.notNull(packageInfo, "套餐明细不存在！");
                        DietOrderDetail dietOrderDetail = DietOrderDetail.builder()
                                .tenantId(tenantId)
                                .tenantCode(tenantCode)
                                .branchId(branchId)
                                .dietOrderId(dietOrderId)
                                .dietOrderGroupId(normalDietOrderGroup.getId())
                                .goodsType(Constants.GOODS_TYPE_PACKAGE_DETAIL)
                                .goodsId(BigInteger.valueOf(MapUtils.getLongValue(packageInfo, "goodsId")))
                                .goodsName(MapUtils.getString(packageInfo, "goodsName"))
                                .goodsSpecificationId(BigInteger.valueOf(MapUtils.getLong(packageInfo, "goodsSpecificationId")))
                                .goodsSpecificationName(MapUtils.getString(packageInfo, "goodsSpecificationName"))
                                .packageId(BigInteger.valueOf(MapUtils.getLongValue(packageInfo, "packageId")))
                                .packageGroupId(BigInteger.valueOf(MapUtils.getLongValue(packageInfo, "packageGroupId")))
                                .packageGroupName(MapUtils.getString(packageInfo, "packageGroupName"))
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
                List<Map<String, Object>> packageInfos = requiredGroupInfoMap.get(goodsId);
                for (Map<String, Object> packageInfo : packageInfos) {
                    DietOrderDetail dietOrderDetail = DietOrderDetail.builder()
                            .tenantId(tenantId)
                            .tenantCode(tenantCode)
                            .branchId(branchId)
                            .dietOrderId(dietOrderId)
                            .dietOrderGroupId(normalDietOrderGroup.getId())
                            .goodsType(Constants.GOODS_TYPE_PACKAGE_DETAIL)
                            .goodsId(BigInteger.valueOf(MapUtils.getLongValue(packageInfo, "goodsId")))
                            .goodsName(MapUtils.getString(packageInfo, "goodsName"))
                            .goodsSpecificationId(BigInteger.valueOf(MapUtils.getLong(packageInfo, "goodsSpecificationId")))
                            .goodsSpecificationName(MapUtils.getString(packageInfo, "goodsSpecificationName"))
                            .packageId(BigInteger.valueOf(MapUtils.getLongValue(packageInfo, "packageId")))
                            .packageGroupId(BigInteger.valueOf(MapUtils.getLongValue(packageInfo, "packageGroupId")))
                            .packageGroupName(MapUtils.getString(packageInfo, "packageGroupName"))
                            .categoryId(goods.getCategoryId())
                            .categoryName(goods.getCategoryName())
                            .price(price)
                            .attributeIncrease(BigDecimal.ZERO)
                            .quantity(quantity.multiply(BigDecimal.valueOf(MapUtils.getDoubleValue(packageInfo, "quantity"))))
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
                if (type == 1) {
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

                if (type == 3) {
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
        DatabaseHelper.update(dietOrder);

        return ApiRest.builder().data(dietOrder).message("保存订单成功！").successful(true).build();
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

        return ApiRest.builder().message("接单成功！").successful(true).build();
    }

    @Transactional(rollbackFor = Exception.class)
    public ApiRest cancelOrder(CancelOrderModel cancelOrderModel) throws IOException, DocumentException {
        BigInteger tenantId = cancelOrderModel.getTenantId();
        BigInteger branchId = cancelOrderModel.getBranchId();
        BigInteger orderId = cancelOrderModel.getOrderId();

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        searchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        searchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUAL, orderId);
        DietOrder dietOrder = DatabaseHelper.find(DietOrder.class, searchModel);
        ValidateUtils.notNull(dietOrder, "订单不存在！");

        DietOrderUtils.recoveryStock(dietOrder);
        DietOrderUtils.refund(dietOrder);

        dietOrder.setOrderStatus(DietOrderConstants.ORDER_STATUS_INVALID);
        DatabaseHelper.update(dietOrder);

        return ApiRest.builder().message("取消订单成功").successful(true).build();
    }

    @Transactional(readOnly = true)
    public ApiRest doPay(DoPayModel doPayModel) throws IOException, DocumentException {
        BigInteger tenantId = doPayModel.getTenantId();
        BigInteger branchId = doPayModel.getBranchId();
        BigInteger dietOrderId = doPayModel.getDietOrderId();
        Integer paidScene = doPayModel.getPaidScene();
        String authCode = doPayModel.getAuthCode();
        String openId = doPayModel.getOpenId();
        String subOpenId = doPayModel.getSubOpenId();
        String userId = doPayModel.getUserId();

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        searchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        searchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUAL, dietOrderId);
        DietOrder dietOrder = DatabaseHelper.find(DietOrder.class, searchModel);
        ValidateUtils.notNull(dietOrder, "订单不存在！");
        ValidateUtils.isTrue(dietOrder.getOrderStatus() == DietOrderConstants.ORDER_STATUS_PENDING, "订单状态异常！");

        String orderNumber = dietOrder.getOrderNumber();
        BigDecimal payableAmount = dietOrder.getPayableAmount();
        String partitionCode = ConfigurationUtils.getConfiguration(Constants.PARTITION_CODE);

        Object result = null;
        if (ArrayUtils.contains(Constants.WEI_XIN_PAID_SCENES, paidScene)) {
            int totalFee = payableAmount.multiply(Constants.BIG_DECIMAL_ONE_HUNDRED).intValue();
            String spbillCreateIp = ApplicationHandler.getRemoteAddress();
            if (paidScene == Constants.PAID_SCENE_WEI_XIN_MICROPAY) {
                MicroPayModel microPayModel = new MicroPayModel();
                microPayModel.setSignType(Constants.MD5);
                microPayModel.setBody("订单支付");
                microPayModel.setOutTradeNo(orderNumber);
                microPayModel.setTotalFee(totalFee);
                microPayModel.setSpbillCreateIp(spbillCreateIp);
                microPayModel.setAuthCode(authCode);
                result = WeiXinPayUtils.microPay(tenantId.toString(), branchId.toString(), microPayModel);
            } else {
                String notifyUrl = CommonUtils.getUrl(partitionCode, Constants.SERVICE_NAME_CATERING, "dietOrder", "weiXinPayCallback");
                UnifiedOrderModel unifiedOrderModel = new UnifiedOrderModel();
                unifiedOrderModel.setSignType(Constants.MD5);
                unifiedOrderModel.setBody("订单支付");
                unifiedOrderModel.setOutTradeNo(orderNumber);
                unifiedOrderModel.setTotalFee(totalFee);
                unifiedOrderModel.setSpbillCreateIp(spbillCreateIp);
                unifiedOrderModel.setNotifyUrl(notifyUrl);
                if (StringUtils.isNotBlank(openId)) {
                    unifiedOrderModel.setOpenId(openId);
                }
                if (StringUtils.isNotBlank(subOpenId)) {
                    unifiedOrderModel.setSubOpenId(subOpenId);
                }
                if (paidScene == Constants.PAID_SCENE_WEI_XIN_JSAPI_PUBLIC_ACCOUNT) {
                    unifiedOrderModel.setTradeType(Constants.WEI_XIN_PAY_TRADE_TYPE_JSAPI);
                } else if (paidScene == Constants.PAID_SCENE_WEI_XIN_NATIVE) {
                    unifiedOrderModel.setTradeType(Constants.WEI_XIN_PAY_TRADE_TYPE_NATIVE);
                } else if (paidScene == Constants.PAID_SCENE_WEI_XIN_APP) {
                    unifiedOrderModel.setTradeType(Constants.WEI_XIN_PAY_TRADE_TYPE_APP);
                } else if (paidScene == Constants.PAID_SCENE_WEI_XIN_MWEB) {
                    unifiedOrderModel.setTradeType(Constants.WEI_XIN_PAY_TRADE_TYPE_MWEB);
                } else if (paidScene == Constants.PAID_SCENE_WEI_XIN_JSAPI_MINI_PROGRAM) {
                    unifiedOrderModel.setTradeType(Constants.WEI_XIN_PAY_TRADE_TYPE_MINI_PROGRAM);
                }
                result = WeiXinPayUtils.unifiedOrder(tenantId.toString(), branchId.toString(), unifiedOrderModel);
            }
        } else if (paidScene == Constants.PAID_SCENE_ALIPAY_MOBILE_WEBSITE) {
            String returnUrl = "";
            String notifyUrl = "";

            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            AlipayTradeWapPayModel alipayTradeWapPayModel = new AlipayTradeWapPayModel();
            alipayTradeWapPayModel.setSubject("订单支付");
            alipayTradeWapPayModel.setOutTradeNo(orderNumber);
            alipayTradeWapPayModel.setTotalAmount(decimalFormat.format(dietOrder.getPayableAmount()));
            alipayTradeWapPayModel.setProductCode("");
            Map<String, Object> passBackParams = new HashMap<String, Object>();
            passBackParams.put("paidScene", paidScene);
            alipayTradeWapPayModel.setPassbackParams(URLEncoder.encode(GsonUtils.toJson(passBackParams), Constants.CHARSET_NAME_UTF_8));

            result = AlipayUtils.alipayTradeWapPay(tenantId.toString(), branchId.toString(), returnUrl, notifyUrl, alipayTradeWapPayModel);
        } else if (paidScene == Constants.PAID_SCENE_ALIPAY_PC_WEBSITE) {
            String returnUrl = "";
            String notifyUrl = "";

            AlipayTradePagePayModel alipayTradePagePayModel = new AlipayTradePagePayModel();
            Map<String, Object> passBackParams = new HashMap<String, Object>();
            passBackParams.put("paidScene", paidScene);
            alipayTradePagePayModel.setPassbackParams(URLEncoder.encode(GsonUtils.toJson(passBackParams), Constants.CHARSET_NAME_UTF_8));

            result = AlipayUtils.alipayTradePagePay(tenantId.toString(), branchId.toString(), returnUrl, notifyUrl, alipayTradePagePayModel);
        } else if (paidScene == Constants.PAID_SCENE_ALIPAY_APP) {
            String notifyUrl = "";
            String appAuthToken = "";
        }

        return ApiRest.builder().data(result).message("发起支付成功！").successful(true).build();
    }

    @Transactional(rollbackFor = Exception.class)
    public void handleCallback(Map<String, String> parameters, String paymentCode) throws ParseException {
        String orderNumber = null;
        Date occurrenceTime = null;
        BigDecimal totalAmount = null;
        if (Constants.PAYMENT_CODE_ALIPAY.equals(paymentCode)) {
            orderNumber = parameters.get("out_trade_no");
            occurrenceTime = new SimpleDateFormat(Constants.DEFAULT_DATE_PATTERN).parse(parameters.get("gmt_payment"));
            totalAmount = BigDecimal.valueOf(Double.valueOf(parameters.get("total_amount")));
        } else if (Constants.PAYMENT_CODE_WX.equals(paymentCode)) {
            orderNumber = "";
        }

        SearchModel dietOrderSearchModel = new SearchModel(true);
        dietOrderSearchModel.addSearchCondition("order_number", Constants.SQL_OPERATION_SYMBOL_EQUAL, orderNumber);
        DietOrder dietOrder = DatabaseHelper.find(DietOrder.class, dietOrderSearchModel);
        Validate.notNull(dietOrder, "订单不存在！");
        if (dietOrder.getOrderStatus() == DietOrderConstants.PAY_STATUS_PAID) {
            return;
        }

        BigInteger tenantId = dietOrder.getTenantId();
        String tenantCode = dietOrder.getTenantCode();
        BigInteger branchId = dietOrder.getBranchId();

        SearchModel paymentSearchModel = new SearchModel(true);
        paymentSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        paymentSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        paymentSearchModel.addSearchCondition("code", Constants.SQL_OPERATION_SYMBOL_EQUAL, paymentCode);
        Payment payment = DatabaseHelper.find(Payment.class, paymentSearchModel);

        DietOrderPayment dietOrderPayment = DietOrderPayment.builder()
                .tenantId(tenantId)
                .tenantCode(tenantCode)
                .branchId(branchId)
                .dietOrderId(dietOrder.getId())
                .paymentId(payment.getId())
                .paymentCode(payment.getCode())
                .paymentName(payment.getName())
                .occurrenceTime(occurrenceTime)
                .extraInfo(GsonUtils.toJson(parameters))
                .build();
        DatabaseHelper.insert(dietOrderPayment);

        dietOrder.setPaidAmount(dietOrder.getPaidAmount().add(totalAmount));
        dietOrder.setPayStatus(DietOrderConstants.PAY_STATUS_PAID);
        dietOrder.setOrderStatus(DietOrderConstants.ORDER_STATUS_UNPROCESSED);
        dietOrder.setActiveTime(occurrenceTime);

        BigInteger userId = CommonUtils.getServiceSystemUserId();
        dietOrder.setUpdatedUserId(userId);
        DatabaseHelper.update(dietOrder);
    }

    /**
     * 获取POS订单
     *
     * @param obtainPosOrderModel
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiRest obtainPosOrder(ObtainPosOrderModel obtainPosOrderModel) throws IOException {
        BigInteger tenantId = obtainPosOrderModel.getTenantId();
        BigInteger branchId = obtainPosOrderModel.getBranchId();
        String tableCode = obtainPosOrderModel.getTableCode();
        BigInteger vipId = obtainPosOrderModel.getVipId();
        PushMessageToAndroidModel pushMessageToAndroidModel = new PushMessageToAndroidModel();
        pushMessageToAndroidModel.setAppKey("");
        pushMessageToAndroidModel.setTarget(AliyunPushUtils.TAG);
        pushMessageToAndroidModel.setTargetValue("POS" + tenantId + "_" + branchId);
        pushMessageToAndroidModel.setTitle("获取POS订单");

        Map<String, Object> body = new HashMap<String, Object>();
        body.put("code", "");

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("tableCode", tableCode);

        String uuid = UUID.randomUUID().toString();
        map.put("uuid", uuid);

        body.put("data", map);

        pushMessageToAndroidModel.setBody(GsonUtils.toJson(body));
        Map<String, Object> result = AliyunPushUtils.pushMessageToAndroid(pushMessageToAndroidModel);

        String dataJson = null;
        int times = 0;
        while (times < 120) {
            times += 1;
            dataJson = CacheUtils.get(uuid);
            if (StringUtils.isNotBlank(dataJson)) {
                break;
            }
            ThreadUtils.sleepSafe(500);
        }

        ValidateUtils.notBlank(dataJson, "POS端未响应");

        Map<String, Object> dataMap = JacksonUtils.readValueAsMap(dataJson, String.class, Object.class);

        /*String platformPrivateKey = ConfigurationUtils.getConfiguration(Constants.PLATFORM_PRIVATE_KEY);
        PrivateKey privateKey = RSAUtils.restorePrivateKey(platformPrivateKey);
        String encryptedData = Base64.encodeBase64String(RSAUtils.encryptByPrivateKey(dataJson.getBytes(Constants.CHARSET_NAME_UTF_8), privateKey, PADDING_MODE_RSA_ECB_PKCS1PADDING));
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("order", dataMap.get("order"));
        data.put("encryptedData", encryptedData);*/

        String order = MapUtils.getString(dataMap, "order");
        String orderGroups = MapUtils.getString(dataMap, "orderGroups");
        String orderDetails = MapUtils.getString(dataMap, "orderDetails");
        String orderDetailGoodsAttributes = MapUtils.getString(dataMap, "orderDetailGoodsAttributes");

        DietOrder dietOrder = JacksonUtils.readValue(order, DietOrder.class);
        List<DietOrderGroup> dietOrderGroups = JacksonUtils.readValueAsList(orderGroups, DietOrderGroup.class);
        List<DietOrderDetail> dietOrderDetails = JacksonUtils.readValueAsList(orderDetails, DietOrderDetail.class);
        DatabaseHelper.insert(dietOrder);

        BigInteger dietOrderId = dietOrder.getId();

        Map<String, DietOrderGroup> dietOrderGroupMap = new HashMap<String, DietOrderGroup>();
        for (DietOrderGroup dietOrderGroup : dietOrderGroups) {
            dietOrderGroupMap.put(dietOrder.getLocalId(), dietOrderGroup);
            dietOrderGroup.setDietOrderId(dietOrderId);
        }

        DatabaseHelper.insertAll(dietOrderGroups);

        Map<String, DietOrderDetail> dietOrderDetailMap = new HashMap<String, DietOrderDetail>();
        for (DietOrderDetail dietOrderDetail : dietOrderDetails) {
            DietOrderGroup dietOrderGroup = dietOrderGroupMap.get(dietOrderDetail.getLocalDietOrderGroupId());
            dietOrderDetail.setDietOrderId(dietOrderId);
            dietOrderDetail.setDietOrderGroupId(dietOrderGroup.getId());
            dietOrderDetailMap.put(dietOrderDetail.getLocalId(), dietOrderDetail);
        }

        DatabaseHelper.insertAll(dietOrderDetails);

        if (StringUtils.isNotBlank(orderDetailGoodsAttributes)) {
            List<DietOrderDetailGoodsAttribute> dietOrderDetailGoodsAttributes = JacksonUtils.readValueAsList(orderDetailGoodsAttributes, DietOrderDetailGoodsAttribute.class);
            for (DietOrderDetailGoodsAttribute dietOrderDetailGoodsAttribute : dietOrderDetailGoodsAttributes) {
                DietOrderGroup dietOrderGroup = dietOrderGroupMap.get(dietOrderDetailGoodsAttribute.getLocalDietOrderGroupId());
                DietOrderDetail dietOrderDetail = dietOrderDetailMap.get(dietOrderDetailGoodsAttribute.getLocalDietOrderDetailId());

                dietOrderDetailGoodsAttribute.setDietOrderId(dietOrderId);
                dietOrderDetailGoodsAttribute.setDietOrderGroupId(dietOrderGroup.getId());
                dietOrderDetailGoodsAttribute.setDietOrderDetailId(dietOrderDetail.getId());
            }
            DatabaseHelper.insertAll(dietOrderDetailGoodsAttributes);
        }

        return ApiRest.builder().data(dataMap).message("获取POS订单成功！").successful(true).build();
    }
}
