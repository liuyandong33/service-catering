package build.dream.catering.utils;

import build.dream.catering.constants.Constants;
import build.dream.common.erp.catering.domains.*;
import build.dream.common.utils.DatabaseHelper;
import build.dream.common.utils.SearchCondition;
import build.dream.common.utils.SearchModel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.Validate;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public class SaleFlowUtils {
    public static void writeSaleFlow(DietOrder dietOrder, List<DietOrderGroup> dietOrderGroups, List<DietOrderDetail> dietOrderDetails, List<DietOrderActivity> dietOrderActivities, List<DietOrderPayment> dietOrderPayments) {
        Map<BigInteger, List<DietOrderDetail>> dietOrderDetailListMap = new HashMap<BigInteger, List<DietOrderDetail>>();
        for (DietOrderDetail dietOrderDetail : dietOrderDetails) {
            BigInteger dietOrderGroupId = dietOrderDetail.getDietOrderGroupId();
            List<DietOrderDetail> dietOrderDetailList = dietOrderDetailListMap.get(dietOrderGroupId);
            if (CollectionUtils.isEmpty(dietOrderDetailList)) {
                dietOrderDetailList = new ArrayList<DietOrderDetail>();
                dietOrderDetailListMap.put(dietOrderGroupId, dietOrderDetailList);
            }
            dietOrderDetailList.add(dietOrderDetail);
        }

        List<DietOrderDetail> normalDietOrderDetails = null;
        List<DietOrderDetail> extraDietOrderDetails = null;
        List<DietOrderDetail> discountDietOrderDetails = null;
        for (DietOrderGroup dietOrderGroup : dietOrderGroups) {
            String type = dietOrderGroup.getType();
            BigInteger dietOrderGroupId = dietOrderGroup.getId();
            if (Constants.NORMAL.equals(type)) {
                normalDietOrderDetails = dietOrderDetailListMap.get(dietOrderGroupId);
            } else if (Constants.EXTRA.equals(type)) {
                extraDietOrderDetails = dietOrderDetailListMap.get(dietOrderGroupId);
            } else if (Constants.DISCOUNT.equals(type)) {
                discountDietOrderDetails = dietOrderDetailListMap.get(dietOrderGroupId);
            }
        }

        BigInteger tenantId = dietOrder.getTenantId();
        String tenantCode = dietOrder.getTenantCode();
        BigInteger branchId = dietOrder.getBranchId();
        Date saleTime = dietOrder.getActiveTime();

        BigInteger userId = BigInteger.ZERO;
        Date date = new Date();

        Sale sale = new Sale();
        sale.setTenantId(tenantId);
        sale.setTenantCode(tenantCode);
        sale.setBranchId(branchId);
        sale.setSaleCode(dietOrder.getOrderNumber());
        sale.setSaleTime(saleTime);
        sale.setTotalAmount(dietOrder.getTotalAmount());
        sale.setDiscountAmount(dietOrder.getDiscountAmount());
        sale.setPayableAmount(dietOrder.getPayableAmount());
        sale.setPaidAmount(dietOrder.getPaidAmount());
        sale.setCreateTime(date);
        sale.setLastUpdateTime(date);
        sale.setCreateUserId(userId);
        sale.setLastUpdateUserId(userId);
        DatabaseHelper.insert(sale);

        BigInteger saleId = sale.getId();

        // 保存订单中整单优惠活动（包括整单优惠，支付促销）的优惠金额
        BigDecimal discountAmount = BigDecimal.ZERO;
        for (DietOrderActivity dietOrderActivity : dietOrderActivities) {
            int activityType = dietOrderActivity.getActivityType();
            if (activityType == 2 || activityType == 4) {
                discountAmount = discountAmount.add(dietOrderActivity.getAmount());
            }
        }
        if (discountAmount.compareTo(BigDecimal.ZERO) > 0) {
            calculateShare(normalDietOrderDetails, discountAmount);
        }

        List<SaleDetail> saleDetails = new ArrayList<SaleDetail>();
        for (DietOrderDetail dietOrderDetail : normalDietOrderDetails) {
            saleDetails.add(buildSaleDetail(saleId, saleTime, tenantId, tenantCode, branchId, dietOrderDetail, userId));
        }
        DatabaseHelper.insertAll(saleDetails);
    }

    public static void writeSaleFlow(BigInteger dietOrderId) {
        DietOrder dietOrder = DatabaseHelper.find(DietOrder.class, dietOrderId);
        Validate.notNull(dietOrder, "订单不存在！");

        List<SearchCondition> searchConditions = new ArrayList<SearchCondition>();
        searchConditions.add(new SearchCondition("diet_order_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, dietOrderId));
        searchConditions.add(new SearchCondition("deleted", Constants.SQL_OPERATION_SYMBOL_EQUAL, 0));

        SearchModel dietOrderGroupSearchModel = new SearchModel();
        dietOrderGroupSearchModel.setSearchConditions(searchConditions);
        List<DietOrderGroup> dietOrderGroups = DatabaseHelper.findAll(DietOrderGroup.class, dietOrderGroupSearchModel);

        SearchModel dietOrderDetailSearchModel = new SearchModel();
        dietOrderDetailSearchModel.setSearchConditions(searchConditions);
        List<DietOrderDetail> dietOrderDetails = DatabaseHelper.findAll(DietOrderDetail.class, dietOrderDetailSearchModel);

        SearchModel dietOrderActivitySearchModel = new SearchModel();
        dietOrderActivitySearchModel.setSearchConditions(searchConditions);
        List<DietOrderActivity> dietOrderActivities = DatabaseHelper.findAll(DietOrderActivity.class, dietOrderActivitySearchModel);

        SearchModel dietOrderPaymentSearchModel = new SearchModel();
        dietOrderPaymentSearchModel.setSearchConditions(searchConditions);
        List<DietOrderPayment> dietOrderPayments = DatabaseHelper.findAll(DietOrderPayment.class, dietOrderPaymentSearchModel);

        writeSaleFlow(dietOrder, dietOrderGroups, dietOrderDetails, dietOrderActivities, dietOrderPayments);
    }

    public static void calculateShare(List<DietOrderDetail> dietOrderDetails, BigDecimal discountAmount) {
        BigDecimal discountAmountShareSum = BigDecimal.ZERO;
        BigDecimal denominator = obtainDenominator(dietOrderDetails);

        int size = dietOrderDetails.size();
        for (int index = 0; index < size; index++) {
            DietOrderDetail dietOrderDetail = dietOrderDetails.get(index);

            BigDecimal discountAmountShare = null;
            if (index == size - 1) {
                discountAmountShare = discountAmount.subtract(discountAmountShareSum);
            } else {
                BigDecimal weight = dietOrderDetail.getTotalAmount().divide(denominator, 10, BigDecimal.ROUND_DOWN);
                discountAmountShare = discountAmount.multiply(weight).setScale(2, BigDecimal.ROUND_DOWN);
                discountAmountShareSum = discountAmountShareSum.add(discountAmountShare);
            }
            dietOrderDetail.setDiscountShare(discountAmountShare);
        }
    }

    public static BigDecimal obtainDenominator(List<DietOrderDetail> dietOrderDetails) {
        BigDecimal denominator = BigDecimal.ZERO;
        for (DietOrderDetail dietOrderDetail : dietOrderDetails) {
            denominator = denominator.add(dietOrderDetail.getTotalAmount());
        }
        return denominator;
    }

    public static SaleDetail buildSaleDetail(BigInteger saleId, Date saleTime, BigInteger tenantId, String tenantCode, BigInteger branchId, DietOrderDetail dietOrderDetail, BigInteger userId) {
        SaleDetail saleDetail = new SaleDetail();
        saleDetail.setSaleId(saleId);
        saleDetail.setSaleTime(saleTime);
        saleDetail.setTenantId(tenantId);
        saleDetail.setTenantCode(tenantCode);
        saleDetail.setBranchId(branchId);
        saleDetail.setGoodsId(dietOrderDetail.getGoodsId());
        saleDetail.setGoodsName(dietOrderDetail.getGoodsName());
        saleDetail.setGoodsSpecificationId(dietOrderDetail.getGoodsSpecificationId());
        saleDetail.setGoodsSpecificationName(dietOrderDetail.getGoodsSpecificationName());
        saleDetail.setCategoryId(dietOrderDetail.getCategoryId());
        saleDetail.setCategoryName(dietOrderDetail.getCategoryName());
        saleDetail.setPrice(dietOrderDetail.getPrice());
        saleDetail.setQuantity(dietOrderDetail.getQuantity());
        saleDetail.setTotalAmount(dietOrderDetail.getTotalAmount());
        saleDetail.setDiscountAmount(dietOrderDetail.getDiscountAmount());
        saleDetail.setPayableAmount(dietOrderDetail.getPayableAmount());
        saleDetail.setPaidAmount(dietOrderDetail.getPaidAmount());

        BigDecimal discountShare = dietOrderDetail.getDiscountShare();
        saleDetail.setDiscountShare(discountShare != null ? discountShare : BigDecimal.ZERO);

        saleDetail.setCreateUserId(userId);
        saleDetail.setLastUpdateUserId(userId);
        return saleDetail;
    }
}
