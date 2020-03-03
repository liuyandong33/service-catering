package build.dream.catering.services;

import build.dream.catering.constants.Constants;
import build.dream.catering.models.jddj.AddTipsModel;
import build.dream.catering.models.jddj.AdjustOrderModel;
import build.dream.catering.models.jddj.*;
import build.dream.catering.models.jddj.CancelAndRefundModel;
import build.dream.catering.models.jddj.CheckSelfPickCodeModel;
import build.dream.catering.models.jddj.ConfirmReceiveGoodsModel;
import build.dream.catering.models.jddj.DeliveryEndOrderModel;
import build.dream.catering.models.jddj.ModifySellerDeliveryModel;
import build.dream.catering.models.jddj.OrderDDTCDeliveryModel;
import build.dream.catering.models.jddj.OrderJDZBDeliveryModel;
import build.dream.catering.models.jddj.PrintOrderModel;
import build.dream.catering.models.jddj.UrgeDispatchingModel;
import build.dream.common.api.ApiRest;
import build.dream.common.constants.DietOrderConstants;
import build.dream.common.domains.catering.DietOrder;
import build.dream.common.domains.catering.DietOrderActivity;
import build.dream.common.domains.catering.DietOrderDetail;
import build.dream.common.domains.catering.DietOrderGroup;
import build.dream.common.models.jddj.*;
import build.dream.common.utils.DatabaseHelper;
import build.dream.common.utils.JDDJUtils;
import build.dream.common.utils.SearchModel;
import build.dream.common.utils.ValidateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public void handleNewOrder(Long tenantId, String tenantCode, Map<String, Object> message) {
        Map<String, Object> resultMap = null;
        Map<String, Object> result = MapUtils.getMap(resultMap, "result");
        List<Map<String, Object>> resultList = (List<Map<String, Object>>) result.get("resultList");
        ValidateUtils.isTrue(resultList.size() == 1, "订单不存在！");

        Long branchId = 0L;
        Long userId = 0L;

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
        Long dietOrderId = dietOrder.getId();

        DietOrderGroup dietOrderGroup = DietOrderGroup.builder()
                .tenantId(tenantId)
                .tenantCode(tenantCode)
                .branchId(branchId)
                .dietOrderId(dietOrderId)
                .name("正常的商品")
                .type(DietOrderConstants.GROUP_TYPE_NORMAL)
                .build();
        DatabaseHelper.insert(dietOrderGroup);

        Long dietOrderGroupId = dietOrderGroup.getId();

        List<DietOrderDetail> dietOrderDetails = new ArrayList<DietOrderDetail>();
        for (Map<String, Object> product : products) {
            long skuId = MapUtils.getLongValue(product, "skuId");
            String skuName = MapUtils.getString(product, "skuName");
            Long skuIdIsv = Long.valueOf(MapUtils.getLongValue(product, "skuIdIsv"));
            Double skuJdPrice = MapUtils.getDoubleValue(product, "skuJdPrice") / 100;
            Double skuCount = MapUtils.getDoubleValue(product, "skuCount");
            DietOrderDetail dietOrderDetail = DietOrderDetail.builder()
                    .tenantId(tenantId)
                    .tenantCode(tenantCode)
                    .branchId(branchId)
                    .dietOrderId(dietOrderId)
                    .dietOrderGroupId(dietOrderGroupId)
                    .goodsType(Constants.GOODS_TYPE_ORDINARY_GOODS)
                    .goodsId(skuIdIsv)
                    .goodsName(skuName)
                    .goodsSpecificationId(0L)
                    .goodsSpecificationName(Constants.VARCHAR_DEFAULT_VALUE)
                    .categoryId(0L)
                    .categoryName(Constants.VARCHAR_DEFAULT_VALUE)
                    .price(skuJdPrice)
                    .quantity(skuCount)
                    .totalAmount(skuJdPrice * skuCount)
                    .discountAmount(0D)
                    .payableAmount(skuJdPrice * skuCount)
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
                    .activityId(0L)
                    .activityName("")
                    .activityType(1)
                    .amount(venderPayMoney / 100)
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
    public void handleOrderAdjust(Long tenantId, String tenantCode, Map<String, Object> message) {

    }

    /**
     * 处理用户取消申请消息
     *
     * @param tenantId
     * @param tenantCode
     * @param message
     */
    public void handleApplyCancelOrder(Long tenantId, String tenantCode, Map<String, Object> message) {

    }

    /**
     * 处理订单等待出库消息
     *
     * @param tenantId
     * @param tenantCode
     * @param message
     */
    public void handleOrderWaitOutStore(Long tenantId, String tenantCode, Map<String, Object> message) {

    }

    /**
     * 处理订单开始配送消息
     *
     * @param tenantId
     * @param tenantCode
     * @param message
     */
    public void handleDeliveryOrder(Long tenantId, String tenantCode, Map<String, Object> message) {

    }

    /**
     * 处理订单拣货完成消息
     *
     * @param tenantId
     * @param tenantCode
     * @param message
     */
    public void handlePickFinishOrder(Long tenantId, String tenantCode, Map<String, Object> message) {

    }

    /**
     * 处理订单妥投消息
     *
     * @param tenantId
     * @param tenantCode
     * @param message
     */
    public void handleFinishOrder(Long tenantId, String tenantCode, Map<String, Object> message) {

    }

    /**
     * 处理订单锁定消息
     *
     * @param tenantId
     * @param tenantCode
     * @param message
     */
    public void handleLockOrder(Long tenantId, String tenantCode, Map<String, Object> message) {

    }

    /**
     * 处理订单解锁消息
     *
     * @param tenantId
     * @param tenantCode
     * @param message
     */
    public void handleUnlockOrder(Long tenantId, String tenantCode, Map<String, Object> message) {

    }

    /**
     * 处理用户取消消息
     *
     * @param tenantId
     * @param tenantCode
     * @param message
     */
    public void handleUserCancelOrder(Long tenantId, String tenantCode, Map<String, Object> message) {

    }

    /**
     * 处理订单运单状态消息
     *
     * @param tenantId
     * @param tenantCode
     * @param message
     */
    public void handlePushDeliveryStatus(Long tenantId, String tenantCode, Map<String, Object> message) {

    }

    /**
     * 处理订单信息变更消息
     *
     * @param tenantId
     * @param tenantCode
     * @param message
     */
    public void handleOrderInfoChange(Long tenantId, String tenantCode, Map<String, Object> message) {

    }

    /**
     * 订单商家小费消息
     *
     * @param tenantId
     * @param tenantCode
     * @param message
     */
    public void handleOrderAddTips(Long tenantId, String tenantCode, Map<String, Object> message) {

    }

    /**
     * 订单应结消息
     *
     * @param tenantId
     * @param tenantCode
     * @param message
     */
    public void handleOrderAccounting(Long tenantId, String tenantCode, Map<String, Object> message) {

    }

    /**
     * 订单转自送消息
     *
     * @param tenantId
     * @param tenantCode
     * @param message
     */
    public void handleDeliveryCarrierModify(Long tenantId, String tenantCode, Map<String, Object> message) {

    }

    private DietOrder obtainDietOrder(Long tenantId, Long branchId, Long orderId) {
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
        Long tenantId = confirmOrderModel.obtainTenantId();
        Long branchId = confirmOrderModel.obtainBranchId();
        Long orderId = confirmOrderModel.getOrderId();
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
        Long tenantId = cancelOrderModel.obtainTenantId();
        Long branchId = cancelOrderModel.obtainBranchId();
        Long orderId = cancelOrderModel.getOrderId();
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
        Long tenantId = cancelAndRefundModel.obtainTenantId();
        Long branchId = cancelAndRefundModel.obtainBranchId();
        Long orderId = cancelAndRefundModel.getOrderId();
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
        Long tenantId = printOrderModel.obtainTenantId();
        Long branchId = printOrderModel.obtainBranchId();
        Long orderId = printOrderModel.getOrderId();
        DietOrder dietOrder = obtainDietOrder(tenantId, branchId, orderId);

        build.dream.common.models.jddj.PrintOrderModel jddjPrintOrderModel = build.dream.common.models.jddj.PrintOrderModel.builder()
                .orderId(Long.valueOf(dietOrder.getOrderNumber().substring(4)))
                .build();
        Map<String, Object> result = JDDJUtils.printOrder(jddjPrintOrderModel);
        return ApiRest.builder().message("订单已打印成功！").successful(true).build();
    }

    /**
     * 同意取消订单
     *
     * @param agreeCancelOrderModel
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest agreeCancelOrder(AgreeCancelOrderModel agreeCancelOrderModel) {
        Long tenantId = agreeCancelOrderModel.obtainTenantId();
        Long branchId = agreeCancelOrderModel.obtainBranchId();
        Long orderId = agreeCancelOrderModel.getOrderId();
        String remark = agreeCancelOrderModel.getRemark();

        DietOrder dietOrder = obtainDietOrder(tenantId, branchId, orderId);

        OrderCancelOperateModel orderCancelOperateModel = OrderCancelOperateModel.builder()
                .orderId(Long.valueOf(dietOrder.getOrderNumber().substring(4)))
                .isAgreed(Boolean.TRUE)
                .operator("")
                .remark(remark)
                .build();
        Map<String, Object> result = JDDJUtils.orderCancelOperate(orderCancelOperateModel);
        return ApiRest.builder().message("同意取消订单成功！").successful(true).build();
    }

    /**
     * 不同意取消订单
     *
     * @param disagreeCancelOrderModel
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest disagreeCancelOrder(DisagreeCancelOrderModel disagreeCancelOrderModel) {
        Long tenantId = disagreeCancelOrderModel.obtainTenantId();
        Long branchId = disagreeCancelOrderModel.obtainBranchId();
        Long orderId = disagreeCancelOrderModel.getOrderId();
        String remark = disagreeCancelOrderModel.getRemark();

        DietOrder dietOrder = obtainDietOrder(tenantId, branchId, orderId);

        OrderCancelOperateModel orderCancelOperateModel = OrderCancelOperateModel.builder()
                .orderId(Long.valueOf(dietOrder.getOrderNumber().substring(4)))
                .isAgreed(Boolean.FALSE)
                .operator("")
                .remark(remark)
                .build();
        Map<String, Object> result = JDDJUtils.orderCancelOperate(orderCancelOperateModel);
        return ApiRest.builder().message("不同意取消订单成功！").successful(true).build();
    }

    /**
     * 订单调整
     *
     * @param adjustOrderModel
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest adjustOrder(AdjustOrderModel adjustOrderModel) {
        return ApiRest.builder().message("订单调整成功！").build();
    }

    /**
     * 拣货完成且众包配送
     *
     * @param orderJDZBDeliveryModel
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest orderJDZBDelivery(OrderJDZBDeliveryModel orderJDZBDeliveryModel) {
        Long tenantId = orderJDZBDeliveryModel.obtainTenantId();
        Long branchId = orderJDZBDeliveryModel.obtainBranchId();
        Long orderId = orderJDZBDeliveryModel.getOrderId();

        DietOrder dietOrder = obtainDietOrder(tenantId, branchId, orderId);
        build.dream.common.models.jddj.OrderJDZBDeliveryModel jddjOrderJDZBDeliveryModel = build.dream.common.models.jddj.OrderJDZBDeliveryModel.builder()
                .orderId(Long.valueOf(dietOrder.getOrderNumber().substring(4)))
                .operator("")
                .build();
        Map<String, Object> result = JDDJUtils.orderJDZBDelivery(jddjOrderJDZBDeliveryModel);
        return ApiRest.builder().message("拣货完成且众包配送成功！").successful(true).build();
    }

    /**
     * 拣货完成且达达同城配送
     *
     * @param orderDDTCDeliveryModel
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest orderDDTCDelivery(OrderDDTCDeliveryModel orderDDTCDeliveryModel) {
        Long tenantId = orderDDTCDeliveryModel.obtainTenantId();
        Long branchId = orderDDTCDeliveryModel.obtainBranchId();
        Long orderId = orderDDTCDeliveryModel.getOrderId();

        DietOrder dietOrder = obtainDietOrder(tenantId, branchId, orderId);

        build.dream.common.models.jddj.OrderDDTCDeliveryModel jddjOrderDDTCDeliveryModel = build.dream.common.models.jddj.OrderDDTCDeliveryModel.builder()
                .orderId(Long.valueOf(dietOrder.getOrderNumber().substring(4)))
                .operator("")
                .build();
        Map<String, Object> result = JDDJUtils.orderDDTCDelivery(jddjOrderDDTCDeliveryModel);
        return ApiRest.builder().message("拣货完成且达达同城配送成功！").successful(true).build();
    }

    /**
     * 拣货完成且商家自送
     *
     * @param orderSellerDeliveryModel
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest orderSellerDelivery(OrderSellerDeliveryModel orderSellerDeliveryModel) {
        Long tenantId = orderSellerDeliveryModel.obtainTenantId();
        Long branchId = orderSellerDeliveryModel.obtainBranchId();
        Long orderId = orderSellerDeliveryModel.getOrderId();

        DietOrder dietOrder = obtainDietOrder(tenantId, branchId, orderId);

        OrderSerllerDeliveryModel orderSerllerDeliveryModel = OrderSerllerDeliveryModel.builder()
                .orderId(Long.valueOf(dietOrder.getOrderNumber().substring(4)))
                .operator("")
                .build();
        Map<String, Object> result = JDDJUtils.orderSerllerDelivery(orderSerllerDeliveryModel);
        return ApiRest.builder().message("拣货完成且商家自送成功！").successful(true).build();
    }

    /**
     * 订单达达配送转商家自送
     *
     * @param modifySellerDeliveryModel
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest modifySellerDelivery(ModifySellerDeliveryModel modifySellerDeliveryModel) {
        Long tenantId = modifySellerDeliveryModel.obtainTenantId();
        Long branchId = modifySellerDeliveryModel.obtainBranchId();
        Long orderId = modifySellerDeliveryModel.getOrderId();

        DietOrder dietOrder = obtainDietOrder(tenantId, branchId, orderId);

        build.dream.common.models.jddj.ModifySellerDeliveryModel jddjModifySellerDeliveryModel = build.dream.common.models.jddj.ModifySellerDeliveryModel.builder()
                .orderId(Long.valueOf(dietOrder.getOrderNumber().substring(4)))
                .updatePin("")
                .build();
        Map<String, Object> result = JDDJUtils.modifySellerDelivery(jddjModifySellerDeliveryModel);
        return ApiRest.builder().message("订单达达配送转商家自送成功！").successful(true).build();
    }

    /**
     * 同意配送员取货失败
     *
     * @param agreePickUpFailedModel
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest agreePickUpFailed(AgreePickUpFailedModel agreePickUpFailedModel) {
        Long tenantId = agreePickUpFailedModel.obtainTenantId();
        Long branchId = agreePickUpFailedModel.obtainBranchId();
        Long orderId = agreePickUpFailedModel.getOrderId();
        String remark = agreePickUpFailedModel.getRemark();

        DietOrder dietOrder = obtainDietOrder(tenantId, branchId, orderId);

        ReceiveFailedAuditModel receiveFailedAuditModel = ReceiveFailedAuditModel.builder()
                .orderId(Long.valueOf(dietOrder.getOrderNumber().substring(4)))
                .isAgreed(Boolean.TRUE)
                .operator("")
                .remark(remark)
                .build();

        Map<String, Object> result = JDDJUtils.receiveFailedAudit(receiveFailedAuditModel);
        return ApiRest.builder().message("同意配送员取货失败成功！").successful(true).build();
    }

    /**
     * 不同意配送员取货失败
     *
     * @param disagreePickUpFailedModel
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest disagreePickUpFailed(DisagreePickUpFailedModel disagreePickUpFailedModel) {
        Long tenantId = disagreePickUpFailedModel.obtainTenantId();
        Long branchId = disagreePickUpFailedModel.obtainBranchId();
        Long orderId = disagreePickUpFailedModel.getOrderId();
        String remark = disagreePickUpFailedModel.getRemark();

        DietOrder dietOrder = obtainDietOrder(tenantId, branchId, orderId);

        ReceiveFailedAuditModel receiveFailedAuditModel = ReceiveFailedAuditModel.builder()
                .orderId(Long.valueOf(dietOrder.getOrderNumber().substring(4)))
                .isAgreed(Boolean.FALSE)
                .operator("")
                .remark(remark)
                .build();

        Map<String, Object> result = JDDJUtils.receiveFailedAudit(receiveFailedAuditModel);
        return ApiRest.builder().message("不同意配送员取货失败成功！").successful(true).build();
    }

    /**
     * 订单妥投
     *
     * @param deliveryEndOrderModel
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest deliveryEndOrder(DeliveryEndOrderModel deliveryEndOrderModel) {
        Long tenantId = deliveryEndOrderModel.obtainTenantId();
        Long branchId = deliveryEndOrderModel.obtainBranchId();
        Long orderId = deliveryEndOrderModel.getOrderId();

        DietOrder dietOrder = obtainDietOrder(tenantId, branchId, orderId);

        build.dream.common.models.jddj.DeliveryEndOrderModel jddjDeliveryEndOrderModel = build.dream.common.models.jddj.DeliveryEndOrderModel.builder()
                .orderId(Long.valueOf(dietOrder.getOrderNumber().substring(4)))
                .operPin("")
                .operTime(new Date())
                .build();
        Map<String, Object> result = JDDJUtils.deliveryEndOrder(jddjDeliveryEndOrderModel);
        return ApiRest.builder().message("不同意配送员取货失败成功！").successful(true).build();
    }

    /**
     * 商家确认收到拒收退回（或取消）的商品
     *
     * @param confirmReceiveGoodsModel
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest confirmReceiveGoods(ConfirmReceiveGoodsModel confirmReceiveGoodsModel) {
        Long tenantId = confirmReceiveGoodsModel.obtainTenantId();
        Long branchId = confirmReceiveGoodsModel.obtainBranchId();
        Long orderId = confirmReceiveGoodsModel.getOrderId();

        DietOrder dietOrder = obtainDietOrder(tenantId, branchId, orderId);

        build.dream.common.models.jddj.ConfirmReceiveGoodsModel jddjConfirmReceiveGoodsModel = build.dream.common.models.jddj.ConfirmReceiveGoodsModel.builder()
                .orderId(Long.valueOf(dietOrder.getOrderNumber().substring(4)))
                .operateTime(new Date())
                .build();

        Map<String, Object> result = JDDJUtils.confirmReceiveGoods(jddjConfirmReceiveGoodsModel);
        return ApiRest.builder().message("确认收到拒收退回（或取消）的商品成功！").successful(true).build();
    }

    /**
     * 取货失败后催配送员抢单
     *
     * @param urgeDispatchingModel
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest urgeDispatching(UrgeDispatchingModel urgeDispatchingModel) {
        Long tenantId = urgeDispatchingModel.obtainTenantId();
        Long branchId = urgeDispatchingModel.obtainBranchId();
        Long orderId = urgeDispatchingModel.getOrderId();

        DietOrder dietOrder = obtainDietOrder(tenantId, branchId, orderId);

        build.dream.common.models.jddj.UrgeDispatchingModel jddjUrgeDispatchingModel = build.dream.common.models.jddj.UrgeDispatchingModel.builder()
                .orderId(Long.valueOf(dietOrder.getOrderNumber().substring(4)))
                .updatePin("")
                .build();
        Map<String, Object> result = JDDJUtils.urgeDispatching(jddjUrgeDispatchingModel);
        return ApiRest.builder().message("催配送员抢单成功！").successful(true).build();
    }

    /**
     * 订单商家加小费
     *
     * @param addTipsModel
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest addTips(AddTipsModel addTipsModel) {
        Long tenantId = addTipsModel.obtainTenantId();
        Long branchId = addTipsModel.obtainBranchId();
        Long orderId = addTipsModel.getOrderId();
        Integer tips = addTipsModel.getTips();

        DietOrder dietOrder = obtainDietOrder(tenantId, branchId, orderId);

        build.dream.common.models.jddj.AddTipsModel jddjAddTipsModel = build.dream.common.models.jddj.AddTipsModel.builder()
                .orderId(Long.valueOf(dietOrder.getOrderNumber().substring(4)))
                .tips(tips)
                .operator("")
                .build();
        Map<String, Object> result = JDDJUtils.addTips(jddjAddTipsModel);
        return ApiRest.builder().message("订单加小费成功！").successful(true).build();
    }

    /**
     * 应结金额接口
     *
     * @param orderShouldSettlementServiceModel
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest orderShouldSettlementService(OrderShouldSettlementServiceModel orderShouldSettlementServiceModel) {
        Long tenantId = orderShouldSettlementServiceModel.obtainTenantId();
        Long branchId = orderShouldSettlementServiceModel.obtainBranchId();
        Long orderId = orderShouldSettlementServiceModel.getOrderId();

        DietOrder dietOrder = obtainDietOrder(tenantId, branchId, orderId);

        OrderShoudSettlementServiceModel orderShoudSettlementServiceModel = OrderShoudSettlementServiceModel.builder()
                .orderId(Long.valueOf(dietOrder.getOrderNumber().substring(4)))
                .build();

        Map<String, Object> result = JDDJUtils.orderShoudSettlementService(orderShoudSettlementServiceModel);
        return ApiRest.builder().message("获取订单应结金额成功！").successful(true).build();
    }

    /**
     * 订单自提码核验
     *
     * @param checkSelfPickCodeModel
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest checkSelfPickCode(CheckSelfPickCodeModel checkSelfPickCodeModel) {
        Long tenantId = checkSelfPickCodeModel.obtainTenantId();
        Long branchId = checkSelfPickCodeModel.obtainBranchId();
        String selfPickCode = checkSelfPickCodeModel.getSelfPickCode();
        Long orderId = checkSelfPickCodeModel.getOrderId();

        DietOrder dietOrder = obtainDietOrder(tenantId, branchId, orderId);

        build.dream.common.models.jddj.CheckSelfPickCodeModel jddjCheckSelfPickCodeModel = build.dream.common.models.jddj.CheckSelfPickCodeModel.builder()
                .selfPickCode(selfPickCode)
                .orderId(dietOrder.getOrderNumber().substring(4))
                .operPin("")
                .build();

        Map<String, Object> result = JDDJUtils.checkSelfPickCode(jddjCheckSelfPickCodeModel);
        return ApiRest.builder().message("自提码核验成功！").successful(true).build();
    }
}
