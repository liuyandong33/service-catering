package build.dream.catering.services;

import build.dream.catering.constants.Constants;
import build.dream.catering.mappers.ActivityMapper;
import build.dream.catering.models.activity.*;
import build.dream.catering.utils.ActivityUtils;
import build.dream.catering.utils.CanNotOperateUtils;
import build.dream.common.api.ApiRest;
import build.dream.common.domains.catering.*;
import build.dream.common.utils.DatabaseHelper;
import build.dream.common.utils.SearchModel;
import build.dream.common.utils.ValidateUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ActivityService {
    @Autowired
    private ActivityMapper activityMapper;

    @Transactional(rollbackFor = Exception.class)
    public ApiRest saveBuyGiveActivity(SaveBuyGiveActivityModel saveBuyGiveActivityModel) throws ParseException {
        Long tenantId = saveBuyGiveActivityModel.obtainTenantId();
        String tenantCode = saveBuyGiveActivityModel.obtainTenantCode();
        List<Long> branchIds = saveBuyGiveActivityModel.getBranchIds();
        Long userId = saveBuyGiveActivityModel.getUserId();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = simpleDateFormat.parse(saveBuyGiveActivityModel.getStartDate());
        Date endDate = simpleDateFormat.parse(saveBuyGiveActivityModel.getEndDate());
        Time startTime = Time.valueOf(saveBuyGiveActivityModel.getStartTime());
        Time endTime = Time.valueOf(saveBuyGiveActivityModel.getEndTime());
        ValidateUtils.isTrue(endDate.after(startDate), "活动结束日期必须大于开始日期！");
        ValidateUtils.isTrue(endTime.after(startTime), "活动结束时间必须大于开始时间！");
        int weekSign = saveBuyGiveActivityModel.getWeekSign();

        List<SaveBuyGiveActivityModel.BuyGiveActivityInfo> buyGiveActivityInfos = saveBuyGiveActivityModel.getBuyGiveActivityInfos();

        List<Long> goodsIds = new ArrayList<Long>();
        List<Long> goodsSpecificationIds = new ArrayList<Long>();
        for (SaveBuyGiveActivityModel.BuyGiveActivityInfo buyGiveActivityInfo : buyGiveActivityInfos) {
            goodsIds.add(buyGiveActivityInfo.getBuyGoodsId());
            goodsSpecificationIds.add(buyGiveActivityInfo.getBuyGoodsSpecificationId());
            goodsIds.add(buyGiveActivityInfo.getGiveGoodsId());
            goodsSpecificationIds.add(buyGiveActivityInfo.getGiveGoodsSpecificationId());
        }

        // 查询出涉及的所有商品
        SearchModel goodsSearchModel = new SearchModel(true);
        goodsSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_IN, goodsIds);
        goodsSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        goodsSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, 0L);
        List<Goods> goodses = DatabaseHelper.findAll(Goods.class, goodsSearchModel);

        // 封装商品id与商品之间的map
        Map<Long, Goods> goodsMap = new HashMap<Long, Goods>();
        for (Goods goods : goodses) {
            goodsMap.put(goods.getId(), goods);
        }

        SearchModel canNotOperateReasonSearchModel = new SearchModel();
        canNotOperateReasonSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        canNotOperateReasonSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, 0L);
        canNotOperateReasonSearchModel.addSearchCondition("table_id", Constants.SQL_OPERATION_SYMBOL_IN, goodsIds);
        canNotOperateReasonSearchModel.addSearchCondition("operate_type", Constants.SQL_OPERATION_SYMBOL_EQUAL, 4);
        CanNotOperateReason persistenceCanNotOperateReason = DatabaseHelper.find(CanNotOperateReason.class, canNotOperateReasonSearchModel);
        if (persistenceCanNotOperateReason != null) {
            Goods goods = goodsMap.get(persistenceCanNotOperateReason.getTableId());
            ValidateUtils.notNull(goods, "商品不存在！");

            throw new RuntimeException(String.format(persistenceCanNotOperateReason.getReason(), goods.getName()));
        }

        // 查询出涉及的所有商品规格
        SearchModel goodsSpecificationSearchModel = new SearchModel(true);
        goodsSpecificationSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_IN, goodsSpecificationIds);
        goodsSpecificationSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        goodsSpecificationSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, 0L);
        List<GoodsSpecification> goodsSpecifications = DatabaseHelper.findAll(GoodsSpecification.class, goodsSpecificationSearchModel);

        // 封装商品规格id与商品规格之间的map
        Map<Long, GoodsSpecification> goodsSpecificationMap = new HashMap<Long, GoodsSpecification>();
        for (GoodsSpecification goodsSpecification : goodsSpecifications) {
            goodsSpecificationMap.put(goodsSpecification.getId(), goodsSpecification);
        }

        Activity activity = ActivityUtils.constructActivity(tenantId, tenantCode, saveBuyGiveActivityModel.getName(), 1, startDate, startTime, endDate, endTime, weekSign, userId, "保存活动信息！");
        DatabaseHelper.insert(activity);

        Long activityId = activity.getId();

        activityMapper.insertAllActivityBranchR(activityId, tenantId, branchIds);

        List<BuyGiveActivity> buyGiveActivities = new ArrayList<BuyGiveActivity>();
        List<CanNotOperateReason> canNotOperateReasons = new ArrayList<CanNotOperateReason>();
        for (SaveBuyGiveActivityModel.BuyGiveActivityInfo buyGiveActivityInfo : buyGiveActivityInfos) {
            Goods buyGoods = goodsMap.get(buyGiveActivityInfo.getBuyGoodsId());
            ValidateUtils.notNull(buyGoods, "商品不存在！");

            GoodsSpecification buyGoodsSpecification = goodsSpecificationMap.get(buyGiveActivityInfo.getBuyGoodsSpecificationId());
            ValidateUtils.notNull(buyGoods, "商品规格不存在！");

            Goods giveGoods = goodsMap.get(buyGiveActivityInfo.getGiveGoodsId());
            ValidateUtils.notNull(buyGoods, "商品不存在！");

            GoodsSpecification giveGoodsSpecification = goodsSpecificationMap.get(buyGiveActivityInfo.getGiveGoodsSpecificationId());
            ValidateUtils.notNull(buyGoods, "商品规格不存在！");

            BuyGiveActivity buyGiveActivity = new BuyGiveActivity();
            buyGiveActivity.setTenantId(tenantId);
            buyGiveActivity.setTenantCode(tenantCode);
            buyGiveActivity.setActivityId(activityId);
            buyGiveActivity.setBuyGoodsId(buyGoods.getId());
            buyGiveActivity.setBuyGoodsSpecificationId(buyGoodsSpecification.getId());
            buyGiveActivity.setBuyQuantity(buyGiveActivityInfo.getBuyQuantity());
            buyGiveActivity.setGiveGoodsId(giveGoods.getId());
            buyGiveActivity.setGiveGoodsSpecificationId(giveGoodsSpecification.getId());
            buyGiveActivity.setGiveQuantity(buyGiveActivityInfo.getGiveQuantity());
            buyGiveActivity.setCreatedUserId(userId);
            buyGiveActivity.setUpdatedUserId(userId);
            buyGiveActivity.setUpdatedRemark("保存买A赠B活动！");
            buyGiveActivities.add(buyGiveActivity);

            String reason = "该商品已参与促销活动【" + activity.getName() + "】，活动期间不可%s！如需更改，请先取消活动！";
            canNotOperateReasons.add(CanNotOperateUtils.buildCanNotOperateReason(tenantId, tenantCode, 0L, buyGoods.getId(), "goods", activityId, "activity", 3, reason));
            canNotOperateReasons.add(CanNotOperateUtils.buildCanNotOperateReason(tenantId, tenantCode, 0L, giveGoods.getId(), "goods", activityId, "activity", 3, reason));
        }
        DatabaseHelper.insertAll(buyGiveActivities);
        DatabaseHelper.insertAll(canNotOperateReasons);
        return ApiRest.builder().message("保存买A赠B活动成功！").successful(true).build();
    }

    @Transactional(rollbackFor = Exception.class)
    public ApiRest saveFullReductionActivity(SaveFullReductionActivityModel saveFullReductionActivityModel) throws ParseException {
        Long tenantId = saveFullReductionActivityModel.obtainTenantId();
        String tenantCode = saveFullReductionActivityModel.obtainTenantCode();
        Long userId = saveFullReductionActivityModel.obtainUserId();
        List<Long> branchIds = saveFullReductionActivityModel.getBranchIds();

        String name = saveFullReductionActivityModel.getName();
        String startDate = saveFullReductionActivityModel.getStartDate();
        String startTime = saveFullReductionActivityModel.getStartTime();
        String endDate = saveFullReductionActivityModel.getEndDate();
        String endTime = saveFullReductionActivityModel.getEndTime();
        Integer weekSign = saveFullReductionActivityModel.getWeekSign();
        Double totalAmount = saveFullReductionActivityModel.getTotalAmount();
        Integer discountType = saveFullReductionActivityModel.getDiscountType();
        Double discountRate = saveFullReductionActivityModel.getDiscountRate();
        Double discountAmount = saveFullReductionActivityModel.getDiscountAmount();


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date _startDate = simpleDateFormat.parse(startDate);
        Date _endDate = simpleDateFormat.parse(endDate);
        Time _startTime = Time.valueOf(startTime);
        Time _endTime = Time.valueOf(endTime);

        ValidateUtils.isTrue(_endDate.after(_startDate), "活动结束日期必须大于开始日期！");
        ValidateUtils.isTrue(_endTime.after(_startTime), "活动结束时间必须大于开始时间！");

//        throw new RuntimeException("活动日期与促销活动【" + "" + "】在时间上冲突！");

        Activity activity = Activity.builder()
                .tenantId(tenantId)
                .tenantCode(tenantCode)
                .name(name)
                .type(2)
                .startDate(_startDate)
                .startTime(_startTime)
                .endDate(_endDate)
                .endTime(_endTime)
                .weekSign(weekSign)
                .createdUserId(userId)
                .updatedUserId(userId)
                .updatedRemark("保存活动信息！")
                .build();
        DatabaseHelper.insert(activity);

        Long activityId = activity.getId();

        activityMapper.insertAllActivityBranchR(activityId, tenantId, branchIds);

        FullReductionActivity fullReductionActivity = FullReductionActivity.builder()
                .tenantId(tenantId)
                .tenantCode(tenantCode)
                .activityId(activityId)
                .totalAmount(totalAmount)
                .discountType(discountType)
                .discountRate(discountRate)
                .discountAmount(discountAmount)
                .createdUserId(userId)
                .updatedUserId(userId)
                .updatedRemark("保存满减活动！")
                .build();
        DatabaseHelper.insert(fullReductionActivity);

        return ApiRest.builder().message("保存满减活动成功！").successful(true).build();
    }

    /**
     * 保存特价商品活动
     *
     * @param saveSpecialGoodsActivityModel
     * @return
     * @throws ParseException
     */
    public ApiRest saveSpecialGoodsActivity(SaveSpecialGoodsActivityModel saveSpecialGoodsActivityModel) throws ParseException {
        Long tenantId = saveSpecialGoodsActivityModel.obtainTenantId();
        String tenantCode = saveSpecialGoodsActivityModel.obtainTenantCode();
        List<Long> branchIds = saveSpecialGoodsActivityModel.getBranchIds();
        Long userId = saveSpecialGoodsActivityModel.getUserId();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DEFAULT_DATE_PATTERN);
        Date startDate = simpleDateFormat.parse(saveSpecialGoodsActivityModel.getStartTime());
        Date endDate = simpleDateFormat.parse(saveSpecialGoodsActivityModel.getEndTime());

        Time startTime = Time.valueOf(saveSpecialGoodsActivityModel.getStartTime());
        Time endTime = Time.valueOf(saveSpecialGoodsActivityModel.getEndTime());

        ValidateUtils.isTrue(endDate.after(startDate), "活动结束日期必须大于开始日期！");
        ValidateUtils.isTrue(endTime.after(startTime), "活动结束时间必须大于开始时间！");

        int weekSign = saveSpecialGoodsActivityModel.getWeekSign();

        String sql = "SELECT * " +
                "FROM activity " +
                "WHERE tenant_id = #{tenantId} " +
                "AND deleted = 0 " +
                "AND type = 3 " +
                "AND status IN (1, 2) " +
                "AND ((start_time <= #{startTime} AND end_time >= #{endTime}) OR (start_time >= #{startTime} AND start_time <= #{endTime}) OR (end_time >= #{startTime} AND end_time <= #{endTime})) " +
                "LIMIT 0, 1";
        Map<String, Object> findActivityParameters = new HashMap<String, Object>();
        findActivityParameters.put("sql", sql);
        findActivityParameters.put("tenantId", tenantId);
        findActivityParameters.put("startTime", startTime);
        findActivityParameters.put("endTime", endTime);
        Map<String, Object> activityMap = DatabaseHelper.executeUniqueResultQuery(findActivityParameters);
        if (MapUtils.isNotEmpty(activityMap)) {
            throw new RuntimeException("活动日期与促销活动【" + activityMap.get("name") + "】在时间上冲突！");
        }

        List<SaveSpecialGoodsActivityModel.SpecialGoodsActivityInfo> specialGoodsActivityInfos = saveSpecialGoodsActivityModel.getSpecialGoodsActivityInfos();
        List<Long> goodsIds = new ArrayList<Long>();
        List<Long> goodsSpecificationIds = new ArrayList<Long>();
        for (SaveSpecialGoodsActivityModel.SpecialGoodsActivityInfo specialGoodsActivityInfo : specialGoodsActivityInfos) {
            goodsIds.add(specialGoodsActivityInfo.getGoodsId());
            goodsSpecificationIds.add(specialGoodsActivityInfo.getGoodsSpecificationId());
        }

        // 查询出涉及的所有商品
        SearchModel goodsSearchModel = new SearchModel(true);
        goodsSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_IN, goodsIds);
        goodsSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        goodsSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, 0L);
        List<Goods> goodses = DatabaseHelper.findAll(Goods.class, goodsSearchModel);

        // 封装商品id与商品之间的map
        Map<Long, Goods> goodsMap = new HashMap<Long, Goods>();
        for (Goods goods : goodses) {
            goodsMap.put(goods.getId(), goods);
        }

        SearchModel canNotOperateReasonSearchModel = new SearchModel();
        canNotOperateReasonSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        canNotOperateReasonSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, 0L);
        canNotOperateReasonSearchModel.addSearchCondition("table_id", Constants.SQL_OPERATION_SYMBOL_IN, goodsIds);
        canNotOperateReasonSearchModel.addSearchCondition("operate_type", Constants.SQL_OPERATION_SYMBOL_EQUAL, 4);
        CanNotOperateReason persistenceCanNotOperateReason = DatabaseHelper.find(CanNotOperateReason.class, canNotOperateReasonSearchModel);
        if (persistenceCanNotOperateReason != null) {
            Goods goods = goodsMap.get(persistenceCanNotOperateReason.getTableId());
            ValidateUtils.notNull(goods, "商品不存在！");

            throw new RuntimeException(String.format(persistenceCanNotOperateReason.getReason(), goods.getName()));
        }

        // 查询出涉及的所有商品规格
        SearchModel goodsSpecificationSearchModel = new SearchModel(true);
        goodsSpecificationSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_IN, goodsSpecificationIds);
        goodsSpecificationSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        goodsSpecificationSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, 0L);
        List<GoodsSpecification> goodsSpecifications = DatabaseHelper.findAll(GoodsSpecification.class, goodsSpecificationSearchModel);

        // 封装商品规格id与商品规格之间的map
        Map<Long, GoodsSpecification> goodsSpecificationMap = new HashMap<Long, GoodsSpecification>();
        for (GoodsSpecification goodsSpecification : goodsSpecifications) {
            goodsSpecificationMap.put(goodsSpecification.getId(), goodsSpecification);
        }

        Activity activity = ActivityUtils.constructActivity(tenantId, tenantCode, saveSpecialGoodsActivityModel.getName(), 3, startDate, startTime, endDate, endTime, weekSign, userId, "保存活动信息！");
        DatabaseHelper.insert(activity);

        Long activityId = activity.getId();

        activityMapper.insertAllActivityBranchR(activityId, tenantId, branchIds);

        List<SpecialGoodsActivity> specialGoodsActivities = new ArrayList<SpecialGoodsActivity>();
        List<CanNotOperateReason> canNotOperateReasons = new ArrayList<CanNotOperateReason>();
        for (SaveSpecialGoodsActivityModel.SpecialGoodsActivityInfo specialGoodsActivityInfo : specialGoodsActivityInfos) {
            Goods goods = goodsMap.get(specialGoodsActivityInfo.getGoodsId());
            ValidateUtils.notNull(goods, "商品不存在！");

            GoodsSpecification goodsSpecification = goodsSpecificationMap.get(specialGoodsActivityInfo.getGoodsSpecificationId());
            ValidateUtils.notNull(goodsSpecification, "商品规格不存在！");

            SpecialGoodsActivity specialGoodsActivity = new SpecialGoodsActivity();
            specialGoodsActivity.setTenantId(tenantId);
            specialGoodsActivity.setTenantCode(tenantCode);
            specialGoodsActivity.setActivityId(activityId);
            specialGoodsActivity.setGoodsId(goods.getId());
            specialGoodsActivity.setGoodsSpecificationId(goodsSpecification.getId());
            int discountType = specialGoodsActivityInfo.getDiscountType();
            specialGoodsActivity.setDiscountType(discountType);
            if (discountType == 1) {
                specialGoodsActivity.setSpecialPrice(specialGoodsActivityInfo.getSpecialPrice());
            } else if (discountType == 2) {
                specialGoodsActivity.setDiscountRate(specialGoodsActivityInfo.getDiscountRate());
            }
            specialGoodsActivity.setCreatedUserId(userId);
            specialGoodsActivity.setUpdatedUserId(userId);
            specialGoodsActivity.setUpdatedRemark("保存特价商品活动！");
            specialGoodsActivities.add(specialGoodsActivity);

            String reason = "该商品已参与促销活动【" + activity.getName() + "】，活动期间不可%s！如需更改，请先取消活动！";
            CanNotOperateReason canNotOperateReason = CanNotOperateUtils.buildCanNotOperateReason(tenantId, tenantCode, 0L, goods.getId(), "goods", activityId, "activity", 3, reason);
            canNotOperateReasons.add(canNotOperateReason);

            String usedOtherActivityReason = "该商品已参与促销活动【" + activity.getName() + "】，不可参与其他促销活动！";
            CanNotOperateReason canNotUsedOtherActivityReason = CanNotOperateUtils.buildCanNotOperateReason(tenantId, tenantCode, 0L, goods.getId(), "goods", activityId, "activity", 4, usedOtherActivityReason);
            canNotOperateReasons.add(canNotUsedOtherActivityReason);
        }

        DatabaseHelper.insertAll(specialGoodsActivities);
        DatabaseHelper.insertAll(canNotOperateReasons);
        return ApiRest.builder().message("保存特价商品活动成功！").successful(true).build();
    }

    /**
     * 查询生效的活动
     *
     * @param listEffectiveActivitiesModel
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiRest listEffectiveActivities(ListEffectiveActivitiesModel listEffectiveActivitiesModel) {
        Long tenantId = listEffectiveActivitiesModel.obtainTenantId();
        Long branchId = listEffectiveActivitiesModel.obtainBranchId();
        List<EffectiveActivity> effectiveActivities = activityMapper.listEffectiveActivities(tenantId, branchId);

        return ApiRest.builder().data(effectiveActivities).message("查询生效的活动成功！").successful(true).build();
    }

    /**
     * 查询所有生效的整单满减活动
     *
     * @param listFullReductionActivitiesModel
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest listFullReductionActivities(ListFullReductionActivitiesModel listFullReductionActivitiesModel) {
        Long tenantId = listFullReductionActivitiesModel.obtainTenantId();
        Long branchId = listFullReductionActivitiesModel.obtainBranchId();

        List<FullReductionActivity> fullReductionActivities = activityMapper.listFullReductionActivities(tenantId, branchId);
        return ApiRest.builder().data(fullReductionActivities).message("查询所有生效的整单满减活动成功！").successful(true).build();
    }

    /**
     * 查询所有生效的支付促销活动
     *
     * @param listPaymentActivitiesModel
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest listPaymentActivities(ListPaymentActivitiesModel listPaymentActivitiesModel) {
        Long tenantId = listPaymentActivitiesModel.obtainTenantId();
        Long branchId = listPaymentActivitiesModel.obtainBranchId();

        List<PaymentActivity> paymentActivities = activityMapper.listPaymentActivities(tenantId, branchId);
        return ApiRest.builder().data(paymentActivities).message("查询所有生效的支付促销活动成功！").successful(true).build();
    }
}
