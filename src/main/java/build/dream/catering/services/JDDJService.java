package build.dream.catering.services;

import build.dream.catering.constants.Constants;
import build.dream.catering.models.jddj.CancelAndRefundModel;
import build.dream.catering.models.jddj.CancelOrderModel;
import build.dream.catering.models.jddj.ConfirmOrderModel;
import build.dream.catering.models.jddj.PrintOrderModel;
import build.dream.common.api.ApiRest;
import build.dream.common.catering.domains.DietOrder;
import build.dream.common.catering.domains.DietOrderActivity;
import build.dream.common.catering.domains.DietOrderDetail;
import build.dream.common.catering.domains.DietOrderGroup;
import build.dream.common.constants.DietOrderConstants;
import build.dream.common.models.jddj.OrderAcceptOperateModel;
import build.dream.common.utils.DatabaseHelper;
import build.dream.common.utils.JDDJUtils;
import build.dream.common.utils.SearchModel;
import build.dream.common.utils.ValidateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

@Service
public class JDDJService {
    /**
     * 处理新订单回调
     *
     * @param tenantId
     * @param tenantCode
     * @param message
     */
    @Transactional
    public void handleNewOrder(BigInteger tenantId, String tenantCode, Map<String, Object> message) {
        Map<String, Object> resultMap = null;
        Map<String, Object> result = MapUtils.getMap(resultMap, "result");
        List<Map<String, Object>> resultList = (List<Map<String, Object>>) result.get("resultList");
        ValidateUtils.isTrue(resultList.size() == 1, "订单不存在！");

        BigInteger branchId = BigInteger.ZERO;
        BigInteger userId = BigInteger.ZERO;

        Map<String, Object> orderInfo = resultList.get(0);
        List<Map<String, Object>> products = (List<Map<String, Object>>) orderInfo.get("product");
        Map<String, Object> orderInvoice = MapUtils.getMap(orderInfo, "orderInvoice");

        boolean invoiced = MapUtils.isNotEmpty(orderInvoice);

        DietOrder dietOrder = DietOrder.builder()
                .tenantId(tenantId)
                .tenantCode(tenantCode)
                .branchId(branchId)
                .orderNumber("JDDJ" + MapUtils.getLong(orderInfo, "orderId"))
                .orderType(5)
                .orderStatus(DietOrderConstants.ORDER_STATUS_UNPROCESSED)
                .payStatus(DietOrderConstants.PAY_STATUS_PAID)
                .refundStatus(DietOrderConstants.REFUND_STATUS_NO_REFUND)
                .invoiced(invoiced)
                .invoiceType(invoiced ? (MapUtils.getIntValue(orderInvoice, "invoiceType") == 0 ? DietOrderConstants.INVOICE_TYPE_PERSONAL : DietOrderConstants.INVOICE_TYPE_COMPANY) : Constants.VARCHAR_DEFAULT_VALUE)
                .invoice(invoiced ? MapUtils.getString(orderInvoice, "invoiceTitle") : Constants.VARCHAR_DEFAULT_VALUE)
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
                    .goodsType(Constants.GOODS_TYPE_ORDINARY_GOODS)
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

        List<Map<String, Object>> discounts = (List<Map<String, Object>>) orderInfo.get("discount");
        List<DietOrderActivity> dietOrderActivities = new ArrayList<DietOrderActivity>();
        for (Map<String, Object> discount : discounts) {
            Double venderPayMoney = MapUtils.getDouble(discount, "venderPayMoney");
            if (Objects.isNull(venderPayMoney)) {
                continue;
            }

            DietOrderActivity dietOrderActivity = DietOrderActivity.builder()
                    .tenantId(tenantId)
                    .tenantCode(tenantCode)
                    .branchId(branchId)
                    .dietOrderId(dietOrderId)
                    .activityId(BigInteger.ZERO)
                    .activityName("")
                    .activityType(1)
                    .amount(BigDecimal.valueOf(venderPayMoney).divide(Constants.BIG_DECIMAL_ONE_HUNDRED))
                    .createdUserId(userId)
                    .updatedUserId(userId)
                    .build();
            dietOrderActivities.add(dietOrderActivity);
        }
        if (CollectionUtils.isNotEmpty(dietOrderActivities)) {
            DatabaseHelper.insertAll(dietOrderActivities);
        }
    }

