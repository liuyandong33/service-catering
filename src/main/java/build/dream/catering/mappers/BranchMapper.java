package build.dream.catering.mappers;

import build.dream.common.catering.domains.Branch;
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

    List<BigInteger> findAllUserIds(@Param("tenantId") BigInteger tenantId,
                                    @Param("branchId") BigInteger branchId,
                                    @Param("offset") int offset,
                                    @Param("maxResults") int maxResults);

    long countUsers(@Param("tenantId") BigInteger tenantId, @Param("branchId") BigInteger branchId);

    Branch findByTenantIdAndUserId(@Param("tenantId") BigInteger tenantId, @Param("userId") BigInteger userId);
}
