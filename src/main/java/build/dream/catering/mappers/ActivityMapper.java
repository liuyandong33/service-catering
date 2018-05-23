package build.dream.catering.mappers;

import build.dream.common.erp.catering.domains.EffectiveActivity;
import build.dream.common.erp.catering.domains.FullReductionActivity;
import build.dream.common.erp.catering.domains.PaymentActivity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;

@Mapper
public interface ActivityMapper {
    List<EffectiveActivity> callProcedureEffectiveActivity(@Param("tenantId") BigInteger tenantId, @Param("branchId") BigInteger branchId);

    long insertAllActivityBranchR(@Param("activityId") BigInteger activityId, @Param("tenantId") BigInteger tenantId, @Param("branchIds") List<BigInteger> branchIds);

    List<FullReductionActivity> listFullReductionActivities(@Param("tenantId") BigInteger tenantId, @Param("branchId") BigInteger branchId);

    List<PaymentActivity> listPaymentActivities(@Param("tenantId") BigInteger tenantId, @Param("branchId") BigInteger branchId);
}
