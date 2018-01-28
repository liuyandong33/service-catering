package build.dream.catering.utils;

import build.dream.common.erp.catering.domains.DietOrderActivity;
import build.dream.common.erp.catering.domains.DietOrderDetail;
import build.dream.common.erp.catering.domains.DietOrderDetailGoodsFlavor;
import build.dream.common.erp.catering.domains.DietOrderGroup;

import java.math.BigDecimal;
import java.math.BigInteger;

public class DietOrderUtils {
    public static DietOrderActivity constructDietOrderActivity(BigInteger tenantId, String tenantCode, BigInteger branchId, BigInteger dietOrderId, BigInteger activityId, String activityName, Integer activityType, BigDecimal amount, BigInteger userId, String lastUpdateRemark) {
        DietOrderActivity dietOrderActivity = new DietOrderActivity();
        dietOrderActivity.setTenantId(tenantId);
        dietOrderActivity.setTenantCode(tenantCode);
        dietOrderActivity.setBranchId(branchId);
        dietOrderActivity.setDietOrderId(dietOrderId);
        dietOrderActivity.setActivityId(activityId);
        dietOrderActivity.setActivityName(activityName);
        dietOrderActivity.setActivityType(activityType);
        dietOrderActivity.setAmount(amount);
        dietOrderActivity.setCreateUserId(userId);
        dietOrderActivity.setLastUpdateUserId(userId);
        dietOrderActivity.setLastUpdateRemark(lastUpdateRemark);
        return dietOrderActivity;
    }

    public static DietOrderGroup constructDietOrderGroup(BigInteger tenantId, String tenantCode, BigInteger branchId, BigInteger dietOrderId, String name, String type, BigInteger userId, String lastUpdateRemark) {
        DietOrderGroup dietOrderGroup = new DietOrderGroup();
        dietOrderGroup.setTenantId(tenantId);
        dietOrderGroup.setTenantCode(tenantCode);
        dietOrderGroup.setBranchId(branchId);
        dietOrderGroup.setDietOrderId(dietOrderId);
        dietOrderGroup.setName(name);
        dietOrderGroup.setType(type);
        dietOrderGroup.setCreateUserId(userId);
        dietOrderGroup.setLastUpdateUserId(userId);
        dietOrderGroup.setLastUpdateRemark(lastUpdateRemark);
        return dietOrderGroup;
    }

    public static DietOrderDetail constructDietOrderDetail(BigInteger tenantId, String tenantCode, BigInteger branchId, BigInteger dietOrderId, BigInteger dietOrderGroupId, BigInteger goodsId, String goodsName, BigInteger goodsSpecificationId, String goodsSpecificationName, BigDecimal price, BigDecimal flavorIncrease, Integer quantity, BigDecimal totalAmount, BigDecimal discountAmount, BigDecimal payableAmount, BigInteger userId, String lastUpdateRemark) {
        DietOrderDetail dietOrderDetail = new DietOrderDetail();
        dietOrderDetail.setTenantId(tenantId);
        dietOrderDetail.setTenantCode(tenantCode);
        dietOrderDetail.setBranchId(branchId);
        dietOrderDetail.setDietOrderId(dietOrderId);
        dietOrderDetail.setDietOrderGroupId(dietOrderGroupId);
        dietOrderDetail.setGoodsId(goodsId);
        dietOrderDetail.setGoodsName(goodsName);
        dietOrderDetail.setGoodsSpecificationId(goodsSpecificationId);
        dietOrderDetail.setGoodsSpecificationName(goodsSpecificationName);
        dietOrderDetail.setPrice(price);
        dietOrderDetail.setFlavorIncrease(flavorIncrease);
        dietOrderDetail.setQuantity(quantity);
        dietOrderDetail.setTotalAmount(totalAmount);
        dietOrderDetail.setDiscountAmount(discountAmount);
        dietOrderDetail.setPayableAmount(payableAmount);
        dietOrderDetail.setCreateUserId(userId);
        dietOrderDetail.setLastUpdateUserId(userId);
        dietOrderDetail.setLastUpdateRemark(lastUpdateRemark);
        return dietOrderDetail;
    }

    public static DietOrderDetailGoodsFlavor constructDietOrderDetailGoodsFlavor(BigInteger tenantId, String tenantCode, BigInteger branchId, BigInteger dietOrderId, BigInteger dietOrderGroupId, BigInteger dietOrderDetailId, BigInteger goodsFlavorGroupId, String goodsFlavorGroupName, BigInteger goodsFlavorId, String goodsFlavorName, BigDecimal price, BigInteger userId, String lastUpdateRemark) {
        DietOrderDetailGoodsFlavor dietOrderDetailGoodsFlavor = new DietOrderDetailGoodsFlavor();
        dietOrderDetailGoodsFlavor.setTenantId(tenantId);
        dietOrderDetailGoodsFlavor.setTenantCode(tenantCode);
        dietOrderDetailGoodsFlavor.setBranchId(branchId);
        dietOrderDetailGoodsFlavor.setDietOrderId(dietOrderId);
        dietOrderDetailGoodsFlavor.setDietOrderGroupId(dietOrderGroupId);
        dietOrderDetailGoodsFlavor.setDietOrderDetailId(dietOrderDetailId);
        dietOrderDetailGoodsFlavor.setGoodsFlavorGroupId(goodsFlavorGroupId);
        dietOrderDetailGoodsFlavor.setGoodsFlavorGroupName(goodsFlavorGroupName);
        dietOrderDetailGoodsFlavor.setGoodsFlavorId(goodsFlavorId);
        dietOrderDetailGoodsFlavor.setGoodsFlavorName(goodsFlavorName);
        dietOrderDetailGoodsFlavor.setPrice(price);
        dietOrderDetailGoodsFlavor.setCreateUserId(userId);
        dietOrderDetailGoodsFlavor.setLastUpdateUserId(userId);
        dietOrderDetailGoodsFlavor.setLastUpdateRemark(lastUpdateRemark);
        return dietOrderDetailGoodsFlavor;
    }
}
