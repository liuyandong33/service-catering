package build.dream.catering.services;

import build.dream.catering.constants.Constants;
import build.dream.catering.mappers.*;
import build.dream.catering.models.activity.*;
import build.dream.catering.utils.ActivityUtils;
import build.dream.catering.utils.CanNotOperateReasonUtils;
import build.dream.catering.utils.DatabaseHelper;
import build.dream.common.api.ApiRest;
import build.dream.common.erp.catering.domains.*;
import build.dream.common.utils.SearchModel;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.Validate;
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
        BigInteger tenantId = saveBuyGiveActivityModel.getTenantId();
        String tenantCode = saveBuyGiveActivityModel.getTenantCode();
        List<BigInteger> branchIds = saveBuyGiveActivityModel.getBranchIds();
        BigInteger userId = saveBuyGiveActivityModel.getUserId();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = simpleDateFormat.parse(saveBuyGiveActivityModel.getStartDate());
        Date endDate = simpleDateFormat.parse(saveBuyGiveActivityModel.getEndDate());
        Time startTime = Time.valueOf(saveBuyGiveActivityModel.getStartTime());
        Time endTime = Time.valueOf(saveBuyGiveActivityModel.getEndTime());
        Validate.isTrue(endDate.after(startDate), "活动结束日期必须大于开始日期！");
        Validate.isTrue(endTime.after(startTime), "活动结束时间必须大于开始时间！");
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
            Validate.notNull(goods, "商品不存在！");

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
            Validate.notNull(buyGoods, "商品不存在！");

            GoodsSpecification buyGoodsSpecification = goodsSpecificationMap.get(buyGiveActivityInfo.getBuyGoodsSpecificationId());
            Validate.notNull(buyGoods, "商品规格不存在！");

            Goods giveGoods = goodsMap.get(buyGiveActivityInfo.getGiveGoodsId());
            Validate.notNull(buyGoods, "商品不存在！");

            GoodsSpecification giveGoodsSpecification = goodsSpecificationMap.get(buyGiveActivityInfo.getGiveGoodsSpecificationId());
            Validate.notNull(buyGoods, "商品规格不存在！");

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
            buyGiveActivity.setCreateUserId(userId);
            buyGiveActivity.setLastUpdateUserId(userId);
            buyGiveActivity.setLastUpdateRemark("保存买A赠B活动！");
            buyGiveActivities.add(buyGiveActivity);

            String reason = "该商品已参与促销活动【" + activity.getName() + "】，活动期间不可%s！如需更改，请先取消活动！";
            canNotOperateReasons.add(CanNotOperateReasonUtils.constructCanNotOperateReason(tenantId, tenantCode, BigInteger.ZERO, buyGoods.getId(), "goods", activityId, "activity", 3, reason));
            canNotOperateReasons.add(CanNotOperateReasonUtils.constructCanNotOperateReason(tenantId, tenantCode, BigInteger.ZERO, giveGoods.getId(), "goods", activityId, "activity", 3, reason));
        }
        DatabaseHelper.insertAll(buyGiveActivities);
        DatabaseHelper.insertAll(canNotOperateReasons);
        ApiRest apiRest = new ApiRest();
        apiRest.setMessage("保存买A赠B活动成功！");
        apiRest.setSuccessful(true);
        return apiRest;
    }

    @Transactional(rollbackFor = Exception.class)
    public ApiRest saveFullReductionActivity(SaveFullReductionActivityModel saveFullReductionActivityModel) throws ParseException {
        BigInteger tenantId = saveFullReductionActivityModel.getTenantId();
        String tenantCode = saveFullReductionActivityModel.getTenantCode();
        List<BigInteger> branchIds = saveFullReductionActivityModel.getBranchIds();
        BigInteger userId = saveFullReductionActivityModel.getUserId();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = simpleDateFormat.parse(saveFullReductionActivityModel.getStartTime());
        Date endDate = simpleDateFormat.parse(saveFullReductionActivityModel.getEndTime());
        Time startTime = Time.valueOf(saveFullReductionActivityModel.getStartTime());
        Time endTime = Time.valueOf(saveFullReductionActivityModel.getEndTime());

        Validate.isTrue(endDate.after(startDate), "活动结束日期必须大于开始日期！");
        Validate.isTrue(endTime.after(startTime), "活动结束时间必须大于开始时间！");

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

        ApiRest apiRest = new ApiRest();
        apiRest.setMessage("保存满减活动成功！");
        apiRest.setSuccessful(true);
        return apiRest;
    }

    /**
     * 保存特价商品活动
     *
     * @param saveSpecialGoodsActivityModel
     * @return
     * @throws ParseException
     */
    public ApiRest saveSpecialGoodsActivity(SaveSpecialGoodsActivityModel saveSpecialGoodsActivityModel) throws ParseException {
        BigInteger tenantId = saveSpecialGoodsActivityModel.getTenantId();
        String tenantCode = saveSpecialGoodsActivityModel.getTenantCode();
        List<BigInteger> branchIds = saveSpecialGoodsActivityModel.getBranchIds();
        BigInteger userId = saveSpecialGoodsActivityModel.getUserId();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DEFAULT_DATE_PATTERN);
        Date startDate = simpleDateFormat.parse(saveSpecialGoodsActivityModel.getStartTime());
        Date endDate = simpleDateFormat.parse(saveSpecialGoodsActivityModel.getEndTime());

        Time startTime = Time.valueOf(saveSpecialGoodsActivityModel.getStartTime());
        Time endTime = Time.valueOf(saveSpecialGoodsActivityModel.getEndTime());

        Validate.isTrue(endDate.after(startDate), "活动结束日期必须大于开始日期！");
        Validate.isTrue(endTime.after(startTime), "活动结束时间必须大于开始时间！");

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
            Validate.notNull(goods, "商品不存在！");

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
            Validate.notNull(goods, "商品不存在！");

            GoodsSpecification goodsSpecification = goodsSpecificationMap.get(specialGoodsActivityInfo.getGoodsSpecificationId());
            Validate.notNull(goodsSpecification, "商品规格不存在！");

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
            specialGoodsActivity.setCreateUserId(userId);
            specialGoodsActivity.setLastUpdateUserId(userId);
            specialGoodsActivity.setLastUpdateRemark("保存特价商品活动！");
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
        ApiRest apiRest = new ApiRest();
        apiRest.setMessage("保存特价商品活动成功！");
        apiRest.setSuccessful(true);
        return apiRest;
    }

    /**
     * 查询生效的活动
     *
     * @param listEffectiveActivitiesModel
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiRest listEffectiveActivities(ListEffectiveActivitiesModel listEffectiveActivitiesModel) {
        BigInteger tenantId = listEffectiveActivitiesModel.getTenantId();
        BigInteger branchId = listEffectiveActivitiesModel.getBranchId();
        List<EffectiveActivity> effectiveActivities = activityMapper.callProcedureEffectiveActivity(tenantId, branchId);

        ApiRest apiRest = new ApiRest();
        apiRest.setData(effectiveActivities);
        apiRest.setMessage("查询生效的活动成功！");
        apiRest.setSuccessful(true);
        return apiRest;
    }

    /**
     * 查询所有生效的整单满减活动
     *
     * @param listFullReductionActivitiesModel
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest listFullReductionActivities(ListFullReductionActivitiesModel listFullReductionActivitiesModel) {
        BigInteger tenantId = listFullReductionActivitiesModel.getTenantId();
        BigInteger branchId = listFullReductionActivitiesModel.getBranchId();

        List<FullReductionActivity> fullReductionActivities = activityMapper.listFullReductionActivities(tenantId, branchId);
        ApiRest apiRest = new ApiRest();
        apiRest.setData(fullReductionActivities);
        apiRest.setMessage("查询所有生效的整单满减活动成功！");
        apiRest.setSuccessful(true);
        return apiRest;
    }

    /**
     * 查询所有生效的支付促销活动
     *
     * @param listPaymentActivitiesModel
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest listPaymentActivities(ListPaymentActivitiesModel listPaymentActivitiesModel) {
        BigInteger tenantId = listPaymentActivitiesModel.getTenantId();
        BigInteger branchId = listPaymentActivitiesModel.getBranchId();

        List<PaymentActivity> paymentActivities = activityMapper.listPaymentActivities(tenantId, branchId);
        ApiRest apiRest = new ApiRest();
        apiRest.setData(paymentActivities);
        apiRest.setMessage("查询所有生效的支付促销活动成功！");
        apiRest.setSuccessful(true);
        return apiRest;
    }
}
