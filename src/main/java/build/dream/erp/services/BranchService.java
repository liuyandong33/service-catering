package build.dream.erp.services;

import build.dream.common.api.ApiRest;
import build.dream.common.erp.domains.Branch;
import build.dream.common.utils.PagedSearchModel;
import build.dream.common.utils.SearchModel;
import build.dream.common.utils.SerialNumberGenerator;
import build.dream.erp.constants.Constants;
import build.dream.erp.mappers.BranchMapper;
import build.dream.erp.mappers.DataDefinitionMapper;
import build.dream.erp.mappers.SequenceMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class BranchService {
    @Autowired
    private BranchMapper branchMapper;
    @Autowired
    private SequenceMapper sequenceMapper;
    @Autowired
    private DataDefinitionMapper dataDefinitionMapper;

    @Transactional(rollbackFor = Exception.class)
    public ApiRest initializeBranch(BigInteger userId, BigInteger tenantId, String tenantCode) {
        Branch branch = new Branch();
        branch.setCode(SerialNumberGenerator.nextSerialNumber(4, sequenceMapper.nextValue(tenantCode + "_branch_count")));
        branch.setName("总部");
        branch.setType(Constants.BRANCH_TYPE_HEADQUARTERS);
        branch.setStatus(Constants.BRANCH_STATUS_ENABLED);
        branch.setCreateUserId(BigInteger.ZERO);
        branch.setLastUpdateUserId(BigInteger.ZERO);
        branch.setTenantId(tenantId);
        branchMapper.insert(branch);
        branchMapper.insertMergeUserBranch(userId, tenantId, branch.getId());
        dataDefinitionMapper.createTableWithTemplate("goods_" + tenantCode, Constants.GOODS_TABLE_TEMPLATE);
        dataDefinitionMapper.createTableWithTemplate("sale_" + tenantCode, Constants.SALE_TABLE_TEMPLATE);

        ApiRest apiRest = new ApiRest();
        apiRest.setData(branch);
        apiRest.setClassName(Branch.class.getName());
        apiRest.setMessage("初始化门店成功！");
        apiRest.setSuccessful(true);
        return apiRest;
    }

    @Transactional(readOnly = true)
    public ApiRest listBranches(Map<String, String> parameters) {
        String tenantId = parameters.get("tenantId");
        Validate.notNull(tenantId, "参数(tenantId)不能为空！");
        PagedSearchModel pagedSearchModel = new PagedSearchModel(true);
        pagedSearchModel.addSearchCondition("tenant_id", "=", BigInteger.valueOf(Long.valueOf(tenantId)));
        String name = parameters.get("name");
        if (StringUtils.isNotBlank(name)) {
            pagedSearchModel.addSearchCondition("name", "LIKE", "%" + name + "%");
        }
        String page = parameters.get("page");
        if (StringUtils.isBlank(page)) {
            page = "1";
        }
        String rows = parameters.get("rows");
        if (StringUtils.isBlank(rows)) {
            rows = "20";
        }
        pagedSearchModel.setOffsetAndMaxResults(Integer.valueOf(page), Integer.valueOf(rows));
        List<Branch> branches = branchMapper.findAllPaged(pagedSearchModel);
        long total = branchMapper.countBranches(pagedSearchModel);
        ApiRest apiRest = new ApiRest(branches, "查询门店列表成功！");
        return apiRest;
    }

    @Transactional(readOnly = true)
    public ApiRest findBranchInfoById(BigInteger tenantId, BigInteger branchId) {
        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("id", "=", branchId);
        searchModel.addSearchCondition("tenant_id", "=", tenantId);
        Branch branch =  branchMapper.find(searchModel);

        ApiRest apiRest = new ApiRest();
        apiRest.setData(branch);
        apiRest.setClassName(Branch.class.getName());
        apiRest.setMessage("查询门店信息成功！");
        apiRest.setSuccessful(true);
        return apiRest;
    }

    @Transactional(readOnly = true)
    public ApiRest findAllBranchInfos() {
        List<Map<String, Object>> branchInfos = branchMapper.findAllBranchInfos();
        ApiRest apiRest = new ApiRest();
        apiRest.setData(branchInfos);
        apiRest.setMessage("查询门店信息成功！");
        apiRest.setSuccessful(true);
        return apiRest;
    }

    @Transactional(readOnly = true)
    public ApiRest findBranchInfo(BigInteger tenantId, BigInteger userId) {
        Branch branch = branchMapper.findBranchInfo(tenantId, userId);
        ApiRest apiRest = new ApiRest();
        apiRest.setData(branch);
        apiRest.setMessage("查询门店信息成功！");
        apiRest.setClassName(Branch.class.getName());
        apiRest.setSuccessful(true);
        return apiRest;
    }
}
