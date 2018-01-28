package build.dream.catering.services;

import build.dream.catering.constants.Constants;
import build.dream.catering.mappers.ActivityMapper;
import build.dream.catering.mappers.UniversalMapper;
import build.dream.common.api.ApiRest;
import build.dream.common.utils.CacheUtils;
import build.dream.common.utils.GsonUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ActivityService {
    @Autowired
    private ActivityMapper activityMapper;
    @Autowired
    private UniversalMapper universalMapper;

    public ApiRest test() {
        String sql = "SELECT " +
                "activity.tenant_id, " +
                "activity.branch_id, " +
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
                "activity_buy_give.buy_quantity, " +
                "give_goods.id AS give_goods_id, " +
                "give_goods.name AS give_goods_name, " +
                "give_goods_specification.id AS give_goods_specification_id, " +
                "give_goods_specification.name AS give_goods_specification_name, " +
                "activity_buy_give.give_quantity " +
                "FROM activity " +
                "LEFT JOIN activity_buy_give ON activity.id = activity_buy_give.activity_id AND activity_buy_give.deleted = 0 " +
                "LEFT JOIN goods AS buy_goods ON buy_goods.id = activity_buy_give.buy_goods_id " +
                "LEFT JOIN goods_specification AS buy_goods_specification ON buy_goods_specification.id = activity_buy_give.buy_goods_specification_id " +
                "LEFT JOIN goods AS give_goods ON give_goods.id = activity_buy_give.give_goods_id " +
                "LEFT JOIN goods_specification AS give_goods_specification ON give_goods_specification.id = activity_buy_give.give_goods_specification_id " +
                "WHERE activity.tenant_id = #{tenantId} " +
                "AND activity.branch_id = #{branchId} " +
                "AND activity.status = #{status} " +
                "AND activity.type = #{type} " +
                "AND activity.deleted = 0";
        Map<String, Object> findAllBuyGiveActivityParameters = new HashMap<String, Object>();
        findAllBuyGiveActivityParameters.put("sql", sql);
        findAllBuyGiveActivityParameters.put("tenantId", BigInteger.ONE);
        findAllBuyGiveActivityParameters.put("branchId", BigInteger.ONE);
        findAllBuyGiveActivityParameters.put("status", 2);
        findAllBuyGiveActivityParameters.put("type", 1);
        List<Map<String, Object>> allBuyGiveActivities = universalMapper.executeQuery(findAllBuyGiveActivityParameters);

        if (CollectionUtils.isNotEmpty(allBuyGiveActivities)) {
            for (Map<String, Object> buyGiveActivity : allBuyGiveActivities) {
                BigInteger tenantId = BigInteger.valueOf(MapUtils.getLongValue(buyGiveActivity, "tenantId"));
                BigInteger branchId = BigInteger.valueOf(MapUtils.getLongValue(buyGiveActivity, "branchId"));
                BigInteger buyGoodsId = BigInteger.valueOf(MapUtils.getLongValue(buyGiveActivity, "buyGoodsId"));
                BigInteger buyGoodsSpecificationId = BigInteger.valueOf(MapUtils.getLongValue(buyGiveActivity, "buyGoodsSpecificationId"));
                CacheUtils.hset(Constants.KEY_BUY_GIVE_ACTIVITIES, tenantId + "_" + branchId + "_" + buyGoodsId + "_" + buyGoodsSpecificationId, GsonUtils.toJson(buyGiveActivity));
            }
        }

        return new ApiRest(allBuyGiveActivities, "查询成功！");
    }
}
