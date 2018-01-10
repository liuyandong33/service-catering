package build.dream.erp.services;

import build.dream.common.api.ApiRest;
import build.dream.common.erp.catering.domains.Activity;
import build.dream.common.erp.catering.domains.BasicActivity;
import build.dream.common.utils.SearchModel;
import build.dream.erp.constants.Constants;
import build.dream.erp.mappers.ActivityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ActivityService {
    @Autowired
    private ActivityMapper activityMapper;

    @Transactional
    public ApiRest findAllActivities(BigInteger tenantId, BigInteger branchId) {
        SearchModel activitySearchModel = new SearchModel(true);
        activitySearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        activitySearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        List<Activity> activities = activityMapper.findAll(activitySearchModel);
        Map<BigInteger, Activity> activityMap = new HashMap<BigInteger, Activity>();
        for (Activity activity : activities) {
            activityMap.put(activity.getId(), activity);
        }

        List<List<BasicActivity>> activityDetails = activityMapper.findAllActivities();
        List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        for (List<BasicActivity> activityDetail : activityDetails) {
            BasicActivity basicActivity = activityDetail.get(0);
            Map<String, Object> activityInfo = new HashMap<String, Object>();
            activityInfo.put("activity", activityMap.get(basicActivity.getActivityId()));
            activityInfo.put("activityDetail", basicActivity);
            data.add(activityInfo);
        }
        ApiRest apiRest = new ApiRest();
        apiRest.setData(data);
        apiRest.setMessage("查询成功！");
        apiRest.setSuccessful(true);
        return apiRest;
    }
}
