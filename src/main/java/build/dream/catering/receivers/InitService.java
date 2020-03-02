package build.dream.catering.receivers;

import build.dream.catering.models.init.InitTenantConfigModel;
import build.dream.common.api.ApiRest;
import build.dream.common.domains.catering.TenantConfig;
import build.dream.common.utils.DatabaseHelper;
import build.dream.common.utils.SearchModel;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;

@Service
public class InitService {
    @Transactional(rollbackFor = Exception.class)
    public ApiRest initTenantConfig(InitTenantConfigModel initTenantConfigModel) {
        BigInteger tenantId = initTenantConfigModel.getTenantId();
        String tenantCode = initTenantConfigModel.getTenantCode();

        SearchModel searchModel = SearchModel.builder()
                .equal("tenant_id", BigInteger.ZERO)
                .build();
        List<TenantConfig> tenantConfigs = DatabaseHelper.findAll(TenantConfig.class, searchModel);
        if (CollectionUtils.isNotEmpty(tenantConfigs)) {
            tenantConfigs.forEach(tenantConfig -> {
                tenantConfig.setTenantId(tenantId);
                tenantConfig.setTenantCode(tenantCode);
                tenantConfig.setCreatedTime(null);
                tenantConfig.setUpdatedTime(null);
                tenantConfig.setUpdatedRemark("初始化商户配置！");
            });
            DatabaseHelper.insertAll(tenantConfigs);
        }
        return ApiRest.builder().message("初始化商户配置成功！").successful(true).build();
    }
}
