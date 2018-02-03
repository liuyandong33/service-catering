package build.dream.catering.services;

import build.dream.catering.constants.Constants;
import build.dream.catering.mappers.BranchMapper;
import build.dream.catering.models.anubis.ChainStoreModel;
import build.dream.catering.utils.AnubisUtils;
import build.dream.common.api.ApiRest;
import build.dream.common.erp.catering.domains.Branch;
import build.dream.common.utils.ConfigurationUtils;
import build.dream.common.utils.SearchModel;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

@Service
public class AnubisService {
    @Autowired
    private BranchMapper branchMapper;

    @Transactional(rollbackFor = Exception.class)
    public ApiRest chainStore(ChainStoreModel chainStoreModel) throws IOException {
        BigInteger tenantId = chainStoreModel.getTenantId();
        BigInteger branchId = chainStoreModel.getBranchId();
        BigInteger userId = chainStoreModel.getUserId();

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        Branch branch = branchMapper.find(searchModel);
        Validate.notNull(branch, "门店不存在！");

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("chain_store_code", tenantId + "Z" + branchId);
        data.put("chain_store_name", branch.getTenantCode() + "Z" + branch.getCode() + "Z" + branch.getName());
        data.put("contact_phone", "13789871965");
        data.put("address", branch.getProvinceName() + branch.getCityName() + branch.getDistrictName() + branch.getAddress());
        data.put("position_source", 2);
        data.put("longitude", branch.getLongitude());
        data.put("latitude", branch.getLatitude());
        data.put("service_code", 1);

        String url = ConfigurationUtils.getConfiguration(Constants.ANUBIS_SERVICE_URL) + Constants.ANUBIS_CHAIN_STORE_URI;
        String appId = ConfigurationUtils.getConfiguration(Constants.ANUBIS_APP_ID);
        ApiRest apiRest = AnubisUtils.callAnubisSystem(url, appId, data, 1500);
        return apiRest;
    }
}
