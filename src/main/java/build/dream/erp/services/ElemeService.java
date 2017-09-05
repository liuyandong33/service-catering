package build.dream.erp.services;

import build.dream.common.api.ApiRest;
import build.dream.common.erp.domains.Branch;
import build.dream.common.utils.ConfigurationUtils;
import build.dream.common.utils.ProxyUtils;
import build.dream.common.utils.SearchModel;
import build.dream.common.utils.SystemPartitionUtils;
import build.dream.erp.constants.Constants;
import build.dream.erp.mappers.BranchMapper;
import net.sf.json.JSONObject;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Service
public class ElemeService {
    @Autowired
    private BranchMapper branchMapper;

    @Transactional(readOnly = true)
    public ApiRest tenantAuthorize(BigInteger tenantId, BigInteger branchId) throws IOException {
        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("tenant_id", "=", tenantId);
        searchModel.addSearchCondition("id", "=", branchId);
        Branch branch = branchMapper.find(searchModel);
        Validate.notNull(branch, "门店不存在！");
        Map<String, String> checkIsAuthorizeRequestParameters = new HashMap<String, String>();
        checkIsAuthorizeRequestParameters.put("tenantId", tenantId.toString());
        checkIsAuthorizeRequestParameters.put("branchId", branchId.toString());
        checkIsAuthorizeRequestParameters.put("branchType", branch.getType().toString());
        String checkIsAuthorizeResult = ProxyUtils.doGetWithRequestParameters(Constants.SERVICE_NAME_OUT, "eleme", "checkIsAuthorize", checkIsAuthorizeRequestParameters);
        ApiRest checkIsAuthorizeApiRest = ApiRest.fromJson(checkIsAuthorizeResult);
        Validate.isTrue(checkIsAuthorizeApiRest.isSuccessful(), checkIsAuthorizeApiRest.getError());
        boolean isAuthorize = (boolean) checkIsAuthorizeApiRest.getData();

        String data = null;
        if (isAuthorize) {
            String serviceName = ConfigurationUtils.getConfiguration(Constants.SERVICE_NAME);
            data = SystemPartitionUtils.getOutsideServiceDomain(serviceName) + "/eleme/bindingRestaurant?tenantId=" + tenantId + "&branchId=" + branchId;
        } else {
            String elemeUrl = ConfigurationUtils.getConfiguration(Constants.ELEME_URL);
            String elemeAppKey = ConfigurationUtils.getConfiguration(Constants.ELEME_APP_KEY);

            String outServiceOutsideServiceDomain = SystemPartitionUtils.getOutsideServiceDomain(Constants.SERVICE_NAME_OUT);
            data = String.format(Constants.ELEME_TENANT_AUTHORIZE_URL_FORMAT, elemeUrl + "/" + "authorize", "code", elemeAppKey, URLEncoder.encode(outServiceOutsideServiceDomain + "/eleme/tenantAuthorizeCallback", Constants.CHARSET_UTF_8), tenantId + "Z" + branchId, "all");
        }
        ApiRest apiRest = new ApiRest(data, "生成授权链接成功！");
        return apiRest;
    }

    @Transactional(rollbackFor = Exception.class)
    public ApiRest saveElemeOrder(BigInteger shopId, JSONObject message, Integer type) {
        return new ApiRest();
    }
}
