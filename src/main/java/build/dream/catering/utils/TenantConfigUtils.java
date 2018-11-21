package build.dream.catering.utils;

import build.dream.catering.constants.Constants;
import build.dream.catering.mappers.TenantConfigMapper;
import build.dream.common.catering.domains.TenantConfig;
import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.SearchModel;

import java.math.BigInteger;

public class TenantConfigUtils {
    public static TenantConfigMapper TENANT_CONFIG_MAPPER = null;

    public static TenantConfigMapper obtainTenantConfigMapper() {
        if (TENANT_CONFIG_MAPPER == null) {
            TENANT_CONFIG_MAPPER = ApplicationHandler.getBean(TenantConfigMapper.class);
        }
        return TENANT_CONFIG_MAPPER;
    }

    public static TenantConfig obtainTenantConfig(BigInteger tenantId, String name) {
        SearchModel searchModel = new SearchModel();
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        searchModel.addSearchCondition("name", Constants.SQL_OPERATION_SYMBOL_EQUAL, name);
        return obtainTenantConfigMapper().find(searchModel);
    }

    public static TenantConfig addTenantConfig(BigInteger tenantId, String name, int increment) {
        return obtainTenantConfigMapper().addTenantConfig(tenantId, name, increment);
    }
}
