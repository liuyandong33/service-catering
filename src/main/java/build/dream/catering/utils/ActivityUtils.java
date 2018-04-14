package build.dream.catering.utils;

import build.dream.common.erp.catering.domains.Activity;
import build.dream.common.erp.catering.domains.FullReductionActivity;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.util.Date;

public class ActivityUtils {
    public static Activity constructActivity(BigInteger tenantId, String tenantCode, String name, Integer type, Date startDate, Time startTime, Date endDate, Time endTime, BigInteger userId, String lastUpdateRemark) {
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
        if (currentTimeMillis >= startTime.getTime() && currentTimeMillis <= endTime.getTime()) {
            activity.setStatus(2);
        } else {
            activity.setStatus(1);
        }
        activity.setCreateUserId(userId);
        activity.setLastUpdateUserId(userId);
        activity.setLastUpdateRemark(lastUpdateRemark);
        return activity;
    }

    public static FullReductionActivity constructFullReductionActivity(BigInteger tenantId, String tenantCode, BigInteger branchId, BigInteger activityId, BigDecimal totalAmount, Integer discountType, BigDecimal discountRate, BigDecimal discountAmount, BigInteger userId, String lastUpdateRemark) {
        FullReductionActivity fullReductionActivity = new FullReductionActivity();
        fullReductionActivity.setTenantId(tenantId);
        fullReductionActivity.setTenantCode(tenantCode);
        fullReductionActivity.setBranchId(branchId);
        fullReductionActivity.setActivityId(activityId);
        fullReductionActivity.setTotalAmount(totalAmount);
        fullReductionActivity.setDiscountType(discountType);
        fullReductionActivity.setDiscountRate(discountRate);
        fullReductionActivity.setDiscountAmount(discountAmount);
        fullReductionActivity.setCreateUserId(userId);
        fullReductionActivity.setLastUpdateUserId(userId);
        fullReductionActivity.setLastUpdateRemark(lastUpdateRemark);
        return fullReductionActivity;
    }
}
