package build.dream.erp.services;

import build.dream.common.api.ApiRest;
import build.dream.common.erp.domains.Branch;
import build.dream.common.erp.domains.MeiTuanOrder;
import build.dream.common.utils.ConfigurationUtils;
import build.dream.common.utils.GsonUtils;
import build.dream.common.utils.SearchModel;
import build.dream.erp.constants.Constants;
import build.dream.erp.mappers.BranchMapper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Map;

@Service
public class MeiTuanService {
    @Autowired
    private BranchMapper branchMapper;

    @Transactional(readOnly = true)
    public ApiRest generateBindingStoreLink(BigInteger tenantId, BigInteger branchId, String businessId) throws IOException {
        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        Branch branch = branchMapper.find(searchModel);
        Validate.notNull(branch, "门店不存在！");
        String meiTuanErpServiceUrl = ConfigurationUtils.getConfiguration(Constants.MEI_TUAN_ERP_SERVICE_URL);
        String meiTuanDeveloperId = ConfigurationUtils.getConfiguration(Constants.MEI_TUAN_DEVELOPER_ID);
        String meiTuanSignKey = ConfigurationUtils.getConfiguration(Constants.MEI_TUAN_SIGN_KEY);
        StringBuffer bindingStoreLink = new StringBuffer(meiTuanErpServiceUrl);
        bindingStoreLink.append("?developerId=").append(meiTuanDeveloperId);
        bindingStoreLink.append("&businessId=").append(businessId);
        bindingStoreLink.append("&ePoiId=").append(tenantId).append("Z").append(branchId);
        bindingStoreLink.append("&signKey=").append(meiTuanSignKey);
        bindingStoreLink.append("&ePoiName=").append(branch.getName());
        ApiRest apiRest = new ApiRest();
        apiRest.setData(bindingStoreLink.toString());
        apiRest.setMessage("生成门店绑定链接成功！");
        apiRest.setSuccessful(true);
        return apiRest;
    }

    @Transactional(rollbackFor = Exception.class)
    public ApiRest handleOrderEffectiveCallback(Map<String, String> parameters) {
        String developerId = parameters.get("developerId");
        String ePoiId = parameters.get("ePoiId");
        String sign = parameters.get("sign");
        String orderJson = parameters.get("order");
        JSONObject orderJsonObject = JSONObject.fromObject(orderJson);
        JSONArray detailJsonArray = orderJsonObject.optJSONArray("detail");
        JSONArray extrasJsonArray = orderJsonObject.optJSONArray("extras");
        JSONObject poiReceiveDetailJsonObject = orderJsonObject.optJSONObject("poiReceiveDetail");
        long ctime = orderJsonObject.optLong("ctime");
        long utime = orderJsonObject.optLong("utime");
        long deliveryTime = orderJsonObject.optLong("deliveryTime");
        int hasInvoiced = orderJsonObject.optInt("hasInvoiced");
        int isThirdShipping = orderJsonObject.optInt("isThirdShipping");
        orderJsonObject.remove("detail");
        orderJsonObject.remove("ePoiId");
        orderJsonObject.remove("extras");
        orderJsonObject.remove("poiReceiveDetail");
        orderJsonObject.remove("ctime");
        orderJsonObject.remove("utime");
        orderJsonObject.remove("deliveryTime");
        orderJsonObject.remove("hasInvoiced");
        orderJsonObject.remove("isThirdShipping");

        String[] tenantIdAndBranchIdArray = ePoiId.split("Z");
        BigInteger tenantId = NumberUtils.createBigInteger(tenantIdAndBranchIdArray[0]);
        BigInteger branchId = NumberUtils.createBigInteger(tenantIdAndBranchIdArray[1]);
        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        Branch branch = branchMapper.find(searchModel);
        Validate.notNull(branch, "门店不存在！");

        MeiTuanOrder meiTuanOrder = GsonUtils.fromJson(orderJsonObject.toString(), MeiTuanOrder.class);
        Calendar ctimeCalendar = Calendar.getInstance();
        ctimeCalendar.setTimeInMillis(ctime);
        meiTuanOrder.setCtime(ctimeCalendar.getTime());

        Calendar utimeCalendar = Calendar.getInstance();
        utimeCalendar.setTimeInMillis(utime);
        meiTuanOrder.setUtime(utimeCalendar.getTime());

        Calendar deliveryTimeCalendar = Calendar.getInstance();
        deliveryTimeCalendar.setTimeInMillis(deliveryTime);
        meiTuanOrder.setDeliveryTime(deliveryTimeCalendar.getTime());
        meiTuanOrder.setHasInvoiced(hasInvoiced == 1);
        meiTuanOrder.setThirdShipping(isThirdShipping == 1);

        return null;
    }
}
