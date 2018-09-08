package build.dream.catering.utils;

import build.dream.catering.constants.Constants;
import build.dream.common.api.ApiRest;
import build.dream.common.saas.domains.Tenant;
import build.dream.common.utils.ProxyUtils;
import build.dream.common.utils.ValidateUtils;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class TenantUtils {
    public static Tenant obtainTenantInfo(BigInteger tenantId) throws IOException {
        Map<String, String> obtainTenantInfoRequestParameters = new HashMap<String, String>();
        obtainTenantInfoRequestParameters.put("tenantId", tenantId.toString());

        ApiRest apiRest = ProxyUtils.doGetWithRequestParameters(Constants.SERVICE_NAME_PLATFORM, "tenant", "obtainTenantInfo", obtainTenantInfoRequestParameters);
        ValidateUtils.isTrue(apiRest.isSuccessful(), apiRest.getError());
        return (Tenant) apiRest.getData();
    }

    public static Tenant obtainTenantInfo(String tenantCode) throws IOException {
        Map<String, String> obtainTenantInfoRequestParameters = new HashMap<String, String>();
        obtainTenantInfoRequestParameters.put("tenantCode", tenantCode);
        ApiRest apiRest = ProxyUtils.doGetWithRequestParameters(Constants.SERVICE_NAME_PLATFORM, "tenant", "obtainTenantInfo", obtainTenantInfoRequestParameters);
        ValidateUtils.isTrue(apiRest.isSuccessful(), apiRest.getError());
        return (Tenant) apiRest.getData();
    }
}
