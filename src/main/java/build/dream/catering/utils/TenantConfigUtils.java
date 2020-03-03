package build.dream.catering.utils;

import build.dream.catering.constants.Constants;
import build.dream.catering.mappers.TenantConfigMapper;
import build.dream.common.domains.catering.TenantConfig;
import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.DatabaseHelper;
import build.dream.common.utils.SearchModel;

public class TenantConfigUtils {
    public static TenantConfigMapper tenantConfigMapper = null;

    public static TenantConfigMapper obtainTenantConfigMapper() {
        if (tenantConfigMapper == null) {
            tenantConfigMapper = ApplicationHandler.getBean(TenantConfigMapper.class);
        }
        return tenantConfigMapper;
    }

    public static TenantConfig obtainTenantConfig(Long tenantId, String name) {
        SearchModel searchModel = new SearchModel();
        searchModel.addSearchCondition(TenantConfig.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        searchModel.addSearchCondition("name", Constants.SQL_OPERATION_SYMBOL_EQUAL, name);
        return DatabaseHelper.find(TenantConfig.class, searchModel);
    }

    public static TenantConfig addTenantConfig(Long tenantId, String name, int increment) {
        obtainTenantConfigMapper().updateTenantConfig(tenantId, name, increment);
        return obtainTenantConfig(tenantId, name);
    }
}
