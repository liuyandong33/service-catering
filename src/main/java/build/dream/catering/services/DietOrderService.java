package build.dream.catering.services;

import build.dream.catering.constants.Constants;
import build.dream.catering.mappers.ActivityMapper;
import build.dream.catering.mappers.SequenceMapper;
import build.dream.catering.models.dietorder.ObtainDietOrderInfoModel;
import build.dream.catering.models.dietorder.SaveDietOrderModel;
import build.dream.catering.utils.DatabaseHelper;
import build.dream.common.api.ApiRest;
import build.dream.common.constants.DietOrderConstants;
import build.dream.common.erp.catering.domains.*;
import build.dream.common.utils.SearchModel;
import build.dream.common.utils.SerialNumberGenerator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.Validate;
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
        Validate.notNull(dietOrder, "订单不存在！");

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
        SearchModel dietOrderDetailGoodsFlavorSearchModel = new SearchModel(true);
        dietOrderDetailGoodsFlavorSearchModel.addSearchCondition("diet_order_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, dietOrderId);
        dietOrderDetailGoodsFlavorSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        dietOrderDetailGoodsFlavorSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        List<DietOrderDetailGoodsFlavor> dietOrderDetailGoodsFlavors = DatabaseHelper.findAll(DietOrderDetailGoodsFlavor.class, dietOrderDetailGoodsFlavorSearchModel);

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
        Map<BigInteger, List<DietOrderDetailGoodsFlavor>> dietOrderDetailGoodsFlavorMap = new HashMap<BigInteger, List<DietOrderDetailGoodsFlavor>>();
        for (DietOrderDetailGoodsFlavor dietOrderDetailGoodsFlavor : dietOrderDetailGoodsFlavors) {
            List<DietOrderDetailGoodsFlavor> dietOrderDetailGoodsFlavorList = dietOrderDetailGoodsFlavorMap.get(dietOrderDetailGoodsFlavor.getDietOrderDetailId());
            if (dietOrderDetailGoodsFlavorList == null) {
                dietOrderDetailGoodsFlavorList = new ArrayList<DietOrderDetailGoodsFlavor>();
                dietOrderDetailGoodsFlavorMap.put(dietOrderDetailGoodsFlavor.getDietOrderDetailId(), dietOrderDetailGoodsFlavorList);
            }
            dietOrderDetailGoodsFlavorList.add(dietOrderDetailGoodsFlavor);
        }

        ApiRest apiRest = new ApiRest(buildDietOrderInfo(dietOrder, dietOrderGroups, dietOrderDetailMap, dietOrderDetailGoodsFlavorMap, dietOrderActivities), "获取订单信息成功！");
        return apiRest;
    }

    /**
     * 构建订单信息
     *
     * @param dietOrder
     * @param dietOrderGroups
     * @param dietOrderDetailMap
     * @param dietOrderDetailGoodsFlavorMap
     * @return
     */
    private Map<String, Object> buildDietOrderInfo(DietOrder dietOrder, List<DietOrderGroup> dietOrderGroups, Map<BigInteger, List<DietOrderDetail>> dietOrderDetailMap, Map<BigInteger, List<DietOrderDetailGoodsFlavor>> dietOrderDetailGoodsFlavorMap, List<DietOrderActivity> dietOrderActivities) {
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
        dietOrderInfo.put("groups", buildGroups(dietOrderGroups, dietOrderDetailMap, dietOrderDetailGoodsFlavorMap));
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
     * @param dietOrderDetailGoodsFlavorMap
     * @return
     */
    private List<Map<String, Object>> buildGroups(List<DietOrderGroup> dietOrderGroups, Map<BigInteger, List<DietOrderDetail>> dietOrderDetailMap, Map<BigInteger, List<DietOrderDetailGoodsFlavor>> dietOrderDetailGoodsFlavorMap) {
        List<Map<String, Object>> groups = new ArrayList<Map<String, Object>>();
        for (DietOrderGroup dietOrderGroup : dietOrderGroups) {
            Map<String, Object> group = new HashMap<String, Object>();
            group.put("id", dietOrderGroup.getId());
            group.put("name", dietOrderGroup.getName());
            group.put("type", dietOrderGroup.getType());
            group.put("details", buildDetails(dietOrderDetailMap.get(dietOrderGroup.getId()), dietOrderDetailGoodsFlavorMap));
            groups.add(group);
        }
        return groups;
    }

    /**
     * 构建订单详情信息
     *
     * @param dietOrderDetails
     * @param dietOrderDetailGoodsFlavorMap
     * @return
     */
    private List<Map<String, Object>> buildDetails(List<DietOrderDetail> dietOrderDetails, Map<BigInteger, List<DietOrderDetailGoodsFlavor>> dietOrderDetailGoodsFlavorMap) {
        List<Map<String, Object>> dietOrderDetailInfos = new ArrayList<Map<String, Object>>();
        for (DietOrderDetail dietOrderDetail : dietOrderDetails) {
            Map<String, Object> dietOrderDetailInfo = new HashMap<String, Object>();
            dietOrderDetailInfo.put("goodsId", dietOrderDetail.getGoodsId());
            dietOrderDetailInfo.put("goodsName", dietOrderDetail.getGoodsName());
            dietOrderDetailInfo.put("goodsSpecificationId", dietOrderDetail.getGoodsSpecificationId());
            dietOrderDetailInfo.put("goodsSpecificationName", dietOrderDetail.getGoodsSpecificationName());
            dietOrderDetailInfo.put("price", dietOrderDetail.getPrice());
            dietOrderDetailInfo.put("flavorIncrease", dietOrderDetail.getFlavorIncrease());
            dietOrderDetailInfo.put("quantity", dietOrderDetail.getQuantity());
            dietOrderDetailInfo.put("totalAmount", dietOrderDetail.getTotalAmount());
            dietOrderDetailInfo.put("discountAmount", dietOrderDetail.getDiscountAmount());
            dietOrderDetailInfo.put("payableAmount", dietOrderDetail.getPayableAmount());

            List<DietOrderDetailGoodsFlavor> dietOrderDetailGoodsFlavors = dietOrderDetailGoodsFlavorMap.get(dietOrderDetail.getId());
            if (CollectionUtils.isNotEmpty(dietOrderDetailGoodsFlavors)) {
                List<Map<String, Object>> flavors = new ArrayList<Map<String, Object>>();
                for (DietOrderDetailGoodsFlavor dietOrderDetailGoodsFlavor : dietOrderDetailGoodsFlavors) {
                    Map<String, Object> flavor = new HashMap<String, Object>();
                    flavor.put("flavorGroupId", dietOrderDetailGoodsFlavor.getGoodsFlavorGroupId());
                    flavor.put("flavorGroupName", dietOrderDetailGoodsFlavor.getGoodsFlavorGroupName());
                    flavor.put("flavorId", dietOrderDetailGoodsFlavor.getGoodsFlavorId());
                    flavor.put("flavorName", dietOrderDetailGoodsFlavor.getGoodsFlavorName());
                    flavor.put("price", dietOrderDetailGoodsFlavor.getPrice());
                    flavors.add(flavor);
                }
                dietOrderDetailInfo.put("flavors", flavors);
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

        List<BigInteger> goodsIds = new ArrayList<BigInteger>();
        List<BigInteger> goodsSpecificationIds = new ArrayList<BigInteger>();
        List<BigInteger> goodsFlavorGroupIds = new ArrayList<BigInteger>();
        List<BigInteger> goodsFlavorIds = new ArrayList<BigInteger>();
        List<SaveDietOrderModel.GoodsInfo> goodsInfos = saveDietOrderModel.getGoodsInfos();
        for (SaveDietOrderModel.GoodsInfo goodsInfo : goodsInfos) {
            goodsIds.add(goodsInfo.getGoodsId());
            goodsSpecificationIds.add(goodsInfo.getGoodsSpecificationId());

            List<SaveDietOrderModel.FlavorInfo> flavorInfos = goodsInfo.getFlavorInfos();
            if (CollectionUtils.isNotEmpty(flavorInfos)) {
                for (SaveDietOrderModel.FlavorInfo flavorInfo : flavorInfos) {
                    goodsFlavorGroupIds.add(flavorInfo.getFlavorGroupId());
                    goodsFlavorIds.add(flavorInfo.getFlavorId());
                }
            }
        }

        // 查询出订单中包含的所有商品
        SearchModel goodsSearchModel = new SearchModel(true);
        goodsSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        goodsSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        goodsSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_IN, goodsIds);
        List<Goods> goodses = DatabaseHelper.findAll(Goods.class, goodsSearchModel);

        // 查询出订单中包含的所有商品规格
        SearchModel goodsSpecificationSearchModel = new SearchModel(true);
        goodsSpecificationSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        goodsSpecificationSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        goodsSpecificationSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_IN, goodsSpecificationIds);
        List<GoodsSpecification> goodsSpecifications = DatabaseHelper.findAll(GoodsSpecification.class, goodsSpecificationSearchModel);

        // 查询出订单中包含的所有口味组
        List<GoodsFlavorGroup> goodsFlavorGroups = new ArrayList<GoodsFlavorGroup>();
        if (CollectionUtils.isNotEmpty(goodsFlavorGroupIds)) {
            SearchModel goodsFlavorGroupSearchModel = new SearchModel(true);
            goodsFlavorGroupSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
            goodsFlavorGroupSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
            goodsFlavorGroupSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_IN, goodsFlavorGroupIds);
            goodsFlavorGroups = DatabaseHelper.findAll(GoodsFlavorGroup.class, goodsFlavorGroupSearchModel);
        }

        // 查询出订单中包含的所有口味
        List<GoodsFlavor> goodsFlavors = new ArrayList<GoodsFlavor>();
        if (CollectionUtils.isNotEmpty(goodsFlavorIds)) {
            SearchModel goodsFlavorSearchModel = new SearchModel(true);
            goodsFlavorSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
            goodsFlavorSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
            goodsFlavorSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_IN, goodsFlavorIds);
            goodsFlavors = DatabaseHelper.findAll(GoodsFlavor.class, goodsFlavorSearchModel);
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
        Map<BigInteger, GoodsFlavorGroup> goodsFlavorGroupMap = new HashMap<BigInteger, GoodsFlavorGroup>();
        for (GoodsFlavorGroup goodsFlavorGroup : goodsFlavorGroups) {
            goodsFlavorGroupMap.put(goodsFlavorGroup.getId(), goodsFlavorGroup);
        }

        // 封装商品口味id与商品口味之间的map
        Map<BigInteger, GoodsFlavor> goodsFlavorMap = new HashMap<BigInteger, GoodsFlavor>();
        for (GoodsFlavor goodsFlavor : goodsFlavors) {
            goodsFlavorMap.put(goodsFlavor.getId(), goodsFlavor);
        }

        DietOrder dietOrder = new DietOrder();
        dietOrder.setTenantId(tenantId);
        dietOrder.setTenantCode(tenantCode);
        dietOrder.setBranchId(branchId);

        String orderNumberPrefix = null;
        Integer orderType = saveDietOrderModel.getOrderType();
        if (orderType == DietOrderConstants.ORDER_TYPE_SCAN_CODE_ORDER) {
            orderNumberPrefix = "SC";
        }
        Integer daySerialNumber = sequenceMapper.nextValue(SerialNumberGenerator.generatorTodaySequenceName(tenantId, branchId, "diet_order_number"));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        String orderNumber = orderNumberPrefix + simpleDateFormat.format(new Date()) + SerialNumberGenerator.nextSerialNumber(8, daySerialNumber);
        dietOrder.setOrderNumber(orderNumber);
        dietOrder.setOrderType(orderType);
        dietOrder.setOrderStatus(DietOrderConstants.ORDER_STATUS_PENDING);
        dietOrder.setPayStatus(DietOrderConstants.PAY_STATUS_UNPAID);
        dietOrder.setRefundStatus(DietOrderConstants.REFUND_STATUS_NO_REFUND);
        dietOrder.setDaySerialNumber(daySerialNumber.toString());

        boolean invoiced = saveDietOrderModel.getInvoiced();
        dietOrder.setInvoiced(invoiced);
        if (invoiced) {
            dietOrder.setInvoiceType(saveDietOrderModel.getInvoiceType());
            dietOrder.setInvoice(saveDietOrderModel.getInvoice());
        }
        dietOrder.setCreateUserId(userId);
        dietOrder.setLastUpdateUserId(userId);
        dietOrder.setLastUpdateRemark("保存订单信息！");
        DatabaseHelper.insert(dietOrder);

        BigDecimal dietOrderTotalAmount = BigDecimal.ZERO;
        BigInteger dietOrderId = dietOrder.getId();

        // 存放订单优惠金额
        BigDecimal dietOrderDiscountAmount = BigDecimal.ZERO;
        // 用来保存订单活动

        List<EffectiveActivity> effectiveActivities = activityMapper.callProcedureEffectiveActivity(tenantId, branchId);
        Map<String, EffectiveActivity> effectiveActivityMap = new HashMap<String, EffectiveActivity>();
        for (EffectiveActivity effectiveActivity : effectiveActivities) {
            int type = effectiveActivity.getType();
            if (type == 1 || type == 3) {
                effectiveActivityMap.put(effectiveActivity.getGoodsId() + "_" + effectiveActivity.getGoodsSpecificationId(), effectiveActivity);
            }
        }

        List<DietOrderDetail> dietOrderDetails = new ArrayList<DietOrderDetail>();
        Map<BigInteger, DietOrderActivity> dietOrderActivityMap = new HashMap<BigInteger, DietOrderActivity>();

        DietOrderGroup discountDietOrderGroup = null;
        DietOrderGroup normalDietOrderGroup = DietOrderGroup.builder().tenantId(tenantId).tenantCode(tenantCode).branchId(branchId).dietOrderId(dietOrderId).name("正常的菜品").type(Constants.NORMAL).createUserId(userId).lastUpdateUserId(userId).lastUpdateRemark("保存订单分组信息！").build();
        DatabaseHelper.insert(normalDietOrderGroup);

        for (SaveDietOrderModel.GoodsInfo goodsInfo : goodsInfos) {

            Goods goods = goodsMap.get(goodsInfo.getGoodsId());
            Validate.notNull(goods, "商品不存在！");

            GoodsSpecification goodsSpecification = goodsSpecificationMap.get(goodsInfo.getGoodsSpecificationId());
            Validate.notNull(goodsSpecification, "商品规格不存在！");

            BigDecimal flavorIncrease = BigDecimal.ZERO;
            List<SaveDietOrderModel.FlavorInfo> flavorInfos = goodsInfo.getFlavorInfos();
            List<DietOrderDetailGoodsFlavor> dietOrderDetailGoodsFlavors = new ArrayList<DietOrderDetailGoodsFlavor>();
            if (CollectionUtils.isNotEmpty(flavorInfos)) {
                for (SaveDietOrderModel.FlavorInfo flavorInfo : flavorInfos) {
                    GoodsFlavorGroup goodsFlavorGroup = goodsFlavorGroupMap.get(flavorInfo.getFlavorGroupId());
                    Validate.notNull(goodsFlavorGroup, "口味组不存在！");

                    GoodsFlavor goodsFlavor = goodsFlavorMap.get(flavorInfo.getFlavorId());
                    Validate.notNull(goodsFlavor, "口味不存在！");
                    flavorIncrease = flavorIncrease.add(goodsFlavor.getPrice());

                    DietOrderDetailGoodsFlavor dietOrderDetailGoodsFlavor = DietOrderDetailGoodsFlavor.builder().tenantId(tenantId).tenantCode(tenantCode).branchId(branchId).dietOrderId(dietOrderId).dietOrderGroupId(normalDietOrderGroup.getId()).dietOrderDetailId(null).goodsFlavorGroupId(goodsFlavorGroup.getId()).goodsFlavorGroupName(goodsFlavorGroup.getName()).goodsFlavorId(goodsFlavor.getId()).goodsFlavorName(goodsFlavor.getName()).price(goodsFlavor.getPrice()).createUserId(userId).lastUpdateUserId(userId).build();
                    dietOrderDetailGoodsFlavors.add(dietOrderDetailGoodsFlavor);
                }
            }

            BigDecimal quantity = goodsInfo.getQuantity();
            BigDecimal price = goodsSpecification.getPrice();

            // 开始处理促销活动
            EffectiveActivity effectiveActivity = effectiveActivityMap.get(goods.getId() + "_" + goodsSpecification.getId());
            if (effectiveActivity != null) {
                int type = effectiveActivity.getType();
                // 买A赠B活动
                if (type == 1) {
                    if (discountDietOrderGroup == null) {
                        discountDietOrderGroup = DietOrderGroup.builder().tenantId(tenantId).tenantCode(tenantCode).branchId(branchId).dietOrderId(dietOrderId).name("赠送的菜品").type(Constants.DISCOUNT).createUserId(userId).lastUpdateUserId(userId).lastUpdateRemark("保存订单分组信息！").build();
                        DatabaseHelper.insert(discountDietOrderGroup);
                    }

                    if (quantity.compareTo(effectiveActivity.getBuyQuantity()) >= 0) {
                        BigDecimal giveQuantity = quantity.divide(effectiveActivity.getBuyQuantity(), 0, BigDecimal.ROUND_DOWN).multiply(effectiveActivity.getGiveQuantity());
                        BigDecimal giveTotalAmount = giveQuantity.multiply(effectiveActivity.getSpecialPrice());
                        DietOrderDetail giveDietOrderDetail = DietOrderDetail.builder().tenantId(tenantId).tenantCode(tenantCode).branchId(branchId).dietOrderId(dietOrderId).dietOrderGroupId(discountDietOrderGroup.getId()).goodsType(1).goodsId(effectiveActivity.getGiveGoodsId()).goodsName("").goodsSpecificationId(effectiveActivity.getGoodsSpecificationId()).goodsSpecificationName("").categoryId(BigInteger.ZERO).categoryName("").price(BigDecimal.ZERO).flavorIncrease(BigDecimal.ZERO).quantity(effectiveActivity.getGiveQuantity()).totalAmount(giveTotalAmount).discountAmount(giveTotalAmount).payableAmount(BigDecimal.ZERO).paidAmount(BigDecimal.ZERO).createUserId(userId).lastUpdateUserId(userId).build();
                        dietOrderDetails.add(giveDietOrderDetail);

                        BigInteger activityId = effectiveActivity.getActivityId();
                        DietOrderActivity dietOrderActivity = dietOrderActivityMap.get(activityId);
                        if (dietOrderActivity == null) {
                            dietOrderActivity = new DietOrderActivity();
                            dietOrderActivity.setTenantId(tenantId);
                            dietOrderActivity.setTenantCode(tenantCode);
                            dietOrderActivity.setBranchId(branchId);
                            dietOrderActivity.setDietOrderId(dietOrderId);
                            dietOrderActivity.setActivityId(activityId);
                            dietOrderActivity.setActivityName(effectiveActivity.getName());
                            dietOrderActivity.setActivityType(effectiveActivity.getType());
                            dietOrderActivity.setAmount(giveDietOrderDetail.getDiscountAmount());
                            dietOrderActivity.setCreateUserId(userId);
                            dietOrderActivity.setLastUpdateUserId(userId);
                            dietOrderActivityMap.put(activityId, dietOrderActivity);
                        } else {
                            dietOrderActivity.setAmount(dietOrderActivity.getAmount().add(giveDietOrderDetail.getDiscountAmount()));
                        }
                    }
                }

                if (type == 3) {
                    Integer discountType = effectiveActivity.getDiscountType();
                    BigDecimal dietOrderDetailTotalAmount = price.add(flavorIncrease).multiply(quantity);
                    BigDecimal dietOrderDetailPayableAmount = null;
                    if (discountType == 1) {
                        dietOrderDetailPayableAmount = effectiveActivity.getSpecialPrice().add(flavorIncrease).multiply(quantity);
                    } else {
                        dietOrderDetailPayableAmount = price.subtract(price.multiply(effectiveActivity.getDiscountRate()).add(flavorIncrease).divide(Constants.BIG_DECIMAL_HUNDRED));
                    }
                    BigDecimal dietOrderDetailDiscountAmount = dietOrderDetailTotalAmount.subtract(dietOrderDetailPayableAmount);
                    dietOrderTotalAmount = dietOrderTotalAmount.add(dietOrderDetailTotalAmount);
                    dietOrderDiscountAmount = dietOrderDiscountAmount.add(dietOrderDetailDiscountAmount);
                    DietOrderDetail dietOrderDetail = DietOrderDetail.builder().tenantId(tenantId).tenantCode(tenantCode).branchId(branchId).dietOrderId(dietOrderId).dietOrderGroupId(normalDietOrderGroup.getId()).goodsType(goods.getType()).goodsId(goods.getId()).goodsName(goods.getName()).goodsSpecificationId(goodsSpecification.getId()).goodsSpecificationName(goodsSpecification.getName()).categoryId(goods.getCategoryId()).categoryName("").price(price).flavorIncrease(flavorIncrease).quantity(quantity).totalAmount(dietOrderDetailTotalAmount).discountAmount(dietOrderDetailDiscountAmount).payableAmount(dietOrderDetailPayableAmount).paidAmount(BigDecimal.ZERO).createUserId(userId).lastUpdateUserId(userId).build();
                    dietOrderDetails.add(dietOrderDetail);

                    BigInteger activityId = effectiveActivity.getActivityId();
                    DietOrderActivity dietOrderActivity = dietOrderActivityMap.get(activityId);
                    if (dietOrderActivity == null) {
                        dietOrderActivity = new DietOrderActivity();
                        dietOrderActivity.setTenantId(tenantId);
                        dietOrderActivity.setTenantCode(tenantCode);
                        dietOrderActivity.setBranchId(branchId);
                        dietOrderActivity.setDietOrderId(dietOrderId);
                        dietOrderActivity.setActivityId(activityId);
                        dietOrderActivity.setActivityName(effectiveActivity.getName());
                        dietOrderActivity.setActivityType(effectiveActivity.getType());
                        dietOrderActivity.setAmount(dietOrderDetailDiscountAmount);
                        dietOrderActivity.setCreateUserId(userId);
                        dietOrderActivity.setLastUpdateUserId(userId);
                        dietOrderActivityMap.put(activityId, dietOrderActivity);
                    } else {
                        dietOrderActivity.setAmount(dietOrderActivity.getAmount().add(dietOrderDetailDiscountAmount));
                    }
                }
            } else {
                BigDecimal dietOrderDetailTotalAmount = price.add(flavorIncrease).multiply(quantity);
                dietOrderTotalAmount = dietOrderTotalAmount.add(dietOrderDetailTotalAmount);
                DietOrderDetail dietOrderDetail = DietOrderDetail.builder().tenantId(tenantId).tenantCode(tenantCode).branchId(branchId).dietOrderId(dietOrderId).dietOrderGroupId(discountDietOrderGroup.getId()).goodsType(goods.getType()).goodsId(effectiveActivity.getGiveGoodsId()).goodsName("").goodsSpecificationId(effectiveActivity.getGoodsSpecificationId()).goodsSpecificationName(goodsSpecification.getName()).categoryId(goods.getCategoryId()).categoryName("").price(price).flavorIncrease(flavorIncrease).quantity(quantity).totalAmount(dietOrderDetailTotalAmount).discountAmount(BigDecimal.ZERO).payableAmount(dietOrderDetailTotalAmount).paidAmount(BigDecimal.ZERO).createUserId(userId).lastUpdateUserId(userId).build();
                dietOrderDetails.add(dietOrderDetail);
            }
        }

        DatabaseHelper.insertAll(dietOrderDetails);
        if (MapUtils.isNotEmpty(dietOrderActivityMap)) {
            List<DietOrderActivity> dietOrderActivities = new ArrayList<>(dietOrderActivityMap.values());
            DatabaseHelper.insertAll(dietOrderActivities);
        }

        dietOrder.setTotalAmount(dietOrderTotalAmount);
        dietOrder.setDiscountAmount(dietOrderDiscountAmount);
        dietOrder.setPayableAmount(dietOrderTotalAmount.subtract(dietOrderDiscountAmount));
        dietOrder.setPaidAmount(BigDecimal.ZERO);
        DatabaseHelper.update(dietOrder);

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("id", dietOrder.getId());
        data.put("orderNumber", dietOrder.getOrderNumber());
        return new ApiRest(data, "保存订单成功！");
    }
}
