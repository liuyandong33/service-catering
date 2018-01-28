package build.dream.catering.services;

import build.dream.catering.constants.Constants;
import build.dream.catering.mappers.ActivityMapper;
import build.dream.catering.mappers.BuyGiveActivityMapper;
import build.dream.catering.mappers.UniversalMapper;
import build.dream.catering.models.activity.SaveBuyGiveActivityModel;
import build.dream.common.api.ApiRest;
import build.dream.common.erp.catering.domains.Activity;
import build.dream.common.erp.catering.domains.BuyGiveActivity;
import build.dream.common.utils.CacheUtils;
import build.dream.common.utils.GsonUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ActivityService {
    @Autowired
    private ActivityMapper activityMapper;
    @Autowired
    private UniversalMapper universalMapper;
    @Autowired
    private BuyGiveActivityMapper buyGiveActivityMapper;

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
            for (Map<String, Object> fullReductionActivity : allFullReductionActivities) {
                BigInteger tenantId = BigInteger.valueOf(MapUtils.getLongValue(fullReductionActivity, "tenantId"));
                BigInteger branchId = BigInteger.valueOf(MapUtils.getLongValue(fullReductionActivity, "branchId"));
                CacheUtils.hset(Constants.KEY_FULL_REDUCTION_ACTIVITIES, tenantId + "_" + branchId, GsonUtils.toJson(fullReductionActivity));
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
        Activity activity = new Activity();
        activity.setTenantId(tenantId);
        activity.setTenantCode(tenantCode);
        activity.setBranchId(branchId);
        activity.setName(saveBuyGiveActivityModel.getName());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DEFAULT_DATE_PATTERN);
        activity.setStartTime(simpleDateFormat.parse(saveBuyGiveActivityModel.getStartTime() + " 00:00:00"));
        activity.setEndTime(simpleDateFormat.parse(saveBuyGiveActivityModel.getEndTime() + " 23:59:59"));
        activity.setType(1);
        activity.setStatus(1);
        activity.setCreateUserId(userId);
        activity.setLastUpdateUserId(userId);
        activity.setLastUpdateRemark("保存活动信息！");
        activityMapper.insert(activity);

        List<BuyGiveActivity> buyGiveActivities = new ArrayList<BuyGiveActivity>();
        List<SaveBuyGiveActivityModel.BuyGiveActivityInfo> buyGiveActivityInfos = saveBuyGiveActivityModel.getBuyGiveActivityInfos();
        for (SaveBuyGiveActivityModel.BuyGiveActivityInfo buyGiveActivityInfo : buyGiveActivityInfos) {
            BuyGiveActivity buyGiveActivity = new BuyGiveActivity();
            buyGiveActivity.setTenantId(tenantId);
            buyGiveActivity.setTenantCode(tenantCode);
            buyGiveActivity.setBranchId(branchId);
            buyGiveActivity.setActivityId(activity.getId());
            buyGiveActivity.setBuyGoodsId(buyGiveActivityInfo.getBuyGoodsId());
            buyGiveActivity.setBuyGoodsSpecificationId(buyGiveActivityInfo.getBuyGoodsSpecificationId());
            buyGiveActivity.setBuyQuantity(buyGiveActivityInfo.getBuyQuantity());
            buyGiveActivity.setGiveGoodsId(buyGiveActivityInfo.getGiveGoodsId());
            buyGiveActivity.setGiveGoodsSpecificationId(buyGiveActivityInfo.getGiveGoodsSpecificationId());
            buyGiveActivity.setGiveQuantity(buyGiveActivityInfo.getGiveQuantity());
            buyGiveActivity.setCreateUserId(userId);
            buyGiveActivity.setLastUpdateUserId(userId);
            buyGiveActivity.setLastUpdateRemark("保存买A赠B活动！");
            buyGiveActivities.add(buyGiveActivity);
        }
        buyGiveActivityMapper.insertAll(buyGiveActivities);
        ApiRest apiRest = new ApiRest();
        apiRest.setMessage("保存买A赠B活动成功！");
        apiRest.setSuccessful(true);
        return apiRest;
    }
}
