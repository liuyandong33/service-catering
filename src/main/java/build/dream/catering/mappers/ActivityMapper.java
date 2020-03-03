package build.dream.catering.mappers;

import build.dream.common.domains.catering.EffectiveActivity;
import build.dream.common.domains.catering.FullReductionActivity;
import build.dream.common.domains.catering.PaymentActivity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ActivityMapper {
    List<EffectiveActivity> listEffectiveActivities(@Param("tenantId") Long tenantId, @Param("branchId") Long branchId);

    long insertAllActivityBranchR(@Param("activityId") Long activityId, @Param("tenantId") Long tenantId, @Param("branchIds") List<Long> branchIds);

    List<FullReductionActivity> listFullReductionActivities(@Param("tenantId") Long tenantId, @Param("branchId") Long branchId);

    List<PaymentActivity> listPaymentActivities(@Param("tenantId") Long tenantId, @Param("branchId") Long branchId);
}
