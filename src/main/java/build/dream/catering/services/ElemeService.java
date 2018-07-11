package build.dream.catering.services;

import build.dream.catering.constants.Constants;
import build.dream.catering.models.eleme.*;
import build.dream.catering.tools.PushMessageThread;
import build.dream.common.api.ApiRest;
import build.dream.common.constants.DietOrderConstants;
import build.dream.common.erp.catering.domains.*;
import build.dream.common.utils.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ElemeService {
    @Transactional(readOnly = true)
    public ApiRest tenantAuthorize(TenantAuthorizeModel tenantAuthorizeModel) throws IOException {
        BigInteger tenantId = tenantAuthorizeModel.getTenantId();
        BigInteger branchId = tenantAuthorizeModel.getBranchId();
        BigInteger userId = tenantAuthorizeModel.getUserId();
        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        searchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        Branch branch = DatabaseHelper.find(Branch.class, searchModel);
        Validate.notNull(branch, "门店不存在！");
        Map<String, String> checkIsAuthorizeRequestParameters = new HashMap<String, String>();
        checkIsAuthorizeRequestParameters.put("tenantId", tenantId.toString());
        checkIsAuthorizeRequestParameters.put("branchId", branchId.toString());

        String elemeAccountType = branch.getElemeAccountType().toString();
        checkIsAuthorizeRequestParameters.put("elemeAccountType", elemeAccountType);
        String checkIsAuthorizeResult = ProxyUtils.doGetOriginalWithRequestParameters(Constants.SERVICE_NAME_OUT, "eleme", "checkIsAuthorize", checkIsAuthorizeRequestParameters);
        ApiRest checkIsAuthorizeApiRest = ApiRest.fromJson(checkIsAuthorizeResult);
        Validate.isTrue(checkIsAuthorizeApiRest.isSuccessful(), checkIsAuthorizeApiRest.getError());
        Map<String, Object> checkIsAuthorizeApiRestData = (Map<String, Object>) checkIsAuthorizeApiRest.getData();
        boolean isAuthorize = (boolean) checkIsAuthorizeApiRestData.get("isAuthorize");

        String data = null;
        if (isAuthorize) {
            String serviceName = ConfigurationUtils.getConfiguration(Constants.SERVICE_NAME);
            data = CommonUtils.getOutsideUrl(Constants.SERVICE_NAME_POSAPI, "eleme", "bindingStore") + "?serviceName=" + serviceName + "&controllerName=eleme&actionName=bindingStore" + "&tenantId=" + tenantId + "&branchId=" + branchId + "&userId=" + userId;
        } else {
            String elemeUrl = ConfigurationUtils.getConfiguration(Constants.ELEME_SERVICE_URL);
            String elemeAppKey = ConfigurationUtils.getConfiguration(Constants.ELEME_APP_KEY);

            String outServiceOutsideServiceDomain = CommonUtils.getOutsideServiceDomain(Constants.SERVICE_NAME_OUT);
            data = String.format(Constants.ELEME_TENANT_AUTHORIZE_URL_FORMAT, elemeUrl + "/" + "authorize", "code", elemeAppKey, URLEncoder.encode(outServiceOutsideServiceDomain + "/eleme/tenantAuthorizeCallback", Constants.CHARSET_NAME_UTF_8), tenantId + "Z" + branchId + "Z" + userId + "Z" + elemeAccountType, "all");
        }
        ApiRest apiRest = new ApiRest(data, "生成授权链接成功！");
        return apiRest;
    }

    /**
     * 保存饿了么订单
     *
     * @param elemeCallbackMessage
     * @param uuid
     * @throws IOException
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveElemeOrder(ElemeCallbackMessage elemeCallbackMessage, String uuid) throws IOException, ParseException {
        JSONObject messageJsonObject = JSONObject.fromObject(elemeCallbackMessage.getMessage());

        String openId = messageJsonObject.getString("openId");
        String[] array = openId.split("Z");
        BigInteger tenantId = NumberUtils.createBigInteger(array[0]);
        BigInteger branchId = NumberUtils.createBigInteger(array[1]);

        SearchModel branchSearchModel = new SearchModel(true);
        branchSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        branchSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        branchSearchModel.addSearchCondition("shop_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, elemeCallbackMessage.getShopId());
        Branch branch = DatabaseHelper.find(Branch.class, branchSearchModel);
        Validate.notNull(branch, "门店不存在！");

        String tenantCode = branch.getTenantCode();

        // 开始保存饿了么订单
        JSONArray phoneList = messageJsonObject.optJSONArray("phoneList");

        BigInteger userId = CommonUtils.getServiceSystemUserId();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        int orderType = DietOrderConstants.ORDER_TYPE_ELEME_ORDER;
        int orderStatus = Constants.INT_DEFAULT_VALUE;
        String elemeOrderStatus = messageJsonObject.getString("status");
        if (DietOrderConstants.PENDING.equals(elemeOrderStatus)) {
            orderStatus = DietOrderConstants.ORDER_STATUS_PENDING;
        } else if (DietOrderConstants.UNPROCESSED.equals(elemeOrderStatus)) {
            orderStatus = DietOrderConstants.ORDER_STATUS_UNPROCESSED;
        } else if (DietOrderConstants.REFUNDING.equals(elemeOrderStatus)) {
            orderStatus = DietOrderConstants.ORDER_STATUS_REFUNDING;
        } else if (DietOrderConstants.VALID.equals(elemeOrderStatus)) {
            orderStatus = DietOrderConstants.ORDER_STATUS_VALID;
        } else if (DietOrderConstants.INVALID.equals(elemeOrderStatus)) {
            orderStatus = DietOrderConstants.ORDER_STATUS_INVALID;
        } else if (DietOrderConstants.SETTLED.equals(elemeOrderStatus)) {
            orderStatus = DietOrderConstants.ORDER_STATUS_INVALID;
        }
        int payStatus = 0;
        int paidType = 0;
        BigDecimal totalAmount = BigDecimal.valueOf(messageJsonObject.getDouble("originalPrice"));
        BigDecimal discountAmount = BigDecimal.valueOf(messageJsonObject.getDouble("shopPart")).abs();
        BigDecimal payableAmount = totalAmount.subtract(discountAmount);
        BigDecimal paidAmount = BigDecimal.ZERO;
        boolean onlinePaid = messageJsonObject.getBoolean("onlinePaid");
        if (onlinePaid) {
            payStatus = DietOrderConstants.PAY_STATUS_PAID;
            paidType = Constants.PAID_TYPE_ELM;
            paidAmount = payableAmount;
        } else {
            payStatus = DietOrderConstants.PAY_STATUS_UNPAID;
        }
        int refundStatus = 0;
        String elemeRefundStatus = messageJsonObject.getString("refundStatus");
        if (DietOrderConstants.NO_REFUND.equals(elemeRefundStatus)) {
            refundStatus = DietOrderConstants.REFUND_STATUS_NO_REFUND;
        } else if (DietOrderConstants.APPLIED.equals(elemeOrderStatus)) {
            refundStatus = DietOrderConstants.REFUND_STATUS_APPLIED;
        } else if (DietOrderConstants.REJECTED.equals(elemeOrderStatus)) {
            refundStatus = DietOrderConstants.REFUND_STATUS_REJECTED;
        } else if (DietOrderConstants.ARBITRATING.equals(elemeOrderStatus)) {
            refundStatus = DietOrderConstants.REFUND_STATUS_ARBITRATING;
        } else if (DietOrderConstants.FAILED.equals(elemeOrderStatus)) {
            refundStatus = DietOrderConstants.REFUND_STATUS_FAILED;
        } else if (DietOrderConstants.SUCCESSFUL.equals(elemeOrderStatus)) {
            refundStatus = DietOrderConstants.REFUND_STATUS_SUCCESSFUL;
        }

        String description = messageJsonObject.getString("description");
        String deliveryGeo = messageJsonObject.getString("deliveryGeo");
        String[] geolocation = deliveryGeo.split(",");
        String deliveryLongitude = geolocation[0];
        String deliveryLatitude = geolocation[1];
        Date deliverTime = Constants.DATETIME_DEFAULT_VALUE;
        Object deliverTimeObject = messageJsonObject.opt("deliverTime");
        if (deliverTimeObject != null && !(deliverTimeObject instanceof JSONNull)) {
            deliverTime = simpleDateFormat.parse(deliverTimeObject.toString());
        }
        Date activeTime = simpleDateFormat.parse(messageJsonObject.getString("activeAt"));
        boolean invoiced = messageJsonObject.getBoolean("invoiced");
        String invoiceType = Constants.VARCHAR_DEFAULT_VALUE;
        String invoice = Constants.VARCHAR_DEFAULT_VALUE;
        if (invoiced) {
            invoiceType = messageJsonObject.getString("invoiceType");
            invoice = messageJsonObject.getString("invoice");
        }
        BigDecimal deliverFee = BigDecimal.valueOf(messageJsonObject.getDouble("deliverFee"));

        DietOrder dietOrder = DietOrder.builder()
                .tenantId(tenantId)
                .tenantCode(tenantCode)
                .branchId(branchId)
                .orderNumber("E" + messageJsonObject.get("id"))
                .orderType(orderType)
                .orderStatus(orderStatus)
                .payStatus(payStatus)
                .refundStatus(refundStatus)
                .totalAmount(totalAmount)
                .discountAmount(discountAmount)
                .payableAmount(payableAmount)
                .paidAmount(paidAmount)
                .paidType(paidType)
                .remark(StringUtils.isNotBlank(description) ? description : Constants.VARCHAR_DEFAULT_VALUE)
                .deliveryAddress(messageJsonObject.getString("address"))
                .deliveryLongitude(deliveryLongitude)
                .deliveryLatitude(deliveryLatitude)
                .deliverTime(deliverTime)
                .activeTime(activeTime)
                .deliverFee(deliverFee)
                .telephoneNumber(StringUtils.join(phoneList, ","))
                .daySerialNumber(messageJsonObject.getString("daySn"))
                .consignee(messageJsonObject.getString("consignee"))
                .invoiced(invoiced)
                .invoiceType(invoiceType)
                .invoice(invoice)
                .createUserId(userId)
                .lastUpdateUserId(userId)
                .build();
        DatabaseHelper.insert(dietOrder);
        BigInteger dietOrderId = dietOrder.getId();

        JSONArray orderActivitiesJsonArray = messageJsonObject.optJSONArray("orderActivities");
        if (orderActivitiesJsonArray != null) {
            int orderActivitiesSize = orderActivitiesJsonArray.size();
            for (int orderActivitiesIndex = 0; orderActivitiesIndex < orderActivitiesSize; orderActivitiesIndex++) {
                JSONObject elemeActivityJsonObject = orderActivitiesJsonArray.optJSONObject(orderActivitiesIndex);
                int categoryId = elemeActivityJsonObject.getInt("categoryId");
                DietOrderActivity dietOrderActivity = DietOrderActivity.builder()
                        .tenantId(tenantId)
                        .tenantCode(tenantCode)
                        .branchId(branchId)
                        .dietOrderId(dietOrderId)
                        .activityId(BigInteger.valueOf(elemeActivityJsonObject.getLong("id")))
                        .activityName(elemeActivityJsonObject.getString("name"))
                        .activityType(categoryId)
                        .amount(BigDecimal.valueOf(elemeActivityJsonObject.getDouble("restaurantPart")).abs())
                        .createUserId(userId)
                        .lastUpdateUserId(userId)
                        .build();
                DatabaseHelper.insert(dietOrderActivity);
            }
        }

        if (onlinePaid) {
            DietOrderPayment dietOrderPayment = DietOrderPayment.builder()
                    .tenantId(tenantId)
                    .tenantCode(tenantCode)
                    .branchId(branchId)
                    .dietOrderId(dietOrderId)
                    .paymentId(Constants.ELM_PAYMENT_ID)
                    .paymentCode(Constants.ELM_PAYMENT_CODE)
                    .paymentName(Constants.ELM_PAYMENT_NAME)
                    .occurrenceTime(activeTime)
                    .createUserId(userId)
                    .lastUpdateUserId(userId)
                    .paidAmount(paidAmount)
                    .build();
            DatabaseHelper.insert(dietOrderPayment);
        }

        JSONArray groupsJsonArray = messageJsonObject.optJSONArray("groups");
        DietOrderGroup extraDietOrderGroup = null;
        int groupsSize = groupsJsonArray.size();
        BigInteger packageFeeItemId = BigInteger.valueOf(-70000);
        BigInteger packageFeeSkuId = Constants.BIG_INTEGER_MINUS_ONE;
        for (int groupsIndex = 0; groupsIndex < groupsSize; groupsIndex++) {
            JSONObject elemeGroupJsonObject = groupsJsonArray.getJSONObject(groupsIndex);
            String name = elemeGroupJsonObject.getString("name");
            String type = elemeGroupJsonObject.getString("type");
            DietOrderGroup dietOrderGroup = DietOrderGroup.builder()
                    .tenantId(tenantId)
                    .tenantCode(tenantCode)
                    .branchId(branchId)
                    .dietOrderId(dietOrderId)
                    .name(name)
                    .type(type)
                    .createUserId(userId)
                    .lastUpdateUserId(userId)
                    .build();
            DatabaseHelper.insert(dietOrderGroup);

            BigInteger dietOrderGroupId = dietOrderGroup.getId();

            JSONArray itemsJsonArray = elemeGroupJsonObject.optJSONArray("items");
            int itemsSize = itemsJsonArray.size();
            for (int itemsIndex = 0; itemsIndex < itemsSize; itemsIndex++) {
                JSONObject elemeOrderItemJsonObject = itemsJsonArray.optJSONObject(itemsIndex);
                JSONArray newSpecsJsonArray = elemeOrderItemJsonObject.optJSONArray("newSpecs");
                String goodsSpecificationName = "";
                BigInteger categoryId = Constants.ELEME_GOODS_CATEGORY_ID;
                String categoryName = Constants.ELEME_GOODS_CATEGORY_NAME;
                if (CollectionUtils.isNotEmpty(newSpecsJsonArray)) {
                    goodsSpecificationName = newSpecsJsonArray.getJSONObject(0).getString("value");
                }

                DietOrderDetail.Builder dietOrderDetailBuilder = DietOrderDetail.builder()
                        .tenantId(tenantId)
                        .tenantCode(tenantCode)
                        .branchId(branchId)
                        .dietOrderId(dietOrderId)
                        .dietOrderGroupId(dietOrderGroupId)
                        .goodsType(Constants.GOODS_TYPE_ORDINARY_GOODS)
                        .goodsName(elemeOrderItemJsonObject.getString("name"))
                        .goodsSpecificationName(goodsSpecificationName)
                        .price(BigDecimal.valueOf(elemeOrderItemJsonObject.getDouble("price")))
                        .flavorIncrease(BigDecimal.ZERO)
                        .quantity(BigDecimal.valueOf(elemeOrderItemJsonObject.getDouble("quantity")))
                        .totalAmount(BigDecimal.valueOf(elemeOrderItemJsonObject.getDouble("total")))
                        .discountAmount(BigDecimal.ZERO)
                        .payableAmount(BigDecimal.ZERO)
                        .createUserId(userId)
                        .lastUpdateUserId(userId);

                BigInteger id = BigInteger.valueOf(elemeOrderItemJsonObject.getLong("id"));
                BigInteger skuId = BigInteger.valueOf(elemeOrderItemJsonObject.getLong("skuId"));
                boolean isPackageFee = packageFeeItemId.compareTo(id) == 0 && packageFeeSkuId.compareTo(skuId) == 0;

                DietOrderDetail dietOrderDetail = null;
                if (isPackageFee) {
                    dietOrderDetail = dietOrderDetailBuilder.goodsId(Constants.BIG_INTEGER_MINUS_TWO)
                            .goodsSpecificationId(Constants.BIG_INTEGER_MINUS_TWO)
                            .categoryId(Constants.FICTITIOUS_GOODS_CATEGORY_ID)
                            .categoryName(Constants.FICTITIOUS_GOODS_CATEGORY_NAME)
                            .build();
                } else {
                    dietOrderDetail = dietOrderDetailBuilder.goodsId(id)
                            .goodsSpecificationId(skuId)
                            .categoryId(categoryId)
                            .categoryName(categoryName)
                            .build();
                }
                DatabaseHelper.insert(dietOrderDetail);

                BigInteger dietOrderDetailId = dietOrderDetail.getId();
                JSONArray attributesJsonArray = elemeOrderItemJsonObject.optJSONArray("attributes");
                if (CollectionUtils.isNotEmpty(attributesJsonArray)) {
                    int attributesSize = attributesJsonArray.size();
                    for (int attributesIndex = 0; attributesIndex < attributesSize; attributesIndex++) {
                        JSONObject attributeJsonObject = attributesJsonArray.optJSONObject(attributesIndex);
                        DietOrderDetailGoodsFlavor dietOrderDetailGoodsFlavor = DietOrderDetailGoodsFlavor.builder()
                                .tenantId(tenantId)
                                .tenantCode(tenantCode)
                                .branchId(branchId)
                                .dietOrderId(dietOrderId)
                                .dietOrderGroupId(dietOrderGroupId)
                                .dietOrderDetailId(dietOrderDetailId)
                                .goodsFlavorGroupId(BigInteger.ZERO)
                                .goodsFlavorGroupName(attributeJsonObject.getString("name"))
                                .goodsFlavorId(BigInteger.ZERO)
                                .goodsFlavorName(attributeJsonObject.getString("value"))
                                .price(BigDecimal.ZERO)
                                .createUserId(userId)
                                .lastUpdateUserId(userId)
                                .build();
                        DatabaseHelper.insert(dietOrderDetailGoodsFlavor);
                    }
                }
            }

            if (DietOrderConstants.GROUP_TYPE_EXTRA.equals(type)) {
                extraDietOrderGroup = dietOrderGroup;
            }
        }

        if (deliverFee.compareTo(BigDecimal.ZERO) > 0) {
            if (extraDietOrderGroup == null) {
                extraDietOrderGroup = DietOrderGroup.builder()
                        .tenantId(tenantId)
                        .tenantCode(tenantCode)
                        .branchId(branchId)
                        .dietOrderId(dietOrderId)
                        .name("其他费用")
                        .type(DietOrderConstants.GROUP_TYPE_EXTRA)
                        .createUserId(userId)
                        .lastUpdateUserId(userId)
                        .build();
                DatabaseHelper.insert(extraDietOrderGroup);
            }
            DietOrderDetail dietOrderDetail = DietOrderDetail.builder()
                    .tenantId(tenantId)
                    .tenantCode(tenantCode)
                    .branchId(branchId)
                    .dietOrderId(dietOrderId)
                    .dietOrderGroupId(extraDietOrderGroup.getId())
                    .goodsType(Constants.GOODS_TYPE_DELIVER_FEE)
                    .goodsId(Constants.BIG_INTEGER_MINUS_ONE)
                    .goodsName("配送费")
                    .goodsSpecificationId(Constants.BIG_INTEGER_MINUS_ONE)
                    .goodsSpecificationName(Constants.VARCHAR_DEFAULT_VALUE)
                    .categoryId(Constants.FICTITIOUS_GOODS_CATEGORY_ID)
                    .categoryName(Constants.FICTITIOUS_GOODS_CATEGORY_NAME)
                    .price(deliverFee)
                    .flavorIncrease(Constants.DECIMAL_DEFAULT_VALUE)
                    .quantity(BigDecimal.ONE)
                    .totalAmount(deliverFee)
                    .discountAmount(BigDecimal.ZERO)
                    .payableAmount(BigDecimal.ZERO)
                    .createUserId(userId)
                    .lastUpdateUserId(userId)
                    .build();
            DatabaseHelper.insert(dietOrderDetail);
        }
        DatabaseHelper.insert(elemeCallbackMessage);
    }

    /**
     * 处理饿了么退单消息
     *
     * @param elemeCallbackMessage
     * @param uuid
     * @throws IOException
     */
    @Transactional(rollbackFor = Exception.class)
    public void handleElemeRefundOrderMessage(ElemeCallbackMessage elemeCallbackMessage, String uuid) throws IOException {
        JSONObject messageJsonObject = JSONObject.fromObject(elemeCallbackMessage.getMessage());
        String orderId = messageJsonObject.optString("orderId");
        SearchModel elemeOrderSearchModel = new SearchModel(true);
        elemeOrderSearchModel.addSearchCondition("order_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, orderId);
        ElemeOrder elemeOrder = DatabaseHelper.find(ElemeOrder.class, elemeOrderSearchModel);
        Validate.notNull(elemeOrder, "饿了么订单不存在！");

        String refundStatus = messageJsonObject.optString("refundStatus");
        elemeOrder.setRefundStatus(refundStatus);

        BigInteger userId = CommonUtils.getServiceSystemUserId();
        elemeOrder.setLastUpdateUserId(userId);
        elemeOrder.setLastUpdateRemark("处理退单消息回调！");
        DatabaseHelper.update(elemeOrder);

        elemeCallbackMessage.setOrderId(orderId);
        DatabaseHelper.insert(elemeCallbackMessage);

        int type = elemeCallbackMessage.getType();
        if (type == 30 || type == 31 || type == 34 || type == 35 || type == 36) {
//            publishElemeOrderMessage(elemeOrder.getTenantId(), elemeOrder.getBranchId(), elemeOrder.getId(), type, uuid);
            pushElemeMessage(elemeOrder.getTenantId(), elemeOrder.getBranchId(), elemeOrder.getId(), type, uuid, 5, 60000);
        }
    }

    /**
     * 处理饿了么催单消息
     *
     * @param elemeCallbackMessage
     * @param uuid
     * @throws IOException
     */
    @Transactional(rollbackFor = Exception.class)
    public void handleElemeReminderMessage(ElemeCallbackMessage elemeCallbackMessage, String uuid) throws IOException {
        JSONObject messageJsonObject = JSONObject.fromObject(elemeCallbackMessage.getMessage());
        String orderId = messageJsonObject.optString("orderId");
        SearchModel elemeOrderSearchModel = new SearchModel(true);
        elemeOrderSearchModel.addSearchCondition("order_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, orderId);
        ElemeOrder elemeOrder = DatabaseHelper.find(ElemeOrder.class, elemeOrderSearchModel);
        Validate.notNull(elemeOrder, "饿了么订单不存在！");

        elemeCallbackMessage.setOrderId(orderId);
        DatabaseHelper.insert(elemeCallbackMessage);

        int type = elemeCallbackMessage.getType();
        if (type == 45) {
//            publishElemeOrderMessage(elemeOrder.getTenantId(), elemeOrder.getBranchId(), elemeOrder.getId(), type, uuid);
            pushElemeMessage(elemeOrder.getTenantId(), elemeOrder.getBranchId(), elemeOrder.getId(), type, uuid, 5, 60000);
        }
    }

    /**
     * 处理饿了么取消单消息
     *
     * @param elemeCallbackMessage
     * @param uuid
     * @throws IOException
     */
    @Transactional(rollbackFor = Exception.class)
    public void handleElemeCancelOrderMessage(ElemeCallbackMessage elemeCallbackMessage, String uuid) throws IOException {
        JSONObject messageJsonObject = JSONObject.fromObject(elemeCallbackMessage.getMessage());
        String orderId = messageJsonObject.optString("orderId");
        SearchModel elemeOrderSearchModel = new SearchModel(true);
        elemeOrderSearchModel.addSearchCondition("order_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, orderId);
        ElemeOrder elemeOrder = DatabaseHelper.find(ElemeOrder.class, elemeOrderSearchModel);
        Validate.notNull(elemeOrder, "饿了么订单不存在！");

        elemeCallbackMessage.setOrderId(orderId);
        DatabaseHelper.insert(elemeCallbackMessage);

        int type = elemeCallbackMessage.getType();
        if (type == 20 || type == 21 || type == 24 || type == 25 || type == 26) {
//            publishElemeOrderMessage(elemeOrder.getTenantId(), elemeOrder.getBranchId(), elemeOrder.getId(), type, uuid);
            pushElemeMessage(elemeOrder.getTenantId(), elemeOrder.getBranchId(), elemeOrder.getId(), type, uuid, 5, 60000);
        }
    }

    /**
     * 处理订单状态变更消息
     *
     * @param elemeCallbackMessage
     * @param uuid
     * @throws IOException
     */
    @Transactional(rollbackFor = Exception.class)
    public void handleElemeOrderStateChangeMessage(ElemeCallbackMessage elemeCallbackMessage, String uuid) throws IOException {
        JSONObject messageJsonObject = JSONObject.fromObject(elemeCallbackMessage.getMessage());
        String orderId = messageJsonObject.optString("id");
        SearchModel elemeOrderSearchModel = new SearchModel(true);
        elemeOrderSearchModel.addSearchCondition("order_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, orderId);
        ElemeOrder elemeOrder = DatabaseHelper.find(ElemeOrder.class, elemeOrderSearchModel);
        Validate.notNull(elemeOrder, "饿了么订单不存在！");


        String state = messageJsonObject.getString("state");
        BigInteger userId = CommonUtils.getServiceSystemUserId();
        elemeOrder.setStatus(state);
        elemeOrder.setLastUpdateUserId(userId);
        elemeOrder.setLastUpdateRemark("处理饿了么订单状态变更消息，修改订单状态！");
        DatabaseHelper.update(elemeOrder);

        elemeCallbackMessage.setOrderId(orderId);
        DatabaseHelper.insert(elemeCallbackMessage);
//        publishElemeOrderMessage(elemeOrder.getTenantId(), elemeOrder.getBranchId(), elemeOrder.getId(), elemeCallbackMessage.getType(), uuid);
        pushElemeMessage(elemeOrder.getTenantId(), elemeOrder.getBranchId(), elemeOrder.getId(), elemeCallbackMessage.getType(), uuid, 5, 60000);
    }

    /**
     * 处理运单状态变更消息
     *
     * @param elemeCallbackMessage
     * @param uuid
     * @throws IOException
     */
    @Transactional(rollbackFor = Exception.class)
    public void handleElemeDeliveryOrderStateChangeMessage(ElemeCallbackMessage elemeCallbackMessage, String uuid) throws IOException {
        JSONObject messageJsonObject = JSONObject.fromObject(elemeCallbackMessage.getMessage());
        String orderId = messageJsonObject.optString("orderId");
        SearchModel elemeOrderSearchModel = new SearchModel(true);
        elemeOrderSearchModel.addSearchCondition("order_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, orderId);
        ElemeOrder elemeOrder = DatabaseHelper.find(ElemeOrder.class, elemeOrderSearchModel);
        Validate.notNull(elemeOrder, "饿了么订单不存在！");

        elemeCallbackMessage.setOrderId(orderId);
        DatabaseHelper.insert(elemeCallbackMessage);
//        publishElemeOrderMessage(elemeOrder.getTenantId(), elemeOrder.getBranchId(), elemeOrder.getId(), elemeCallbackMessage.getType(), uuid);
        pushElemeMessage(elemeOrder.getTenantId(), elemeOrder.getBranchId(), elemeOrder.getId(), elemeCallbackMessage.getType(), uuid, 5, 60000);
    }

    public void handleElemeShopStateChangeMessage(ElemeCallbackMessage elemeCallbackMessage, String uuid) {

    }

    public void handleAuthorizationStateChangeMessage(ElemeCallbackMessage elemeCallbackMessage, String uuid) {

    }

    @Transactional(readOnly = true)
    public Branch findBranch(BigInteger tenantId, BigInteger branchId) {
        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        searchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        Branch branch = DatabaseHelper.find(Branch.class, searchModel);
        Validate.notNull(branch, "门店不存在！");
        return branch;
    }

    @Transactional(readOnly = true)
    public GoodsCategory findGoodsCategoryInfo(BigInteger tenantId, BigInteger branchId, BigInteger categoryId) {
        SearchModel searchModel = new SearchModel();
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        searchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        searchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUAL, categoryId);
        GoodsCategory goodsCategory = DatabaseHelper.find(GoodsCategory.class, searchModel);
        Validate.notNull(goodsCategory, "分类信息不存在！");
        return goodsCategory;
    }

    @Transactional(readOnly = true)
    public DietOrder findElemeOrder(BigInteger tenantId, BigInteger branchId, BigInteger elemeOrderId) {
        SearchModel searchModel = new SearchModel();
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        searchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        searchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUAL, elemeOrderId);
        DietOrder dietOrder = DatabaseHelper.find(DietOrder.class, searchModel);
        ValidateUtils.notNull(dietOrder, "订单不存在！");
        return dietOrder;
    }

    @Transactional(readOnly = true)
    public List<DietOrder> findAllElemeOrders(BigInteger tenantId, BigInteger branchId, List<BigInteger> elemeOrderIds) {
        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        searchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        searchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_IN, elemeOrderIds);
        List<DietOrder> dietOrders = DatabaseHelper.findAll(DietOrder.class, searchModel);
        ValidateUtils.notEmpty(dietOrders, "订单不存在！");
        return dietOrders;
    }

    public List<String> obtainOrderIds(List<DietOrder> dietOrders) {
        List<String> orderIds = new ArrayList<String>();
        for (DietOrder dietOrder : dietOrders) {
            orderIds.add(dietOrder.getOrderNumber().substring(1));
        }
        return orderIds;
    }

    @Transactional(readOnly = true)
    public ApiRest obtainElemeCallbackMessage(ObtainElemeCallbackMessageModel obtainElemeCallbackMessageModel) {
        SearchModel searchModel = new SearchModel(false);
        searchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUAL, obtainElemeCallbackMessageModel.getElemeCallbackMessageId());
        ElemeCallbackMessage elemeCallbackMessage = DatabaseHelper.find(ElemeCallbackMessage.class, searchModel);

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("id", elemeCallbackMessage.getId());
        data.put("orderId", elemeCallbackMessage.getOrderId());
        data.put("requestId", elemeCallbackMessage.getRequestId());
        data.put("type", elemeCallbackMessage.getType());
        data.put("appId", elemeCallbackMessage.getAppId());
        data.put("message", JSONObject.fromObject(elemeCallbackMessage.getMessage()));
        data.put("shopId", elemeCallbackMessage.getShopId());
        data.put("timestamp", elemeCallbackMessage.getTimestamp());
        data.put("signature", elemeCallbackMessage.getSignature());
        data.put("userId", elemeCallbackMessage.getUserId());

        return new ApiRest(data, "获取饿了么回调消息成功！");
    }

    @Transactional(readOnly = true)
    public ApiRest obtainElemeOrder(ObtainElemeOrderModel obtainElemeOrderModel) {
        BigInteger tenantId = obtainElemeOrderModel.getTenantId();
        BigInteger branchId = obtainElemeOrderModel.getBranchId();
        BigInteger elemeOrderId = obtainElemeOrderModel.getElemeOrderId();
        // 查询订单
        SearchModel elemeOrderSearchModel = new SearchModel(true);
        elemeOrderSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        elemeOrderSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        elemeOrderSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUAL, elemeOrderId);
        ElemeOrder elemeOrder = DatabaseHelper.find(ElemeOrder.class, elemeOrderSearchModel);
        Validate.notNull(elemeOrder, "订单不存在！");

        // 查询订单分组
        SearchModel elemeOrderGroupSearchModel = new SearchModel(true);
        elemeOrderGroupSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        elemeOrderGroupSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        elemeOrderGroupSearchModel.addSearchCondition("eleme_order_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, elemeOrderId);
        List<ElemeOrderGroup> elemeOrderGroups = DatabaseHelper.findAll(ElemeOrderGroup.class, elemeOrderGroupSearchModel);

        // 查询所有订单分组明细
        SearchModel elemeOrderItemSearchModel = new SearchModel(true);
        elemeOrderItemSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        elemeOrderItemSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        elemeOrderItemSearchModel.addSearchCondition("eleme_order_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, elemeOrderId);
        List<ElemeOrderItem> elemeOrderItems = DatabaseHelper.findAll(ElemeOrderItem.class, elemeOrderItemSearchModel);

        // 查询出所有的商品规格
        SearchModel elemeOrderItemNewSpecSearchModel = new SearchModel(true);
        elemeOrderItemNewSpecSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        elemeOrderItemNewSpecSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        elemeOrderItemNewSpecSearchModel.addSearchCondition("eleme_order_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, elemeOrderId);
        List<ElemeOrderItemNewSpec> elemeOrderItemNewSpecs = DatabaseHelper.findAll(ElemeOrderItemNewSpec.class, elemeOrderItemNewSpecSearchModel);

        // 查询出所有的商品属性
        SearchModel elemeOrderItemAttributeSearchModel = new SearchModel(true);
        elemeOrderItemAttributeSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        elemeOrderItemAttributeSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        elemeOrderItemAttributeSearchModel.addSearchCondition("eleme_order_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, elemeOrderId);
        List<ElemeOrderItemAttribute> elemeOrderItemAttributes = DatabaseHelper.findAll(ElemeOrderItemAttribute.class, elemeOrderItemAttributeSearchModel);

        // 查询出订单包含的所有活动
        SearchModel elemeOrderActivitySearchModel = new SearchModel(true);
        elemeOrderActivitySearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        elemeOrderActivitySearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        elemeOrderActivitySearchModel.addSearchCondition("eleme_order_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, elemeOrder.getId());
        List<ElemeOrderActivity> elemeActivities = DatabaseHelper.findAll(ElemeOrderActivity.class, elemeOrderActivitySearchModel);

        // 封装订单分组与订单明细之间的 map
        Map<BigInteger, List<ElemeOrderItem>> elemeOrderItemMap = new HashMap<BigInteger, List<ElemeOrderItem>>();
        for (ElemeOrderItem elemeOrderItem : elemeOrderItems) {
            List<ElemeOrderItem> elemeOrderItemList = elemeOrderItemMap.get(elemeOrderItem.getElemeOrderGroupId());
            if (elemeOrderItemList == null) {
                elemeOrderItemList = new ArrayList<ElemeOrderItem>();
                elemeOrderItemMap.put(elemeOrderItem.getElemeOrderGroupId(), elemeOrderItemList);
            }
            elemeOrderItemList.add(elemeOrderItem);
        }

        // 封装订单明细与商品规格之间的 map
        Map<BigInteger, List<ElemeOrderItemNewSpec>> elemeOrderItemNewSpecMap = new HashMap<BigInteger, List<ElemeOrderItemNewSpec>>();
        for (ElemeOrderItemNewSpec elemeOrderItemNewSpec : elemeOrderItemNewSpecs) {
            List<ElemeOrderItemNewSpec> elemeOrderItemNewSpecList = elemeOrderItemNewSpecMap.get(elemeOrderItemNewSpec.getElemeOrderItemId());
            if (elemeOrderItemNewSpecList == null) {
                elemeOrderItemNewSpecList = new ArrayList<ElemeOrderItemNewSpec>();
                elemeOrderItemNewSpecMap.put(elemeOrderItemNewSpec.getElemeOrderItemId(), elemeOrderItemNewSpecList);
            }
            elemeOrderItemNewSpecList.add(elemeOrderItemNewSpec);
        }

        // 封装订单明细与商品属性之间的 map
        Map<BigInteger, List<ElemeOrderItemAttribute>> elemeOrderItemAttributeMap = new HashMap<BigInteger, List<ElemeOrderItemAttribute>>();
        for (ElemeOrderItemAttribute elemeOrderItemAttribute : elemeOrderItemAttributes) {
            List<ElemeOrderItemAttribute> elemeOrderItemAttributeList = elemeOrderItemAttributeMap.get(elemeOrderItemAttribute.getElemeOrderItemId());
            if (elemeOrderItemAttributeList == null) {
                elemeOrderItemAttributeList = new ArrayList<ElemeOrderItemAttribute>();
                elemeOrderItemAttributeMap.put(elemeOrderItemAttribute.getElemeOrderItemId(), elemeOrderItemAttributeList);
            }
            elemeOrderItemAttributeList.add(elemeOrderItemAttribute);
        }

        List<Map<String, Object>> groups = new ArrayList<Map<String, Object>>();
        for (ElemeOrderGroup elemeOrderGroup : elemeOrderGroups) {
            Map<String, Object> elemeGroupMap = new HashMap<String, Object>();
            elemeGroupMap.put("name", elemeOrderGroup.getName());
            elemeGroupMap.put("type", elemeOrderGroup.getType());
            List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
            List<ElemeOrderItem> elemeOrderItemList = elemeOrderItemMap.get(elemeOrderGroup.getId());
            for (ElemeOrderItem elemeOrderItem : elemeOrderItemList) {
                Map<String, Object> item = new HashMap<String, Object>();
                item.put("id", elemeOrderItem.getElemeItemId());
                item.put("skuId", elemeOrderItem.getSkuId());
                item.put("name", elemeOrderItem.getName());
                item.put("categoryId", elemeOrderItem.getCategoryId());
                item.put("price", elemeOrderItem.getPrice());
                item.put("quantity", elemeOrderItem.getQuantity());
                item.put("total", elemeOrderItem.getTotal());

                List<ElemeOrderItemNewSpec> elemeOrderItemNewSpecList = elemeOrderItemNewSpecMap.get(elemeOrderItem.getId());
                List<Map<String, String>> newSpecs = new ArrayList<Map<String, String>>();
                if (CollectionUtils.isNotEmpty(elemeOrderItemNewSpecList)) {
                    for (ElemeOrderItemNewSpec elemeOrderItemNewSpec : elemeOrderItemNewSpecList) {
                        Map<String, String> newSpec = new HashMap<String, String>();
                        newSpec.put("name", elemeOrderItemNewSpec.getName());
                        newSpec.put("value", elemeOrderItemNewSpec.getValue());
                        newSpecs.add(newSpec);
                    }
                }
                item.put("newSpecs", newSpecs);

                List<ElemeOrderItemAttribute> elemeOrderItemAttributeList = elemeOrderItemAttributeMap.get(elemeOrderItem.getId());
                List<Map<String, String>> attributes = new ArrayList<Map<String, String>>();
                if (CollectionUtils.isNotEmpty(elemeOrderItemAttributeList)) {
                    for (ElemeOrderItemAttribute elemeOrderItemAttribute : elemeOrderItemAttributeList) {
                        Map<String, String> attribute = new HashMap<String, String>();
                        attribute.put("name", elemeOrderItemAttribute.getName());
                        attribute.put("value", elemeOrderItemAttribute.getValue());
                        attributes.add(attribute);
                    }
                }
                item.put("attributes", attributes);

                item.put("extendCode", elemeOrderItem.getExtendCode());
                item.put("barCode", elemeOrderItem.getBarCode());
                item.put("weight", elemeOrderItem.getWeight());
                item.put("userPrice", elemeOrderItem.getUserPrice());
                item.put("shopPrice", elemeOrderItem.getShopPrice());
                item.put("vfoodId", elemeOrderItem.getVfoodId());
                items.add(item);
            }
            elemeGroupMap.put("items", items);
            groups.add(elemeGroupMap);
        }
        Map<String, Object> elemeOrderMap = ApplicationHandler.toMap(elemeOrder);
        elemeOrderMap.put("groups", groups);
        List<Map<String, Object>> orderActivities = new ArrayList<Map<String, Object>>();
        if (CollectionUtils.isNotEmpty(elemeActivities)) {
            for (ElemeOrderActivity elemeOrderActivity : elemeActivities) {
                Map<String, Object> elemeActivityMap = new HashMap<String, Object>();
                elemeActivityMap.put("id", elemeOrderActivity.getElemeActivityId());
                elemeActivityMap.put("name", elemeOrderActivity.getName());
                elemeActivityMap.put("categoryId", elemeOrderActivity.getCategoryId());
                elemeActivityMap.put("elemePart", elemeOrderActivity.getElemePart());
                elemeActivityMap.put("restaurantPart", elemeOrderActivity.getRestaurantPart());
                elemeActivityMap.put("amount", elemeOrderActivity.getAmount());
                orderActivities.add(elemeActivityMap);
            }
        }
        elemeOrderMap.put("orderActivities", orderActivities);

        ApiRest apiRest = new ApiRest();
        apiRest.setData(elemeOrderMap);
        apiRest.setMessage("获取饿了么订单成功！");
        apiRest.setSuccessful(true);
        return apiRest;
    }

    @Transactional(rollbackFor = Exception.class)
    public ApiRest doBindingStore(DoBindingStoreModel doBindingStoreModel) throws IOException {
        BigInteger tenantId = doBindingStoreModel.getTenantId();
        BigInteger branchId = doBindingStoreModel.getBranchId();
        BigInteger shopId = doBindingStoreModel.getShopId();
        BigInteger userId = doBindingStoreModel.getUserId();

        String lastUpdateRemark = "门店(" + branchId + ")绑定饿了么(" + shopId + ")，清除绑定关系！";
        UpdateModel updateModel = new UpdateModel(true);
        updateModel.setTableName("branch");
        updateModel.addContentValue("shop_id", null);
        updateModel.addContentValue("last_update_user_id", userId);
        updateModel.addContentValue("last_update_remark", lastUpdateRemark);
        updateModel.addSearchCondition("shop_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, shopId);
        DatabaseHelper.universalUpdate(updateModel);

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        Branch branch = DatabaseHelper.find(Branch.class, searchModel);
        Validate.notNull(branch, "门店不存在！");
        branch.setShopId(doBindingStoreModel.getShopId());
        DatabaseHelper.update(branch);

        Map<String, String> saveElemeBranchMappingRequestParameters = new HashMap<String, String>();
        saveElemeBranchMappingRequestParameters.put("tenantId", tenantId.toString());
        saveElemeBranchMappingRequestParameters.put("branchId", branchId.toString());
        saveElemeBranchMappingRequestParameters.put("shopId", shopId.toString());
        saveElemeBranchMappingRequestParameters.put("userId", userId.toString());

        ApiRest saveElemeBranchMappingApiRest = ProxyUtils.doPostWithRequestParameters(Constants.SERVICE_NAME_OUT, "eleme", "saveElemeBranchMapping", saveElemeBranchMappingRequestParameters);
        Validate.isTrue(saveElemeBranchMappingApiRest.isSuccessful(), saveElemeBranchMappingApiRest.getError());

        ApiRest apiRest = new ApiRest();
        apiRest.setMessage("饿了么门店绑定成功！");
        apiRest.setSuccessful(true);
        return apiRest;
    }

    /**
     * 发布饿了么订单消息
     *
     * @param tenantId：商户ID
     * @param branchId：门店ID
     * @param elemeOrderId：饿了么订单ID
     * @param type：消息类型
     * @param uuid：消息唯一ID
     * @throws IOException
     */
    private void publishElemeOrderMessage(BigInteger tenantId, BigInteger branchId, BigInteger elemeOrderId, Integer type, String uuid) throws IOException {
        String elemeMessageChannelTopic = ConfigurationUtils.getConfiguration(Constants.ELEME_MESSAGE_CHANNEL_TOPIC);
        JSONObject messageJsonObject = new JSONObject();
        messageJsonObject.put("tenantId", tenantId);
        messageJsonObject.put("branchId", branchId);
        messageJsonObject.put("type", type);
        messageJsonObject.put("elemeOrderId", elemeOrderId);
        messageJsonObject.put("uuid", uuid);
        QueueUtils.convertAndSend(elemeMessageChannelTopic, messageJsonObject.toString());
    }

    @Transactional(readOnly = true)
    public void pushElemeMessage(BigInteger tenantId, BigInteger branchId, BigInteger elemeOrderId, Integer type, String uuid, final int count, int interval) {
        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        searchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        searchModel.addSearchCondition("online", Constants.SQL_OPERATION_SYMBOL_EQUAL, 1);
        List<Pos> poses = DatabaseHelper.findAll(Pos.class, searchModel);
        if (CollectionUtils.isNotEmpty(poses)) {
            List<String> registrationIds = new ArrayList<String>();
            for (Pos pos : poses) {
                registrationIds.add(pos.getRegistrationId());
            }
            Map<String, Object> audience = new HashMap<String, Object>();
            audience.put("registrationId", registrationIds);

            Map<String, Object> extras = new HashMap<String, Object>();
            extras.put("elemeOrderId", elemeOrderId);
            extras.put("type", type);
            extras.put("uuid", uuid);
            extras.put("code", Constants.MESSAGE_CODE_ELEME_MESSAGE);

            Map<String, Object> android = new HashMap<String, Object>();
            android.put("alert", "");
            android.put("title", "Send to Android");
            android.put("builderId", 1);
            android.put("extras", extras);

            Map<String, Object> ios = new HashMap<String, Object>();
            ios.put("alert", "Send to Ios");
            ios.put("sound", "default");
            ios.put("badge", "+1");
            ios.put("extras", extras);

            Map<String, Object> notification = new HashMap<String, Object>();
            notification.put("alert", "饿了么新订单消息！");
            notification.put("android", android);
            notification.put("ios", ios);

            Map<String, Object> message = new HashMap<String, Object>();
            message.put("platform", "all");
            message.put("audience", audience);
            message.put("notification", notification);
            PushMessageThread pushMessageThread = new PushMessageThread(GsonUtils.toJson(message), uuid, count, interval);
            new Thread(pushMessageThread).start();
        }
    }

    /**
     * 获取订单
     *
     * @param getOrderModel
     * @return
     */
    public ApiRest getOrder(GetOrderModel getOrderModel) throws IOException {
        BigInteger tenantId = getOrderModel.getTenantId();
        BigInteger branchId = getOrderModel.getBranchId();
        BigInteger elemeOrderId = getOrderModel.getElemeOrderId();


        Branch branch = findBranch(tenantId, branchId);
        DietOrder dietOrder = findElemeOrder(tenantId, branchId, elemeOrderId);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("orderId", dietOrder.getOrderNumber().substring(1));

        Map<String, Object> result = ElemeUtils.callElemeSystem(tenantId.toString(), branchId.toString(), branch.getElemeAccountType(), "eleme.order.getOrder", params);

        return new ApiRest(result, "获取订单成功！");
    }

    /**
     * 批量查询订单
     *
     * @param batchGetOrdersModel
     * @return
     * @throws IOException
     */
    @Transactional(readOnly = true)
    public ApiRest batchGetOrders(BatchGetOrdersModel batchGetOrdersModel) throws IOException {
        BigInteger tenantId = batchGetOrdersModel.getTenantId();
        BigInteger branchId = batchGetOrdersModel.getBranchId();

        Branch branch = findBranch(tenantId, branchId);
        List<DietOrder> dietOrders = findAllElemeOrders(tenantId, branchId, batchGetOrdersModel.getElemeOrderIds());
        List<String> orderIds = obtainOrderIds(dietOrders);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("orderIds", orderIds);

        Map<String, Object> result = ElemeUtils.callElemeSystem(tenantId.toString(), branchId.toString(), branch.getElemeAccountType(), "eleme.order.mgetOrders", params);

        return new ApiRest(result, "批量查询订单成功！");
    }

    /**
     * 确认订单
     *
     * @param confirmOrderLiteModel
     * @return
     * @throws IOException
     */
    public ApiRest confirmOrderLite(ConfirmOrderLiteModel confirmOrderLiteModel) throws IOException {
        BigInteger tenantId = confirmOrderLiteModel.getTenantId();
        BigInteger branchId = confirmOrderLiteModel.getBranchId();
        BigInteger elemeOrderId = confirmOrderLiteModel.getElemeOrderId();

        Branch branch = findBranch(tenantId, branchId);
        DietOrder dietOrder = findElemeOrder(tenantId, branchId, elemeOrderId);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("orderId", dietOrder.getOrderNumber().substring(1));
        Map<String, Object> result = ElemeUtils.callElemeSystem(tenantId.toString(), branchId.toString(), branch.getElemeAccountType(), "eleme.order.confirmOrderLite", params);

        return new ApiRest(result, "确认订单成功！");
    }

    /**
     * 取消订单
     *
     * @param cancelOrderLiteModel
     * @return
     * @throws IOException
     */
    public ApiRest cancelOrderLite(CancelOrderLiteModel cancelOrderLiteModel) throws IOException {
        BigInteger tenantId = cancelOrderLiteModel.getTenantId();
        BigInteger branchId = cancelOrderLiteModel.getBranchId();
        BigInteger elemeOrderId = cancelOrderLiteModel.getElemeOrderId();

        Branch branch = findBranch(tenantId, branchId);
        DietOrder dietOrder = findElemeOrder(tenantId, branchId, elemeOrderId);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("orderId", dietOrder.getOrderNumber().substring(1));
        params.put("type", cancelOrderLiteModel.getType());
        ApplicationHandler.ifNotNullPut(params, "remark", cancelOrderLiteModel.getRemark());

        Map<String, Object> result = ElemeUtils.callElemeSystem(tenantId.toString(), branchId.toString(), branch.getElemeAccountType(), "eleme.order.cancelOrderLite", params);

        return new ApiRest(result, "取消订单成功！");
    }

    /**
     * 同意退单/同意取消单
     *
     * @param agreeRefundLiteModel
     * @return
     * @throws IOException
     */
    public ApiRest agreeRefundLite(AgreeRefundLiteModel agreeRefundLiteModel) throws IOException {
        BigInteger tenantId = agreeRefundLiteModel.getTenantId();
        BigInteger branchId = agreeRefundLiteModel.getBranchId();
        BigInteger elemeOrderId = agreeRefundLiteModel.getElemeOrderId();

        Branch branch = findBranch(tenantId, branchId);
        DietOrder dietOrder = findElemeOrder(tenantId, branchId, elemeOrderId);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("orderId", dietOrder.getOrderNumber().substring(1));
        Map<String, Object> result = ElemeUtils.callElemeSystem(tenantId.toString(), branchId.toString(), branch.getElemeAccountType(), "eleme.order.agreeRefundLite", params);

        return new ApiRest(result, "同意退单/同意取消单成功！");
    }

    /**
     * 不同意退单/不同意取消单
     *
     * @param disagreeRefundLiteModel
     * @return
     * @throws IOException
     */
    public ApiRest disagreeRefundLite(DisagreeRefundLiteModel disagreeRefundLiteModel) throws IOException {
        BigInteger tenantId = disagreeRefundLiteModel.getTenantId();
        BigInteger branchId = disagreeRefundLiteModel.getBranchId();

        Branch branch = findBranch(tenantId, branchId);
        DietOrder dietOrder = findElemeOrder(tenantId, branchId, disagreeRefundLiteModel.getElemeOrderId());

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("orderId", dietOrder.getOrderNumber().substring(1));
        Map<String, Object> result = ElemeUtils.callElemeSystem(tenantId.toString(), branchId.toString(), branch.getElemeAccountType(), "eleme.order.disagreeRefundLite", params);

        return new ApiRest(result, "不同意退单/不同意取消单成功！");
    }

    /**
     * 配送异常或者物流拒单后选择自行配送
     *
     * @param deliveryBySelfLiteModel
     * @return
     * @throws IOException
     */
    public ApiRest deliveryBySelfLite(DeliveryBySelfLiteModel deliveryBySelfLiteModel) throws IOException {
        BigInteger tenantId = deliveryBySelfLiteModel.getTenantId();
        BigInteger branchId = deliveryBySelfLiteModel.getBranchId();
        BigInteger elemeOrderId = deliveryBySelfLiteModel.getElemeOrderId();

        Branch branch = findBranch(tenantId, branchId);
        DietOrder dietOrder = findElemeOrder(tenantId, branchId, elemeOrderId);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("orderId", dietOrder.getOrderNumber().substring(1));
        Map<String, Object> result = ElemeUtils.callElemeSystem(tenantId.toString(), branchId.toString(), branch.getElemeAccountType(), "eleme.order.deliveryBySelfLite", params);

        return new ApiRest(result, "配送异常或者物流拒单后选择自行配送成功！");
    }

    /**
     * 配送异常或者物流拒单后选择不再配送
     *
     * @param noMoreDeliveryLiteModel
     * @return
     * @throws IOException
     */
    public ApiRest noMoreDeliveryLite(NoMoreDeliveryLiteModel noMoreDeliveryLiteModel) throws IOException {
        BigInteger tenantId = noMoreDeliveryLiteModel.getTenantId();
        BigInteger branchId = noMoreDeliveryLiteModel.getBranchId();

        Branch branch = findBranch(tenantId, branchId);
        DietOrder dietOrder = findElemeOrder(tenantId, branchId, noMoreDeliveryLiteModel.getElemeOrderId());

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("orderId", dietOrder.getOrderNumber().substring(1));
        Map<String, Object> result = ElemeUtils.callElemeSystem(tenantId.toString(), branchId.toString(), branch.getElemeAccountType(), "eleme.order.noMoreDeliveryLite", params);

        return new ApiRest(result, "配送异常或者物流拒单后选择不再配送成功！");
    }

    /**
     * 订单确认送达
     *
     * @param receivedOrderLiteModel
     * @return
     * @throws IOException
     */
    public ApiRest receivedOrderLite(ReceivedOrderLiteModel receivedOrderLiteModel) throws IOException {
        BigInteger tenantId = receivedOrderLiteModel.getTenantId();
        BigInteger branchId = receivedOrderLiteModel.getBranchId();

        Branch branch = findBranch(tenantId, branchId);
        DietOrder dietOrder = findElemeOrder(tenantId, branchId, receivedOrderLiteModel.getElemeOrderId());

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("orderId", dietOrder.getOrderNumber().substring(1));
        Map<String, Object> result = ElemeUtils.callElemeSystem(tenantId.toString(), branchId.toString(), branch.getElemeAccountType(), "eleme.order.receivedOrderLite", params);

        return new ApiRest(result, "订单确认送达成功！");
    }

    /**
     * 回复催单
     *
     * @param replyReminderModel
     * @return
     * @throws IOException
     */
    public ApiRest replyReminder(ReplyReminderModel replyReminderModel) throws IOException {
        BigInteger tenantId = replyReminderModel.getTenantId();
        BigInteger branchId = replyReminderModel.getBranchId();

        Branch branch = findBranch(tenantId, branchId);
        DietOrder dietOrder = findElemeOrder(tenantId, branchId, replyReminderModel.getElemeOrderId());

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("orderId", dietOrder.getOrderNumber().substring(1));
        params.put("type", replyReminderModel.getType());
        ApplicationHandler.ifNotNullPut(params, "content", replyReminderModel.getContent());
        Map<String, Object> result = ElemeUtils.callElemeSystem(tenantId.toString(), branchId.toString(), branch.getElemeAccountType(), "eleme.order.replyReminder", params);

        return new ApiRest(result, "回复催单成功！");
    }

    /**
     * 获取商户账号信息
     *
     * @param getUserModel
     * @return
     * @throws IOException
     */
    public ApiRest getUser(GetUserModel getUserModel) throws IOException {
        BigInteger tenantId = getUserModel.getTenantId();
        BigInteger branchId = getUserModel.getBranchId();

        Branch branch = findBranch(tenantId, branchId);

        Map<String, Object> result = ElemeUtils.callElemeSystem(tenantId.toString(), branchId.toString(), branch.getElemeAccountType(), "eleme.user.getUser", null);

        return new ApiRest(result, "获取商户账号信息成功！");
    }
}
