package build.dream.catering.utils;

import build.dream.catering.constants.Constants;
import build.dream.common.erp.catering.domains.*;
import build.dream.common.utils.ObjectUtils;
import build.dream.common.utils.SearchModel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.Validate;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public class SaleFlowUtils {
    public static void writeSaleFlow(DietOrder dietOrder, List<DietOrderDetail> dietOrderDetails, List<DietOrderPayment> dietOrderPayments) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        Map<String, List<DietOrderDetail>> dietOrderDetailListMap = new HashMap<String, List<DietOrderDetail>>();
        for (DietOrderDetail dietOrderDetail : dietOrderDetails) {
            String key = dietOrderDetail.getGoodsId() + "_" + dietOrderDetail.getGoodsSpecificationId();
            List<DietOrderDetail> dietOrderDetailList = dietOrderDetailListMap.get(key);
            if (CollectionUtils.isEmpty(dietOrderDetailList)) {
                dietOrderDetailList = new ArrayList<DietOrderDetail>();
                dietOrderDetailListMap.put(key, dietOrderDetailList);
            }
            dietOrderDetailList.add(dietOrderDetail);
        }

        List<DietOrderDetail> mergedDietOrderDetails = new ArrayList<DietOrderDetail>();
        for (Map.Entry<String, List<DietOrderDetail>> entry : dietOrderDetailListMap.entrySet()) {
            List<DietOrderDetail> dietOrderDetailList = entry.getValue();
            if (dietOrderDetailList.size() > 1) {
                BigDecimal quantity = BigDecimal.ZERO;
                BigDecimal totalAmount = BigDecimal.ZERO;
                BigDecimal discountAmount = BigDecimal.ZERO;
                BigDecimal payableAmount = BigDecimal.ZERO;
                for (DietOrderDetail dietOrderDetail : dietOrderDetailList) {
                    quantity = quantity.add(dietOrderDetail.getQuantity());
                    totalAmount = totalAmount.add(dietOrderDetail.getTotalAmount());
                    discountAmount = discountAmount.add(dietOrderDetail.getDiscountAmount());
                    payableAmount = payableAmount.add(dietOrderDetail.getPayableAmount());
                }
                DietOrderDetail mergedDietOrderDetail = ObjectUtils.clone(DietOrderDetail.class, dietOrderDetailList.get(0));
                mergedDietOrderDetail.setQuantity(quantity);
                mergedDietOrderDetail.setTotalAmount(totalAmount);
                mergedDietOrderDetail.setDiscountAmount(discountAmount);
                mergedDietOrderDetail.setPayableAmount(payableAmount);
                mergedDietOrderDetails.add(mergedDietOrderDetail);
            } else {
                mergedDietOrderDetails.add(dietOrderDetailList.get(0));
            }
        }

        BigDecimal totalAmount = dietOrder.getTotalAmount();
        BigDecimal discountAmount = dietOrder.getDiscountAmount();
        BigDecimal payableAmount = dietOrder.getPayableAmount();
        BigDecimal paidAmount = dietOrder.getPaidAmount();

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
        sale.setTotalAmount(totalAmount);
        sale.setDiscountAmount(discountAmount);
        sale.setPayableAmount(payableAmount);
        sale.setPaidAmount(paidAmount);
        sale.setCreateTime(date);
        sale.setLastUpdateTime(date);
        sale.setCreateUserId(userId);
        sale.setLastUpdateUserId(userId);
        DatabaseHelper.insert(sale);

        BigInteger saleId = sale.getId();

        List<SaleDetail> saleDetails = new ArrayList<SaleDetail>();
        BigDecimal weightSum = BigDecimal.ZERO;
        int size = mergedDietOrderDetails.size();
        for (int index = 0; index < size; index++) {
            DietOrderDetail dietOrderDetail = mergedDietOrderDetails.get(index);
            BigDecimal weight = null;
            if (index == size - 1) {
                weight = BigDecimal.ONE.subtract(weightSum);
            } else {
                weight = dietOrderDetail.getTotalAmount().divide(totalAmount, 1, BigDecimal.ROUND_DOWN);
                weightSum = weightSum.add(weight);
            }
            dietOrderDetail.setDiscountAmount(discountAmount.multiply(weight));
            dietOrderDetail.setPayableAmount(payableAmount.multiply(weight));
            dietOrderDetail.setPaidAmount(paidAmount.multiply(weight));

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
            saleDetail.setPaidAmount(null);
            saleDetail.setCreateUserId(userId);
            saleDetail.setLastUpdateUserId(userId);
            saleDetail.setCreateTime(date);
            saleDetail.setLastUpdateTime(date);
            saleDetails.add(saleDetail);
        }
        DatabaseHelper.insertAll(saleDetails);
    }

    public static void writeSaleFlow(BigInteger dietOrderId) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        DietOrder dietOrder = DatabaseHelper.find(DietOrder.class, dietOrderId);
        Validate.notNull(dietOrder, "订单不存在！");

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("diet_order_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, dietOrderId);

        List<DietOrderDetail> dietOrderDetails = DatabaseHelper.findAll(DietOrderDetail.class, searchModel);
        List<DietOrderPayment> dietOrderPayments = DatabaseHelper.findAll(DietOrderPayment.class, searchModel);
        writeSaleFlow(dietOrder, dietOrderDetails, dietOrderPayments);
    }

    public static void main(String[] args) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        writeSaleFlow(null, null, null);
    }
}
