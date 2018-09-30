package build.dream.catering.services;

import build.dream.catering.constants.Constants;
import build.dream.catering.mappers.BranchMapper;
import build.dream.catering.models.user.ListUsersModel;
import build.dream.catering.models.user.ObtainUserInfoModel;
import build.dream.common.api.ApiRest;
import build.dream.common.utils.PagedSearchModel;
import build.dream.common.utils.ProxyUtils;
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
    public ApiRest listUsers(ListUsersModel listUsersModel) throws IOException {
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

    public ApiRest obtainUserInfo(ObtainUserInfoModel obtainUserInfoModel) {
        return ApiRest.builder().successful(true).build();
    }
}
