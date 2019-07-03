package build.dream.catering.mappers;

import build.dream.common.catering.domains.Branch;
import build.dream.common.utils.SearchCondition;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;

@Mapper
public interface BranchMapper {
    long insertMergeUserBranch(@Param("userId") BigInteger userId,
                               @Param("tenantId") BigInteger tenantId,
                               @Param("tenantCode") String tenantCode,
                               @Param("branchId") BigInteger branchId,
                               @Param("currentUserId") BigInteger currentUserId,
                               @Param("updatedRemark") String updatedRemark);

    List<BigInteger> findAllUserIds(@Param("searchConditions") List<SearchCondition> searchConditions, @Param("offset") int offset, @Param("maxResults") int maxResults);

    long countUsers(@Param("searchConditions") List<SearchCondition> searchConditions);

    Branch findByTenantIdAndUserId(@Param("tenantId") BigInteger tenantId, @Param("userId") BigInteger userId);
}
