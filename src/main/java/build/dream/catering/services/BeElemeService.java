package build.dream.catering.services;

import build.dream.catering.constants.Constants;
import build.dream.common.catering.domains.Branch;
import build.dream.common.catering.domains.DietOrder;
import build.dream.common.constants.DietOrderConstants;
import build.dream.common.utils.DatabaseHelper;
import build.dream.common.utils.JacksonUtils;
import build.dream.common.utils.SearchModel;
import build.dream.common.utils.ValidateUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

@Service
public class BeElemeService {
    /**
     * 处理订单索赔状态推送
     */
    public void handleOrderClaimPush(Map<String, String> params) {

    }

    /**
     * 处理订单创建推送
     */
    public void handleOrderCreate(Map<String, String> params) {
        String source = params.get("source");
        String body = params.get("body");
        Map<String, Object> bodyMap = JacksonUtils.readValueAsMap(body, String.class, Object.class);
        Map<String, Object> data = MapUtils.getMap(bodyMap, "data");

        String orderId = MapUtils.getString(data, "order_id");

        Map<String, Object> orderGetResult = new HashMap<String, Object>();
        Map<String, Object> orderGetResultData = MapUtils.getMap(orderGetResult, "data");

        Map<String, Object> shop = MapUtils.getMap(orderGetResultData, "shop");
        Map<String, Object> user = MapUtils.getMap(orderGetResultData, "user");
        Map<String, Object> order = MapUtils.getMap(orderGetResultData, "order");
        String id = MapUtils.getString(shop, "id");
        String[] array = id.split("Z");
        BigInteger tenantId = BigInteger.valueOf(Long.valueOf(array[0]));
        BigInteger branchId = BigInteger.valueOf(Long.valueOf(array[1]));

        SearchModel searchModel = SearchModel.builder()
                .autoSetDeletedFalse()
                .addSearchCondition(Branch.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId)
                .addSearchCondition(Branch.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId)
                .build();
        Branch branch = DatabaseHelper.find(Branch.class, searchModel);
        ValidateUtils.notNull(branch, "门店不存在！");

        DietOrder dietOrder = DietOrder.builder()
                .tenantId(tenantId)
                .tenantCode(branch.getTenantCode())
                .branchId(branchId)
                .orderNumber("BE" + orderId)
                .orderType(DietOrderConstants.ORDER_TYPE_ELEME_ORDER)
                .orderStatus(DietOrderConstants.ORDER_STATUS_INVALID)
                .payStatus(DietOrderConstants.PAY_STATUS_PAID)
                .refundStatus(DietOrderConstants.REFUND_STATUS_NO_REFUND)
                .totalAmount(Constants.DECIMAL_DEFAULT_VALUE)
                .discountAmount(Constants.DECIMAL_DEFAULT_VALUE)
                .payableAmount(Constants.DECIMAL_DEFAULT_VALUE)
                .paidAmount(Constants.DECIMAL_DEFAULT_VALUE)
                .paidType(Constants.PAID_TYPE_ELM)
                .remark(Constants.VARCHAR_DEFAULT_VALUE)
                .deliveryAddress(Constants.VARCHAR_DEFAULT_VALUE)
                .deliveryLongitude(Constants.VARCHAR_DEFAULT_VALUE)
                .deliveryLatitude(Constants.VARCHAR_DEFAULT_VALUE)
                .deliverTime(Constants.DATETIME_DEFAULT_VALUE)
                .activeTime(Constants.DATETIME_DEFAULT_VALUE)
                .deliverFee(Constants.DECIMAL_DEFAULT_VALUE)
                .telephoneNumber(Constants.VARCHAR_DEFAULT_VALUE)
                .daySerialNumber(Constants.VARCHAR_DEFAULT_VALUE)
                .consignee(Constants.VARCHAR_DEFAULT_VALUE)
                .invoiced(false)
                .invoiceType(Constants.VARCHAR_DEFAULT_VALUE)
                .invoice(Constants.VARCHAR_DEFAULT_VALUE)
                .vipId(Constants.BIGINT_DEFAULT_VALUE)
                .createdUserId(Constants.BIG_INTEGER_EIGHT)
                .updatedUserId(Constants.BIG_INTEGER_EIGHT)
                .updatedRemark("保存饿百新零售订单！")
                .build();
        DatabaseHelper.insert(dietOrder);
    }
}
