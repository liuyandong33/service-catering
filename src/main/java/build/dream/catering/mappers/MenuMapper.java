package build.dream.catering.mappers;

import build.dream.common.catering.domains.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@Mapper
public interface MenuMapper {
    long insertAllMenuBranchR(@Param("menuId") BigInteger menuId,
                              @Param("tenantId") BigInteger tenantId,
                              @Param("tenantCode") String tenantCode,
                              @Param("branchIds") List<BigInteger> branchIds);

    long deleteAllMenuBranchR(@Param("menuId") BigInteger menuId, @Param("tenantId") BigInteger tenantId);

    Menu findEffectiveMenu(@Param("tenantId") BigInteger tenantId, @Param("branchId") BigInteger branchId, @Param("effectiveScope") int effectiveScope);

    List<Map<String, Object>> findMenuDetails(@Param("tenantId") BigInteger tenantId, @Param("menuId") BigInteger menuId);
}
