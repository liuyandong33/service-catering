package build.dream.catering.utils;

import build.dream.catering.beans.BuyGiveActivityBean;
import build.dream.catering.beans.FullReductionActivityBean;
import build.dream.catering.beans.SpecialGoodsActivityBean;
import build.dream.catering.constants.Constants;
import build.dream.common.erp.catering.domains.DietOrderActivity;
import build.dream.common.erp.catering.domains.DietOrderDetail;
import build.dream.common.erp.catering.domains.DietOrderDetailGoodsFlavor;
import build.dream.common.erp.catering.domains.DietOrderGroup;
import build.dream.common.utils.CacheUtils;
import build.dream.common.utils.GsonUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

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

    public static DietOrderDetail constructDietOrderDetail(BigInteger tenantId, String tenantCode, BigInteger branchId, BigInteger dietOrderId, BigInteger dietOrderGroupId, BigInteger goodsId, String goodsName, BigInteger goodsSpecificationId, String goodsSpecificationName, BigInteger categoryId, BigDecimal price, BigDecimal flavorIncrease, Integer quantity, BigDecimal totalAmount, BigDecimal discountAmount, BigDecimal payableAmount, BigInteger userId, String lastUpdateRemark) {
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
        dietOrderDetail.setCategoryId(categoryId);
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

    public static BuyGiveActivityBean findBuyGiveActivityBean(BigInteger tenantId, BigInteger branchId, BigInteger goodsId, BigInteger goodsSpecificationId, Integer quantity) {
        String buyGiveActivityJson = CacheUtils.hget(Constants.KEY_BUY_GIVE_ACTIVITIES, tenantId + "_" + branchId + "_" + goodsId + "_" + goodsSpecificationId);
        if (StringUtils.isNotBlank(buyGiveActivityJson)) {
            BuyGiveActivityBean buyGiveActivityBean = GsonUtils.fromJson(buyGiveActivityJson, BuyGiveActivityBean.class);
            if (quantity >= buyGiveActivityBean.getBuyQuantity()) {
                return buyGiveActivityBean;
            }
        }
        return null;
    }

    public static FullReductionActivityBean findFullReductionActivityBean(BigDecimal dietOrderTotalAmount, String tenantId, String branchId) {
        String fullReductionActivitiesJson = CacheUtils.hget(Constants.KEY_FULL_REDUCTION_ACTIVITIES, tenantId + "_" + branchId);
        if (StringUtils.isBlank(fullReductionActivitiesJson)) {
            return null;
        }
        List<FullReductionActivityBean> fullReductionActivityBeans = GsonUtils.jsonToList(fullReductionActivitiesJson, FullReductionActivityBean.class);
        Map<Integer, FullReductionActivityBean> fullReductionActivityBeanMap = new HashMap<Integer, FullReductionActivityBean>();
        Map<BigDecimal, Integer> totalAmountAndHashCodeMap = new HashMap<BigDecimal, Integer>();
        List<BigDecimal> keys = new ArrayList<BigDecimal>();
        for (FullReductionActivityBean fullReductionActivityBean : fullReductionActivityBeans) {
            keys.add(fullReductionActivityBean.getTotalAmount());
            fullReductionActivityBeanMap.put(fullReductionActivityBean.hashCode(), fullReductionActivityBean);
            totalAmountAndHashCodeMap.put(fullReductionActivityBean.getTotalAmount(), fullReductionActivityBean.hashCode());
        }
        Collections.sort(keys, new Comparator<BigDecimal>() {
            @Override
            public int compare(BigDecimal o1, BigDecimal o2) {
                return o1.compareTo(o2) * -1;
            }
        });
        FullReductionActivityBean fullReductionActivityBean = null;
        for (BigDecimal key : keys) {
            if (dietOrderTotalAmount.compareTo(key) >= 0) {
                fullReductionActivityBean = fullReductionActivityBeanMap.get(totalAmountAndHashCodeMap.get(key));
                break;
            }
        }
        return fullReductionActivityBean;
    }

    public static SpecialGoodsActivityBean findSpecialGoodsActivityBean(BigInteger tenantId, BigInteger branchId, BigInteger goodsId, BigInteger goodsSpecificationId) {
        String specialGoodsActivityJson = CacheUtils.hget(Constants.KEY_SPECIAL_GOODS_ACTIVITIES, tenantId + "_" + branchId + "_" + goodsId + "_" + goodsSpecificationId);
        SpecialGoodsActivityBean specialGoodsActivityBean = null;
        if (StringUtils.isNotBlank(specialGoodsActivityJson)) {
            specialGoodsActivityBean = GsonUtils.fromJson(specialGoodsActivityJson, SpecialGoodsActivityBean.class);
        }
        return specialGoodsActivityBean;
    }
}
