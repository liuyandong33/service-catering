package build.dream.catering.services;

import build.dream.catering.constants.Constants;
import build.dream.common.catering.domains.*;
import build.dream.common.constants.DietOrderConstants;
import build.dream.common.models.beeleme.OrderGetModel;
import build.dream.common.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
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

        OrderGetModel orderGetModel = OrderGetModel.builder()
                .source(source)
                .orderId(orderId)
                .build();
        Map<String, Object> orderGetResult = BeElemeUtils.orderGet(orderGetModel);
        Map<String, Object> orderGetResultData = MapUtils.getMap(orderGetResult, "data");

        Map<String, Object> shop = MapUtils.getMap(orderGetResultData, "shop");
        Map<String, Object> user = MapUtils.getMap(orderGetResultData, "user");
        Map<String, Object> order = MapUtils.getMap(orderGetResultData, "order");
        List<List<Map<String, Object>>> products = (List<List<Map<String, Object>>>) orderGetResultData.get("products");
        List<Map<String, Object>> discounts = (List<Map<String, Object>>) orderGetResultData.get("discount");
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

        String tenantCode = branch.getTenantCode();

        BigInteger userId = Constants.BIG_INTEGER_EIGHT;
        DietOrder dietOrder = DietOrder.builder()
                .tenantId(tenantId)
                .tenantCode(tenantCode)
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
                .createdUserId(userId)
                .updatedUserId(userId)
                .updatedRemark("保存饿百新零售订单！")
                .build();
        DatabaseHelper.insert(dietOrder);

        BigInteger dietOrderId = dietOrder.getId();

        Map<String, DietOrderGroup> dietOrderGroupMap = new HashMap<String, DietOrderGroup>();
        for (List<Map<String, Object>> product : products) {
            DietOrderGroup dietOrderGroup = DietOrderGroup.builder()
                    .tenantId(tenantId)
                    .tenantCode(tenantCode)
                    .branchId(branchId)
                    .dietOrderId(dietOrderId)
                    .name("")
                    .type(DietOrderConstants.GROUP_TYPE_NORMAL)
                    .createdUserId(userId)
                    .updatedUserId(userId)
                    .build();
            DatabaseHelper.insert(dietOrderGroup);
            BigInteger dietOrderGroupId = dietOrderGroup.getId();

            for (Map<String, Object> goodsInfo : product) {
                BigDecimal price = BigDecimal.valueOf(MapUtils.getDoubleValue(goodsInfo, "product_fee"));
                BigDecimal quantity = BigDecimal.valueOf(MapUtils.getDoubleValue(goodsInfo, "product_amount"));
                BigDecimal totalAmount = price.multiply(quantity);
                Map<String, Object> productSubsidy = MapUtils.getMap(goodsInfo, "product_subsidy");
                BigDecimal discountAmount = BigDecimal.valueOf(MapUtils.getDoubleValue(productSubsidy, "shop_rate")).divide(Constants.BIG_DECIMAL_ONE_HUNDRED);
                DietOrderDetail dietOrderDetail = DietOrderDetail.builder()
                        .tenantId(tenantId)
                        .tenantCode(tenantCode)
                        .branchId(branchId)
                        .dietOrderId(dietOrderId)
                        .dietOrderGroupId(dietOrderGroupId)
                        .goodsType(Constants.GOODS_TYPE_ORDINARY_GOODS)
                        .goodsId(BigInteger.valueOf(MapUtils.getLongValue(goodsInfo, "baidu_product_id")))
                        .goodsName(MapUtils.getString(goodsInfo, "goodsName"))
                        .goodsSpecificationId(Constants.BIGINT_DEFAULT_VALUE)
                        .goodsSpecificationName(Constants.VARCHAR_DEFAULT_VALUE)
                        .categoryId(Constants.BIGINT_DEFAULT_VALUE)
                        .categoryName(Constants.VARCHAR_DEFAULT_VALUE)
                        .price(price)
                        .quantity(quantity)
                        .totalAmount(totalAmount)
                        .discountAmount(discountAmount)
                        .payableAmount(totalAmount.subtract(discountAmount))
                        .createdUserId(userId)
                        .updatedUserId(userId)
                        .build();
                DatabaseHelper.insert(dietOrderDetail);
                List<Map<String, Object>> productFeatures = (List<Map<String, Object>>) goodsInfo.get("product_features");
                if (MapUtils.isEmpty(productSubsidy)) {
                    continue;
                }

                BigInteger dietOrderDetailId = dietOrderDetail.getId();
                for (Map<String, Object> productFeature : productFeatures) {
                    DietOrderDetailGoodsAttribute dietOrderDetailGoodsAttribute = DietOrderDetailGoodsAttribute.builder()
                            .tenantId(tenantId)
                            .tenantCode(tenantCode)
                            .branchId(branchId)
                            .dietOrderId(dietOrderId)
                            .dietOrderGroupId(dietOrderGroupId)
                            .dietOrderDetailId(dietOrderDetailId)
                            .goodsAttributeGroupId(Constants.BIG_INTEGER_EIGHT)
                            .goodsAttributeGroupName(MapUtils.getString(productFeature, "name"))
                            .goodsAttributeId(BigInteger.valueOf(MapUtils.getLongValue(productFeature, "baidu_feature_id")))
                            .goodsAttributeName(MapUtils.getString(productFeature, "option"))
                            .createdUserId(userId)
                            .updatedUserId(userId)
                            .build();
                    DatabaseHelper.insert(dietOrderDetailGoodsAttribute);
                }
            }

            if (CollectionUtils.isNotEmpty(discounts)) {
                for (Map<String, Object> discount : discounts) {
                    DietOrderActivity dietOrderActivity = DietOrderActivity.builder()
                            .tenantId(tenantId)
                            .tenantCode(tenantCode)
                            .branchId(branchId)
                            .dietOrderId(dietOrderId)
                            .activityId(Constants.BIGINT_DEFAULT_VALUE)
                            .activityName(Constants.VARCHAR_DEFAULT_VALUE)
                            .activityType(Constants.INT_DEFAULT_VALUE)
                            .amount(BigDecimal.valueOf(MapUtils.getDoubleValue(discount, "shop_rate")).divide(Constants.BIG_DECIMAL_ONE_HUNDRED))
                            .createdUserId(userId)
                            .updatedUserId(userId)
                            .build();
                    DatabaseHelper.insert(dietOrderActivity);
                }
            }
        }
    }
}
