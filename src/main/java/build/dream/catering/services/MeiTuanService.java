package build.dream.catering.services;

import build.dream.catering.constants.Constants;
import build.dream.catering.models.meituan.*;
import build.dream.catering.utils.MeiTuanUtils;
import build.dream.common.api.ApiRest;
import build.dream.common.constants.DietOrderConstants;
import build.dream.common.domains.catering.*;
import build.dream.common.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;

@Service
public class MeiTuanService {
    /**
     * 生成门店绑定链接
     *
     * @param generateBindingStoreLinkModel
     * @return
     * @throws IOException
     */
    @Transactional(readOnly = true)
    public ApiRest generateBindingStoreLink(GenerateBindingStoreLinkModel generateBindingStoreLinkModel) {
        Long tenantId = generateBindingStoreLinkModel.obtainTenantId();
        Long branchId = generateBindingStoreLinkModel.obtainBranchId();
        String businessId = generateBindingStoreLinkModel.getBusinessId();

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition(Branch.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        searchModel.addSearchCondition(Branch.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        Branch branch = DatabaseHelper.find(Branch.class, searchModel);
        ValidateUtils.notNull(branch, "门店不存在！");

        String meiTuanErpServiceUrl = ConfigurationUtils.getConfiguration(Constants.MEI_TUAN_ERP_SERVICE_URL);
        String developerId = ConfigurationUtils.getConfiguration(Constants.MEI_TUAN_DEVELOPER_ID);
        String signKey = ConfigurationUtils.getConfiguration(Constants.MEI_TUAN_SIGN_KEY);

        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put("developerId", developerId);
        requestParameters.put("businessId", businessId);
        requestParameters.put("ePoiId", tenantId + "Z" + branchId);
        requestParameters.put("ePoiName", branch.getName());
        requestParameters.put("timestamp", String.valueOf(System.currentTimeMillis()));
        requestParameters.put("sign", MeiTuanUtils.generateSignature(signKey, requestParameters));

        String bindingStoreLink = meiTuanErpServiceUrl + "/storemap?" + WebUtils.buildQueryString(requestParameters);
        return ApiRest.builder().data(bindingStoreLink).message("生成门店绑定链接成功！").successful(true).build();
    }

    /**
     * 处理订单生效回调
     *
     * @param callbackParameters
     * @return
     * @throws IOException
     */
    @Transactional(rollbackFor = Exception.class)
    public void handleOrderEffectiveCallback(Map<String, String> callbackParameters, String uuid, Integer type) {
        String ePoiId = callbackParameters.get("ePoiId");
        String order = callbackParameters.get("order");
        Map<String, Object> orderMap = JacksonUtils.readValueAsMap(order, String.class, Object.class);

        String[] tenantIdAndBranchIdArray = ePoiId.split("Z");
        Long tenantId = NumberUtils.createLong(tenantIdAndBranchIdArray[0]);
        Long branchId = NumberUtils.createLong(tenantIdAndBranchIdArray[1]);
        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition(Branch.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        searchModel.addSearchCondition(Branch.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        Branch branch = DatabaseHelper.find(Branch.class, searchModel);
        ValidateUtils.notNull(branch, "门店不存在！");

        String tenantCode = branch.getTenantCode();

        int status = MapUtils.getIntValue(orderMap, "status");
        int orderStatus = Constants.INT_DEFAULT_VALUE;
        if (status == 1) {
            orderStatus = DietOrderConstants.ORDER_STATUS_PENDING;
        } else if (status == 2) {
            orderStatus = DietOrderConstants.ORDER_STATUS_UNPROCESSED;
        } else if (status == 4) {
            orderStatus = DietOrderConstants.ORDER_STATUS_VALID;
        } else if (status == 8) {
            orderStatus = DietOrderConstants.ORDER_STATUS_SETTLED;
        } else if (status == 9) {
            orderStatus = DietOrderConstants.ORDER_STATUS_INVALID;
        }

        Long userId = CommonUtils.getServiceSystemUserId();

        Double totalAmount =MapUtils.getDoubleValue(orderMap, "originalPrice");
        Double discountAmount = Constants.DECIMAL_DEFAULT_VALUE;

        String extras = MapUtils.getString(orderMap, "extras");
        List<DietOrderActivity> dietOrderActivities = new ArrayList<DietOrderActivity>();
        if (StringUtils.isNotBlank(extras)) {
            List<Map> extraList = JacksonUtils.readValueAsList(extras, Map.class);
            for (Map extraMap : extraList) {
                if (MapUtils.isEmpty(extraMap)) {
                    continue;
                }
                Double poiCharge = MapUtils.getDoubleValue(extraMap, "poi_charge");
                if (poiCharge > 0) {
                    discountAmount = discountAmount + poiCharge;
                    DietOrderActivity dietOrderActivity = DietOrderActivity.builder()
                            .tenantId(tenantId)
                            .tenantCode(tenantCode)
                            .branchId(branchId)
                            .activityId(Constants.BIGINT_DEFAULT_VALUE)
                            .activityName(MapUtils.getString(extraMap, "remark"))
                            .activityType(MapUtils.getIntValue(extraMap, "type"))
                            .amount(poiCharge)
                            .createdUserId(userId)
                            .updatedUserId(userId)
                            .build();
                    dietOrderActivities.add(dietOrderActivity);
                }
            }
        }

        Double payableAmount = totalAmount - discountAmount;
        Double paidAmount = Constants.DECIMAL_DEFAULT_VALUE;

        int payType = MapUtils.getIntValue(orderMap, "payType");
        int payStatus = Constants.INT_DEFAULT_VALUE;
        int paidType = Constants.INT_DEFAULT_VALUE;
        if (payType == 1) {
            payStatus = DietOrderConstants.PAY_STATUS_UNPAID;
        } else if (payType == 2) {
            payStatus = DietOrderConstants.PAY_STATUS_PAID;
            paidType = Constants.PAID_TYPE_MT;
            paidAmount = payableAmount;
        }

        int refundStatus = DietOrderConstants.REFUND_STATUS_NO_REFUND;

        String caution = MapUtils.getString(orderMap, "caution");

        long deliveryTime = MapUtils.getLongValue(orderMap, "deliveryTime");
        Calendar deliveryTimeCalendar = Calendar.getInstance();
        deliveryTimeCalendar.setTimeInMillis(deliveryTime * 1000);

        Date activeTime = null;
        if (orderMap.containsKey("orderSendTime")) {
            long orderSendTime = MapUtils.getLongValue(orderMap, "orderSendTime");
            Calendar orderSendTimeCalendar = Calendar.getInstance();
            orderSendTimeCalendar.setTimeInMillis(orderSendTime * 1000);
            activeTime = orderSendTimeCalendar.getTime();
        } else {
            long ctime = MapUtils.getLongValue(orderMap, "ctime");
            Calendar ctimeCalendar = Calendar.getInstance();
            ctimeCalendar.setTimeInMillis(ctime * 1000);
            activeTime = ctimeCalendar.getTime();
        }

        Double shippingFee = MapUtils.getDoubleValue(orderMap, "shippingFee");

        int hasInvoiced = MapUtils.getIntValue(orderMap, "hasInvoiced");
        boolean invoiced = false;
        String invoiceType = Constants.VARCHAR_DEFAULT_VALUE;
        String invoice = Constants.VARCHAR_DEFAULT_VALUE;
        if (hasInvoiced == 0) {
            invoiced = false;
        } else {
            invoiced = true;
            invoice = MapUtils.getString(orderMap, "invoiceTitle");
        }

        DietOrder dietOrder = DietOrder.builder()
                .tenantId(tenantId)
                .tenantCode(tenantCode)
                .branchId(branchId)
                .orderNumber("M" + MapUtils.getLong(orderMap, "orderId"))
                .orderType(DietOrderConstants.ORDER_TYPE_MEI_TUAN_ORDER)
                .orderStatus(orderStatus)
                .payStatus(payStatus)
                .refundStatus(refundStatus)
                .totalAmount(totalAmount)
                .discountAmount(discountAmount)
                .payableAmount(payableAmount)
                .paidAmount(paidAmount)
                .paidType(paidType)
                .remark(StringUtils.isNotBlank(caution) ? caution : Constants.VARCHAR_DEFAULT_VALUE)
                .deliveryAddress(MapUtils.getString(orderMap, "recipientAddress"))
                .deliveryLongitude(MapUtils.getString(orderMap, "longitude"))
                .deliveryLatitude(MapUtils.getString(orderMap, "latitude"))
                .deliverTime(deliveryTimeCalendar.getTime())
                .activeTime(activeTime)
                .deliverFee(shippingFee)
                .telephoneNumber(MapUtils.getString(orderMap, "recipientPhone"))
                .daySerialNumber(MapUtils.getString(orderMap, "daySeq"))
                .consignee(MapUtils.getString(orderMap, "recipientName"))
                .invoiced(invoiced)
                .invoiceType(invoiceType)
                .invoice(invoice)
                .vipId(Constants.BIGINT_DEFAULT_VALUE)
                .createdUserId(userId)
                .updatedUserId(userId)
                .updatedRemark("接收美团订单生效回调，保存订单信息！")
                .build();
        DatabaseHelper.insert(dietOrder);
        Long dietOrderId = dietOrder.getId();

        if (payStatus == DietOrderConstants.PAY_STATUS_PAID) {
            DietOrderPayment dietOrderPayment = DietOrderPayment.builder()
                    .tenantId(tenantId)
                    .tenantCode(tenantCode)
                    .branchId(branchId)
                    .dietOrderId(dietOrderId)
                    .paymentId(Constants.MT_PAYMENT_ID)
                    .paymentCode(Constants.MT_PAYMENT_CODE)
                    .paymentName(Constants.MT_PAYMENT_NAME)
                    .occurrenceTime(activeTime)
                    .createdUserId(userId)
                    .updatedUserId(userId)
                    .paidAmount(paidAmount)
                    .build();
            DatabaseHelper.insert(dietOrderPayment);
        }

        if (CollectionUtils.isNotEmpty(dietOrderActivities)) {
            for (DietOrderActivity dietOrderActivity : dietOrderActivities) {
                dietOrderActivity.setDietOrderId(dietOrderId);
            }
            DatabaseHelper.insertAll(dietOrderActivities);
        }

        String detail = MapUtils.getString(orderMap, "detail");
        List<Map> detailList = JacksonUtils.readValueAsList(detail, Map.class);

        Map<Integer, DietOrderGroup> dietOrderGroupMap = new HashMap<Integer, DietOrderGroup>();
        Double packageFee = 0D;
        Double boxQuantity = 0D;

        for (Map detailMap : detailList) {
            int cartId = MapUtils.getIntValue(detailMap, "cart_id");
            DietOrderGroup dietOrderGroup = dietOrderGroupMap.get(cartId);
            if (dietOrderGroup == null) {
                dietOrderGroup = DietOrderGroup.builder()
                        .tenantId(tenantId)
                        .tenantCode(tenantCode)
                        .branchId(branchId)
                        .dietOrderId(dietOrderId)
                        .name(cartId + 1 + "号口袋")
                        .type(DietOrderConstants.GROUP_TYPE_NORMAL)
                        .createdUserId(userId)
                        .updatedUserId(userId)
                        .build();
                DatabaseHelper.insert(dietOrderGroup);
                dietOrderGroupMap.put(cartId, dietOrderGroup);
            }
            Double boxNum = MapUtils.getDoubleValue(detailMap, "box_num");
            Double boxPrice = MapUtils.getDoubleValue(detailMap, "box_price");
            packageFee = packageFee + (boxNum * boxPrice);
            boxQuantity = boxQuantity + boxNum;

            Double price = MapUtils.getDoubleValue(detailMap, "price");
            Double quantity = MapUtils.getDoubleValue(detailMap, "quantity");
            Double dietOrderDetailTotalAmount = price * quantity;
            DietOrderDetail dietOrderDetail = DietOrderDetail.builder()
                    .tenantId(tenantId)
                    .tenantCode(tenantCode)
                    .branchId(branchId)
                    .dietOrderId(dietOrderId)
                    .dietOrderGroupId(dietOrderGroup.getId())
                    .goodsType(Constants.GOODS_TYPE_ORDINARY_GOODS)
                    .goodsId(Constants.BIGINT_DEFAULT_VALUE)
                    .goodsName(MapUtils.getString(detailMap, "food_name"))
                    .goodsSpecificationId(Constants.BIGINT_DEFAULT_VALUE)
                    .goodsSpecificationName(Constants.VARCHAR_DEFAULT_VALUE)
                    .categoryId(Constants.MEI_TUAN_GOODS_CATEGORY_ID)
                    .categoryName(Constants.MEI_TUAN_GOODS_CATEGORY_NAME)
                    .price(price)
                    .attributeIncrease(Constants.DECIMAL_DEFAULT_VALUE)
                    .quantity(quantity)
                    .totalAmount(dietOrderDetailTotalAmount)
                    .discountAmount(0D)
                    .payableAmount(dietOrderDetailTotalAmount)
                    .createdUserId(userId)
                    .updatedUserId(userId)
                    .build();
            DatabaseHelper.insert(dietOrderDetail);

            String foodProperty = MapUtils.getString(detailMap, "food_property");
            if (StringUtils.isNotBlank(foodProperty)) {
                String[] foodProperties = foodProperty.split(",");
                List<DietOrderDetailGoodsAttribute> dietOrderDetailGoodsAttributes = new ArrayList<DietOrderDetailGoodsAttribute>();
                for (String property : foodProperties) {
                    DietOrderDetailGoodsAttribute dietOrderDetailGoodsAttribute = DietOrderDetailGoodsAttribute.builder()
                            .tenantId(tenantId)
                            .tenantCode(tenantCode)
                            .branchId(branchId)
                            .dietOrderId(dietOrderId)
                            .dietOrderGroupId(dietOrderGroup.getId())
                            .dietOrderDetailId(dietOrderDetail.getId())
                            .goodsAttributeGroupId(Constants.BIGINT_DEFAULT_VALUE)
                            .goodsAttributeName(Constants.VARCHAR_DEFAULT_VALUE)
                            .goodsAttributeId(Constants.BIGINT_DEFAULT_VALUE)
                            .goodsAttributeName(property)
                            .price(Constants.DECIMAL_DEFAULT_VALUE)
                            .createdUserId(userId)
                            .updatedUserId(userId)
                            .build();
                    dietOrderDetailGoodsAttributes.add(dietOrderDetailGoodsAttribute);
                }
                DatabaseHelper.insertAll(dietOrderDetailGoodsAttributes);
            }
        }
        if (packageFee > 0 || shippingFee > 0) {
            DietOrderGroup dietOrderGroup = DietOrderGroup.builder()
                    .tenantId(tenantId)
                    .tenantCode(tenantCode)
                    .branchId(branchId)
                    .dietOrderId(dietOrderId)
                    .name("其他费用")
                    .type(DietOrderConstants.GROUP_TYPE_EXTRA)
                    .createdUserId(userId)
                    .updatedUserId(userId)
                    .build();
            DatabaseHelper.insert(dietOrderGroup);
            if (packageFee > 0) {
                DietOrderDetail dietOrderDetail = DietOrderDetail.builder()
                        .tenantId(tenantId)
                        .tenantCode(tenantCode)
                        .branchId(branchId)
                        .dietOrderId(dietOrderId)
                        .dietOrderGroupId(dietOrderGroup.getId())
                        .goodsType(Constants.GOODS_TYPE_PACKAGE_FEE)
                        .goodsId(-1L)
                        .goodsName("餐盒")
                        .goodsSpecificationId(-2L)
                        .goodsSpecificationName(Constants.VARCHAR_DEFAULT_VALUE)
                        .categoryId(Constants.FICTITIOUS_GOODS_CATEGORY_ID)
                        .categoryName(Constants.FICTITIOUS_GOODS_CATEGORY_NAME)
                        .price(Constants.DECIMAL_DEFAULT_VALUE)
                        .attributeIncrease(Constants.DECIMAL_DEFAULT_VALUE)
                        .quantity(boxQuantity)
                        .totalAmount(packageFee)
                        .discountAmount(0D)
                        .payableAmount(packageFee)
                        .createdUserId(userId)
                        .updatedUserId(userId)
                        .build();
                DatabaseHelper.insert(dietOrderDetail);
            }
            if (shippingFee > 0) {
                DietOrderDetail dietOrderDetail = DietOrderDetail.builder()
                        .tenantId(tenantId)
                        .tenantCode(tenantCode)
                        .branchId(branchId)
                        .dietOrderId(dietOrderId)
                        .dietOrderGroupId(dietOrderGroup.getId())
                        .goodsType(Constants.GOODS_TYPE_DELIVER_FEE)
                        .goodsId(-1L)
                        .goodsName("配送费")
                        .goodsSpecificationId(-2L)
                        .goodsSpecificationName(Constants.VARCHAR_DEFAULT_VALUE)
                        .categoryId(Constants.FICTITIOUS_GOODS_CATEGORY_ID)
                        .categoryName(Constants.FICTITIOUS_GOODS_CATEGORY_NAME)
                        .price(shippingFee)
                        .attributeIncrease(Constants.DECIMAL_DEFAULT_VALUE)
                        .quantity(1D)
                        .totalAmount(shippingFee)
                        .discountAmount(0D)
                        .payableAmount(shippingFee)
                        .createdUserId(userId)
                        .updatedUserId(userId)
                        .build();
                DatabaseHelper.insert(dietOrderDetail);
            }
        }
    }

    /**
     * 处理订单取消回调
     *
     * @param callbackParameters
     * @param uuid
     * @param type
     */
    @Transactional(rollbackFor = Exception.class)
    public void handleOrderCancelCallback(Map<String, String> callbackParameters, String uuid, int type) {
        String developerId = callbackParameters.get("developerId");
        String ePoiId = callbackParameters.get("ePoiId");
        String sign = callbackParameters.get("sign");
        String orderCancel = callbackParameters.get("orderCancel");
        Map<String, Object> orderCancelMap = JacksonUtils.readValueAsMap(orderCancel, String.class, Object.class);
        Long orderId = Long.valueOf(MapUtils.getLongValue(orderCancelMap, "orderId"));

        String[] tenantIdAndBranchIdArray = ePoiId.split("Z");
        Long tenantId = NumberUtils.createLong(tenantIdAndBranchIdArray[0]);
        Long branchId = NumberUtils.createLong(tenantIdAndBranchIdArray[1]);

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition(DietOrder.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        searchModel.addSearchCondition(DietOrder.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        searchModel.addSearchCondition(DietOrder.ColumnName.ORDER_NUMBER, Constants.SQL_OPERATION_SYMBOL_EQUAL, "M" + orderId);
        DietOrder dietOrder = DatabaseHelper.find(DietOrder.class, searchModel);
        ValidateUtils.notNull(dietOrder, "订单不存在！");

        Long userId = CommonUtils.getServiceSystemUserId();

        dietOrder.setOrderStatus(DietOrderConstants.ORDER_STATUS_INVALID);
        dietOrder.setUpdatedUserId(userId);
        dietOrder.setUpdatedRemark("取消订单！");
        DatabaseHelper.update(dietOrder);
    }

    /**
     * 处理订单退款回调
     *
     * @param callbackParameters
     * @param uuid
     * @param type
     */
    @Transactional(rollbackFor = Exception.class)
    public void handleOrderRefundCallback(Map<String, String> callbackParameters, String uuid, int type) {
        String developerId = callbackParameters.get("developerId");
        String ePoiId = callbackParameters.get("ePoiId");
        String sign = callbackParameters.get("sign");
        String orderRefund = callbackParameters.get("orderRefund");
        Map<String, Object> orderRefundMap = JacksonUtils.readValueAsMap(orderRefund, String.class, Object.class);
        Long orderId = Long.valueOf(MapUtils.getLongValue(orderRefundMap, "orderId"));

        String[] tenantIdAndBranchIdArray = ePoiId.split("Z");
        Long tenantId = NumberUtils.createLong(tenantIdAndBranchIdArray[0]);
        Long branchId = NumberUtils.createLong(tenantIdAndBranchIdArray[1]);

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition(DietOrder.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        searchModel.addSearchCondition(DietOrder.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        searchModel.addSearchCondition(DietOrder.ColumnName.ORDER_NUMBER, Constants.SQL_OPERATION_SYMBOL_EQUAL, "M" + orderId);
        DietOrder dietOrder = DatabaseHelper.find(DietOrder.class, searchModel);
        ValidateUtils.notNull(dietOrder, "订单不存在！");

        Long userId = CommonUtils.getServiceSystemUserId();

        Long dietOrderId = dietOrder.getId();

        String notifyType = MapUtils.getString(orderRefundMap, "notifyType");
        int refundStatus = Constants.INT_DEFAULT_VALUE;
        if (DietOrderConstants.APPLY.equals(notifyType)) {
            refundStatus = DietOrderConstants.REFUND_STATUS_APPLIED;
        } else if (DietOrderConstants.AGREE.equals(notifyType)) {
            refundStatus = DietOrderConstants.REFUND_STATUS_SUCCESSFUL;
        } else if (DietOrderConstants.REJECT.equals(notifyType)) {
            refundStatus = DietOrderConstants.REFUND_STATUS_REJECTED;
        } else if (DietOrderConstants.CANCEL_REFUND.equals(notifyType)) {
            refundStatus = DietOrderConstants.REFUND_STATUS_NO_REFUND;
        } else if (DietOrderConstants.CANCEL_REFUND_COMPLAINT.equals(notifyType)) {
            refundStatus = DietOrderConstants.REFUND_STATUS_NO_REFUND;
        }

        dietOrder.setRefundStatus(refundStatus);
        dietOrder.setUpdatedUserId(userId);
        DatabaseHelper.update(dietOrder);
    }

    /**
     * 处理门店绑定回调
     *
     * @param callbackParameters
     * @param uuid
     * @param type
     */
    @Transactional(rollbackFor = Exception.class)
    public void handleBindingStoreCallback(Map<String, String> callbackParameters, String uuid, int type) {
        String ePoiId = callbackParameters.get("ePoiId");
        String appAuthToken = callbackParameters.get("appAuthToken");
        String poiId = callbackParameters.get("poiId");
        String poiName = callbackParameters.get("poiName");

        String[] tenantIdAndBranchIdArray = ePoiId.split("Z");
        Long tenantId = NumberUtils.createLong(tenantIdAndBranchIdArray[0]);
        Long branchId = NumberUtils.createLong(tenantIdAndBranchIdArray[1]);

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition(Branch.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        searchModel.addSearchCondition(Branch.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        Branch branch = DatabaseHelper.find(Branch.class, searchModel);
        ValidateUtils.notNull(branch, "门店不存在！");

        Long userId = CommonUtils.getServiceSystemUserId();
        branch.setAppAuthToken(appAuthToken);
        branch.setPoiId(poiId);
        branch.setPoiName(poiName);
        branch.setUpdatedUserId(userId);
        branch.setUpdatedRemark("处理美团店铺绑定回调，保存appAuthToken、poiId、poiName");
        DatabaseHelper.update(branch);
    }

    /**
     * 查询门店是否绑定美团
     *
     * @param checkIsBindingModel
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest checkIsBinding(CheckIsBindingModel checkIsBindingModel) {
        Long tenantId = checkIsBindingModel.obtainTenantId();
        Long branchId = checkIsBindingModel.obtainBranchId();

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition(Branch.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        searchModel.addSearchCondition(Branch.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        Branch branch = DatabaseHelper.find(Branch.class, searchModel);
        ValidateUtils.notNull(branch, "门店不存在！");

        boolean isBinding = StringUtils.isNotBlank(branch.getAppAuthToken()) && StringUtils.isNotBlank(branch.getPoiId()) && StringUtils.isNotBlank(branch.getPoiName());
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("isBinding", isBinding);

        return ApiRest.builder().data(data).message("查询门店是否绑定美团成功！").successful(true).build();
    }

    @Transactional(readOnly = true)
    public ApiRest queryPoiInfo(QueryPoiInfoModel queryPoiInfoModel) throws IOException {
        Long tenantId = queryPoiInfoModel.obtainTenantId();
        Long branchId = queryPoiInfoModel.obtainBranchId();
        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition(Branch.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        searchModel.addSearchCondition(Branch.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        Branch branch = DatabaseHelper.find(Branch.class, searchModel);
        ValidateUtils.notNull(branch, "门店不存在！");

        String charset = Constants.CHARSET_NAME_UTF_8;
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);

        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put("appAuthToken", branch.getAppAuthToken());
        requestParameters.put("charset", charset);
        requestParameters.put("timestamp", timestamp);
        requestParameters.put("ePoiIds", branch.getTenantId() + "Z" + branch.getId());
        String sign = MeiTuanUtils.generateSignature("01b7d2lgmdylgiee", requestParameters);
        requestParameters.put("sign", sign);
        String url = "http://api.open.cater.meituan.com/waimai/poi/queryPoiInfo";
        String result = OutUtils.doGet(url, requestParameters);
        System.out.println(result);
        return new ApiRest();
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
        Long dietOrderId = confirmOrderModel.getDietOrderId();

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition(DietOrder.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        searchModel.addSearchCondition(DietOrder.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        searchModel.addSearchCondition(DietOrder.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, dietOrderId);
        DietOrder dietOrder = DatabaseHelper.find(DietOrder.class, searchModel);
        ValidateUtils.notNull(dietOrder, "订单不存在！");

        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put("orderId", dietOrder.getOrderNumber().substring(1));

        String url = "https://api-open-cater.meituan.com/waimai/order/confirm";
        Map<String, Object> result = MeiTuanUtils.callMeiTuanSystem(tenantId.toString(), branchId.toString(), requestParameters, url, Constants.REQUEST_METHOD_POST);
        String data = MapUtils.getString(result, "data");
        ValidateUtils.isTrue(Constants.OK.equals(data), "确认订单失败！");
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
        Long dietOrderId = cancelOrderModel.getDietOrderId();
        String reasonCode = cancelOrderModel.getReasonCode();
        String reason = cancelOrderModel.getReason();

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition(DietOrder.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        searchModel.addSearchCondition(DietOrder.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        searchModel.addSearchCondition(DietOrder.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, dietOrderId);
        DietOrder dietOrder = DatabaseHelper.find(DietOrder.class, searchModel);
        ValidateUtils.notNull(dietOrder, "订单不存在！");

        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put("orderId", dietOrder.getOrderNumber().substring(1));
        requestParameters.put("reasonCode", reasonCode);
        requestParameters.put("reason", reason);

        String url = "https://api-open-cater.meituan.com/waimai/order/cancel";
        Map<String, Object> result = MeiTuanUtils.callMeiTuanSystem(tenantId.toString(), branchId.toString(), requestParameters, url, Constants.REQUEST_METHOD_POST);
        String data = MapUtils.getString(result, "data");
        ValidateUtils.isTrue(Constants.OK.equals(data), "取消订单失败！");
        return ApiRest.builder().message("取消订单成功！").successful(true).build();
    }

    /**
     * 自配送－配送状态
     *
     * @param deliveringOrderModel
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest deliveringOrder(DeliveringOrderModel deliveringOrderModel) {
        Long tenantId = deliveringOrderModel.obtainTenantId();
        Long branchId = deliveringOrderModel.obtainBranchId();
        Long dietOrderId = deliveringOrderModel.getDietOrderId();
        String courierName = deliveringOrderModel.getCourierName();
        String courierPhone = deliveringOrderModel.getCourierPhone();

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition(DietOrder.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        searchModel.addSearchCondition(DietOrder.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        searchModel.addSearchCondition(DietOrder.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, dietOrderId);
        DietOrder dietOrder = DatabaseHelper.find(DietOrder.class, searchModel);
        ValidateUtils.notNull(dietOrder, "订单不存在！");

        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put("orderId", dietOrder.getOrderNumber().substring(1));

        if (StringUtils.isNotBlank(courierName)) {
            requestParameters.put("courierName", courierName);
        }

        if (StringUtils.isNotBlank(courierPhone)) {
            requestParameters.put("courierPhone", courierPhone);
        }

        String url = "https://api-open-cater.meituan.com/waimai/order/delivering";
        Map<String, Object> result = MeiTuanUtils.callMeiTuanSystem(tenantId.toString(), branchId.toString(), requestParameters, url, Constants.REQUEST_METHOD_POST);
        String data = MapUtils.getString(result, "data");
        ValidateUtils.isTrue(Constants.OK.equals(data), "设置配送状态失败！");
        return ApiRest.builder().message("设置配送状态成功！").successful(true).build();
    }

    /**
     * 自配送场景－订单已送达
     *
     * @param deliveredOrderModel
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest deliveredOrder(DeliveredOrderModel deliveredOrderModel) {
        Long tenantId = deliveredOrderModel.obtainTenantId();
        Long branchId = deliveredOrderModel.obtainBranchId();
        Long dietOrderId = deliveredOrderModel.getDietOrderId();

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition(DietOrder.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        searchModel.addSearchCondition(DietOrder.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        searchModel.addSearchCondition(DietOrder.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, dietOrderId);
        DietOrder dietOrder = DatabaseHelper.find(DietOrder.class, searchModel);
        ValidateUtils.notNull(dietOrder, "订单不存在！");

        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put("orderId", dietOrder.getOrderNumber().substring(1));

        String url = "https://api-open-cater.meituan.com/waimai/order/delivered";
        Map<String, Object> result = MeiTuanUtils.callMeiTuanSystem(tenantId.toString(), branchId.toString(), requestParameters, url, Constants.REQUEST_METHOD_POST);
        String data = MapUtils.getString(result, "data");
        ValidateUtils.isTrue(Constants.OK.equals(data), "设置订单已送达状态失败！");
        return ApiRest.builder().message("设置订单已送达状态成功！").successful(true).build();
    }

    /**
     * 处理订单确认回调
     *
     * @param callbackParameters
     * @param uuid
     * @param type
     */
    public void handleOrderConfirmCallback(Map<String, String> callbackParameters, String uuid, int type) {
        DietOrder dietOrder = MeiTuanUtils.obtainDietOrder(callbackParameters);
        if (dietOrder == null) {
            return;
        }

        dietOrder.setOrderStatus(DietOrderConstants.ORDER_STATUS_VALID);
        dietOrder.setUpdatedUserId(CommonUtils.getServiceSystemUserId());
        dietOrder.setUpdatedRemark("接收美团订单已确认消息。");
        DatabaseHelper.update(dietOrder);
    }

    /**
     * 处理订单完成回调
     *
     * @param callbackParameters
     * @param uuid
     * @param type
     */
    public void handleOrderSettledCallback(Map<String, String> callbackParameters, String uuid, int type) {
        DietOrder dietOrder = MeiTuanUtils.obtainDietOrder(callbackParameters);
        if (dietOrder == null) {
            return;
        }

        dietOrder.setOrderStatus(DietOrderConstants.ORDER_STATUS_SETTLED);
        dietOrder.setUpdatedUserId(CommonUtils.getServiceSystemUserId());
        dietOrder.setUpdatedRemark("接收美团订单完成消息。");
        DatabaseHelper.update(dietOrder);
    }

    /**
     * 处理订单配送状态改变回调
     *
     * @param callbackParameters
     * @param uuid
     * @param type
     */
    public void handleOrderShippingStatusCallback(Map<String, String> callbackParameters, String uuid, int type) {
        DietOrder dietOrder = MeiTuanUtils.obtainDietOrder(callbackParameters);
        if (dietOrder == null) {
            return;
        }

        Map<String, Object> shippingStatusMap = MapUtils.getMap(callbackParameters, "shippingStatus");
        int shippingStatus = MapUtils.getIntValue(shippingStatusMap, "shippingStatus");
        String dispatcherName = MapUtils.getString(shippingStatusMap, "dispatcherName");
        String dispatcherMobile = MapUtils.getString(shippingStatusMap, "dispatcherMobile");

        DietOrderDeliveryRecord dietOrderDeliveryRecord = DietOrderDeliveryRecord.builder()
                .tenantId(dietOrder.getTenantId())
                .tenantCode(dietOrder.getTenantCode())
                .branchId(dietOrder.getBranchId())
                .dietOrderId(dietOrder.getId())
                .elemeState(Constants.VARCHAR_DEFAULT_VALUE)
                .elemeSubState(Constants.VARCHAR_DEFAULT_VALUE)
                .meiTuanShippingStatus(shippingStatus)
                .deliverName(StringUtils.isBlank(dispatcherName) ? Constants.VARCHAR_DEFAULT_VALUE : dispatcherName)
                .deliverPhone(StringUtils.isBlank(dispatcherMobile) ? Constants.VARCHAR_DEFAULT_VALUE : dispatcherMobile)
                .build();
        DatabaseHelper.insert(dietOrderDeliveryRecord);
    }

    /**
     * 处理门店状态回调
     *
     * @param callbackParameters
     * @param uuid
     * @param type
     */
    public void handlePoiStatusCallback(Map<String, String> callbackParameters, String uuid, int type) {
        Branch branch = MeiTuanUtils.obtainBranch(callbackParameters);
        if (branch == null) {
            return;
        }

        Map<String, Object> poiStatusMap = MapUtils.getMap(callbackParameters, "poiStatus");
        int poiStatus = MapUtils.getIntValue(poiStatusMap, "poiStatus");
        if (poiStatus == 121) {

        } else if (poiStatus == 120) {

        } else if (poiStatus == 18) {

        } else if (poiStatus == 19) {

        }
    }

    /**
     * 处理部分退款回调
     *
     * @param callbackParameters
     * @param uuid
     * @param type
     */
    public void handlePartOrderRefundCallback(Map<String, String> callbackParameters, String uuid, int type) {

    }
}
