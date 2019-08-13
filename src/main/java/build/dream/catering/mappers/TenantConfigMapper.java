package build.dream.catering.mappers;

import build.dream.common.domains.catering.TenantConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;

@Mapper
public interface TenantConfigMapper {
    TenantConfig addTenantConfig(@Param("tenantId") BigInteger tenantId, @Param("name") String name, @Param("increment") Integer increment);
}
