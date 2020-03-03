package build.dream.catering.mappers;

import build.dream.common.domains.catering.Branch;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BranchMapper {
    long insertMergeUserBranch(@Param("userId") Long userId,
                               @Param("tenantId") Long tenantId,
                               @Param("tenantCode") String tenantCode,
                               @Param("branchId") Long branchId,
                               @Param("currentUserId") Long currentUserId,
                               @Param("updatedRemark") String updatedRemark);

    List<Long> findAllUserIds(@Param("tenantId") Long tenantId,
                              @Param("branchId") Long branchId,
                              @Param("offset") int offset,
                              @Param("maxResults") int maxResults);

    long countUsers(@Param("tenantId") Long tenantId, @Param("branchId") Long branchId);

    Branch findByTenantIdAndUserId(@Param("tenantId") Long tenantId, @Param("userId") Long userId);
}
