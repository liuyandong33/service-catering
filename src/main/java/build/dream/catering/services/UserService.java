package build.dream.catering.services;

import build.dream.catering.constants.Constants;
import build.dream.catering.mappers.BranchMapper;
import build.dream.catering.models.user.ListUsersModel;
import build.dream.catering.models.user.ObtainUserInfoModel;
import build.dream.common.api.ApiRest;
import build.dream.common.erp.catering.domains.Branch;
import build.dream.common.utils.PagedSearchModel;
import build.dream.common.utils.ProxyUtils;
import build.dream.common.utils.ValidateUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {
    @Autowired
    private BranchMapper branchMapper;

    @Transactional(readOnly = true)
    public ApiRest listUsers(ListUsersModel listUsersModel) {
        BigInteger tenantId = listUsersModel.getTenantId();
        BigInteger branchId = listUsersModel.getBranchId();
        Integer page = listUsersModel.getPage();
        Integer rows = listUsersModel.getRows();

        PagedSearchModel pagedSearchModel = new PagedSearchModel();
        pagedSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        pagedSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        pagedSearchModel.setPage(page);
        pagedSearchModel.setRows(rows);
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
        return ApiRest.builder().data(data).message("查询员工列表成功！").successful(true).build();
    }

    @Transactional(readOnly = true)
    public ApiRest obtainUserInfo(ObtainUserInfoModel obtainUserInfoModel) {
        String loginName = obtainUserInfoModel.getLoginName();
        Map<String, String> obtainUserInfoRequestParameters = new HashMap<String, String>();
        obtainUserInfoRequestParameters.put("loginName", loginName);

        ApiRest apiRest = ProxyUtils.doGetWithRequestParameters(Constants.SERVICE_NAME_PLATFORM, "user", "obtainUserInfo", obtainUserInfoRequestParameters);
        ValidateUtils.isTrue(apiRest.isSuccessful(), apiRest.getError());

        Map<String, Object> userInfo = (Map<String, Object>) apiRest.getData();

        Map<String, Object> data = new HashMap<String, Object>(userInfo);
        Map<String, Object> user = MapUtils.getMap(userInfo, "user");
        BigInteger tenantId = BigInteger.valueOf(MapUtils.getLongValue(user, "tenantId"));
        BigInteger userId = BigInteger.valueOf(MapUtils.getLongValue(user, "id"));

        Branch branch = branchMapper.findByTenantIdAndUserId(tenantId, userId);
        ValidateUtils.notNull(branch, "门店信息不存在！");

        data.put("branch", branch);
        return ApiRest.builder().data(data).message("获取用户信息成功！").successful(true).build();
    }
}
