package build.dream.catering.services;

import build.dream.catering.constants.Constants;
import build.dream.catering.mappers.BranchMapper;
import build.dream.catering.models.branch.*;
import build.dream.catering.utils.SequenceUtils;
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

    @Transactional(rollbackFor = Exception.class)
    public ApiRest initializeBranch(InitializeBranchModel initializeBranchModel) {
        Branch branch = new Branch();
        branch.setTenantId(initializeBranchModel.getTenantId());
        branch.setTenantCode(initializeBranchModel.getTenantCode());

        String code = SerialNumberGenerator.nextSerialNumber(4, SequenceUtils.nextValue(initializeBranchModel.getTenantCode() + "_branch_count"));
        branch.setCode(code);
        branch.setName(initializeBranchModel.getName());
        branch.setType(initializeBranchModel.getType());
        branch.setStatus(initializeBranchModel.getStatus());
        branch.setProvinceCode(initializeBranchModel.getProvinceCode());
        branch.setProvinceName(initializeBranchModel.getProvinceName());
        branch.setCityCode(initializeBranchModel.getCityCode());
        branch.setCityName(initializeBranchModel.getCityName());
        branch.setDistrictCode(initializeBranchModel.getDistrictCode());
        branch.setDistrictName(initializeBranchModel.getDistrictName());
        branch.setAddress(initializeBranchModel.getAddress());
        branch.setLongitude(initializeBranchModel.getLongitude());
        branch.setLatitude(initializeBranchModel.getLatitude());
        branch.setLinkman(initializeBranchModel.getLinkman());
        branch.setContactPhone(initializeBranchModel.getContactPhone());
        branch.setElemeAccountType(Constants.ELEME_ACCOUNT_TYPE_CHAIN_ACCOUNT);
        branch.setShopId(null);
        branch.setSmartRestaurantStatus(initializeBranchModel.getSmartRestaurantStatus());
        branch.setAppAuthToken(null);
        branch.setPoiId(null);
        branch.setPoiName(null);

        BigInteger userId = initializeBranchModel.getUserId();
        branch.setCreateUserId(userId);
        branch.setLastUpdateUserId(userId);
        DatabaseHelper.insert(branch);
        branchMapper.insertMergeUserBranch(userId, initializeBranchModel.getTenantId(), branch.getId());

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
        searchConditions.add(new SearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, listBranchesModel.getTenantId()));
        if (StringUtils.isNotBlank(listBranchesModel.getName())) {
            searchConditions.add(new SearchCondition("name", Constants.SQL_OPERATION_SYMBOL_LIKE, "%" + listBranchesModel.getName() + "%"));
        }

        SearchModel searchModel = new SearchModel(true);
        searchModel.setSearchConditions(searchConditions);
        long total = DatabaseHelper.count(Branch.class, searchModel);
        List<Branch> branches = new ArrayList<Branch>();
        if (total > 0) {
            PagedSearchModel pagedSearchModel = new PagedSearchModel(true);
            pagedSearchModel.setSearchConditions(searchConditions);
            pagedSearchModel.setPage(listBranchesModel.getPage());
            pagedSearchModel.setRows(listBranchesModel.getRows());
            branches = DatabaseHelper.findAllPaged(Branch.class, pagedSearchModel);
        }
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("total", total);
        data.put("rows", branches);
        return new ApiRest(data, "查询门店列表成功！");
    }


    /**
     * 删除门店信息
     *
     * @param deleteBranchModel
     * @return
     * @throws IOException
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiRest deleteBranch(DeleteBranchModel deleteBranchModel) throws IOException {
        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUAL, deleteBranchModel.getBranchId());
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, deleteBranchModel.getTenantId());
        Branch branch = DatabaseHelper.find(Branch.class, searchModel);
        Validate.notNull(branch, "门店不存在！");

        branch.setDeleted(true);
        branch.setLastUpdateUserId(deleteBranchModel.getUserId());
        branch.setLastUpdateRemark("删除门店信息！");
        DatabaseHelper.update(branch);

        String findUserIdsSql = "SELECT user_id FROM merge_user_branch WHERE tenant_id = #{tenantId} AND branch_id = #{branchId} AND deleted = 0";
        Map<String, Object> findUserIdsParameters = new HashMap<String, Object>();
        findUserIdsParameters.put("sql", findUserIdsSql);
        findUserIdsParameters.put("tenantId", deleteBranchModel.getTenantId());
        findUserIdsParameters.put("branchId", deleteBranchModel.getBranchId());
        List<Map<String, Object>> results = DatabaseHelper.executeQuery(findUserIdsParameters);
        List<BigInteger> userIds = new ArrayList<BigInteger>();
        for (Map<String, Object> map : results) {
            userIds.add(BigInteger.valueOf(MapUtils.getLongValue(map, "userId")));
        }


        String deleteMergeUserBranchSql = "UPDATE merge_user_branch SET deleted = 1 WHERE tenant_id = #{tenantId} AND branch_id = #{branchId} AND deleted = 0";
        Map<String, Object> deleteMergeUserBranchParameters = new HashMap<String, Object>();
        deleteMergeUserBranchParameters.put("sql", deleteMergeUserBranchSql);
        deleteMergeUserBranchParameters.put("tenantId", deleteBranchModel.getTenantId());
        deleteMergeUserBranchParameters.put("branchId", deleteBranchModel.getBranchId());
        DatabaseHelper.executeUpdate(deleteMergeUserBranchParameters);

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
     *
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest pullBranchInfos(PullBranchInfosModel pullBranchInfosModel) {
        String findInsertBranchSql = "SELECT tenant_id, id AS branch_id, code, name, type, status, tenant_code, create_time FROM branch WHERE create_time >= #{lastPullTime} AND deleted = 0";
        Map<String, Object> findInsertBranchParameters = new HashMap<String, Object>();
        findInsertBranchParameters.put("sql", findInsertBranchSql);
        findInsertBranchParameters.put("lastPullTime", pullBranchInfosModel.getLastPullTime());
        List<Map<String, Object>> insertBranchInfos = DatabaseHelper.executeQuery(findInsertBranchParameters);

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("insertBranchInfos", insertBranchInfos);
        if (!pullBranchInfosModel.getReacquire()) {
            String findUpdateBranchSql = "SELECT tenant_id, id AS branch_id, code, name, type, status, tenant_code, create_time, deleted FROM branch WHERE create_time <= #{lastPullTime} AND last_update_time >= #{lastPullTime}";
            Map<String, Object> findUpdateBranchParameters = new HashMap<String, Object>();
            findUpdateBranchParameters.put("sql", findUpdateBranchSql);
            findUpdateBranchParameters.put("lastPullTime", pullBranchInfosModel.getLastPullTime());
            List<Map<String, Object>> updateBranchInfos = DatabaseHelper.executeQuery(findUpdateBranchParameters);
            data.put("updateBranchInfos", updateBranchInfos);
        }

        return new ApiRest(data, "获取所有门店信息成功！");
    }

    /**
     * 门店过期，禁用门店产品
     *
     * @param disableGoodsModel
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiRest disableGoods(DisableGoodsModel disableGoodsModel) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("sql", disableGoodsModel.getDisableSql());
        parameters.put("tenantId", disableGoodsModel.getTenantId());
        parameters.put("branchId", disableGoodsModel.getBranchId());
        DatabaseHelper.executeUpdate(parameters);

        ApiRest apiRest = new ApiRest();
        apiRest.setMessage("禁用门店产品成功！");
        apiRest.setSuccessful(true);
        return apiRest;
    }

    /**
     * 处理门店续费回调
     *
     * @param renewCallbackModel
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiRest handleRenewCallback(RenewCallbackModel renewCallbackModel) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("sql", renewCallbackModel.getRenewSql());
        parameters.put("tenantId", renewCallbackModel.getTenantId());
        parameters.put("branchId", renewCallbackModel.getBranchId());
        DatabaseHelper.executeUpdate(parameters);

        ApiRest apiRest = new ApiRest();
        apiRest.setMessage("处理门店续费回调成功！");
        apiRest.setSuccessful(true);
        return apiRest;
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
        ApiRest apiRest = new ApiRest();
        apiRest.setData(branch);
        apiRest.setClassName(Branch.class.getName());
        apiRest.setMessage("获取门店信息成功！");
        apiRest.setSuccessful(true);
        return apiRest;
    }
}
