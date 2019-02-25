package build.dream.catering.services;

import build.dream.catering.constants.Constants;
import build.dream.catering.mappers.ActivityMapper;
import build.dream.catering.models.activity.*;
import build.dream.catering.utils.ActivityUtils;
import build.dream.catering.utils.CanNotOperateReasonUtils;
import build.dream.common.api.ApiRest;
import build.dream.common.catering.domains.*;
import build.dream.common.utils.DatabaseHelper;
import build.dream.common.utils.SearchModel;
import build.dream.common.utils.ValidateUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
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
        BigInteger tenantId = saveBuyGiveActivityModel.obtainTenantId();
        String tenantCode = saveBuyGiveActivityModel.obtainTenantCode();
        List<BigInteger> branchIds = saveBuyGiveActivityModel.getBranchIds();
        BigInteger userId = saveBuyGiveActivityModel.getUserId();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = simpleDateFormat.parse(saveBuyGiveActivityModel.getStartDate());
        Date endDate = simpleDateFormat.parse(saveBuyGiveActivityModel.getEndDate());
        Time startTime = Time.valueOf(saveBuyGiveActivityModel.getStartTime());
        Time endTime = Time.valueOf(saveBuyGiveActivityModel.getEndTime());
        ValidateUtils.isTrue(endDate.after(startDate), "活动结束日期必须大于开始日期！");
        ValidateUtils.isTrue(endTime.after(startTime), "活动结束时间必须大于开始时间！");
        int weekSign = saveBuyGiveActivityModel.getWeekSign();

        List<SaveBuyGiveActivityModel.BuyGiveActivityInfo> buyGiveActivityInfos = saveBuyGiveActivityModel.getBuyGiveActivityInfos();

        List<BigInteger> goodsIds = new ArrayList<BigInteger>();
        List<BigInteger> goodsSpecificationIds = new ArrayList<BigInteger>();
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
        goodsSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, BigInteger.ZERO);
        List<Goods> goodses = DatabaseHelper.findAll(Goods.class, goodsSearchModel);

        // 封装商品id与商品之间的map
        Map<BigInteger, Goods> goodsMap = new HashMap<BigInteger, Goods>();
        for (Goods goods : goodses) {
            goodsMap.put(goods.getId(), goods);
        }

        SearchModel canNotOperateReasonSearchModel = new SearchModel();
        canNotOperateReasonSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        canNotOperateReasonSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, BigInteger.ZERO);
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
        goodsSpecificationSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, BigInteger.ZERO);
        List<GoodsSpecification> goodsSpecifications = DatabaseHelper.findAll(GoodsSpecification.class, goodsSpecificationSearchModel);

        // 封装商品规格id与商品规格之间的map
        Map<BigInteger, GoodsSpecification> goodsSpecificationMap = new HashMap<BigInteger, GoodsSpecification>();
        for (GoodsSpecification goodsSpecification : goodsSpecifications) {
            goodsSpecificationMap.put(goodsSpecification.getId(), goodsSpecification);
        }

        Activity activity = ActivityUtils.constructActivity(tenantId, tenantCode, saveBuyGiveActivityModel.getName(), 1, startDate, startTime, endDate, endTime, weekSign, userId, "保存活动信息！");
        DatabaseHelper.insert(activity);

        BigInteger activityId = activity.getId();

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
            canNotOperateReasons.add(CanNotOperateReasonUtils.constructCanNotOperateReason(tenantId, tenantCode, BigInteger.ZERO, buyGoods.getId(), "goods", activityId, "activity", 3, reason));
            canNotOperateReasons.add(CanNotOperateReasonUtils.constructCanNotOperateReason(tenantId, tenantCode, BigInteger.ZERO, giveGoods.getId(), "goods", activityId, "activity", 3, reason));
        }
        DatabaseHelper.insertAll(buyGiveActivities);
        DatabaseHelper.insertAll(canNotOperateReasons);
        return ApiRest.builder().message("保存买A赠B活动成功！").successful(true).build();
    }

    @Transactional(rollbackFor = Exception.class)
    public ApiRest saveFullReductionActivity(SaveFullReductionActivityModel saveFullReductionActivityModel) throws ParseException {
        BigInteger tenantId = saveFullReductionActivityModel.obtainTenantId();
        String tenantCode = saveFullReductionActivityModel.obtainTenantCode();
        List<BigInteger> branchIds = saveFullReductionActivityModel.getBranchIds();
        BigInteger userId = saveFullReductionActivityModel.getUserId();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = simpleDateFormat.parse(saveFullReductionActivityModel.getStartTime());
        Date endDate = simpleDateFormat.parse(saveFullReductionActivityModel.getEndTime());
        Time startTime = Time.valueOf(saveFullReductionActivityModel.getStartTime());
        Time endTime = Time.valueOf(saveFullReductionActivityModel.getEndTime());

        ValidateUtils.isTrue(endDate.after(startDate), "活动结束日期必须大于开始日期！");
        ValidateUtils.isTrue(endTime.after(startTime), "活动结束时间必须大于开始时间！");

        int weekSign = saveFullReductionActivityModel.getWeekSign();

        String sql = "SELECT * " +
                "FROM activity " +
                "WHERE tenant_id = #{tenantId} " +
                "AND deleted = 0 " +
                "AND type = 2 " +
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


        Activity activity = ActivityUtils.constructActivity(tenantId, tenantCode, saveFullReductionActivityModel.getName(), 2, startDate, startTime, endDate, endTime, weekSign, userId, "保存活动信息！");
        DatabaseHelper.insert(activity);

        BigInteger activityId = activity.getId();

        activityMapper.insertAllActivityBranchR(activityId, tenantId, branchIds);

        FullReductionActivity fullReductionActivity = ActivityUtils.constructFullReductionActivity(tenantId, tenantCode, activityId, saveFullReductionActivityModel.getTotalAmount(), saveFullReductionActivityModel.getDiscountType(), saveFullReductionActivityModel.getDiscountRate(), saveFullReductionActivityModel.getDiscountAmount(), userId, "保存满减活动！");
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
        BigInteger tenantId = saveSpecialGoodsActivityModel.obtainTenantId();
        String tenantCode = saveSpecialGoodsActivityModel.obtainTenantCode();
        List<BigInteger> branchIds = saveSpecialGoodsActivityModel.getBranchIds();
        BigInteger userId = saveSpecialGoodsActivityModel.getUserId();
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
        List<BigInteger> goodsIds = new ArrayList<BigInteger>();
        List<BigInteger> goodsSpecificationIds = new ArrayList<BigInteger>();
        for (SaveSpecialGoodsActivityModel.SpecialGoodsActivityInfo specialGoodsActivityInfo : specialGoodsActivityInfos) {
            goodsIds.add(specialGoodsActivityInfo.getGoodsId());
            goodsSpecificationIds.add(specialGoodsActivityInfo.getGoodsSpecificationId());
        }

        // 查询出涉及的所有商品
        SearchModel goodsSearchModel = new SearchModel(true);
        goodsSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_IN, goodsIds);
        goodsSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        goodsSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, BigInteger.ZERO);
        List<Goods> goodses = DatabaseHelper.findAll(Goods.class, goodsSearchModel);

        // 封装商品id与商品之间的map
        Map<BigInteger, Goods> goodsMap = new HashMap<BigInteger, Goods>();
        for (Goods goods : goodses) {
            goodsMap.put(goods.getId(), goods);
        }

        SearchModel canNotOperateReasonSearchModel = new SearchModel();
        canNotOperateReasonSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        canNotOperateReasonSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, BigInteger.ZERO);
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
        goodsSpecificationSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, BigInteger.ZERO);
        List<GoodsSpecification> goodsSpecifications = DatabaseHelper.findAll(GoodsSpecification.class, goodsSpecificationSearchModel);

        // 封装商品规格id与商品规格之间的map
        Map<BigInteger, GoodsSpecification> goodsSpecificationMap = new HashMap<BigInteger, GoodsSpecification>();
        for (GoodsSpecification goodsSpecification : goodsSpecifications) {
            goodsSpecificationMap.put(goodsSpecification.getId(), goodsSpecification);
        }

        Activity activity = ActivityUtils.constructActivity(tenantId, tenantCode, saveSpecialGoodsActivityModel.getName(), 3, startDate, startTime, endDate, endTime, weekSign, userId, "保存活动信息！");
        DatabaseHelper.insert(activity);

        BigInteger activityId = activity.getId();

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
            CanNotOperateReason canNotOperateReason = CanNotOperateReasonUtils.constructCanNotOperateReason(tenantId, tenantCode, BigInteger.ZERO, goods.getId(), "goods", activityId, "activity", 3, reason);
            canNotOperateReasons.add(canNotOperateReason);

            String usedOtherActivityReason = "商品【%s】已参与促销活动【" + activity.getName() + "】，不可参与其他促销活动！";
            CanNotOperateReason canNotUsedOtherActivityReason = CanNotOperateReasonUtils.constructCanNotOperateReason(tenantId, tenantCode, BigInteger.ZERO, goods.getId(), "goods", activityId, "activity", 4, usedOtherActivityReason);
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
        BigInteger tenantId = listEffectiveActivitiesModel.obtainTenantId();
        BigInteger branchId = listEffectiveActivitiesModel.obtainBranchId();
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
        BigInteger tenantId = listFullReductionActivitiesModel.obtainTenantId();
        BigInteger branchId = listFullReductionActivitiesModel.obtainBranchId();

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
        BigInteger tenantId = listPaymentActivitiesModel.obtainTenantId();
        BigInteger branchId = listPaymentActivitiesModel.obtainBranchId();

        List<PaymentActivity> paymentActivities = activityMapper.listPaymentActivities(tenantId, branchId);
        return ApiRest.builder().data(paymentActivities).message("查询所有生效的支付促销活动成功！").successful(true).build();
    }
}
