package build.dream.catering.services;

import build.dream.catering.models.requiregoods.SaveRequireGoodsOrderModel;
import build.dream.common.api.ApiRest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;

@Service
public class RequireGoodsService {
    @Transactional(rollbackFor = Exception.class)
    public ApiRest saveRequireGoodsOrder(SaveRequireGoodsOrderModel saveRequireGoodsOrderModel) {
        BigInteger tenantId = saveRequireGoodsOrderModel.obtainTenantId();
        String tenantCode = saveRequireGoodsOrderModel.obtainTenantCode();
        BigInteger branchId = saveRequireGoodsOrderModel.obtainBranchId();

        return null;
    }
}
