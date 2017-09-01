package build.dream.erp.services;

import build.dream.common.api.ApiRest;
import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.ProxyUtils;
import build.dream.erp.constants.Constants;
import org.apache.commons.lang.Validate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {
    @Transactional(readOnly = true)
    public ApiRest obtainUserInfo(String loginName) throws IOException {
        Map<String, String> obtainUserInfoRequestParameters = new HashMap<String, String>();
        obtainUserInfoRequestParameters.put("loginName", loginName);
        String obtainUserInfoResult = ProxyUtils.doGetWithRequestParameters(Constants.SERVICE_NAME_PLATFORM, "user", "obtainUserInfo", obtainUserInfoRequestParameters);
        ApiRest obtainUserInfoApiRest = ApiRest.fromJson(obtainUserInfoResult);
        Validate.isTrue(obtainUserInfoApiRest.isSuccessful(), obtainUserInfoApiRest.getError());
        Map<String, Object> obtainUserInfoApiRestData = (Map<String, Object>) obtainUserInfoApiRest.getData();
        Map<String, Object> userInfo = (Map<String, Object>) obtainUserInfoApiRestData.get("userInfo");
        Map<String, Object> tenantInfo = (Map<String, Object>) obtainUserInfoApiRestData.get("tenantInfo");
        BigInteger userId = ApplicationHandler.obtainBigIntegerFromMap(userInfo, "id");
        BigInteger tenantId = ApplicationHandler.obtainBigIntegerFromMap(tenantInfo, "id");

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("userInfo", userInfo);
        data.put("tenantInfo", tenantInfo);
        data.put("employeeInfo", userId);
        data.put("branchInfo", tenantId);
        ApiRest apiRest = new ApiRest();
        apiRest.setData(data);
        apiRest.setMessage("获取用户信息成功！");
        apiRest.setSuccessful(true);
        return apiRest;
    }
}
