package build.dream.catering.services;

import build.dream.common.catering.domains.DietOrder;
import build.dream.common.catering.domains.DietOrderDetail;
import build.dream.common.catering.domains.DietOrderGroup;
import build.dream.common.constants.Constants;
import build.dream.common.constants.DietOrderConstants;
import build.dream.common.utils.DatabaseHelper;
import build.dream.common.utils.ValidateUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class JDDJService {
    @Transactional
    public void handleNewOrder() {
        Map<String, Object> resultMap = null;
        Map<String, Object> result = MapUtils.getMap(resultMap, "result");
        List<Map<String, Object>> resultList = (List<Map<String, Object>>) result.get("resultList");
        ValidateUtils.isTrue(resultList.size() == 1, "订单不存在！");

        BigInteger tenantId = BigInteger.ZERO;
        String tenantCode = "";
        BigInteger branchId = BigInteger.ZERO;

        Map<String, Object> orderInfo = resultList.get(0);
        List<Map<String, Object>> products = (List<Map<String, Object>>) orderInfo.get("product");
        Map<String, Object> orderInvoice = MapUtils.getMap(orderInfo, "orderInvoice");

        DietOrder dietOrder = DietOrder.builder()
                .tenantId(tenantId)
                .tenantCode(tenantCode)
                .branchId(branchId)
                .orderNumber("JDDJ" + MapUtils.getLong(orderInfo, "orderId"))
                .orderType(5)
                .orderStatus(DietOrderConstants.ORDER_STATUS_UNPROCESSED)
                .payStatus(DietOrderConstants.PAY_STATUS_PAID)
                .refundStatus(DietOrderConstants.REFUND_STATUS_NO_REFUND)
                .build();

        DatabaseHelper.insert(dietOrder);
        BigInteger dietOrderId = dietOrder.getId();

        DietOrderGroup dietOrderGroup = DietOrderGroup.builder()
                .tenantId(tenantId)
                .tenantCode(tenantCode)
                .branchId(branchId)
                .dietOrderId(dietOrderId)
                .name("正常的商品")
                .type(DietOrderConstants.GROUP_TYPE_NORMAL)
                .build();
        DatabaseHelper.insert(dietOrderGroup);

        BigInteger dietOrderGroupId = dietOrderGroup.getId();

        List<DietOrderDetail> dietOrderDetails = new ArrayList<DietOrderDetail>();
        for (Map<String, Object> product : products) {
            long skuId = MapUtils.getLongValue(product, "skuId");
            String skuName = MapUtils.getString(product, "skuName");
            BigInteger skuIdIsv = BigInteger.valueOf(MapUtils.getLongValue(product, "skuIdIsv"));
            BigDecimal skuJdPrice = BigDecimal.valueOf(MapUtils.getDoubleValue(product, "skuJdPrice")).divide(Constants.BIG_DECIMAL_ONE_HUNDRED);
            BigDecimal skuCount = BigDecimal.valueOf(MapUtils.getDoubleValue(product, "skuCount"));
            DietOrderDetail dietOrderDetail = DietOrderDetail.builder()
                    .tenantId(tenantId)
                    .tenantCode(tenantCode)
                    .branchId(branchId)
                    .dietOrderId(dietOrderId)
                    .dietOrderGroupId(dietOrderGroupId)
                    .goodsType(1)
                    .goodsId(skuIdIsv)
                    .goodsName(skuName)
                    .goodsSpecificationId(BigInteger.ZERO)
                    .goodsSpecificationName(Constants.VARCHAR_DEFAULT_VALUE)
                    .categoryId(BigInteger.ZERO)
                    .categoryName(Constants.VARCHAR_DEFAULT_VALUE)
                    .price(skuJdPrice)
                    .quantity(skuCount)
                    .totalAmount(skuJdPrice.multiply(skuCount))
                    .discountAmount(BigDecimal.ZERO)
                    .payableAmount(skuJdPrice.multiply(skuCount))
                    .build();
            dietOrderDetails.add(dietOrderDetail);
        }
        DatabaseHelper.insertAll(dietOrderDetails);
    }
}
