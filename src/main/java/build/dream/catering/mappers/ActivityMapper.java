package build.dream.catering.mappers;

import build.dream.common.erp.catering.domains.Activity;
import build.dream.common.erp.catering.domains.EffectiveActivity;
import build.dream.common.utils.SearchModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;

@Mapper
public interface ActivityMapper {
    long insert(Activity activity);

    Activity find(SearchModel searchModel);

    long count(SearchModel searchModel);

    List<Activity> findAllPaged(SearchModel searchModel);

    List<EffectiveActivity> callProcedureEffectiveActivity(@Param("tenantId") BigInteger tenantId, @Param("branchId") BigInteger branchId);

    long insertAllActivityBranchR(@Param("activityId") BigInteger activityId, @Param("tenantId") BigInteger tenantId, @Param("branchIds") List<BigInteger> branchIds);
}
