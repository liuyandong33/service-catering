package build.dream.catering.services;

import build.dream.catering.constants.Constants;
import build.dream.catering.mappers.BranchMapper;
import build.dream.catering.models.user.ListUsersModel;
import build.dream.catering.models.user.ObtainBranchInfoModel;
import build.dream.catering.models.user.ObtainUserInfoModel;
import build.dream.common.api.ApiRest;
import build.dream.common.erp.catering.domains.Branch;
import build.dream.common.utils.*;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {
    @Autowired
    private BranchMapper branchMapper;

    @Transactional(readOnly = true)
    public ApiRest listUsers(ListUsersModel listUsersModel) {
        BigInteger tenantId = listUsersModel.obtainTenantId();
        BigInteger branchId = listUsersModel.obtainBranchId();
        Integer page = listUsersModel.getPage();
        Integer rows = listUsersModel.getRows();

        List<SearchCondition> searchConditions = new ArrayList<SearchCondition>();
        searchConditions.add(new SearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId));
        searchConditions.add(new SearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId));
        searchConditions.add(new SearchCondition("delete", Constants.SQL_OPERATION_SYMBOL_EQUAL, 0));

        SearchModel searchModel = new SearchModel();
        searchModel.setSearchConditions(searchConditions);
        long count = branchMapper.countUsers(searchModel);

        List<Map<String, Object>> userInfos = null;
        if (count > 0) {
            PagedSearchModel pagedSearchModel = new PagedSearchModel();
            pagedSearchModel.setSearchConditions(searchConditions);
            pagedSearchModel.setPage(page);
            pagedSearchModel.setRows(rows);
            List<BigInteger> userIds = branchMapper.findAllUserIds(pagedSearchModel);
            userInfos = UserUtils.batchGetUsers(userIds);
        } else {
            userInfos = new ArrayList<Map<String, Object>>();
        }

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("total", count);
        data.put("rows", userInfos);
        return ApiRest.builder().data(data).message("查询员工列表成功！").successful(true).build();
    }

    @Transactional(readOnly = true)
    public ApiRest obtainUserInfo(ObtainUserInfoModel obtainUserInfoModel) {
        String loginName = obtainUserInfoModel.getLoginName();
        Map<String, String> obtainUserInfoRequestParameters = new HashMap<String, String>();
        obtainUserInfoRequestParameters.put("loginName", loginName);

        Map<String, Object> userInfo = UserUtils.obtainUserInfo(loginName);

        Map<String, Object> data = new HashMap<String, Object>(userInfo);
        Map<String, Object> user = MapUtils.getMap(userInfo, "user");
        BigInteger tenantId = BigInteger.valueOf(MapUtils.getLongValue(user, "tenantId"));
        BigInteger userId = BigInteger.valueOf(MapUtils.getLongValue(user, "id"));

        Branch branch = branchMapper.findByTenantIdAndUserId(tenantId, userId);
        ValidateUtils.notNull(branch, "门店信息不存在！");

        data.put("branch", branch);
        return ApiRest.builder().data(data).message("获取用户信息成功！").successful(true).build();
    }

    /**
     * 获取门店信息
     *
     * @param obtainBranchInfoModel
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest obtainBranchInfo(ObtainBranchInfoModel obtainBranchInfoModel) {
        BigInteger tenantId = obtainBranchInfoModel.getTenantId();
        BigInteger userId = obtainBranchInfoModel.getUserId();
        Branch branch = branchMapper.findByTenantIdAndUserId(tenantId, userId);
        Validate.notNull(branch, "门店不存在！");

        return ApiRest.builder().data(branch).className(Branch.class.getName()).message("获取门店信息成功！").successful(true).build();
    }
}
