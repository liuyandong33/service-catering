package build.dream.catering.utils;

import build.dream.catering.constants.Constants;
import build.dream.common.constants.DietOrderConstants;
import build.dream.common.domains.catering.*;
import build.dream.common.utils.*;
import org.apache.commons.collections.CollectionUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class SaleFlowUtils {
    public static void writeSaleFlow(DietOrder dietOrder, List<DietOrderGroup> dietOrderGroups, List<DietOrderDetail> dietOrderDetails, List<DietOrderActivity> dietOrderActivities, List<DietOrderPayment> dietOrderPayments) throws IOException {
        Map<Long, List<DietOrderDetail>> dietOrderDetailListMap = dietOrderDetails.stream().collect(Collectors.groupingBy(DietOrderDetail::getDietOrderGroupId));

        List<DietOrderDetail> normalDietOrderDetails = new ArrayList<DietOrderDetail>();
        List<DietOrderDetail> extraDietOrderDetails = new ArrayList<DietOrderDetail>();
        List<DietOrderDetail> discountDietOrderDetails = new ArrayList<DietOrderDetail>();
        for (DietOrderGroup dietOrderGroup : dietOrderGroups) {
            String type = dietOrderGroup.getType();
            Long dietOrderGroupId = dietOrderGroup.getId();
            if (DietOrderConstants.GROUP_TYPE_NORMAL.equals(type)) {
                normalDietOrderDetails.addAll(dietOrderDetailListMap.get(dietOrderGroupId));
            } else if (DietOrderConstants.GROUP_TYPE_EXTRA.equals(type)) {
                extraDietOrderDetails.addAll(dietOrderDetailListMap.get(dietOrderGroupId));
            } else if (DietOrderConstants.GROUP_TYPE_DISCOUNT.equals(type)) {
                discountDietOrderDetails.addAll(dietOrderDetailListMap.get(dietOrderGroupId));
            }
        }

        Long tenantId = dietOrder.getTenantId();
        String tenantCode = dietOrder.getTenantCode();
        Long branchId = dietOrder.getBranchId();
        Date saleTime = dietOrder.getActiveTime();

        Long userId = CommonUtils.getServiceSystemUserId();

        Sale sale = Sale.builder()
                .tenantId(tenantId)
                .tenantCode(tenantCode)
                .branchId(branchId)
                .saleCode(dietOrder.getOrderNumber())
                .saleTime(saleTime)
                .totalAmount(dietOrder.getTotalAmount())
                .discountAmount(dietOrder.getDiscountAmount())
                .payableAmount(dietOrder.getPayableAmount())
                .paidAmount(dietOrder.getPaidAmount())
                .createdUserId(userId)
                .updatedUserId(userId)
                .build();
        DatabaseHelper.insert(sale);

        Long saleId = sale.getId();

        // 保存订单中整单优惠活动（包括整单优惠，支付促销）的优惠金额
        Double discountAmount = 0D;
        int orderType = dietOrder.getOrderType();
        if (orderType == DietOrderConstants.ORDER_TYPE_SCAN_CODE_ORDER || orderType == DietOrderConstants.ORDER_TYPE_WEI_XIN_ORDER) {
            for (DietOrderActivity dietOrderActivity : dietOrderActivities) {
                int activityType = dietOrderActivity.getActivityType();
                if (activityType == 2 || activityType == 4) {
                    discountAmount = discountAmount + dietOrderActivity.getAmount();
                }
            }
        } else if (orderType == DietOrderConstants.ORDER_TYPE_ELEME_ORDER) {
            for (DietOrderActivity dietOrderActivity : dietOrderActivities) {
                discountAmount = discountAmount + dietOrderActivity.getAmount();
            }
        } else if (orderType == DietOrderConstants.ORDER_TYPE_MEI_TUAN_ORDER) {
            for (DietOrderActivity dietOrderActivity : dietOrderActivities) {
                discountAmount = discountAmount + dietOrderActivity.getAmount();
            }
        }
        if (discountAmount > 0) {
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

    public static void writeSaleFlow(Long tenantId, Long branchId, Long dietOrderId) throws IOException {
        SearchModel searchModel = SearchModel.builder()
                .autoSetDeletedFalse()
                .equal(DietOrder.ColumnName.TENANT_ID, tenantId)
                .equal(DietOrder.ColumnName.BRANCH_ID, branchId)
                .equal(DietOrder.ColumnName.ID, dietOrderId)
                .build();
        DietOrder dietOrder = DatabaseHelper.find(DietOrder.class, searchModel);
        ValidateUtils.notNull(dietOrder, "订单不存在！");

        List<SearchCondition> searchConditions = new ArrayList<SearchCondition>();
        searchConditions.add(new SearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId));
        searchConditions.add(new SearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId));
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

    public static void calculateShare(List<DietOrderDetail> dietOrderDetails, Double discountAmount) {
        Double discountAmountShareSum = 0D;
        Double denominator = obtainDenominator(dietOrderDetails);

        int size = dietOrderDetails.size();
        for (int index = 0; index < size; index++) {
            DietOrderDetail dietOrderDetail = dietOrderDetails.get(index);

            Double discountAmountShare = null;
            if (index == size - 1) {
                discountAmountShare = discountAmount - discountAmountShareSum;
            } else {
                Double weight = dietOrderDetail.getTotalAmount() / denominator;
                discountAmountShare = BigDecimal.valueOf(discountAmount * weight).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                discountAmountShareSum = discountAmountShareSum + discountAmountShare;
            }
            dietOrderDetail.setDiscountShare(discountAmountShare);
        }
    }

    public static Double obtainDenominator(List<DietOrderDetail> dietOrderDetails) {
        Double denominator = 0D;
        for (DietOrderDetail dietOrderDetail : dietOrderDetails) {
            denominator = denominator + dietOrderDetail.getTotalAmount();
        }
        return denominator;
    }

    public static SaleDetail buildSaleDetail(Long saleId, Date saleTime, Long tenantId, String tenantCode, Long branchId, DietOrderDetail dietOrderDetail, Long userId) {
        Double discountShare = dietOrderDetail.getDiscountShare();
        return SaleDetail.builder()
                .saleId(saleId)
                .saleTime(saleTime)
                .tenantId(tenantId)
                .tenantCode(tenantCode)
                .branchId(branchId)
                .goodsId(dietOrderDetail.getGoodsId())
                .goodsName(dietOrderDetail.getGoodsName())
                .goodsSpecificationId(dietOrderDetail.getGoodsSpecificationId())
                .goodsSpecificationName(dietOrderDetail.getGoodsSpecificationName())
                .categoryId(dietOrderDetail.getCategoryId())
                .categoryName(dietOrderDetail.getCategoryName())
                .price(dietOrderDetail.getPrice())
                .quantity(dietOrderDetail.getQuantity())
                .totalAmount(dietOrderDetail.getTotalAmount())
                .discountAmount(dietOrderDetail.getDiscountAmount())
                .payableAmount(dietOrderDetail.getPayableAmount())
                .discountShare(Objects.isNull(discountShare) ? 0D : discountShare)
                .createdUserId(userId)
                .updatedUserId(userId)
                .build();
    }

    public static SalePayment buildSalePayment(Long saleId, Date saleTime, Long tenantId, String tenantCode, Long branchId, DietOrderPayment dietOrderPayment, Long userId) {
        return SalePayment.builder()
                .saleId(saleId)
                .saleTime(saleTime)
                .tenantId(tenantId)
                .tenantCode(tenantCode)
                .branchId(branchId)
                .paymentId(dietOrderPayment.getPaymentId())
                .paymentCode(dietOrderPayment.getPaymentCode())
                .paymentName(dietOrderPayment.getPaymentName())
                .paidAmount(dietOrderPayment.getPaidAmount())
                .createdUserId(userId)
                .updatedUserId(userId)
                .build();
    }
}
