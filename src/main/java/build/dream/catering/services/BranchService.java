package build.dream.catering.services;

import build.dream.catering.constants.Constants;
import build.dream.catering.mappers.BranchMapper;
import build.dream.catering.mappers.SequenceMapper;
import build.dream.catering.mappers.UniversalMapper;
import build.dream.catering.models.branch.DeleteBranchModel;
import build.dream.catering.models.branch.InitializeBranchModel;
import build.dream.catering.models.branch.ListBranchesModel;
import build.dream.common.api.ApiRest;
import build.dream.common.erp.catering.domains.Branch;
import build.dream.common.utils.*;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BranchService {
    @Autowired
    private BranchMapper branchMapper;
    @Autowired
    private SequenceMapper sequenceMapper;
    @Autowired
    private UniversalMapper universalMapper;

    @Transactional(rollbackFor = Exception.class)
    public ApiRest initializeBranch(InitializeBranchModel initializeBranchModel) {
        Branch branch = new Branch();
        branch.setCode(SerialNumberGenerator.nextSerialNumber(4, sequenceMapper.nextValue(initializeBranchModel.getTenantCode() + "_branch_count")));
        branch.setName("总部");
        branch.setType(Constants.BRANCH_TYPE_HEADQUARTERS);
        branch.setStatus(Constants.BRANCH_STATUS_ENABLED);
        branch.setCreateUserId(BigInteger.ZERO);
        branch.setLastUpdateUserId(BigInteger.ZERO);
        branch.setTenantId(initializeBranchModel.getTenantId());
        branch.setTenantCode(initializeBranchModel.getTenantCode());
        branch.setProvinceCode(initializeBranchModel.getProvinceCode());
        branch.setProvinceName(initializeBranchModel.getProvinceName());
        branch.setCityCode(initializeBranchModel.getCityCode());
        branch.setCityName(initializeBranchModel.getCityName());
        branch.setDistrictCode(initializeBranchModel.getDistrictCode());
        branch.setDistrictName(initializeBranchModel.getDistrictName());
        branch.setAddress(initializeBranchModel.getAddress());
        branch.setLongitude(initializeBranchModel.getLongitude());
        branch.setLatitude(initializeBranchModel.getLatitude());
        branchMapper.insert(branch);
        branchMapper.insertMergeUserBranch(initializeBranchModel.getUserId(), initializeBranchModel.getTenantId(), branch.getId());

        ApiRest apiRest = new ApiRest();
        apiRest.setData(branch);
        apiRest.setClassName(Branch.class.getName());
        apiRest.setMessage("初始化门店成功！");
        apiRest.setSuccessful(true);
        return apiRest;
    }

    @Transactional(readOnly = true)
    public ApiRest listBranches(ListBranchesModel listBranchesModel) {
        List<SearchCondition> searchConditions = new ArrayList<SearchCondition>();
        searchConditions.add(new SearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, listBranchesModel.getTenantId()));
        if (StringUtils.isNotBlank(listBranchesModel.getName())) {
            searchConditions.add(new SearchCondition("name", Constants.SQL_OPERATION_SYMBOL_LIKE, "%" + listBranchesModel.getName() + "%"));
        }

        SearchModel searchModel = new SearchModel(true);
        searchModel.setSearchConditions(searchConditions);
        long total = branchMapper.count(searchModel);
        List<Branch> branches = new ArrayList<Branch>();
        if (total > 0) {
            PagedSearchModel pagedSearchModel = new PagedSearchModel(true);
            pagedSearchModel.setSearchConditions(searchConditions);
            pagedSearchModel.setPage(listBranchesModel.getPage());
            pagedSearchModel.setRows(listBranchesModel.getRows());
            branches = branchMapper.findAllPaged(pagedSearchModel);
        }
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("total", total);
        data.put("rows", branches);
        return new ApiRest(data, "查询门店列表成功！");
    }


    /**
     * 删除门店信息
     * @param deleteBranchModel
     * @return
     * @throws IOException
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiRest deleteBranch(DeleteBranchModel deleteBranchModel) throws IOException {
        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUALS, deleteBranchModel.getBranchId());
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, deleteBranchModel.getTenantId());
        Branch branch = branchMapper.find(searchModel);
        Validate.notNull(branch, "门店不存在！");

        branch.setDeleted(true);
        branch.setLastUpdateUserId(deleteBranchModel.getUserId());
        branch.setLastUpdateRemark("删除门店信息！");
        branchMapper.update(branch);

        String findUserIdsSql = "SELECT user_id FROM merge_user_branch WHERE tenant_id = #{tenantId} AND branch_id = #{branchId} AND deleted = 0";
        Map<String, Object> findUserIdsParameters = new HashMap<String, Object>();
        findUserIdsParameters.put("sql", findUserIdsSql);
        findUserIdsParameters.put("tenantId", deleteBranchModel.getTenantId());
        findUserIdsParameters.put("branchId", deleteBranchModel.getBranchId());
        List<Map<String, Object>> results = universalMapper.executeQuery(findUserIdsParameters);
        List<BigInteger> userIds = new ArrayList<BigInteger>();
        for (Map<String, Object> map : results) {
            userIds.add(BigInteger.valueOf(MapUtils.getLongValue(map, "userId")));
        }


        String deleteMergeUserBranchSql = "UPDATE merge_user_branch SET deleted = 1 WHERE tenant_id = #{tenantId} AND branch_id = #{branchId} AND deleted = 0";
        Map<String, Object> deleteMergeUserBranchParameters = new HashMap<String, Object>();
        deleteMergeUserBranchParameters.put("sql", deleteMergeUserBranchSql);
        deleteMergeUserBranchParameters.put("tenantId", deleteBranchModel.getTenantId());
        deleteMergeUserBranchParameters.put("branchId", deleteBranchModel.getBranchId());
        universalMapper.executeUpdate(deleteMergeUserBranchParameters);

        Map<String, String> batchDeleteUserRequestParameters = new HashMap<String, String>();
        batchDeleteUserRequestParameters.put("userIds", StringUtils.join(userIds, ","));
        ApiRest batchDeleteUserApiRest = ProxyUtils.doPostWithRequestParameters(Constants.SERVICE_NAME_PLATFORM, "user", "batchDeleteUser", batchDeleteUserRequestParameters);
        Validate.isTrue(batchDeleteUserApiRest.isSuccessful(), batchDeleteUserApiRest.getError());

        ApiRest apiRest = new ApiRest();
        apiRest.setMessage("删除门店信息成功！");
        apiRest.setSuccessful(true);
        return apiRest;
    }

    /**
     * 获取所有门店信息
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest obtainAllBranchInfos() {
        String sql = "SELECT id, code, name, type, status, tenant_id, tenant_code, create_time, last_update_time FROM branch WHERE deleted = 0";
        Map<String, Object> obtainAllBranchInfosParameters = new HashMap<String, Object>();
        obtainAllBranchInfosParameters.put("sql", sql);
        List<Map<String, Object>> allBranchInfos = universalMapper.executeQuery(obtainAllBranchInfosParameters);

        return new ApiRest(allBranchInfos, "获取所有门店信息成功！");
    }
}
