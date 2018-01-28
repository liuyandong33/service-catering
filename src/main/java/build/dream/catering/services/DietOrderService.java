package build.dream.catering.services;

import build.dream.catering.beans.BuyGiveActivityBean;
import build.dream.catering.beans.FullReductionActivityBean;
import build.dream.catering.constants.Constants;
import build.dream.catering.mappers.*;
import build.dream.catering.models.dietorder.ObtainDietOrderInfoModel;
import build.dream.catering.models.dietorder.SaveDietOrderModel;
import build.dream.common.api.ApiRest;
import build.dream.common.constants.DietOrderConstants;
import build.dream.common.erp.catering.domains.*;
import build.dream.common.utils.CacheUtils;
import build.dream.common.utils.GsonUtils;
import build.dream.common.utils.SearchModel;
import build.dream.common.utils.SerialNumberGenerator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
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
    private DietOrderMapper dietOrderMapper;
    @Autowired
    private DietOrderDetailMapper dietOrderDetailMapper;
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private GoodsSpecificationMapper goodsSpecificationMapper;
    @Autowired
    private GoodsFlavorMapper goodsFlavorMapper;
    @Autowired
    private SequenceMapper sequenceMapper;
    @Autowired
    private GoodsFlavorGroupMapper goodsFlavorGroupMapper;
    @Autowired
    private DietOrderGroupMapper dietOrderGroupMapper;
    @Autowired
    private DietOrderDetailGoodsFlavorMapper dietOrderDetailGoodsFlavorMapper;
    @Autowired
    private DietOrderActivityMapper dietOrderActivityMapper;

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
        dietOrderSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUALS, dietOrderId);
        dietOrderSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        dietOrderSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        DietOrder dietOrder = dietOrderMapper.find(dietOrderSearchModel);
        Validate.notNull(dietOrder, "订单不存在！");

        // 查询出订单组信息
        SearchModel dietOrderGroupSearchModel = new SearchModel(true);
        dietOrderGroupSearchModel.addSearchCondition("diet_order_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, dietOrderId);
        dietOrderGroupSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        dietOrderGroupSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        List<DietOrderGroup> dietOrderGroups = dietOrderGroupMapper.findAll(dietOrderGroupSearchModel);

        // 查询出订单详情信息
        SearchModel dietOrderDetailSearchModel = new SearchModel(true);
        dietOrderDetailSearchModel.addSearchCondition("diet_order_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, dietOrderId);
        dietOrderDetailSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        dietOrderDetailSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        List<DietOrderDetail> dietOrderDetails = dietOrderDetailMapper.findAll(dietOrderDetailSearchModel);

        // 查询出订单口味信息
        SearchModel dietOrderDetailGoodsFlavorSearchModel = new SearchModel(true);
        dietOrderDetailGoodsFlavorSearchModel.addSearchCondition("diet_order_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, dietOrderId);
        dietOrderDetailGoodsFlavorSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        dietOrderDetailGoodsFlavorSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        List<DietOrderDetailGoodsFlavor> dietOrderDetailGoodsFlavors = dietOrderDetailGoodsFlavorMapper.findAll(dietOrderDetailGoodsFlavorSearchModel);

        SearchModel dietOrderActivitySearchModel = new SearchModel(true);
        dietOrderActivitySearchModel.addSearchCondition("diet_order_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, dietOrderId);
        dietOrderActivitySearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        dietOrderActivitySearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        List<DietOrderActivity> dietOrderActivities = dietOrderActivityMapper.findAll(dietOrderActivitySearchModel);

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
        List<SaveDietOrderModel.GroupInfo> groupInfos = saveDietOrderModel.getGroupInfos();
        for (SaveDietOrderModel.GroupInfo groupInfo : groupInfos) {
            List<SaveDietOrderModel.DetailInfo> detailInfos = groupInfo.getDetailInfos();
            for (SaveDietOrderModel.DetailInfo detailInfo : detailInfos) {
                goodsIds.add(detailInfo.getGoodsId());
                goodsSpecificationIds.add(detailInfo.getGoodsSpecificationId());
                List<SaveDietOrderModel.FlavorInfo> flavorInfos = detailInfo.getFlavorInfos();
                if (CollectionUtils.isNotEmpty(flavorInfos)) {
                    for (SaveDietOrderModel.FlavorInfo flavorInfo : flavorInfos) {
                        goodsFlavorGroupIds.add(flavorInfo.getFlavorGroupId());
                        goodsFlavorIds.add(flavorInfo.getFlavorId());
                    }
                }
            }
        }

        // 查询出订单中包含的所有商品
        SearchModel goodsSearchModel = new SearchModel(true);
        goodsSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        goodsSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        goodsSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_IN, goodsIds);
        List<Goods> goodses = goodsMapper.findAll(goodsSearchModel);

        // 查询出订单中包含的所有商品规格
        SearchModel goodsSpecificationSearchModel = new SearchModel(true);
        goodsSpecificationSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        goodsSpecificationSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        goodsSpecificationSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_IN, goodsSpecificationIds);
        List<GoodsSpecification> goodsSpecifications = goodsSpecificationMapper.findAll(goodsSpecificationSearchModel);

        // 查询出订单中包含的所有口味组
        List<GoodsFlavorGroup> goodsFlavorGroups = new ArrayList<GoodsFlavorGroup>();
        if (CollectionUtils.isNotEmpty(goodsFlavorGroupIds)) {
            SearchModel goodsFlavorGroupSearchModel = new SearchModel(true);
            goodsFlavorGroupSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
            goodsFlavorGroupSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
            goodsFlavorGroupSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_IN, goodsFlavorGroupIds);
            goodsFlavorGroups = goodsFlavorGroupMapper.findAll(goodsFlavorGroupSearchModel);
        }

        // 查询出订单中包含的所有口味
        List<GoodsFlavor> goodsFlavors = new ArrayList<GoodsFlavor>();
        if (CollectionUtils.isNotEmpty(goodsFlavorIds)) {
            SearchModel goodsFlavorSearchModel = new SearchModel(true);
            goodsFlavorSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
            goodsFlavorSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
            goodsFlavorSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_IN, goodsFlavorIds);
            goodsFlavors = goodsFlavorMapper.findAll(goodsFlavorSearchModel);
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
        dietOrder.setCreateUserId(userId);
        dietOrder.setLastUpdateUserId(userId);
        dietOrder.setLastUpdateRemark("保存订单信息！");
        dietOrderMapper.insert(dietOrder);

        BigDecimal dietOrderTotalAmount = BigDecimal.ZERO;
        BigInteger dietOrderId = dietOrder.getId();

        // 存放赠品订单详情
        List<DietOrderDetail> giveDietOrderDetails = new ArrayList<DietOrderDetail>();
        // 用来保存订单活动
        Map<BigInteger, DietOrderActivity> dietOrderActivityMap = new HashMap<BigInteger, DietOrderActivity>();

        for (SaveDietOrderModel.GroupInfo groupInfo : groupInfos) {
            DietOrderGroup dietOrderGroup = new DietOrderGroup();
            dietOrderGroup.setTenantId(tenantId);
            dietOrderGroup.setTenantCode(tenantCode);
            dietOrderGroup.setBranchId(branchId);
            dietOrderGroup.setDietOrderId(dietOrderId);
            dietOrderGroup.setName(groupInfo.getName());
            dietOrderGroup.setType(groupInfo.getType());
            dietOrderGroup.setCreateUserId(userId);
            dietOrderGroup.setLastUpdateUserId(userId);
            dietOrderGroup.setLastUpdateRemark("保存订单分组信息！");
            dietOrderGroupMapper.insert(dietOrderGroup);

            List<SaveDietOrderModel.DetailInfo> detailInfos = groupInfo.getDetailInfos();
            for (SaveDietOrderModel.DetailInfo detailInfo : detailInfos) {
                Goods goods = goodsMap.get(detailInfo.getGoodsId());
                Validate.notNull(goods, "商品不存在！");

                GoodsSpecification goodsSpecification = goodsSpecificationMap.get(detailInfo.getGoodsSpecificationId());
                Validate.notNull(goodsSpecification, "商品规格不存在！");

                DietOrderDetail dietOrderDetail = new DietOrderDetail();
                dietOrderDetail.setTenantId(tenantId);
                dietOrderDetail.setTenantCode(tenantCode);
                dietOrderDetail.setBranchId(branchId);
                dietOrderDetail.setDietOrderId(dietOrderId);
                dietOrderDetail.setDietOrderGroupId(dietOrderGroup.getId());
                dietOrderDetail.setGoodsId(goods.getId());
                dietOrderDetail.setGoodsName(goods.getName());
                dietOrderDetail.setGoodsSpecificationId(goodsSpecification.getId());
                dietOrderDetail.setGoodsSpecificationName(goodsSpecification.getName());
                dietOrderDetail.setPrice(goodsSpecification.getPrice());
                dietOrderDetail.setCreateUserId(userId);
                dietOrderDetail.setLastUpdateUserId(userId);
                dietOrderDetail.setLastUpdateRemark("保存订单详情信息！");

                BigDecimal flavorIncrease = BigDecimal.ZERO;
                List<SaveDietOrderModel.FlavorInfo> flavorInfos = detailInfo.getFlavorInfos();
                List<DietOrderDetailGoodsFlavor> dietOrderDetailGoodsFlavors = new ArrayList<DietOrderDetailGoodsFlavor>();
                if (CollectionUtils.isNotEmpty(flavorInfos)) {
                    for (SaveDietOrderModel.FlavorInfo flavorInfo : flavorInfos) {
                        GoodsFlavorGroup goodsFlavorGroup = goodsFlavorGroupMap.get(flavorInfo.getFlavorGroupId());
                        Validate.notNull(goodsFlavorGroup, "口味组不存在！");

                        GoodsFlavor goodsFlavor = goodsFlavorMap.get(flavorInfo.getFlavorId());
                        Validate.notNull(goodsFlavor, "口味不存在！");
                        flavorIncrease = flavorIncrease.add(goodsFlavor.getPrice());

                        DietOrderDetailGoodsFlavor dietOrderDetailGoodsFlavor = new DietOrderDetailGoodsFlavor();
                        dietOrderDetailGoodsFlavor.setTenantId(tenantId);
                        dietOrderDetailGoodsFlavor.setTenantCode(tenantCode);
                        dietOrderDetailGoodsFlavor.setBranchId(branchId);
                        dietOrderDetailGoodsFlavor.setDietOrderId(dietOrderId);
                        dietOrderDetailGoodsFlavor.setDietOrderGroupId(dietOrderGroup.getId());
                        dietOrderDetailGoodsFlavor.setGoodsFlavorGroupId(goodsFlavorGroup.getId());
                        dietOrderDetailGoodsFlavor.setGoodsFlavorGroupName(goodsFlavorGroup.getName());
                        dietOrderDetailGoodsFlavor.setGoodsFlavorId(goodsFlavor.getId());
                        dietOrderDetailGoodsFlavor.setGoodsFlavorName(goodsFlavor.getName());
                        dietOrderDetailGoodsFlavor.setPrice(goodsFlavor.getPrice());
                        dietOrderDetailGoodsFlavor.setCreateUserId(userId);
                        dietOrderDetailGoodsFlavor.setLastUpdateUserId(userId);
                        dietOrderDetailGoodsFlavor.setLastUpdateRemark("保存订单口味信息！");
                        dietOrderDetailGoodsFlavors.add(dietOrderDetailGoodsFlavor);
                    }
                }


                dietOrderDetail.setFlavorIncrease(flavorIncrease);
                dietOrderDetail.setQuantity(detailInfo.getQuantity());

                BigDecimal totalAmount = goodsSpecification.getPrice().add(flavorIncrease).multiply(BigDecimal.valueOf(detailInfo.getQuantity()));
                dietOrderDetail.setTotalAmount(totalAmount);
                dietOrderDetail.setDiscountAmount(BigDecimal.ZERO);
                dietOrderDetail.setPayableAmount(totalAmount);
                dietOrderDetail.setCreateUserId(userId);
                dietOrderDetail.setLastUpdateUserId(userId);
                dietOrderDetail.setLastUpdateRemark("保存订单详情信息！");
                dietOrderDetailMapper.insert(dietOrderDetail);

                if (CollectionUtils.isNotEmpty(dietOrderDetailGoodsFlavors)) {
                    for (DietOrderDetailGoodsFlavor dietOrderDetailGoodsFlavor : dietOrderDetailGoodsFlavors) {
                        dietOrderDetailGoodsFlavor.setDietOrderDetailId(dietOrderDetail.getId());
                    }
                    dietOrderDetailGoodsFlavorMapper.insertAll(dietOrderDetailGoodsFlavors);
                }
                dietOrderTotalAmount = dietOrderTotalAmount.add(totalAmount);

                String buyGiveActivityJson = CacheUtils.hget(Constants.KEY_BUY_GIVE_ACTIVITIES, tenantId + "_" + branchId + "_" + goods.getId() + "_" + goodsSpecification.getId());
                if (StringUtils.isNotBlank(buyGiveActivityJson)) {
                    BuyGiveActivityBean buyGiveActivityBean = GsonUtils.fromJson(buyGiveActivityJson, BuyGiveActivityBean.class);
                    if (detailInfo.getQuantity() >= buyGiveActivityBean.getBuyQuantity()) {
                        DietOrderDetail giveDietOrderDetail = new DietOrderDetail();
                        giveDietOrderDetail.setTenantId(tenantId);
                        giveDietOrderDetail.setTenantCode(tenantCode);
                        giveDietOrderDetail.setBranchId(branchId);
                        giveDietOrderDetail.setDietOrderId(dietOrderId);
                        giveDietOrderDetail.setGoodsId(buyGiveActivityBean.getGiveGoodsId());
                        giveDietOrderDetail.setGoodsName(buyGiveActivityBean.getGiveGoodsName());
                        giveDietOrderDetail.setGoodsSpecificationId(buyGiveActivityBean.getGiveGoodsSpecificationId());
                        giveDietOrderDetail.setGoodsSpecificationName(buyGiveActivityBean.getGiveGoodsSpecificationName());
                        giveDietOrderDetail.setPrice(BigDecimal.ZERO);
                        giveDietOrderDetail.setFlavorIncrease(BigDecimal.ZERO);
                        giveDietOrderDetail.setQuantity(buyGiveActivityBean.getGiveQuantity());
                        giveDietOrderDetail.setTotalAmount(BigDecimal.ZERO);
                        giveDietOrderDetail.setDiscountAmount(BigDecimal.ZERO);
                        giveDietOrderDetail.setPayableAmount(BigDecimal.ZERO);
                        giveDietOrderDetail.setCreateUserId(userId);
                        giveDietOrderDetail.setLastUpdateUserId(userId);
                        giveDietOrderDetail.setLastUpdateRemark("保存订单详情信息！");
                        giveDietOrderDetails.add(giveDietOrderDetail);

                        if (!dietOrderActivityMap.containsKey(buyGiveActivityBean.getActivityId())) {
                            DietOrderActivity dietOrderActivity = new DietOrderActivity();
                            dietOrderActivity.setTenantId(tenantId);
                            dietOrderActivity.setTenantCode(tenantCode);
                            dietOrderActivity.setBranchId(branchId);
                            dietOrderActivity.setDietOrderId(dietOrderId);
                            dietOrderActivity.setActivityId(buyGiveActivityBean.getActivityId());
                            dietOrderActivity.setActivityName(buyGiveActivityBean.getActivityName());
                            dietOrderActivity.setActivityType(buyGiveActivityBean.getActivityType());
                            dietOrderActivity.setAmount(BigDecimal.ZERO);
                            dietOrderActivity.setCreateUserId(userId);
                            dietOrderActivity.setLastUpdateUserId(userId);
                            dietOrderActivity.setLastUpdateRemark("保存订单活动信息！");
                            dietOrderActivityMap.put(buyGiveActivityBean.getActivityId(), dietOrderActivity);
                        }
                    }
                }
            }
        }

        if (CollectionUtils.isNotEmpty(giveDietOrderDetails)) {
            DietOrderGroup giveDietOrderGroup = new DietOrderGroup();
            giveDietOrderGroup.setTenantId(tenantId);
            giveDietOrderGroup.setTenantCode(tenantCode);
            giveDietOrderGroup.setBranchId(branchId);
            giveDietOrderGroup.setDietOrderId(dietOrderId);
            giveDietOrderGroup.setName("赠品");
            giveDietOrderGroup.setType("discount");
            giveDietOrderGroup.setCreateUserId(userId);
            giveDietOrderGroup.setLastUpdateUserId(userId);
            giveDietOrderGroup.setLastUpdateRemark("保存订单分组信息！");
            dietOrderGroupMapper.insert(giveDietOrderGroup);

            for (DietOrderDetail giveDietOrderDetail : giveDietOrderDetails) {
                giveDietOrderDetail.setDietOrderGroupId(giveDietOrderGroup.getId());
            }
            dietOrderDetailMapper.insertAll(giveDietOrderDetails);
        }

        BigDecimal dietOrderDiscountAmount = BigDecimal.ZERO;
        // 处理整单优惠活动
        String fullReductionActivityJson = CacheUtils.hget(Constants.KEY_FULL_REDUCTION_ACTIVITIES, tenantId + "_" + branchId);
        if (StringUtils.isNotBlank(fullReductionActivityJson)) {
            FullReductionActivityBean fullReductionActivityBean = GsonUtils.fromJson(fullReductionActivityJson, FullReductionActivityBean.class);
            Integer discountType = fullReductionActivityBean.getDiscountType();
            if (discountType == 1) {
                dietOrderDiscountAmount = fullReductionActivityBean.getDiscountAmount();
            } else if (discountType == 2) {
                dietOrderDiscountAmount = dietOrderTotalAmount.multiply(fullReductionActivityBean.getDiscountRate()).divide(BigDecimal.valueOf(100L));
            }
            DietOrderActivity dietOrderActivity = new DietOrderActivity();
            dietOrderActivity.setTenantId(tenantId);
            dietOrderActivity.setTenantCode(tenantCode);
            dietOrderActivity.setBranchId(branchId);
            dietOrderActivity.setDietOrderId(dietOrderId);
            dietOrderActivity.setActivityId(fullReductionActivityBean.getActivityId());
            dietOrderActivity.setActivityName(fullReductionActivityBean.getActivityName());
            dietOrderActivity.setActivityType(fullReductionActivityBean.getActivityType());
            dietOrderActivity.setAmount(dietOrderDiscountAmount.multiply(BigDecimal.valueOf(-1L)));
            dietOrderActivity.setCreateUserId(userId);
            dietOrderActivity.setLastUpdateUserId(userId);
            dietOrderActivity.setLastUpdateRemark("保存订单活动信息！");
            dietOrderActivityMap.put(fullReductionActivityBean.getActivityId(), dietOrderActivity);
        }

        if (MapUtils.isNotEmpty(dietOrderActivityMap)) {
            dietOrderActivityMapper.insertAll(new ArrayList<DietOrderActivity>(dietOrderActivityMap.values()));
        }

        dietOrder.setTotalAmount(dietOrderTotalAmount);
        dietOrder.setDiscountAmount(dietOrderDiscountAmount);
        dietOrder.setPayableAmount(dietOrderTotalAmount.subtract(dietOrderDiscountAmount));
        dietOrder.setPaidAmount(BigDecimal.ZERO);
        dietOrderMapper.update(dietOrder);

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("id", dietOrder.getId());
        data.put("orderNumber", dietOrder.getOrderNumber());
        return new ApiRest(data, "保存订单成功！");
    }
}
