package build.dream.erp.mappers;

import build.dream.common.saas.domains.Tenant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TenantMapper {
    int insert(Tenant tenant);
    List<build.dream.erp.domains.Tenant> findAll(@Param("pageNumber") int pageNumber, @Param("pageSize") int pageSize);
}