    /**
     * 处理订单调整消息
     *
     * @param tenantId
     * @param tenantCode
     * @param message
     */
    public void handleOrderAdjust(BigInteger tenantId, String tenantCode, Map<String, Object> message) {

    }

    /**
     * 处理用户取消申请消息
     *
     * @param tenantId
     * @param tenantCode
     * @param message
     */
    public void handleApplyCancelOrder(BigInteger tenantId, String tenantCode, Map<String, Object> message) {

    }

    /**
     * 处理订单等待出库消息
     *
     * @param tenantId
     * @param tenantCode
     * @param message
     */
    public void handleOrderWaitOutStore(BigInteger tenantId, String tenantCode, Map<String, Object> message) {

    }

    /**
     * 处理订单开始配送消息
     *
     * @param tenantId
     * @param tenantCode
     * @param message
     */
    public void handleDeliveryOrder(BigInteger tenantId, String tenantCode, Map<String, Object> message) {

    }

    /**
     * 处理订单拣货完成消息
     *
     * @param tenantId
     * @param tenantCode
     * @param message
     */
    public void handlePickFinishOrder(BigInteger tenantId, String tenantCode, Map<String, Object> message) {

    }

    /**
     * 处理订单妥投消息
     *
     * @param tenantId
     * @param tenantCode
     * @param message
     */
    public void handleFinishOrder(BigInteger tenantId, String tenantCode, Map<String, Object> message) {

    }

    /**
     * 处理订单锁定消息
     *
     * @param tenantId
     * @param tenantCode
     * @param message
     */
    public void handleLockOrder(BigInteger tenantId, String tenantCode, Map<String, Object> message) {

    }

    /**
     * 处理订单解锁消息
     *
     * @param tenantId
     * @param tenantCode
     * @param message
     */
    public void handleUnlockOrder(BigInteger tenantId, String tenantCode, Map<String, Object> message) {

    }

    /**
     * 处理用户取消消息
     *
     * @param tenantId
     * @param tenantCode
     * @param message
     */
    public void handleUserCancelOrder(BigInteger tenantId, String tenantCode, Map<String, Object> message) {

    }

    /**
     * 处理订单运单状态消息
     *
     * @param tenantId
     * @param tenantCode
     * @param message
     */
    public void handlePushDeliveryStatus(BigInteger tenantId, String tenantCode, Map<String, Object> message) {

    }

    /**
     * 处理订单信息变更消息
     *
     * @param tenantId
     * @param tenantCode
     * @param message
     */
    public void handleOrderInfoChange(BigInteger tenantId, String tenantCode, Map<String, Object> message) {

    }

    /**
     * 订单商家小费消息
     *
     * @param tenantId
     * @param tenantCode
     * @param message
     */
    public void handleOrderAddTips(BigInteger tenantId, String tenantCode, Map<String, Object> message) {

    }

    /**
     * 订单应结消息
     *
     * @param tenantId
     * @param tenantCode
     * @param message
     */
    public void handleOrderAccounting(BigInteger tenantId, String tenantCode, Map<String, Object> message) {

    }

    /**
     * 订单转自送消息
     *
     * @param tenantId
     * @param tenantCode
     * @param message
     */
    public void handleDeliveryCarrierModify(BigInteger tenantId, String tenantCode, Map<String, Object> message) {

    }

    private DietOrder obtainDietOrder(BigInteger tenantId, BigInteger branchId, BigInteger orderId) {
        SearchModel searchModel = SearchModel.builder()
                .autoSetDeletedFalse()
                .equal(DietOrder.ColumnName.TENANT_ID, tenantId)
                .equal(DietOrder.ColumnName.BRANCH_ID, branchId)
                .equal(DietOrder.ColumnName.ID, orderId)
                .build();
        DietOrder dietOrder = DatabaseHelper.find(DietOrder.class, searchModel);
        ValidateUtils.notNull(dietOrder, "订单不存在！");
        return dietOrder;
    }

