package build.dream.catering.mappers;

import build.dream.common.domains.catering.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface MenuMapper {
    long insertAllMenuBranchR(@Param("menuId") Long menuId,
                              @Param("tenantId") Long tenantId,
                              @Param("tenantCode") String tenantCode,
                              @Param("branchIds") List<Long> branchIds);

    long deleteAllMenuBranchR(@Param("menuId") Long menuId, @Param("tenantId") Long tenantId);

    Menu findEffectiveMenu(@Param("tenantId") Long tenantId, @Param("branchId") Long branchId, @Param("effectiveScope") int effectiveScope);

    List<Map<String, Object>> findAllMenuDetails(@Param("tenantId") Long tenantId, @Param("menuId") Long menuId);
}
