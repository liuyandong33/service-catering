package build.dream.erp.services;

import build.dream.common.api.ApiRest;
import build.dream.common.erp.domains.Branch;
import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.ProxyUtils;
import build.dream.erp.constants.Constants;
import build.dream.erp.mappers.BranchMapper;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {
    @Autowired
    private BranchMapper branchMapper;

    @Transactional(readOnly = true)
    public ApiRest obtainUserInfo(String loginName) throws IOException {
        Map<String, String> obtainUserInfoRequestParameters = new HashMap<String, String>();
        obtainUserInfoRequestParameters.put("loginName", loginName);
        String obtainUserInfoResult = ProxyUtils.doGetWithRequestParameters(Constants.SERVICE_NAME_PLATFORM, "user", "obtainUserInfo", obtainUserInfoRequestParameters);
        ApiRest obtainUserInfoApiRest = ApiRest.fromJson(obtainUserInfoResult);
        Validate.isTrue(obtainUserInfoApiRest.isSuccessful(), obtainUserInfoApiRest.getError());
        Map<String, Object> obtainUserInfoApiRestData = (Map<String, Object>) obtainUserInfoApiRest.getData();
        Map<String, Object> user = (Map<String, Object>) obtainUserInfoApiRestData.get("user");
        Map<String, Object> tenant = (Map<String, Object>) obtainUserInfoApiRestData.get("tenant");
        BigInteger userId = ApplicationHandler.obtainBigIntegerFromMap(user, "id");
        BigInteger tenantId = ApplicationHandler.obtainBigIntegerFromMap(tenant, "id");
        Branch branch = branchMapper.findByUserIdAndTenantId(tenantId, userId);

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("user", user);
        data.put("tenant", tenant);
        data.put("branch", branch);
        ApiRest apiRest = new ApiRest();
        apiRest.setData(data);
        apiRest.setMessage("获取用户信息成功！");
        apiRest.setSuccessful(true);
        return apiRest;
    }

    @Transactional(readOnly = true)
    public ApiRest listUsers(Map<String, String> parameters) {
        String tenantId = parameters.get("tenantId");
        Validate.notNull(tenantId, "参数(tenantId)不能为空！");

        String branchId = parameters.get("branchId");
        Validate.notNull(branchId, "参数(branchId)不能为空！");
        return null;
    }
}
