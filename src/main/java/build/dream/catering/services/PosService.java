package build.dream.catering.services;

import build.dream.catering.constants.Constants;
import build.dream.catering.mappers.PosMapper;
import build.dream.catering.models.pos.InitPosModel;
import build.dream.common.api.ApiRest;
import build.dream.common.erp.catering.domains.Pos;
import build.dream.common.utils.SearchModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

@Service
public class PosService {
    @Autowired
    private PosMapper posMapper;

    /**
     * 初始化POS
     *
     * @param initPosModel
     * @return
     */
    public ApiRest initPos(InitPosModel initPosModel) {
        BigInteger tenantId = initPosModel.getTenantId();
        BigInteger branchId = initPosModel.getBranchId();
        BigInteger userId = initPosModel.getUserId();
        String registrationId = initPosModel.getRegistrationId();

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        searchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        searchModel.addSearchCondition("user_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, userId);
        Pos pos = posMapper.find(searchModel);
        if (pos == null) {
            pos = new Pos();
            pos.setTenantId(tenantId);
            pos.setTenantCode(initPosModel.getTenantCode());
            pos.setBranchId(branchId);
            pos.setBranchCode(initPosModel.getBranchCode());
            pos.setUserId(userId);
            pos.setRegistrationId(registrationId);
            pos.setCreateUserId(userId);
            pos.setLastUpdateUserId(userId);
            pos.setLastUpdateRemark("POS不存在，初始化POS！");
            posMapper.insert(pos);
        } else {
            pos.setUserId(userId);
            pos.setRegistrationId(registrationId);
            pos.setLastUpdateUserId(userId);
            pos.setLastUpdateRemark("POS存在，初始化POS！");
            posMapper.update(pos);
        }
        return new ApiRest(pos, "初始化POS成功！");
    }
}
