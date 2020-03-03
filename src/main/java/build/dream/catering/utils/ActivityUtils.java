package build.dream.catering.utils;

import build.dream.catering.mappers.ActivityMapper;
import build.dream.common.domains.catering.Activity;
import build.dream.common.domains.catering.EffectiveActivity;
import build.dream.common.domains.catering.FullReductionActivity;
import build.dream.common.utils.ApplicationHandler;

import java.sql.Time;
import java.util.Date;
import java.util.List;

public class ActivityUtils {
    private static ActivityMapper activityMapper;

    private static ActivityMapper obtainActivityMapper() {
        if (activityMapper == null) {
            activityMapper = ApplicationHandler.getBean(ActivityMapper.class);
        }
        return activityMapper;
    }

    public static List<EffectiveActivity> listEffectiveActivities(Long tenantId, Long branchId) {
        return obtainActivityMapper().listEffectiveActivities(tenantId, branchId);
    }

    public static Activity constructActivity(Long tenantId, String tenantCode, String name, Integer type, Date startDate, Time startTime, Date endDate, Time endTime, int weekSign, Long userId, String lastUpdateRemark) {
        Activity activity = new Activity();
        activity.setTenantId(tenantId);
        activity.setTenantCode(tenantCode);
        activity.setName(name);
        activity.setType(type);
        activity.setStartDate(startDate);
        activity.setStartTime(startTime);
        activity.setEndDate(endDate);
        activity.setEndTime(endTime);
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis >= startDate.getTime() && currentTimeMillis <= endDate.getTime()) {
            activity.setStatus(2);
        } else {
            activity.setStatus(1);
        }
        activity.setWeekSign(weekSign);
        activity.setCreatedUserId(userId);
        activity.setUpdatedUserId(userId);
        activity.setUpdatedRemark(lastUpdateRemark);
        return activity;
    }

    public static FullReductionActivity constructFullReductionActivity(Long tenantId, String tenantCode, Long activityId, Double totalAmount, Integer discountType, Double discountRate, Double discountAmount, Long userId, String lastUpdateRemark) {
        FullReductionActivity fullReductionActivity = new FullReductionActivity();
        fullReductionActivity.setTenantId(tenantId);
        fullReductionActivity.setTenantCode(tenantCode);
        fullReductionActivity.setActivityId(activityId);
        fullReductionActivity.setTotalAmount(totalAmount);
        fullReductionActivity.setDiscountType(discountType);
        fullReductionActivity.setDiscountRate(discountRate);
        fullReductionActivity.setDiscountAmount(discountAmount);
        fullReductionActivity.setCreatedUserId(userId);
        fullReductionActivity.setUpdatedUserId(userId);
        fullReductionActivity.setUpdatedRemark(lastUpdateRemark);
        return fullReductionActivity;
    }
}
