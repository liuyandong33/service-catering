package build.dream.catering.services;

import build.dream.common.api.ApiRest;
import build.dream.common.utils.PagedSearchModel;
import build.dream.common.utils.ProxyUtils;
import build.dream.catering.constants.Constants;
import build.dream.catering.mappers.BranchMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {
    @Autowired
    private BranchMapper branchMapper;

    @Transactional(readOnly = true)
    public ApiRest listUsers(Map<String, String> parameters) throws IOException {
        String tenantId = parameters.get("tenantId");
        Validate.notNull(tenantId, "参数(tenantId)不能为空！");

        String branchId = parameters.get("branchId");
        Validate.notNull(branchId, "参数(branchId)不能为空！");

        PagedSearchModel pagedSearchModel = new PagedSearchModel();
        pagedSearchModel.addSearchCondition("tenant_id", "=", BigInteger.valueOf(Long.valueOf(tenantId)));
        pagedSearchModel.addSearchCondition("branch_id", "=", BigInteger.valueOf(Long.valueOf(branchId)));
        String page = parameters.get("page");
        if (StringUtils.isBlank(page)) {
            page = "1";
        }
        String rows = parameters.get("rows");
        if (StringUtils.isBlank(rows)) {
            rows = "20";
        }
        pagedSearchModel.setPage(Integer.valueOf(page));
        pagedSearchModel.setRows(Integer.valueOf(rows));
        List<BigInteger> userIds = branchMapper.findAllUserIds(pagedSearchModel);
        Map<String, String> findAllUsersRequestParameters = new HashMap<String, String>();
        findAllUsersRequestParameters.put("userIds", StringUtils.join(userIds, ","));
        String findAllUsersResult = ProxyUtils.doGetOriginalWithRequestParameters(Constants.SERVICE_NAME_PLATFORM, "user", "findAllUsers", findAllUsersRequestParameters);
        ApiRest findAllUsersApiRest = ApiRest.fromJson(findAllUsersResult);
        Validate.isTrue(findAllUsersApiRest.isSuccessful(), findAllUsersApiRest.getError());

        Map<String, Object> data = new HashMap<String, Object>();
        long total = branchMapper.countUsers(pagedSearchModel);
        data.put("total", total);
        data.put("rows", findAllUsersApiRest.getData());
        ApiRest apiRest = new ApiRest(data, "查询员工列表成功！");
        return apiRest;
    }
}