    /**
     * 确认订单
     *
     * @param confirmOrderModel
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest confirmOrder(ConfirmOrderModel confirmOrderModel) {
        BigInteger tenantId = confirmOrderModel.obtainTenantId();
        BigInteger branchId = confirmOrderModel.obtainBranchId();
        BigInteger orderId = confirmOrderModel.getOrderId();
        DietOrder dietOrder = obtainDietOrder(tenantId, branchId, orderId);
        OrderAcceptOperateModel orderAcceptOperateModel = OrderAcceptOperateModel.builder()
                .orderId(Long.valueOf(dietOrder.getOrderNumber().substring(4)))
                .isAgreed(Boolean.TRUE)
                .operator("")
                .build();
        Map<String, Object> result = JDDJUtils.orderAcceptOperate(orderAcceptOperateModel);

        return ApiRest.builder().message("确认订单成功！").successful(true).build();
    }

    /**
     * 取消订单
     *
     * @param cancelOrderModel
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest cancelOrder(CancelOrderModel cancelOrderModel) {
        BigInteger tenantId = cancelOrderModel.obtainTenantId();
        BigInteger branchId = cancelOrderModel.obtainBranchId();
        BigInteger orderId = cancelOrderModel.getOrderId();
        DietOrder dietOrder = obtainDietOrder(tenantId, branchId, orderId);
        OrderAcceptOperateModel orderAcceptOperateModel = OrderAcceptOperateModel.builder()
                .orderId(Long.valueOf(dietOrder.getOrderNumber().substring(4)))
                .isAgreed(Boolean.FALSE)
                .operator("")
                .build();
        Map<String, Object> result = JDDJUtils.orderAcceptOperate(orderAcceptOperateModel);

        return ApiRest.builder().message("确认订单成功！").successful(true).build();
    }

    /**
     * 订单取消且退款接口
     * 1、商家自送订单在配送流程中，若用户拒收，商家可调用接口进行取消；
     * 2、非商家自送订单，调用接口取消失败，仅可用户进行取消；
     * 3、达达配送转商家自送的订单，若用户拒收，商家可调用接口进行取消；
     *
     * @param cancelAndRefundModel
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest cancelAndRefund(CancelAndRefundModel cancelAndRefundModel) {
        BigInteger tenantId = cancelAndRefundModel.obtainTenantId();
        BigInteger branchId = cancelAndRefundModel.obtainBranchId();
        BigInteger orderId = cancelAndRefundModel.getOrderId();
        DietOrder dietOrder = obtainDietOrder(tenantId, branchId, orderId);

        build.dream.common.models.jddj.CancelAndRefundModel jddjCancelAndRefundModel = build.dream.common.models.jddj.CancelAndRefundModel.builder()
                .orderId(Long.valueOf(dietOrder.getOrderNumber().substring(4)))
                .operPin("")
                .operRemark("")
                .operTime(new Date())
                .build();

        Map<String, Object> result = JDDJUtils.cancelAndRefund(jddjCancelAndRefundModel);
        return ApiRest.builder().message("订单取消且退款成功！").successful(true).build();
    }

    /**
     * 订单已打印接口
     *
     * @param printOrderModel
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest printOrder(PrintOrderModel printOrderModel) {
        BigInteger tenantId = printOrderModel.obtainTenantId();
        BigInteger branchId = printOrderModel.obtainBranchId();
        BigInteger orderId = printOrderModel.getOrderId();
        DietOrder dietOrder = obtainDietOrder(tenantId, branchId, orderId);

        build.dream.common.models.jddj.PrintOrderModel jddjPrintOrderModel = build.dream.common.models.jddj.PrintOrderModel.builder()
                .orderId(Long.valueOf(dietOrder.getOrderNumber().substring(4)))
                .build();
        Map<String, Object> result = JDDJUtils.printOrder(jddjPrintOrderModel);
        return ApiRest.builder().message("订单已打印成功！").successful(true).build();
    }
}
