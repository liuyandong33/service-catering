package build.dream.catering.services;

import build.dream.catering.constants.Constants;
import build.dream.catering.mappers.*;
import build.dream.catering.models.activity.SaveBuyGiveActivityModel;
import build.dream.catering.models.activity.SaveFullReductionActivityModel;
import build.dream.catering.models.activity.SaveSpecialGoodsActivityModel;
import build.dream.catering.utils.ActivityUtils;
import build.dream.catering.utils.CanNotOperateReasonUtils;
import build.dream.common.api.ApiRest;
import build.dream.common.erp.catering.domains.*;
import build.dream.common.utils.CacheUtils;
import build.dream.common.utils.GsonUtils;
import build.dream.common.utils.SearchModel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ActivityService {
    @Autowired
    private ActivityMapper activityMapper;
    @Autowired
    private UniversalMapper universalMapper;
    @Autowired
    private BuyGiveActivityMapper buyGiveActivityMapper;
    @Autowired
    private FullReductionActivityMapper fullReductionActivityMapper;
    @Autowired
    private SpecialGoodsActivityMapper specialGoodsActivityMapper;
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private GoodsSpecificationMapper goodsSpecificationMapper;
    @Autowired
    private CanNotOperateReasonMapper canNotOperateReasonMapper;

    public ApiRest test() {
        String findAllBuyGiveActivitiesSql = "SELECT " +
                "activity.tenant_id, " +
                "activity.branch_id, " +
                "activity.tenant_code, " +
                "activity.id AS activity_id, " +
                "activity.name AS activity_name, " +
                "activity.type AS activity_type, " +
                "activity.status AS activity_status, " +
                "activity.start_time, " +
                "activity.end_time, " +
                "buy_goods.id AS buy_goods_id, " +
                "buy_goods.name AS buy_goods_name, " +
                "buy_goods_specification.id AS buy_goods_specification_id, " +
                "buy_goods_specification.name AS buy_goods_specification_name, " +
                "buy_give_activity.buy_quantity, " +
                "give_goods.id AS give_goods_id, " +
                "give_goods.name AS give_goods_name, " +
                "give_goods_specification.id AS give_goods_specification_id, " +
                "give_goods_specification.name AS give_goods_specification_name, " +
                "buy_give_activity.give_quantity " +
                "FROM activity " +
                "LEFT JOIN buy_give_activity ON activity.id = buy_give_activity.activity_id AND buy_give_activity.deleted = 0 " +
                "LEFT JOIN goods AS buy_goods ON buy_goods.id = buy_give_activity.buy_goods_id " +
                "LEFT JOIN goods_specification AS buy_goods_specification ON buy_goods_specification.id = buy_give_activity.buy_goods_specification_id " +
                "LEFT JOIN goods AS give_goods ON give_goods.id = buy_give_activity.give_goods_id " +
                "LEFT JOIN goods_specification AS give_goods_specification ON give_goods_specification.id = buy_give_activity.give_goods_specification_id " +
                "WHERE activity.tenant_id = #{tenantId} " +
                "AND activity.branch_id = #{branchId} " +
                "AND activity.status = #{status} " +
                "AND activity.type = #{type} " +
                "AND activity.deleted = 0";
        Map<String, Object> findAllBuyGiveActivitiesParameters = new HashMap<String, Object>();
        findAllBuyGiveActivitiesParameters.put("sql", findAllBuyGiveActivitiesSql);
        findAllBuyGiveActivitiesParameters.put("tenantId", BigInteger.ONE);
        findAllBuyGiveActivitiesParameters.put("branchId", BigInteger.ONE);
        findAllBuyGiveActivitiesParameters.put("status", 2);
        findAllBuyGiveActivitiesParameters.put("type", 1);
        List<Map<String, Object>> allBuyGiveActivities = universalMapper.executeQuery(findAllBuyGiveActivitiesParameters);

        if (CollectionUtils.isNotEmpty(allBuyGiveActivities)) {
            for (Map<String, Object> buyGiveActivity : allBuyGiveActivities) {
                BigInteger tenantId = BigInteger.valueOf(MapUtils.getLongValue(buyGiveActivity, "tenantId"));
                BigInteger branchId = BigInteger.valueOf(MapUtils.getLongValue(buyGiveActivity, "branchId"));
                BigInteger buyGoodsId = BigInteger.valueOf(MapUtils.getLongValue(buyGiveActivity, "buyGoodsId"));
                BigInteger buyGoodsSpecificationId = BigInteger.valueOf(MapUtils.getLongValue(buyGiveActivity, "buyGoodsSpecificationId"));
                CacheUtils.hset(Constants.KEY_BUY_GIVE_ACTIVITIES, tenantId + "_" + branchId + "_" + buyGoodsId + "_" + buyGoodsSpecificationId, GsonUtils.toJson(buyGiveActivity));
            }
        }

        String findAllFullReductionActivitiesSql = "SELECT " +
                "activity.tenant_id, " +
                "activity.tenant_code, " +
                "activity.branch_id, " +
                "activity.id AS activity_id, " +
                "activity.name AS activity_name, " +
                "activity.type AS activity_type, " +
                "activity.status AS activity_status, " +
                "activity.start_time, " +
                "activity.end_time, " +
                "full_reduction_activity.total_amount, " +
                "full_reduction_activity.discount_type, " +
                "full_reduction_activity.discount_rate, " +
                "full_reduction_activity.discount_amount " +
                "FROM activity " +
                "LEFT JOIN full_reduction_activity ON activity.id = full_reduction_activity.activity_id " +
                "WHERE activity.tenant_id = #{tenantId} " +
                "AND activity.branch_id = #{branchId} " +
                "AND activity.status = #{status} " +
                "AND activity.type = #{type} " +
                "AND activity.deleted = 0";
        Map<String, Object> findAllFullReductionActivitiesParameters = new HashMap<String, Object>();
        findAllFullReductionActivitiesParameters.put("sql", findAllFullReductionActivitiesSql);
        findAllFullReductionActivitiesParameters.put("tenantId", BigInteger.ONE);
        findAllFullReductionActivitiesParameters.put("branchId", BigInteger.ONE);
        findAllFullReductionActivitiesParameters.put("status", 2);
        findAllFullReductionActivitiesParameters.put("type", 2);
        List<Map<String, Object>> allFullReductionActivities = universalMapper.executeQuery(findAllFullReductionActivitiesParameters);
        if (CollectionUtils.isNotEmpty(allFullReductionActivities)) {
            Map<String, List<Map<String, Object>>> fullReductionActivitiesMap = new HashMap<String, List<Map<String, Object>>>();
            for (Map<String, Object> fullReductionActivity : allFullReductionActivities) {
                BigInteger tenantId = BigInteger.valueOf(MapUtils.getLongValue(fullReductionActivity, "tenantId"));
                BigInteger branchId = BigInteger.valueOf(MapUtils.getLongValue(fullReductionActivity, "branchId"));
                List<Map<String, Object>> fullReductionActivities = fullReductionActivitiesMap.get(tenantId + "_" + branchId);
                if (fullReductionActivities == null) {
                    fullReductionActivities = new ArrayList<Map<String, Object>>();
                    fullReductionActivitiesMap.put(tenantId + "_" + branchId, fullReductionActivities);
                }
                fullReductionActivities.add(fullReductionActivity);
            }
            for (Map.Entry<String, List<Map<String, Object>>> entry : fullReductionActivitiesMap.entrySet()) {
                CacheUtils.hset(Constants.KEY_FULL_REDUCTION_ACTIVITIES, entry.getKey(), GsonUtils.toJson(entry.getValue()));
            }
        }


        String findAllSpecialGoodsActivitiesSql = "SELECT " +
                "activity.tenant_id, " +
                "activity.tenant_code, " +
                "activity.branch_id, " +
                "activity.id AS activity_id, " +
                "activity.name AS activity_name, " +
                "activity.type AS activity_type, " +
                "activity.status AS activity_status, " +
                "activity.start_time, " +
                "activity.end_time, " +
                "goods.id AS goods_id, " +
                "goods.name AS goods_name, " +
                "goods_specification.id AS goods_specification_id, " +
                "goods_specification.name AS goods_specification_name, " +
                "special_goods_activity.discount_type, " +
                "special_goods_activity.special_price, " +
                "special_goods_activity.discount_rate " +
                "FROM activity " +
                "LEFT JOIN special_goods_activity ON activity.id = special_goods_activity.activity_id " +
                "LEFT JOIN goods ON goods.id = special_goods_activity.goods_id " +
                "LEFT JOIN goods_specification ON goods_specification.id = special_goods_activity.goods_specification_id " +
                "WHERE activity.tenant_id = #{tenantId} " +
                "AND activity.branch_id = #{branchId} " +
                "AND activity.status = #{status} " +
                "AND activity.type = #{type} " +
                "AND activity.deleted = 0";
        Map<String, Object> findAllSpecialGoodsActivitiesParameters = new HashMap<String, Object>();
        findAllSpecialGoodsActivitiesParameters.put("sql", findAllSpecialGoodsActivitiesSql);
        findAllSpecialGoodsActivitiesParameters.put("tenantId", BigInteger.ONE);
        findAllSpecialGoodsActivitiesParameters.put("branchId", BigInteger.ONE);
        findAllSpecialGoodsActivitiesParameters.put("status", 2);
        findAllSpecialGoodsActivitiesParameters.put("type", 3);
        List<Map<String, Object>> allSpecialGoodsActivities = universalMapper.executeQuery(findAllSpecialGoodsActivitiesParameters);
        if (CollectionUtils.isNotEmpty(allSpecialGoodsActivities)) {
            for (Map<String, Object> specialGoodsActivity : allSpecialGoodsActivities) {
                BigInteger tenantId = BigInteger.valueOf(MapUtils.getLongValue(specialGoodsActivity, "tenantId"));
                BigInteger branchId = BigInteger.valueOf(MapUtils.getLongValue(specialGoodsActivity, "branchId"));
                BigInteger buyGoodsId = BigInteger.valueOf(MapUtils.getLongValue(specialGoodsActivity, "goodsId"));
                BigInteger buyGoodsSpecificationId = BigInteger.valueOf(MapUtils.getLongValue(specialGoodsActivity, "goodsSpecificationId"));
                CacheUtils.hset(Constants.KEY_SPECIAL_GOODS_ACTIVITIES, tenantId + "_" + branchId + "_" + buyGoodsId + "_" + buyGoodsSpecificationId, GsonUtils.toJson(specialGoodsActivity));
            }
        }

        return new ApiRest(allBuyGiveActivities, "查询成功！");
    }

    @Transactional(rollbackFor = Exception.class)
    public ApiRest saveBuyGiveActivity(SaveBuyGiveActivityModel saveBuyGiveActivityModel) throws ParseException {
        BigInteger tenantId = saveBuyGiveActivityModel.getTenantId();
        String tenantCode = saveBuyGiveActivityModel.getTenantCode();
        BigInteger branchId = saveBuyGiveActivityModel.getBranchId();
        BigInteger userId = saveBuyGiveActivityModel.getUserId();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DEFAULT_DATE_PATTERN);
        Date startTime = simpleDateFormat.parse(saveBuyGiveActivityModel.getStartTime() + " 00:00:00");
        Date endTime = simpleDateFormat.parse(saveBuyGiveActivityModel.getEndTime() + " 23:59:59");
        Validate.isTrue(endTime.after(startTime), "活动结束时间必须大于开始时间！");

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
        goodsSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        goodsSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        List<Goods> goodses = goodsMapper.findAll(goodsSearchModel);

        // 封装商品id与商品之间的map
        Map<BigInteger, Goods> goodsMap = new HashMap<BigInteger, Goods>();
        for (Goods goods : goodses) {
            goodsMap.put(goods.getId(), goods);
        }

        SearchModel canNotOperateReasonSearchModel = new SearchModel();
        canNotOperateReasonSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        canNotOperateReasonSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        canNotOperateReasonSearchModel.addSearchCondition("table_id", Constants.SQL_OPERATION_SYMBOL_IN, goodsIds);
        canNotOperateReasonSearchModel.addSearchCondition("operate_type", Constants.SQL_OPERATION_SYMBOL_EQUALS, 4);
        CanNotOperateReason persistenceCanNotOperateReason = canNotOperateReasonMapper.find(canNotOperateReasonSearchModel);
        if (persistenceCanNotOperateReason != null) {
            Goods goods = goodsMap.get(persistenceCanNotOperateReason.getTableId());
            Validate.notNull(goods, "商品不存在！");

            throw new RuntimeException(String.format(persistenceCanNotOperateReason.getReason(), goods.getName()));
        }

        // 查询出涉及的所有商品规格
        SearchModel goodsSpecificationSearchModel = new SearchModel(true);
        goodsSpecificationSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_IN, goodsSpecificationIds);
        goodsSpecificationSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        goodsSpecificationSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        List<GoodsSpecification> goodsSpecifications = goodsSpecificationMapper.findAll(goodsSpecificationSearchModel);

        // 封装商品规格id与商品规格之间的map
        Map<BigInteger, GoodsSpecification> goodsSpecificationMap = new HashMap<BigInteger, GoodsSpecification>();
        for (GoodsSpecification goodsSpecification : goodsSpecifications) {
            goodsSpecificationMap.put(goodsSpecification.getId(), goodsSpecification);
        }

        Activity activity = ActivityUtils.constructActivity(tenantId, tenantCode, branchId, saveBuyGiveActivityModel.getName(), 1, startTime, endTime, userId, "保存活动信息！");
        activityMapper.insert(activity);

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
            buyGiveActivity.setBranchId(branchId);
            buyGiveActivity.setActivityId(activity.getId());
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
            canNotOperateReasons.add(CanNotOperateReasonUtils.constructCanNotOperateReason(tenantId, tenantCode, tenantId, buyGoods.getId(), "goods", activity.getId(), "activity", 3, reason));
            canNotOperateReasons.add(CanNotOperateReasonUtils.constructCanNotOperateReason(tenantId, tenantCode, tenantId, giveGoods.getId(), "goods", activity.getId(), "activity", 3, reason));
        }
        buyGiveActivityMapper.insertAll(buyGiveActivities);
        canNotOperateReasonMapper.insertAll(canNotOperateReasons);
        ApiRest apiRest = new ApiRest();
        apiRest.setMessage("保存买A赠B活动成功！");
        apiRest.setSuccessful(true);
        return apiRest;
    }

    @Transactional(rollbackFor = Exception.class)
    public ApiRest saveFullReductionActivity(SaveFullReductionActivityModel saveFullReductionActivityModel) throws ParseException {
        BigInteger tenantId = saveFullReductionActivityModel.getTenantId();
        String tenantCode = saveFullReductionActivityModel.getTenantCode();
        BigInteger branchId = saveFullReductionActivityModel.getBranchId();
        BigInteger userId = saveFullReductionActivityModel.getUserId();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DEFAULT_DATE_PATTERN);
        Date startTime = simpleDateFormat.parse(saveFullReductionActivityModel.getStartTime() + " 00:00:00");
        Date endTime = simpleDateFormat.parse(saveFullReductionActivityModel.getEndTime() + " 23:59:59");

        Validate.isTrue(endTime.after(startTime), "活动结束时间必须大于开始时间！");

        String sql = "SELECT * " +
                "FROM activity " +
                "WHERE tenant_id = #{tenantId} " +
                "AND branch_id = #{branchId} " +
                "AND deleted = 0 " +
                "AND type = 2 " +
                "AND status IN (1, 2) " +
                "AND ((start_time <= #{startTime} AND end_time >= #{endTime}) OR (start_time >= #{startTime} AND start_time <= #{endTime}) OR (end_time >= #{startTime} AND end_time <= #{endTime})) " +
                "LIMIT 0, 1";
        Map<String, Object> findActivityParameters = new HashMap<String, Object>();
        findActivityParameters.put("sql", sql);
        findActivityParameters.put("tenantId", tenantId);
        findActivityParameters.put("branchId", branchId);
        findActivityParameters.put("startTime", startTime);
        findActivityParameters.put("endTime", endTime);
        Map<String, Object> activityMap = universalMapper.executeUniqueResultQuery(findActivityParameters);
        if (MapUtils.isNotEmpty(activityMap)) {
            throw new RuntimeException("活动日期与促销活动【" + activityMap.get("name") + "】在时间上冲突！");
        }


        Activity activity = ActivityUtils.constructActivity(tenantId, tenantCode, branchId, saveFullReductionActivityModel.getName(), 2, startTime, endTime, userId, "保存活动信息！");
        activityMapper.insert(activity);

        FullReductionActivity fullReductionActivity = ActivityUtils.constructFullReductionActivity(tenantId, tenantCode, branchId, activity.getId(), saveFullReductionActivityModel.getTotalAmount(), saveFullReductionActivityModel.getDiscountType(), saveFullReductionActivityModel.getDiscountRate(), saveFullReductionActivityModel.getDiscountAmount(), userId, "保存满减活动！");
        fullReductionActivityMapper.insert(fullReductionActivity);

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
        BigInteger branchId = saveSpecialGoodsActivityModel.getBranchId();
        BigInteger userId = saveSpecialGoodsActivityModel.getUserId();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DEFAULT_DATE_PATTERN);
        Date startTime = simpleDateFormat.parse(saveSpecialGoodsActivityModel.getStartTime() + " 00:00:00");
        Date endTime = simpleDateFormat.parse(saveSpecialGoodsActivityModel.getEndTime() + " 23:59:59");
        Validate.isTrue(endTime.after(startTime), "活动结束时间必须大于开始时间！");

        String sql = "SELECT * " +
                "FROM activity " +
                "WHERE tenant_id = #{tenantId} " +
                "AND branch_id = #{branchId} " +
                "AND deleted = 0 " +
                "AND type = 3 " +
                "AND status IN (1, 2) " +
                "AND ((start_time <= #{startTime} AND end_time >= #{endTime}) OR (start_time >= #{startTime} AND start_time <= #{endTime}) OR (end_time >= #{startTime} AND end_time <= #{endTime})) " +
                "LIMIT 0, 1";
        Map<String, Object> findActivityParameters = new HashMap<String, Object>();
        findActivityParameters.put("sql", sql);
        findActivityParameters.put("tenantId", tenantId);
        findActivityParameters.put("branchId", branchId);
        findActivityParameters.put("startTime", startTime);
        findActivityParameters.put("endTime", endTime);
        Map<String, Object> activityMap = universalMapper.executeUniqueResultQuery(findActivityParameters);
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
        goodsSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        goodsSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        List<Goods> goodses = goodsMapper.findAll(goodsSearchModel);

        // 封装商品id与商品之间的map
        Map<BigInteger, Goods> goodsMap = new HashMap<BigInteger, Goods>();
        for (Goods goods : goodses) {
            goodsMap.put(goods.getId(), goods);
        }

        SearchModel canNotOperateReasonSearchModel = new SearchModel();
        canNotOperateReasonSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        canNotOperateReasonSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        canNotOperateReasonSearchModel.addSearchCondition("table_id", Constants.SQL_OPERATION_SYMBOL_IN, goodsIds);
        canNotOperateReasonSearchModel.addSearchCondition("operate_type", Constants.SQL_OPERATION_SYMBOL_EQUALS, 4);
        CanNotOperateReason persistenceCanNotOperateReason = canNotOperateReasonMapper.find(canNotOperateReasonSearchModel);
        if (persistenceCanNotOperateReason != null) {
            Goods goods = goodsMap.get(persistenceCanNotOperateReason.getTableId());
            Validate.notNull(goods, "商品不存在！");

            throw new RuntimeException(String.format(persistenceCanNotOperateReason.getReason(), goods.getName()));
        }

        // 查询出涉及的所有商品规格
        SearchModel goodsSpecificationSearchModel = new SearchModel(true);
        goodsSpecificationSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_IN, goodsSpecificationIds);
        goodsSpecificationSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        goodsSpecificationSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        List<GoodsSpecification> goodsSpecifications = goodsSpecificationMapper.findAll(goodsSpecificationSearchModel);

        // 封装商品规格id与商品规格之间的map
        Map<BigInteger, GoodsSpecification> goodsSpecificationMap = new HashMap<BigInteger, GoodsSpecification>();
        for (GoodsSpecification goodsSpecification : goodsSpecifications) {
            goodsSpecificationMap.put(goodsSpecification.getId(), goodsSpecification);
        }

        Activity activity = ActivityUtils.constructActivity(tenantId, tenantCode, branchId, saveSpecialGoodsActivityModel.getName(), 3, startTime, endTime, userId, "保存活动信息！");
        activityMapper.insert(activity);

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
            specialGoodsActivity.setBranchId(branchId);
            specialGoodsActivity.setActivityId(activity.getId());
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
            CanNotOperateReason canNotOperateReason = CanNotOperateReasonUtils.constructCanNotOperateReason(tenantId, tenantCode, branchId, goods.getId(), "goods", activity.getId(), "activity", 3, reason);
            canNotOperateReasons.add(canNotOperateReason);

            String usedOtherActivityReason = "商品【%s】已参与促销活动【" + activity.getName() + "】，不可参与其他促销活动！";
            CanNotOperateReason canNotUsedOtherActivityReason = CanNotOperateReasonUtils.constructCanNotOperateReason(tenantId, tenantCode, branchId, goods.getId(), "goods", activity.getId(), "activity", 4, usedOtherActivityReason);
            canNotOperateReasons.add(canNotUsedOtherActivityReason);
        }

        specialGoodsActivityMapper.insertAll(specialGoodsActivities);
        canNotOperateReasonMapper.insertAll(canNotOperateReasons);
        ApiRest apiRest = new ApiRest();
        apiRest.setMessage("保存特价商品活动成功！");
        apiRest.setSuccessful(true);
        return apiRest;
    }
}
