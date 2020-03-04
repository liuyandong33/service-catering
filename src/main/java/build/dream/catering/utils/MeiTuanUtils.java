package build.dream.catering.utils;

import build.dream.catering.constants.ConfigurationKeys;
import build.dream.catering.constants.Constants;
import build.dream.catering.constants.RedisKeys;
import build.dream.common.domains.catering.Branch;
import build.dream.common.domains.catering.DietOrder;
import build.dream.common.tuples.Tuple2;
import build.dream.common.utils.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class MeiTuanUtils {
    public static String generateSignature(String signKey, Map<String, String> requestParameters) {
        Map<String, String> sortedRequestParameters = new TreeMap<String, String>(requestParameters);
        StringBuilder finalData = new StringBuilder(signKey);
        for (Map.Entry<String, String> sortedRequestParameter : sortedRequestParameters.entrySet()) {
            finalData.append(sortedRequestParameter.getKey()).append(sortedRequestParameter.getValue());
        }
        return DigestUtils.sha1Hex(finalData.toString());
    }

    public static Map<String, Object> callMeiTuanSystem(String tenantId, String branchId, String signKey, Map<String, String> requestParameters, String url, String requestMethod) {
        String appAuthToken = getMeiTuanAppAuthToken(tenantId, branchId);
        String charset = Constants.CHARSET_NAME_UTF_8;
        String timestamp = String.valueOf(System.currentTimeMillis());
        String version = "1";

        Map<String, String> params = new HashMap<String, String>(requestParameters);
        params.put("appAuthToken", appAuthToken);
        params.put("charset", charset);
        params.put("timestamp", timestamp);
        params.put("version", version);

        String sign = generateSignature(signKey, params);
        params.put("sign", sign);

        String result = null;
        if (Constants.REQUEST_METHOD_GET.equals(requestMethod)) {
            result = OutUtils.doGet(url, params);
        } else if (Constants.REQUEST_METHOD_POST.equals(requestMethod)) {
            StringBuilder requestUrl = new StringBuilder(url);
            requestUrl.append("?appAuthToken=").append(appAuthToken);
            requestUrl.append("&charset=").append(charset);
            requestUrl.append("&timestamp=").append(timestamp);
            requestUrl.append("&version=").append(version);
            requestUrl.append("&sign=").append(sign);
            result = OutUtils.doPostWithForm(requestUrl.toString(), requestParameters);
        }
        Map<String, Object> resultMap = JacksonUtils.readValueAsMap(result, String.class, Object.class);
        String code = MapUtils.getString(resultMap, "code");
        ValidateUtils.isTrue(StringUtils.isBlank(code), MapUtils.getString(resultMap, "msg"));
        return resultMap;
    }

    public static Map<String, Object> callMeiTuanSystem(String tenantId, String branchId, Map<String, String> requestParameters, String url, String requestMethod) {
        String signKey = ConfigurationUtils.getConfiguration(ConfigurationKeys.MEI_TUAN_SIGN_KEY);
        return callMeiTuanSystem(tenantId, branchId, signKey, requestParameters, url, requestMethod);
    }

    public static String getMeiTuanAppAuthToken(String tenantId, String branchId) {
        String meiTuanAppAuthToken = CommonRedisUtils.hget(RedisKeys.KEY_MEI_TUAN_APP_AUTH_TOKENS, tenantId + "_" + branchId);
        ValidateUtils.notNull(meiTuanAppAuthToken, "门店未绑定美团！");
        return meiTuanAppAuthToken;
    }

    public static Tuple2<Long, Long> obtainTenantAndBranchId(Map<String, String> callbackParameters) {
        String ePoiId = callbackParameters.get("ePoiId");
        return obtainTenantAndBranchId(ePoiId);
    }

    public static Tuple2<Long, Long> obtainTenantAndBranchId(String ePoiId) {
        String[] tenantIdAndBranchIdArray = ePoiId.split("Z");
        Long tenantId = NumberUtils.createLong(tenantIdAndBranchIdArray[0]);
        Long branchId = NumberUtils.createLong(tenantIdAndBranchIdArray[1]);
        return TupleUtils.buildTuple2(tenantId, branchId);
    }

    public static DietOrder obtainDietOrder(Map<String, String> callbackParameters) {
        Tuple2<Long, Long> tuple2 = MeiTuanUtils.obtainTenantAndBranchId(callbackParameters);
        Long tenantId = tuple2._1();
        Long branchId = tuple2._2();
        String order = callbackParameters.get("order");
        Map<String, Object> orderMap = JacksonUtils.readValueAsMap(order, String.class, Object.class);
        String orderId = MapUtils.getString(orderMap, "orderId");

        SearchModel searchModel = SearchModel.builder()
                .autoSetDeletedFalse()
                .addSearchCondition(DietOrder.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId)
                .addSearchCondition(DietOrder.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId)
                .addSearchCondition(DietOrder.ColumnName.ORDER_NUMBER, Constants.SQL_OPERATION_SYMBOL_EQUAL, "M" + orderId)
                .build();
        return DatabaseHelper.find(DietOrder.class, searchModel);
    }

    public static Branch obtainBranch(String ePoiId) {
        Tuple2<Long, Long> tuple2 = MeiTuanUtils.obtainTenantAndBranchId(ePoiId);
        Long tenantId = tuple2._1();
        Long branchId = tuple2._2();

        SearchModel searchModel = SearchModel.builder()
                .addSearchCondition(Branch.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId)
                .addSearchCondition(Branch.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId)
                .build();
        return DatabaseHelper.find(Branch.class, searchModel);
    }

    public static Branch obtainBranch(Map<String, String> callbackParameters) {
        String ePoiId = callbackParameters.get("ePoiId");
        return obtainBranch(ePoiId);
    }
}
