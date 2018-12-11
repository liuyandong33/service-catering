package build.dream.catering.services;

import build.dream.catering.constants.Constants;
import build.dream.catering.mappers.BranchMapper;
import build.dream.catering.models.branch.*;
import build.dream.catering.utils.SequenceUtils;
import build.dream.common.api.ApiRest;
import build.dream.common.catering.domains.Branch;
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
        BigInteger tenantId = initializeBranchModel.getTenantId();
        String tenantCode = initializeBranchModel.getTenantCode();
        Integer type = initializeBranchModel.getType();
        Integer status = initializeBranchModel.getStatus();
        String name = initializeBranchModel.getName();
        String provinceCode = initializeBranchModel.getProvinceCode();
        String provinceName = initializeBranchModel.getProvinceName();
        String cityCode = initializeBranchModel.getCityCode();
        String cityName = initializeBranchModel.getCityName();
        String districtCode = initializeBranchModel.getDistrictCode();
        String districtName = initializeBranchModel.getDistrictName();
        String address = initializeBranchModel.getAddress();
        String longitude = initializeBranchModel.getLongitude();
        String latitude = initializeBranchModel.getLatitude();
        String linkman = initializeBranchModel.getLinkman();
        String contactPhone = initializeBranchModel.getContactPhone();
        Integer smartRestaurantStatus = initializeBranchModel.getSmartRestaurantStatus();
        BigInteger currentUserId = initializeBranchModel.getCurrentUserId();
        BigInteger userId = initializeBranchModel.getUserId();

        Branch branch = new Branch();
        branch.setTenantId(tenantId);
        branch.setTenantCode(tenantCode);

        String code = SerialNumberGenerator.nextSerialNumber(4, SequenceUtils.nextValue(initializeBranchModel.getTenantCode() + "_branch_code"));
        branch.setCode(code);
        branch.setName(name);
        branch.setType(type);
        branch.setStatus(status);
        branch.setProvinceCode(provinceCode);
        branch.setProvinceName(provinceName);
        branch.setCityCode(cityCode);
        branch.setCityName(cityName);
        branch.setDistrictCode(districtCode);
        branch.setDistrictName(districtName);
        branch.setAddress(address);
        branch.setLongitude(longitude);
        branch.setLatitude(latitude);
        branch.setLinkman(linkman);
        branch.setContactPhone(contactPhone);
        branch.setSmartRestaurantStatus(smartRestaurantStatus);
        branch.setCreatedUserId(userId);
        branch.setUpdatedUserId(userId);
        DatabaseHelper.insert(branch);
        branchMapper.insertMergeUserBranch(userId, initializeBranchModel.getTenantId(), tenantCode, branch.getId(), currentUserId, "初始化门店，增加店长所属门店！");

        return ApiRest.builder().data(branch).className(Branch.class.getName()).message("初始化门店成功！").successful(true).build();
    }

    @Transactional(readOnly = true)
    public ApiRest listBranches(ListBranchesModel listBranchesModel) {
        BigInteger tenantId = listBranchesModel.obtainTenantId();
        String searchString = listBranchesModel.getSearchString();
        int page = listBranchesModel.getPage();
        int rows = listBranchesModel.getRows();
        List<SearchCondition> searchConditions = new ArrayList<SearchCondition>();
        searchConditions.add(new SearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId));
        if (StringUtils.isNotBlank(searchString)) {
            searchConditions.add(new SearchCondition("name", Constants.SQL_OPERATION_SYMBOL_LIKE, "%" + searchString + "%"));
        }

        SearchModel searchModel = new SearchModel(true);
        searchModel.setSearchConditions(searchConditions);
        long total = DatabaseHelper.count(Branch.class, searchModel);
        List<Branch> branches = new ArrayList<Branch>();
        if (total > 0) {
            PagedSearchModel pagedSearchModel = new PagedSearchModel(true);
            pagedSearchModel.setSearchConditions(searchConditions);
            pagedSearchModel.setPage(page);
            pagedSearchModel.setRows(rows);
            branches = DatabaseHelper.findAllPaged(Branch.class, pagedSearchModel);
        }
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("total", total);
        data.put("rows", branches);
        return ApiRest.builder().data(data).message("查询门店列表成功！").successful(true).build();
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
        BigInteger tenantId = deleteBranchModel.obtainTenantId();
        BigInteger branchId = deleteBranchModel.getBranchId();
        BigInteger userId = deleteBranchModel.obtainUserId();

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition(Branch.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        searchModel.addSearchCondition(Branch.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        Branch branch = DatabaseHelper.find(Branch.class, searchModel);
        Validate.notNull(branch, "门店不存在！");

        branch.setDeleted(true);
        branch.setUpdatedUserId(userId);
        branch.setUpdatedRemark("删除门店信息！");
        DatabaseHelper.update(branch);

        String findUserIdsSql = "SELECT user_id FROM merge_user_branch WHERE tenant_id = #{tenantId} AND branch_id = #{branchId} AND deleted = 0";
        Map<String, Object> findUserIdsParameters = new HashMap<String, Object>();
        findUserIdsParameters.put("sql", findUserIdsSql);
        findUserIdsParameters.put("tenantId", tenantId);
        findUserIdsParameters.put("branchId", branchId);
        List<Map<String, Object>> results = DatabaseHelper.executeQuery(findUserIdsParameters);
        List<BigInteger> userIds = new ArrayList<BigInteger>();
        for (Map<String, Object> map : results) {
            userIds.add(BigInteger.valueOf(MapUtils.getLongValue(map, "userId")));
        }


        String deleteMergeUserBranchSql = "UPDATE merge_user_branch SET deleted = 1 WHERE tenant_id = #{tenantId} AND branch_id = #{branchId} AND deleted = 0";
        Map<String, Object> deleteMergeUserBranchParameters = new HashMap<String, Object>();
        deleteMergeUserBranchParameters.put("sql", deleteMergeUserBranchSql);
        deleteMergeUserBranchParameters.put("tenantId", tenantId);
        deleteMergeUserBranchParameters.put("branchId", branchId);
        DatabaseHelper.executeUpdate(deleteMergeUserBranchParameters);

        Map<String, String> batchDeleteUserRequestParameters = new HashMap<String, String>();
        batchDeleteUserRequestParameters.put("userIds", StringUtils.join(userIds, ","));
        ApiRest batchDeleteUserApiRest = ProxyUtils.doPostWithRequestParameters(Constants.SERVICE_NAME_PLATFORM, "user", "batchDeleteUser", batchDeleteUserRequestParameters);
        ValidateUtils.isTrue(batchDeleteUserApiRest.isSuccessful(), batchDeleteUserApiRest.getError());

        return ApiRest.builder().message("删除门店信息成功！").successful(true).build();
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
            String findUpdateBranchSql = "SELECT tenant_id, id AS branch_id, code, name, type, status, tenant_code, create_time, deleted FROM branch WHERE create_time <= #{lastPullTime} AND updated_time >= #{lastPullTime}";
            Map<String, Object> findUpdateBranchParameters = new HashMap<String, Object>();
            findUpdateBranchParameters.put("sql", findUpdateBranchSql);
            findUpdateBranchParameters.put("lastPullTime", pullBranchInfosModel.getLastPullTime());
            List<Map<String, Object>> updateBranchInfos = DatabaseHelper.executeQuery(findUpdateBranchParameters);
            data.put("updateBranchInfos", updateBranchInfos);
        }

        return ApiRest.builder().data(data).message("获取所有门店信息成功！").successful(true).build();
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

        return ApiRest.builder().message("禁用门店产品成功！").successful(true).build();
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

        return ApiRest.builder().message("处理门店续费回调成功！").successful(true).build();
    }

    /**
     * 获取门店信息
     *
     * @param obtainBranchInfoModel
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest obtainBranchInfo(ObtainBranchInfoModel obtainBranchInfoModel) {
        BigInteger tenantId = obtainBranchInfoModel.obtainTenantId();
        BigInteger branchId = obtainBranchInfoModel.getBranchId();
        Branch branch = DatabaseHelper.find(Branch.class, TupleUtils.buildTuple3(Branch.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId), TupleUtils.buildTuple3(Branch.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId));
        Validate.notNull(branch, "门店不存在！");

        return ApiRest.builder().data(branch).className(Branch.class.getName()).message("获取门店信息成功！").successful(true).build();
    }

    /**
     * 获取智慧餐厅门店列表
     *
     * @param obtainAllSmartRestaurantsModel
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest obtainAllSmartRestaurants(ObtainAllSmartRestaurantsModel obtainAllSmartRestaurantsModel) {
        BigInteger tenantId = obtainAllSmartRestaurantsModel.getTenantId();

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition(Branch.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        searchModel.addSearchCondition(Branch.ColumnName.SMART_RESTAURANT_STATUS, Constants.SQL_OPERATION_SYMBOL_EQUAL, Constants.SMART_RESTAURANT_STATUS_NORMAL);

        List<Branch> branches = DatabaseHelper.findAll(Branch.class, searchModel);

        return ApiRest.builder().data(branches).message("获取智慧餐厅门店信息成功！").successful(true).build();
    }

    /**
     * 获取智汇餐厅门店信息
     *
     * @param obtainSmartRestaurantModel
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest obtainSmartRestaurant(ObtainSmartRestaurantModel obtainSmartRestaurantModel) {
        BigInteger tenantId = obtainSmartRestaurantModel.obtainTenantId();
        BigInteger branchId = obtainSmartRestaurantModel.getBranchId();

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition(Branch.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        searchModel.addSearchCondition(Branch.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        Branch branch = DatabaseHelper.find(Branch.class, searchModel);

        return ApiRest.builder().data(branch).className(Branch.class.getName()).message("获取智慧餐厅门店信息成功！").successful(true).build();
    }
}
