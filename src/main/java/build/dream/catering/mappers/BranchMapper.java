package build.dream.catering.mappers;

import build.dream.common.erp.catering.domains.Branch;
import build.dream.common.utils.SearchModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;

@Mapper
public interface BranchMapper {
    long insertMergeUserBranch(@Param("userId") BigInteger userId, @Param("tenantId") BigInteger tenantId, @Param("branchId") BigInteger branchId);

    List<BigInteger> findAllUserIds(SearchModel searchModel);

    long countUsers(SearchModel searchModel);

    Branch findByTenantIdAndUserId(@Param("tenantId") BigInteger tenantId, @Param("userId") BigInteger userId);
}
