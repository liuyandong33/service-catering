package build.dream.catering.services;

import build.dream.catering.mappers.BranchMapper;
import build.dream.common.catering.domains.Branch;
import build.dream.common.catering.domains.DietOrder;
import build.dream.common.catering.domains.DietOrderDetail;
import build.dream.common.catering.domains.DietOrderGroup;
import build.dream.common.constants.Constants;
import build.dream.common.constants.DietOrderConstants;
import build.dream.common.saas.domains.Tenant;
import build.dream.common.utils.*;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class JDDJService {
    @Autowired
    private BranchMapper branchMapper;

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

    @Transactional(readOnly = true)
    public void cacheJDDJVenderInfo() {
        List<Branch> branches = branchMapper.obtainAllBindJDDJBranches();
        Map<String, String> venderInfos = new HashMap<String, String>();
        Map<BigInteger, Tenant> tenantMap = new HashMap<BigInteger, Tenant>();
        for (Branch branch : branches) {
            BigInteger tenantId = branch.getTenantId();
            Tenant tenant = tenantMap.get(tenantId);
            if (tenant == null) {
                tenant = TenantUtils.obtainTenantInfo(tenantId);
                tenantMap.put(tenantId, tenant);
            }

            BigInteger branchId = branch.getId();
            String appKey = branch.getJddjAppKey();
            Map<String, Object> venderInfo = new HashMap<String, Object>();
            venderInfo.put("tenantId", tenantId);
            venderInfo.put("tenantCode", tenant.getCode());
            venderInfo.put("partitionCode", tenant.getPartitionCode());
            venderInfo.put("branchId", branchId);
            venderInfo.put("venderId", appKey);
            venderInfo.put("appKey", branch.getJddjAppKey());
            venderInfo.put("appSecret", branch.getJddjAppSecret());

            String info = JacksonUtils.writeValueAsString(venderInfo);
            venderInfos.put(appKey, info);
            venderInfos.put(tenantId + "_" + branchId, info);
        }
        if (MapUtils.isNotEmpty(venderInfos)) {
            CommonRedisUtils.hmset(Constants.KEY_JDDJ_VENDER_INFOS, venderInfos);
        }
    }
}
