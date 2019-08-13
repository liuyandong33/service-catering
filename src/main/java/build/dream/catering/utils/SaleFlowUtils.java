package build.dream.catering.utils;

import build.dream.catering.constants.Constants;
import build.dream.common.domains.catering.*;
import build.dream.common.constants.DietOrderConstants;
import build.dream.common.utils.*;
import org.apache.commons.collections.CollectionUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SaleFlowUtils {
    public static void writeSaleFlow(DietOrder dietOrder, List<DietOrderGroup> dietOrderGroups, List<DietOrderDetail> dietOrderDetails, List<DietOrderActivity> dietOrderActivities, List<DietOrderPayment> dietOrderPayments) throws IOException {
        Map<BigInteger, List<DietOrderDetail>> dietOrderDetailListMap = dietOrderDetails.stream().collect(Collectors.groupingBy(DietOrderDetail::getDietOrderGroupId));

        List<DietOrderDetail> normalDietOrderDetails = new ArrayList<DietOrderDetail>();
        List<DietOrderDetail> extraDietOrderDetails = new ArrayList<DietOrderDetail>();
        List<DietOrderDetail> discountDietOrderDetails = new ArrayList<DietOrderDetail>();
        for (DietOrderGroup dietOrderGroup : dietOrderGroups) {
            String type = dietOrderGroup.getType();
            BigInteger dietOrderGroupId = dietOrderGroup.getId();
            if (DietOrderConstants.GROUP_TYPE_NORMAL.equals(type)) {
                normalDietOrderDetails.addAll(dietOrderDetailListMap.get(dietOrderGroupId));
            } else if (DietOrderConstants.GROUP_TYPE_EXTRA.equals(type)) {
                extraDietOrderDetails.addAll(dietOrderDetailListMap.get(dietOrderGroupId));
            } else if (DietOrderConstants.GROUP_TYPE_DISCOUNT.equals(type)) {
                discountDietOrderDetails.addAll(dietOrderDetailListMap.get(dietOrderGroupId));
            }
        }

        BigInteger tenantId = dietOrder.getTenantId();
        String tenantCode = dietOrder.getTenantCode();
        BigInteger branchId = dietOrder.getBranchId();
        Date saleTime = dietOrder.getActiveTime();

        BigInteger userId = CommonUtils.getServiceSystemUserId();

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
        sale.setCreatedUserId(userId);
        sale.setUpdatedUserId(userId);
        DatabaseHelper.insert(sale);

        BigInteger saleId = sale.getId();

        // 保存订单中整单优惠活动（包括整单优惠，支付促销）的优惠金额
        BigDecimal discountAmount = BigDecimal.ZERO;
        int orderType = dietOrder.getOrderType();
        if (orderType == DietOrderConstants.ORDER_TYPE_SCAN_CODE_ORDER || orderType == DietOrderConstants.ORDER_TYPE_WEI_XIN_ORDER) {
            for (DietOrderActivity dietOrderActivity : dietOrderActivities) {
                int activityType = dietOrderActivity.getActivityType();
                if (activityType == 2 || activityType == 4) {
                    discountAmount = discountAmount.add(dietOrderActivity.getAmount());
                }
            }
        } else if (orderType == DietOrderConstants.ORDER_TYPE_ELEME_ORDER) {
            for (DietOrderActivity dietOrderActivity : dietOrderActivities) {
                discountAmount = discountAmount.add(dietOrderActivity.getAmount());
            }
        } else if (orderType == DietOrderConstants.ORDER_TYPE_MEI_TUAN_ORDER) {

        }
        if (discountAmount.compareTo(BigDecimal.ZERO) > 0) {
            calculateShare(normalDietOrderDetails, discountAmount);
        }

        List<SaleDetail> saleDetails = new ArrayList<SaleDetail>();
        for (DietOrderDetail dietOrderDetail : normalDietOrderDetails) {
            saleDetails.add(buildSaleDetail(saleId, saleTime, tenantId, tenantCode, branchId, dietOrderDetail, userId));
        }

        for (DietOrderDetail dietOrderDetail : extraDietOrderDetails) {
            saleDetails.add(buildSaleDetail(saleId, saleTime, tenantId, tenantCode, branchId, dietOrderDetail, userId));
        }

        for (DietOrderDetail dietOrderDetail : discountDietOrderDetails) {
            saleDetails.add(buildSaleDetail(saleId, saleTime, tenantId, tenantCode, branchId, dietOrderDetail, userId));
        }
        DatabaseHelper.insertAll(saleDetails);

        List<SalePayment> salePayments = new ArrayList<SalePayment>();
        for (DietOrderPayment dietOrderPayment : dietOrderPayments) {
            salePayments.add(buildSalePayment(saleId, saleTime, tenantId, tenantCode, branchId, dietOrderPayment, userId));
        }
        if (CollectionUtils.isNotEmpty(salePayments)) {
            DatabaseHelper.insertAll(salePayments);
        }
    }

    public static void writeSaleFlow(BigInteger dietOrderId) throws IOException {
        DietOrder dietOrder = DatabaseHelper.find(DietOrder.class, dietOrderId);
        ValidateUtils.notNull(dietOrder, "订单不存在！");

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

        BigDecimal discountShare = dietOrderDetail.getDiscountShare();
        saleDetail.setDiscountShare(discountShare != null ? discountShare : BigDecimal.ZERO);

        saleDetail.setCreatedUserId(userId);
        saleDetail.setUpdatedUserId(userId);
        return saleDetail;
    }

    public static SalePayment buildSalePayment(BigInteger saleId, Date saleTime, BigInteger tenantId, String tenantCode, BigInteger branchId, DietOrderPayment dietOrderPayment, BigInteger userId) {
        SalePayment salePayment = new SalePayment();
        salePayment.setSaleId(saleId);
        salePayment.setSaleTime(saleTime);
        salePayment.setTenantId(tenantId);
        salePayment.setTenantCode(tenantCode);
        salePayment.setBranchId(branchId);
        salePayment.setPaymentId(dietOrderPayment.getPaymentId());
        salePayment.setPaymentCode(dietOrderPayment.getPaymentCode());
        salePayment.setPaymentName(dietOrderPayment.getPaymentName());
        salePayment.setPaidAmount(dietOrderPayment.getPaidAmount());
        salePayment.setCreatedUserId(userId);
        salePayment.setUpdatedUserId(userId);
        return salePayment;
    }
}
