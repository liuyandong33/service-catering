package build.dream.catering.utils;

import build.dream.common.saas.domains.TenantSecretKey;
import build.dream.common.utils.CacheUtils;
import build.dream.common.utils.GsonUtils;
import build.dream.catering.constants.Constants;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

public class TenantSecretKeyUtils {
    public static TenantSecretKey obtainTenantSecretKey(String tenantId) {
        String tenantSecretKeyJson = CacheUtils.hget(Constants.KEY_TENANT_SECRET_KEYS, tenantId);
        Validate.isTrue(StringUtils.isNotBlank(tenantSecretKeyJson), "未检索到商户秘钥配置！");
        return GsonUtils.fromJson(tenantSecretKeyJson, TenantSecretKey.class);
    }
}
