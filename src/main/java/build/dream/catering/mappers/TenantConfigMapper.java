package build.dream.catering.mappers;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TenantConfigMapper {
    long updateTenantConfig(@Param("tenantId") Long tenantId,
                            @Param("name") String name,
                            @Param("increment") Integer increment);
}
