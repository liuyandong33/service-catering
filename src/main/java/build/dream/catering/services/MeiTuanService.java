package build.dream.catering.services;

import build.dream.catering.constants.Constants;
import build.dream.catering.models.meituan.*;
import build.dream.catering.tools.PushMessageThread;
import build.dream.catering.utils.MeiTuanUtils;
import build.dream.common.api.ApiRest;
import build.dream.common.beans.WebResponse;
import build.dream.common.catering.domains.*;
import build.dream.common.constants.DietOrderConstants;
import build.dream.common.models.jpush.PushModel;
import build.dream.common.utils.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
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
        BigInteger tenantId = generateBindingStoreLinkModel.obtainTenantId();
        BigInteger branchId = generateBindingStoreLinkModel.obtainBranchId();

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition(Branch.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        searchModel.addSearchCondition(Branch.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        Branch branch = DatabaseHelper.find(Branch.class, searchModel);
        ValidateUtils.notNull(branch, "门店不存在！");
        String meiTuanErpServiceUrl = ConfigurationUtils.getConfiguration(Constants.MEI_TUAN_ERP_SERVICE_URL);
        String meiTuanDeveloperId = ConfigurationUtils.getConfiguration(Constants.MEI_TUAN_DEVELOPER_ID);
        String meiTuanSignKey = ConfigurationUtils.getConfiguration(Constants.MEI_TUAN_SIGN_KEY);
        StringBuffer bindingStoreLink = new StringBuffer(meiTuanErpServiceUrl);
        bindingStoreLink.append(Constants.MEI_TUAN_STORE_MAP_URI);
        bindingStoreLink.append("?developerId=").append(meiTuanDeveloperId);
        bindingStoreLink.append("&businessId=").append(generateBindingStoreLinkModel.getBusinessId());
        bindingStoreLink.append("&ePoiId=").append(tenantId).append("Z").append(branchId);
        bindingStoreLink.append("&signKey=").append(meiTuanSignKey);
        bindingStoreLink.append("&ePoiName=").append(branch.getName());
        bindingStoreLink.append("&timestamp=").append(System.currentTimeMillis());
        return ApiRest.builder().data(bindingStoreLink.toString()).message("生成门店绑定链接成功！").successful(true).build();
    }

    /**
     * 处理订单生效回调
     *
     * @param callbackParametersJsonObject
     * @return
     * @throws IOException
     */
    @Transactional(rollbackFor = Exception.class)
    public void handleOrderEffectiveCallback(JSONObject callbackParametersJsonObject, String uuid, Integer type) {
//        String ePoiId = callbackParametersJsonObject.getString("ePoiId");
//        JSONObject orderJsonObject = callbackParametersJsonObject.getJSONObject("order");

        String ePoiId = "1Z1074954252843094017";
        JSONObject orderJsonObject = callbackParametersJsonObject;

        String[] tenantIdAndBranchIdArray = ePoiId.split("Z");
        BigInteger tenantId = NumberUtils.createBigInteger(tenantIdAndBranchIdArray[0]);
        BigInteger branchId = NumberUtils.createBigInteger(tenantIdAndBranchIdArray[1]);
        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition(Branch.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        searchModel.addSearchCondition(Branch.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        Branch branch = DatabaseHelper.find(Branch.class, searchModel);
        ValidateUtils.notNull(branch, "门店不存在！");

        String tenantCode = branch.getTenantCode();

        int status = orderJsonObject.getInt("status");
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

        BigInteger userId = CommonUtils.getServiceSystemUserId();

        BigDecimal totalAmount = BigDecimal.valueOf(orderJsonObject.getDouble("originalPrice"));
        BigDecimal discountAmount = Constants.DECIMAL_DEFAULT_VALUE;

        String extras = orderJsonObject.getString("extras");
        List<DietOrderActivity> dietOrderActivities = new ArrayList<DietOrderActivity>();
        if (StringUtils.isNotBlank(extras)) {
            JSONArray extrasJsonArray = JSONArray.fromObject(extras);
            int extrasSize = extrasJsonArray.size();
            for (int index = 0; index < extrasSize; index++) {
                JSONObject extraJsonObject = extrasJsonArray.getJSONObject(index);
                if (extraJsonObject.isEmpty()) {
                    continue;
                }
                BigDecimal poiCharge = BigDecimal.valueOf(extraJsonObject.optDouble("poi_charge", 0));
                if (poiCharge.compareTo(BigDecimal.ZERO) > 0) {
                    discountAmount = discountAmount.add(poiCharge);
                    DietOrderActivity dietOrderActivity = DietOrderActivity.builder()
                            .tenantId(tenantId)
                            .tenantCode(tenantCode)
                            .branchId(branchId)
                            .activityId(Constants.BIGINT_DEFAULT_VALUE)
                            .activityName(extraJsonObject.getString("remark"))
                            .activityType(extraJsonObject.getInt("type"))
                            .amount(poiCharge)
                            .createdUserId(userId)
                            .updatedUserId(userId)
                            .build();
                    dietOrderActivities.add(dietOrderActivity);
                }
            }
        }

        BigDecimal payableAmount = totalAmount.subtract(discountAmount);
        BigDecimal paidAmount = Constants.DECIMAL_DEFAULT_VALUE;

        int payType = orderJsonObject.getInt("payType");
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

        String caution = orderJsonObject.getString("caution");

        long deliveryTime = orderJsonObject.optLong("deliveryTime");
        Calendar deliveryTimeCalendar = Calendar.getInstance();
        deliveryTimeCalendar.setTimeInMillis(deliveryTime * 1000);

        Date activeTime = null;
        if (orderJsonObject.has("orderSendTime")) {
            long orderSendTime = orderJsonObject.getLong("orderSendTime");
            Calendar orderSendTimeCalendar = Calendar.getInstance();
            orderSendTimeCalendar.setTimeInMillis(orderSendTime * 1000);
            activeTime = orderSendTimeCalendar.getTime();
        } else {
            long ctime = orderJsonObject.getLong("ctime");
            Calendar ctimeCalendar = Calendar.getInstance();
            ctimeCalendar.setTimeInMillis(ctime * 1000);
            activeTime = ctimeCalendar.getTime();
        }

        BigDecimal shippingFee = BigDecimal.valueOf(orderJsonObject.getDouble("shippingFee"));

        int hasInvoiced = orderJsonObject.optInt("hasInvoiced");
        boolean invoiced = false;
        String invoiceType = Constants.VARCHAR_DEFAULT_VALUE;
        String invoice = Constants.VARCHAR_DEFAULT_VALUE;
        if (hasInvoiced == 0) {
            invoiced = false;
        } else {
            invoiced = true;
            invoice = orderJsonObject.getString("invoiceTitle");
        }

        DietOrder dietOrder = DietOrder.builder()
                .tenantId(tenantId)
                .tenantCode(tenantCode)
                .branchId(branchId)
                .orderNumber("M" + orderJsonObject.get("orderId"))
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
                .deliveryAddress(orderJsonObject.getString("recipientAddress"))
                .deliveryLongitude(orderJsonObject.getString("longitude"))
                .deliveryLatitude(orderJsonObject.getString("latitude"))
                .deliverTime(deliveryTimeCalendar.getTime())
                .activeTime(activeTime)
                .deliverFee(shippingFee)
                .telephoneNumber(orderJsonObject.getString("recipientPhone"))
                .daySerialNumber(orderJsonObject.getString("daySeq"))
                .consignee(orderJsonObject.getString("recipientName"))
                .invoiced(invoiced)
                .invoiceType(invoiceType)
                .invoice(invoice)
                .vipId(Constants.BIGINT_DEFAULT_VALUE)
                .createdUserId(userId)
                .updatedUserId(userId)
                .updatedRemark("接收美团订单生效回调，保存订单信息！")
                .build();
        DatabaseHelper.insert(dietOrder);
        BigInteger dietOrderId = dietOrder.getId();

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

//        JSONObject poiReceiveDetailJsonObject = orderJsonObject.optJSONObject("poiReceiveDetail");

        String detail = orderJsonObject.getString("detail");
        JSONArray detailJsonArray = JSONArray.fromObject(detail);
        int detailSize = detailJsonArray.size();

        Map<Integer, DietOrderGroup> dietOrderGroupMap = new HashMap<Integer, DietOrderGroup>();
        BigDecimal packageFee = BigDecimal.ZERO;
        BigDecimal boxQuantity = BigDecimal.ZERO;

        for (int index = 0; index < detailSize; index++) {
            JSONObject detailJsonObject = detailJsonArray.getJSONObject(index);
            int cartId = detailJsonObject.getInt("cart_id");
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
            BigDecimal boxNum = BigDecimal.valueOf(detailJsonObject.getDouble("box_num"));
            BigDecimal boxPrice = BigDecimal.valueOf(detailJsonObject.getDouble("box_price"));
            packageFee = packageFee.add(boxNum.multiply(boxPrice));
            boxQuantity = boxQuantity.add(boxNum);

            BigDecimal price = BigDecimal.valueOf(detailJsonObject.getDouble("price"));
            BigDecimal quantity = BigDecimal.valueOf(detailJsonObject.getDouble("quantity"));
            BigDecimal dietOrderDetailTotalAmount = price.multiply(quantity);
            DietOrderDetail dietOrderDetail = DietOrderDetail.builder()
                    .tenantId(tenantId)
                    .tenantCode(tenantCode)
                    .branchId(branchId)
                    .dietOrderId(dietOrderId)
                    .dietOrderGroupId(dietOrderGroup.getId())
                    .goodsType(Constants.GOODS_TYPE_ORDINARY_GOODS)
                    .goodsId(Constants.BIGINT_DEFAULT_VALUE)
                    .goodsName(detailJsonObject.getString("food_name"))
                    .goodsSpecificationId(Constants.BIGINT_DEFAULT_VALUE)
                    .goodsSpecificationName(Constants.VARCHAR_DEFAULT_VALUE)
                    .categoryId(Constants.MEI_TUAN_GOODS_CATEGORY_ID)
                    .categoryName(Constants.MEI_TUAN_GOODS_CATEGORY_NAME)
                    .price(price)
                    .attributeIncrease(Constants.DECIMAL_DEFAULT_VALUE)
                    .quantity(quantity)
                    .totalAmount(dietOrderDetailTotalAmount)
                    .discountAmount(BigDecimal.ZERO)
                    .payableAmount(dietOrderDetailTotalAmount)
                    .createdUserId(userId)
                    .updatedUserId(userId)
                    .build();
            DatabaseHelper.insert(dietOrderDetail);

            String foodProperty = detailJsonObject.getString("food_property");
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
        if (packageFee.compareTo(BigDecimal.ZERO) > 0 || shippingFee.compareTo(BigDecimal.ZERO) > 0) {
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
            if (packageFee.compareTo(BigDecimal.ZERO) > 0) {
                DietOrderDetail dietOrderDetail = DietOrderDetail.builder()
                        .tenantId(tenantId)
                        .tenantCode(tenantCode)
                        .branchId(branchId)
                        .dietOrderId(dietOrderId)
                        .dietOrderGroupId(dietOrderGroup.getId())
                        .goodsType(Constants.GOODS_TYPE_PACKAGE_FEE)
                        .goodsId(Constants.BIG_INTEGER_MINUS_TWO)
                        .goodsName("餐盒")
                        .goodsSpecificationId(Constants.BIG_INTEGER_MINUS_TWO)
                        .goodsSpecificationName(Constants.VARCHAR_DEFAULT_VALUE)
                        .categoryId(Constants.FICTITIOUS_GOODS_CATEGORY_ID)
                        .categoryName(Constants.FICTITIOUS_GOODS_CATEGORY_NAME)
                        .price(Constants.DECIMAL_DEFAULT_VALUE)
                        .attributeIncrease(Constants.DECIMAL_DEFAULT_VALUE)
                        .quantity(boxQuantity)
                        .totalAmount(packageFee)
                        .discountAmount(BigDecimal.ZERO)
                        .payableAmount(packageFee)
                        .createdUserId(userId)
                        .updatedUserId(userId)
                        .build();
                DatabaseHelper.insert(dietOrderDetail);
            }
            if (shippingFee.compareTo(BigDecimal.ZERO) > 0) {
                DietOrderDetail dietOrderDetail = DietOrderDetail.builder()
                        .tenantId(tenantId)
                        .tenantCode(tenantCode)
                        .branchId(branchId)
                        .dietOrderId(dietOrderId)
                        .dietOrderGroupId(dietOrderGroup.getId())
                        .goodsType(Constants.GOODS_TYPE_DELIVER_FEE)
                        .goodsId(Constants.BIG_INTEGER_MINUS_ONE)
                        .goodsName("配送费")
                        .goodsSpecificationId(Constants.BIG_INTEGER_MINUS_ONE)
                        .goodsSpecificationName(Constants.VARCHAR_DEFAULT_VALUE)
                        .categoryId(Constants.FICTITIOUS_GOODS_CATEGORY_ID)
                        .categoryName(Constants.FICTITIOUS_GOODS_CATEGORY_NAME)
                        .price(shippingFee)
                        .attributeIncrease(Constants.DECIMAL_DEFAULT_VALUE)
                        .quantity(BigDecimal.ONE)
                        .totalAmount(shippingFee)
                        .discountAmount(BigDecimal.ZERO)
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
     * @param callbackParametersJsonObject
     * @return
     * @throws IOException
     */
    @Transactional(rollbackFor = Exception.class)
    public void handleOrderCancelCallback(JSONObject callbackParametersJsonObject, String uuid, int type) {
        String developerId = callbackParametersJsonObject.getString("developerId");
        String ePoiId = callbackParametersJsonObject.getString("ePoiId");
        String sign = callbackParametersJsonObject.getString("sign");
        JSONObject orderCancelJsonObject = callbackParametersJsonObject.getJSONObject("orderCancel");
        BigInteger orderId = BigInteger.valueOf(orderCancelJsonObject.getLong("orderId"));

        String[] tenantIdAndBranchIdArray = ePoiId.split("Z");
        BigInteger tenantId = NumberUtils.createBigInteger(tenantIdAndBranchIdArray[0]);
        BigInteger branchId = NumberUtils.createBigInteger(tenantIdAndBranchIdArray[1]);

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition(DietOrder.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        searchModel.addSearchCondition(DietOrder.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        searchModel.addSearchCondition(DietOrder.ColumnName.ORDER_NUMBER, Constants.SQL_OPERATION_SYMBOL_EQUAL, "M" + orderId);
        DietOrder dietOrder = DatabaseHelper.find(DietOrder.class, searchModel);
        ValidateUtils.notNull(dietOrder, "订单不存在！");

        BigInteger userId = CommonUtils.getServiceSystemUserId();

        dietOrder.setOrderStatus(DietOrderConstants.ORDER_STATUS_INVALID);
        dietOrder.setUpdatedUserId(userId);
        dietOrder.setUpdatedRemark("取消订单！");
        DatabaseHelper.update(dietOrder);

        BigInteger dietOrderId = dietOrder.getId();

        pushMeiTuanMessage(tenantId, branchId, dietOrderId, type, uuid, 5, 60000);
    }

    /**
     * 处理订单退款回调
     *
     * @param callbackParametersJsonObject
     * @return
     * @throws IOException
     */
    @Transactional(rollbackFor = Exception.class)
    public void handleOrderRefundCallback(JSONObject callbackParametersJsonObject, String uuid, int type) {
        String developerId = callbackParametersJsonObject.getString("developerId");
        String ePoiId = callbackParametersJsonObject.getString("ePoiId");
        String sign = callbackParametersJsonObject.getString("sign");
        JSONObject orderRefundJsonObject = callbackParametersJsonObject.getJSONObject("orderCancel");
        BigInteger orderId = BigInteger.valueOf(orderRefundJsonObject.getLong("orderId"));

        String[] tenantIdAndBranchIdArray = ePoiId.split("Z");
        BigInteger tenantId = NumberUtils.createBigInteger(tenantIdAndBranchIdArray[0]);
        BigInteger branchId = NumberUtils.createBigInteger(tenantIdAndBranchIdArray[1]);

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition(DietOrder.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        searchModel.addSearchCondition(DietOrder.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        searchModel.addSearchCondition(DietOrder.ColumnName.ORDER_NUMBER, Constants.SQL_OPERATION_SYMBOL_EQUAL, "M" + orderId);
        DietOrder dietOrder = DatabaseHelper.find(DietOrder.class, searchModel);
        ValidateUtils.notNull(dietOrder, "订单不存在！");

        BigInteger userId = CommonUtils.getServiceSystemUserId();

        BigInteger dietOrderId = dietOrder.getId();

        String notifyType = orderRefundJsonObject.getString("notifyType");
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

        pushMeiTuanMessage(tenantId, branchId, dietOrderId, type, uuid, 5, 60000);
    }

    @Transactional(readOnly = true)
    public void pushMeiTuanMessage(BigInteger tenantId, BigInteger branchId, BigInteger meiTuanOrderId, Integer type, String uuid, final int count, int interval) {
        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition(Pos.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        searchModel.addSearchCondition(Pos.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        searchModel.addSearchCondition(Pos.ColumnName.ONLINE, Constants.SQL_OPERATION_SYMBOL_EQUAL, 1);
        List<Pos> poses = DatabaseHelper.findAll(Pos.class, searchModel);
        if (CollectionUtils.isNotEmpty(poses)) {
            List<String> deviceIds = new ArrayList<String>();
            for (Pos pos : poses) {
                deviceIds.add(pos.getDeviceId());
            }
            PushModel pushModel = new PushModel();
            PushMessageThread pushMessageThread = new PushMessageThread(pushModel, uuid, count, interval);
            new Thread(pushMessageThread).start();
        }
    }

    /**
     * 处理门店绑定回调
     *
     * @param callbackParametersJsonObject
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void handleBindingStoreCallback(JSONObject callbackParametersJsonObject, String uuid, int type) {
        String ePoiId = callbackParametersJsonObject.getString("ePoiId");
        String appAuthToken = callbackParametersJsonObject.getString("appAuthToken");
        String poiId = callbackParametersJsonObject.getString("poiId");
        String poiName = callbackParametersJsonObject.getString("poiName");

        String[] tenantIdAndBranchIdArray = ePoiId.split("Z");
        BigInteger tenantId = NumberUtils.createBigInteger(tenantIdAndBranchIdArray[0]);
        BigInteger branchId = NumberUtils.createBigInteger(tenantIdAndBranchIdArray[1]);

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition(Branch.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        searchModel.addSearchCondition(Branch.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        Branch branch = DatabaseHelper.find(Branch.class, searchModel);
        ValidateUtils.notNull(branch, "门店不存在！");

        BigInteger userId = CommonUtils.getServiceSystemUserId();
        branch.setAppAuthToken(appAuthToken);
        branch.setPoiId(poiId);
        branch.setPoiName(poiName);
        branch.setUpdatedUserId(userId);
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
        BigInteger tenantId = checkIsBindingModel.obtainTenantId();
        BigInteger branchId = checkIsBindingModel.obtainBranchId();

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
        BigInteger tenantId = queryPoiInfoModel.obtainTenantId();
        BigInteger branchId = queryPoiInfoModel.obtainBranchId();
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
        WebResponse webResponse = OutUtils.doGetWithRequestParameters(url, requestParameters);
        System.out.println(webResponse.getResult());
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
        BigInteger tenantId = confirmOrderModel.obtainTenantId();
        BigInteger branchId = confirmOrderModel.obtainBranchId();
        BigInteger dietOrderId = confirmOrderModel.getDietOrderId();

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
        BigInteger tenantId = cancelOrderModel.obtainTenantId();
        BigInteger branchId = cancelOrderModel.obtainBranchId();
        BigInteger dietOrderId = cancelOrderModel.getDietOrderId();
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
        BigInteger tenantId = deliveringOrderModel.obtainTenantId();
        BigInteger branchId = deliveringOrderModel.obtainBranchId();
        BigInteger dietOrderId = deliveringOrderModel.getDietOrderId();
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
        BigInteger tenantId = deliveredOrderModel.obtainTenantId();
        BigInteger branchId = deliveredOrderModel.obtainBranchId();
        BigInteger dietOrderId = deliveredOrderModel.getDietOrderId();

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
}
