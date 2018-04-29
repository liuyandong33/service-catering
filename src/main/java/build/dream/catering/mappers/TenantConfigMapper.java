package build.dream.catering.mappers;

import build.dream.common.erp.catering.domains.TenantConfig;
import build.dream.common.utils.SearchModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;

@Mapper
public interface TenantConfigMapper {
    TenantConfig find(SearchModel searchModel);

    TenantConfig addTenantConfig(@Param("tenantId") BigInteger tenantId, @Param("name") String name, @Param("increment") Integer increment);
}
