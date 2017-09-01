package build.dream.erp.services;

import build.dream.common.api.ApiRest;
import build.dream.common.erp.domains.Branch;
import build.dream.common.utils.SerialNumberGenerator;
import build.dream.erp.constants.Constants;
import build.dream.erp.mappers.BranchMapper;
import build.dream.erp.mappers.SequenceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;

@Service
public class BranchService {
    @Autowired
    private BranchMapper branchMapper;
    @Autowired
    private SequenceMapper sequenceMapper;

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
        ApiRest apiRest = new ApiRest();
        apiRest.setData(branch);
        apiRest.setClassName(Branch.class.getName());
        apiRest.setMessage("初始化门店成功！");
        apiRest.setSuccessful(true);
        return apiRest;
    }
}
