package build.dream.catering.mappers;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;

@Mapper
public interface TenantConfigMapper {
    long updateTenantConfig(@Param("tenantId") BigInteger tenantId,
                            @Param("name") String name,
                            @Param("increment") Integer increment);
}
