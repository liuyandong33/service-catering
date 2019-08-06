package build.dream.catering.services;

import build.dream.catering.constants.Constants;
import build.dream.catering.models.dietorder.*;
import build.dream.catering.utils.DietOrderUtils;
import build.dream.catering.utils.ThreadUtils;
import build.dream.catering.utils.VipUtils;
import build.dream.common.api.ApiRest;
import build.dream.common.auth.AbstractUserDetails;
import build.dream.common.auth.TenantUserDetails;
import build.dream.common.auth.VipUserDetails;
import build.dream.common.beans.KafkaFixedTimeSendResult;
import build.dream.common.catering.domains.*;
import build.dream.common.constants.DietOrderConstants;
import build.dream.common.models.alipay.AlipayTradeAppPayModel;
import build.dream.common.models.alipay.AlipayTradePagePayModel;
import build.dream.common.models.alipay.AlipayTradePayModel;
import build.dream.common.models.alipay.AlipayTradeWapPayModel;
import build.dream.common.models.aliyunpush.PushMessageModel;
import build.dream.common.models.weixinpay.MicroPayModel;
import build.dream.common.models.weixinpay.UnifiedOrderModel;
import build.dream.common.saas.domains.Tenant;
import build.dream.common.saas.domains.WeiXinPayAccount;
import build.dream.common.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class DietOrderService {
    /**
     * 获取订单明细
     *
     * @param obtainDietOrderInfoModel
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest obtainDietOrderInfo(ObtainDietOrderInfoModel obtainDietOrderInfoModel) {
        // 查询出订单信息
        BigInteger tenantId = obtainDietOrderInfoModel.obtainTenantId();
        BigInteger branchId = obtainDietOrderInfoModel.obtainBranchId();
        BigInteger dietOrderId = obtainDietOrderInfoModel.getDietOrderId();
        SearchModel dietOrderSearchModel = new SearchModel(true);
        dietOrderSearchModel.addSearchCondition(DietOrder.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, dietOrderId);
        dietOrderSearchModel.addSearchCondition(DietOrder.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        dietOrderSearchModel.addSearchCondition(DietOrder.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        DietOrder dietOrder = DatabaseHelper.find(DietOrder.class, dietOrderSearchModel);
        ValidateUtils.notNull(dietOrder, "订单不存在！");

        // 查询出订单组信息
        SearchModel dietOrderGroupSearchModel = new SearchModel(true);
        dietOrderGroupSearchModel.addSearchCondition(DietOrderGroup.ColumnName.DIET_ORDER_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, dietOrderId);
        dietOrderGroupSearchModel.addSearchCondition(DietOrderGroup.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        dietOrderGroupSearchModel.addSearchCondition(DietOrderGroup.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        List<DietOrderGroup> dietOrderGroups = DatabaseHelper.findAll(DietOrderGroup.class, dietOrderGroupSearchModel);

        // 查询出订单详情信息
        SearchModel dietOrderDetailSearchModel = new SearchModel(true);
        dietOrderDetailSearchModel.addSearchCondition(DietOrderDetail.ColumnName.DIET_ORDER_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, dietOrderId);
        dietOrderDetailSearchModel.addSearchCondition(DietOrderDetail.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        dietOrderDetailSearchModel.addSearchCondition(DietOrderDetail.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        List<DietOrderDetail> dietOrderDetails = DatabaseHelper.findAll(DietOrderDetail.class, dietOrderDetailSearchModel);

        // 查询出订单口味信息
        SearchModel dietOrderDetailGoodsAttributeSearchModel = new SearchModel(true);
        dietOrderDetailGoodsAttributeSearchModel.addSearchCondition(DietOrderDetailGoodsAttribute.ColumnName.DIET_ORDER_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, dietOrderId);
        dietOrderDetailGoodsAttributeSearchModel.addSearchCondition(DietOrderDetailGoodsAttribute.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        dietOrderDetailGoodsAttributeSearchModel.addSearchCondition(DietOrderDetailGoodsAttribute.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        List<DietOrderDetailGoodsAttribute> dietOrderDetailGoodsAttributes = DatabaseHelper.findAll(DietOrderDetailGoodsAttribute.class, dietOrderDetailGoodsAttributeSearchModel);

        SearchModel dietOrderActivitySearchModel = new SearchModel(true);
        dietOrderActivitySearchModel.addSearchCondition(DietOrderActivity.ColumnName.DIET_ORDER_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, dietOrderId);
        dietOrderActivitySearchModel.addSearchCondition(DietOrderActivity.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        dietOrderActivitySearchModel.addSearchCondition(DietOrderActivity.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        List<DietOrderActivity> dietOrderActivities = DatabaseHelper.findAll(DietOrderActivity.class, dietOrderActivitySearchModel);

        Map<String, Object> dietOrderInfo = DietOrderUtils.buildDietOrderInfo(dietOrder, dietOrderGroups, dietOrderDetails, dietOrderDetailGoodsAttributes, dietOrderActivities);
        return ApiRest.builder().data(dietOrderInfo).message("获取订单信息成功！").successful(true).build();
    }

    /**
     * 保存订单信息
     *
     * @param saveDietOrderModel
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiRest saveDietOrder(SaveDietOrderModel saveDietOrderModel) {
        AbstractUserDetails abstractUserDetails = WebSecurityUtils.obtainUserDetails();
        String clientType = abstractUserDetails.getClientType();
        if (Constants.CLIENT_TYPE_POS.equals(clientType) || Constants.CLIENT_TYPE_APP.equals(clientType) || Constants.CLIENT_TYPE_WEB.equals(clientType)) {
            TenantUserDetails tenantUserDetails = (TenantUserDetails) abstractUserDetails;
            saveDietOrderModel.setTenantId(tenantUserDetails.getTenantId());
            saveDietOrderModel.setTenantCode(tenantUserDetails.getTenantCode());
            saveDietOrderModel.setUserId(tenantUserDetails.getUserId());
        } else if (Constants.CLIENT_TYPE_O2O.equals(clientType)) {
            VipUserDetails vipUserDetails = (VipUserDetails) abstractUserDetails;
            saveDietOrderModel.setTenantId(vipUserDetails.getTenantId());
            saveDietOrderModel.setTenantCode(vipUserDetails.getTenantCode());
            saveDietOrderModel.setVipId(vipUserDetails.getVipId());
        }

        DietOrder dietOrder = DietOrderUtils.saveDietOrder(saveDietOrderModel);
        return ApiRest.builder().data(dietOrder).message("保存订单成功！").successful(true).build();
    }

    @Transactional(rollbackFor = Exception.class)
    public ApiRest confirmOrder(ConfirmOrderModel confirmOrderModel) {
        BigInteger tenantId = confirmOrderModel.obtainTenantId();
        BigInteger branchId = confirmOrderModel.obtainBranchId();
        BigInteger orderId = confirmOrderModel.getOrderId();

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition(DietOrder.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        searchModel.addSearchCondition(DietOrder.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        searchModel.addSearchCondition(DietOrder.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, orderId);
        DietOrder dietOrder = DatabaseHelper.find(DietOrder.class, searchModel);
        ValidateUtils.notNull(dietOrder, "订单不存在！");
        ValidateUtils.isTrue(dietOrder.getOrderStatus() == DietOrderConstants.ORDER_STATUS_UNPROCESSED, "只有未处理的订单才能进行接单操作！");
        ValidateUtils.isTrue(new Date().getTime() - dietOrder.getActiveTime().getTime() <= 5 * 60 * 100, "订单已超时！");

        DietOrderUtils.stopOrderInvalidJob(dietOrder.getJobId(), dietOrder.getTriggerId());
        dietOrder.setJobId(Constants.VARCHAR_DEFAULT_VALUE);
        dietOrder.setTriggerId(Constants.VARCHAR_DEFAULT_VALUE);
        dietOrder.setOrderStatus(DietOrderConstants.ORDER_STATUS_VALID);
        DatabaseHelper.update(dietOrder);

        return ApiRest.builder().message("接单成功！").successful(true).build();
    }

    /**
     * 商户拒单取消订单
     *
     * @param cancelOrderModel
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiRest cancelOrder(CancelOrderModel cancelOrderModel) {
        BigInteger tenantId = cancelOrderModel.obtainTenantId();
        BigInteger branchId = cancelOrderModel.obtainBranchId();
        BigInteger orderId = cancelOrderModel.getOrderId();
        DietOrderUtils.cancelOrder(tenantId, branchId, orderId, 2);
        return ApiRest.builder().message("取消订单成功").successful(true).build();
    }

    /**
     * @param tenantId: 商户ID
     * @param branchId: 门店ID
     * @param orderId:  订单ID
     * @param type:     类型，1-超时未付款自动取消，3-超时未接单自动取消
     */
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(BigInteger tenantId, BigInteger branchId, BigInteger orderId, int type) {
        DietOrderUtils.cancelOrder(tenantId, branchId, orderId, type);
    }

    @Transactional(readOnly = true)
    public ApiRest doPay(DoPayModel doPayModel) {
        BigInteger tenantId = doPayModel.getTenantId();
        BigInteger branchId = doPayModel.getBranchId();
        BigInteger dietOrderId = doPayModel.getDietOrderId();
        Integer paidScene = doPayModel.getPaidScene();
        String authCode = doPayModel.getAuthCode();
        String openId = doPayModel.getOpenId();
        String subOpenId = doPayModel.getSubOpenId();

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition(DietOrder.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        searchModel.addSearchCondition(DietOrder.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        searchModel.addSearchCondition(DietOrder.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, dietOrderId);
        DietOrder dietOrder = DatabaseHelper.find(DietOrder.class, searchModel);
        ValidateUtils.notNull(dietOrder, "订单不存在！");
        ValidateUtils.isTrue(dietOrder.getOrderStatus() == DietOrderConstants.ORDER_STATUS_PENDING && dietOrder.getOrderStatus() == DietOrderConstants.ORDER_STATUS_PENDING, "订单状态异常！");
        ValidateUtils.isTrue(new Date().getTime() - dietOrder.getCreatedTime().getTime() <= 15 * 60 * 1000, "订单已超时！");

        String orderNumber = dietOrder.getOrderNumber();
        BigDecimal payableAmount = dietOrder.getPayableAmount();
        String partitionCode = ConfigurationUtils.getConfiguration(Constants.PARTITION_CODE);

        Object result = null;
        if (paidScene == Constants.PAID_SCENE_WEI_XIN_MICROPAY) {
            WeiXinPayAccount weiXinPayAccount = WeiXinPayUtils.obtainWeiXinPayAccount(tenantId.toString(), branchId.toString());
            MicroPayModel microPayModel = MicroPayModel.builder()
                    .appId(weiXinPayAccount.getAppId())
                    .subMchId(weiXinPayAccount.getSubMchId())
                    .apiSecretKey(weiXinPayAccount.getApiSecretKey())
                    .subAppId(weiXinPayAccount.getSubPublicAccountAppId())
                    .subMchId(weiXinPayAccount.getSubMchId())
                    .acceptanceModel(weiXinPayAccount.isAcceptanceModel())
                    .signType(Constants.MD5)
                    .body("订单支付")
                    .outTradeNo(orderNumber)
                    .totalFee(payableAmount.multiply(Constants.BIG_DECIMAL_ONE_HUNDRED).intValue())
                    .spbillCreateIp(ApplicationHandler.getRemoteAddress())
                    .authCode(authCode)
                    .build();
            result = WeiXinPayUtils.microPay(microPayModel);
        } else if (paidScene == Constants.PAID_SCENE_WEI_XIN_JSAPI_PUBLIC_ACCOUNT || paidScene == Constants.PAID_SCENE_WEI_XIN_NATIVE || paidScene == Constants.PAID_SCENE_WEI_XIN_APP || paidScene == Constants.PAID_SCENE_WEI_XIN_MWEB || paidScene == Constants.PAID_SCENE_WEI_XIN_JSAPI_MINI_PROGRAM) {
            String tradeType = null;
            if (paidScene == Constants.PAID_SCENE_WEI_XIN_JSAPI_PUBLIC_ACCOUNT) {
                tradeType = Constants.WEI_XIN_PAY_TRADE_TYPE_JSAPI;
            } else if (paidScene == Constants.PAID_SCENE_WEI_XIN_NATIVE) {
                tradeType = Constants.WEI_XIN_PAY_TRADE_TYPE_NATIVE;
            } else if (paidScene == Constants.PAID_SCENE_WEI_XIN_APP) {
                tradeType = Constants.WEI_XIN_PAY_TRADE_TYPE_APP;
            } else if (paidScene == Constants.PAID_SCENE_WEI_XIN_MWEB) {
                tradeType = Constants.WEI_XIN_PAY_TRADE_TYPE_MWEB;
            } else if (paidScene == Constants.PAID_SCENE_WEI_XIN_JSAPI_MINI_PROGRAM) {
                tradeType = Constants.WEI_XIN_PAY_TRADE_TYPE_JSAPI;
            }
            WeiXinPayAccount weiXinPayAccount = WeiXinPayUtils.obtainWeiXinPayAccount(tenantId.toString(), branchId.toString());
            UnifiedOrderModel unifiedOrderModel = UnifiedOrderModel.builder()
                    .appId(weiXinPayAccount.getAppId())
                    .mchId(weiXinPayAccount.getMchId())
                    .apiSecretKey(weiXinPayAccount.getApiSecretKey())
                    .subAppId(weiXinPayAccount.getSubPublicAccountAppId())
                    .subMchId(weiXinPayAccount.getSubMchId())
                    .acceptanceModel(weiXinPayAccount.isAcceptanceModel())
                    .signType(Constants.MD5)
                    .body("订单支付")
                    .outTradeNo(orderNumber)
                    .totalFee(payableAmount.multiply(Constants.BIG_DECIMAL_ONE_HUNDRED).intValue())
                    .spbillCreateIp(ApplicationHandler.getRemoteAddress())
                    .topic("")
                    .tradeType(tradeType)
                    .openId(openId)
                    .subOpenId(subOpenId)
                    .build();
            result = WeiXinPayUtils.unifiedOrder(unifiedOrderModel);
        } else if (paidScene == Constants.PAID_SCENE_ALIPAY_MOBILE_WEBSITE) {
            String returnUrl = "";

            AlipayTradeWapPayModel alipayTradeWapPayModel = AlipayTradeWapPayModel.builder()
                    .returnUrl(returnUrl)
                    .topic("")
                    .subject("订单支付")
                    .outTradeNo(orderNumber)
                    .totalAmount(payableAmount)
                    .productCode(orderNumber)
                    .build();
            result = AlipayUtils.alipayTradeWapPay(alipayTradeWapPayModel);
        } else if (paidScene == Constants.PAID_SCENE_ALIPAY_PC_WEBSITE) {
            String returnUrl = "";

            AlipayTradePagePayModel alipayTradePagePayModel = AlipayTradePagePayModel.builder()
                    .returnUrl(returnUrl)
                    .topic("")
                    .outTradeNo(orderNumber)
                    .productCode(orderNumber)
                    .totalAmount(payableAmount)
                    .subject("订单支付")
                    .build();
            result = AlipayUtils.alipayTradePagePay(alipayTradePagePayModel);
        } else if (paidScene == Constants.PAID_SCENE_ALIPAY_APP) {
            AlipayTradeAppPayModel alipayTradeAppPayModel = AlipayTradeAppPayModel.builder()
                    .topic("")
                    .outTradeNo(orderNumber)
                    .totalAmount(payableAmount)
                    .subject("订单支付")
                    .build();
            result = AlipayUtils.alipayTradeAppPay(alipayTradeAppPayModel);
        } else if (paidScene == Constants.PAID_SCENE_ALIPAY_FAC_TO_FACE) {
            AlipayTradePayModel alipayTradePayModel = AlipayTradePayModel.builder()
                    .topic("")
                    .outTradeNo(orderNumber)
                    .totalAmount(payableAmount)
                    .scene(Constants.SCENE_BAR_CODE)
                    .authCode(authCode)
                    .subject("订单支付")
                    .build();
            result = AlipayUtils.alipayTradePay(alipayTradePayModel);
        }

        return ApiRest.builder().data(result).message("发起支付成功！").successful(true).build();
    }

    @Transactional(rollbackFor = Exception.class)
    public void handleCallback(Map<String, String> parameters, String paymentCode) throws ParseException {
        String orderNumber = null;
        Date occurrenceTime = null;
        BigDecimal totalAmount = null;
        if (Constants.PAYMENT_CODE_ALIPAY.equals(paymentCode)) {
            orderNumber = parameters.get("out_trade_no");
            occurrenceTime = new SimpleDateFormat(Constants.DEFAULT_DATE_PATTERN).parse(parameters.get("gmt_payment"));
            totalAmount = BigDecimal.valueOf(Double.valueOf(parameters.get("total_amount")));
        } else if (Constants.PAYMENT_CODE_WX.equals(paymentCode)) {
            orderNumber = "";
        }

        SearchModel dietOrderSearchModel = new SearchModel(true);
        dietOrderSearchModel.addSearchCondition(DietOrder.ColumnName.ORDER_NUMBER, Constants.SQL_OPERATION_SYMBOL_EQUAL, orderNumber);
        DietOrder dietOrder = DatabaseHelper.find(DietOrder.class, dietOrderSearchModel);
        ValidateUtils.notNull(dietOrder, "订单不存在！");
        if (dietOrder.getOrderStatus() == DietOrderConstants.PAY_STATUS_PAID) {
            return;
        }

        BigInteger tenantId = dietOrder.getTenantId();
        String tenantCode = dietOrder.getTenantCode();
        BigInteger branchId = dietOrder.getBranchId();

        SearchModel paymentSearchModel = new SearchModel(true);
        paymentSearchModel.addSearchCondition(Payment.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        paymentSearchModel.addSearchCondition(Payment.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        paymentSearchModel.addSearchCondition(Payment.ColumnName.CODE, Constants.SQL_OPERATION_SYMBOL_EQUAL, paymentCode);
        Payment payment = DatabaseHelper.find(Payment.class, paymentSearchModel);

        DietOrderPayment dietOrderPayment = DietOrderPayment.builder()
                .tenantId(tenantId)
                .tenantCode(tenantCode)
                .branchId(branchId)
                .dietOrderId(dietOrder.getId())
                .paymentId(payment.getId())
                .paymentCode(payment.getCode())
                .paymentName(payment.getName())
                .paidAmount(totalAmount)
                .occurrenceTime(occurrenceTime)
                .extraInfo(GsonUtils.toJson(parameters))
                .build();
        DatabaseHelper.insert(dietOrderPayment);

        dietOrder.setPaidAmount(dietOrder.getPaidAmount().add(totalAmount));
        dietOrder.setPayStatus(DietOrderConstants.PAY_STATUS_PAID);
        dietOrder.setOrderStatus(DietOrderConstants.ORDER_STATUS_UNPROCESSED);
        dietOrder.setActiveTime(occurrenceTime);

        BigInteger userId = CommonUtils.getServiceSystemUserId();
        dietOrder.setUpdatedUserId(userId);
        DietOrderUtils.stopOrderInvalidJob(dietOrder.getJobId(), dietOrder.getTriggerId());
        KafkaFixedTimeSendResult kafkaFixedTimeSendResult = DietOrderUtils.startOrderInvalidJob(tenantId, branchId, dietOrder.getId(), 3, DateUtils.addMinutes(occurrenceTime, 5));
        dietOrder.setJobId(kafkaFixedTimeSendResult.getJobId());
        dietOrder.setTriggerId(kafkaFixedTimeSendResult.getTriggerId());
        DatabaseHelper.update(dietOrder);
    }

    /**
     * 获取POS订单
     *
     * @param obtainPosOrderModel
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiRest obtainPosOrder(ObtainPosOrderModel obtainPosOrderModel) {
        BigInteger tenantId = obtainPosOrderModel.obtainTenantId();
        BigInteger branchId = obtainPosOrderModel.obtainBranchId();
        String tableCode = obtainPosOrderModel.obtainBranchCode();
        BigInteger vipId = obtainPosOrderModel.getVipId();

        Map<String, Object> bodyMap = new HashMap<String, Object>();
        bodyMap.put("code", "");

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("tableCode", tableCode);

        String uuid = UUID.randomUUID().toString();
        map.put("uuid", uuid);

        bodyMap.put("data", map);

        String body = JacksonUtils.writeValueAsString(bodyMap);
        PushMessageModel pushMessageModel = PushMessageModel.builder()
                .appKey("")
                .target(AliyunPushUtils.TARGET_TAG)
                .targetValue("pos_" + tenantId + "_" + branchId)
                .title("获取POS订单")
                .body(body)
                .build();
        Map<String, Object> result = AliyunPushUtils.pushMessageToAndroid(pushMessageModel);

        String dataJson = null;
        int times = 0;
        while (times < 120) {
            times += 1;
            dataJson = CommonRedisUtils.get(uuid);
            if (StringUtils.isNotBlank(dataJson)) {
                break;
            }
            ThreadUtils.sleepSafe(500);
        }

        ValidateUtils.notBlank(dataJson, "POS端未响应");

        Map<String, Object> dataMap = JacksonUtils.readValueAsMap(dataJson, String.class, Object.class);

        /*String platformPrivateKey = ConfigurationUtils.getConfiguration(Constants.PLATFORM_PRIVATE_KEY);
        PrivateKey privateKey = RSAUtils.restorePrivateKey(platformPrivateKey);
        String encryptedData = Base64.encodeBase64String(RSAUtils.encryptByPrivateKey(dataJson.getBytes(Constants.CHARSET_NAME_UTF_8), privateKey, PADDING_MODE_RSA_ECB_PKCS1PADDING));
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("order", dataMap.get("order"));
        data.put("encryptedData", encryptedData);*/

        String order = MapUtils.getString(dataMap, "order");
        String orderGroups = MapUtils.getString(dataMap, "orderGroups");
        String orderDetails = MapUtils.getString(dataMap, "orderDetails");
        String orderDetailGoodsAttributes = MapUtils.getString(dataMap, "orderDetailGoodsAttributes");
        String orderActivities = MapUtils.getString(dataMap, "orderActivities");

        DietOrder dietOrder = JacksonUtils.readValue(order, DietOrder.class);
        List<DietOrderGroup> dietOrderGroups = JacksonUtils.readValueAsList(orderGroups, DietOrderGroup.class);
        List<DietOrderDetail> dietOrderDetails = JacksonUtils.readValueAsList(orderDetails, DietOrderDetail.class);
        DatabaseHelper.insert(dietOrder);

        BigInteger dietOrderId = dietOrder.getId();

        Map<String, DietOrderGroup> dietOrderGroupMap = new HashMap<String, DietOrderGroup>();
        for (DietOrderGroup dietOrderGroup : dietOrderGroups) {
            dietOrderGroupMap.put(dietOrder.getLocalId(), dietOrderGroup);
            dietOrderGroup.setDietOrderId(dietOrderId);
        }

        DatabaseHelper.insertAll(dietOrderGroups);

        Map<String, DietOrderDetail> dietOrderDetailMap = new HashMap<String, DietOrderDetail>();
        for (DietOrderDetail dietOrderDetail : dietOrderDetails) {
            DietOrderGroup dietOrderGroup = dietOrderGroupMap.get(dietOrderDetail.getLocalDietOrderGroupId());
            dietOrderDetail.setDietOrderId(dietOrderId);
            dietOrderDetail.setDietOrderGroupId(dietOrderGroup.getId());
            dietOrderDetailMap.put(dietOrderDetail.getLocalId(), dietOrderDetail);
        }

        DatabaseHelper.insertAll(dietOrderDetails);

        List<DietOrderDetailGoodsAttribute> dietOrderDetailGoodsAttributes = null;
        if (StringUtils.isNotBlank(orderDetailGoodsAttributes)) {
            dietOrderDetailGoodsAttributes = JacksonUtils.readValueAsList(orderDetailGoodsAttributes, DietOrderDetailGoodsAttribute.class);
            for (DietOrderDetailGoodsAttribute dietOrderDetailGoodsAttribute : dietOrderDetailGoodsAttributes) {
                DietOrderGroup dietOrderGroup = dietOrderGroupMap.get(dietOrderDetailGoodsAttribute.getLocalDietOrderGroupId());
                DietOrderDetail dietOrderDetail = dietOrderDetailMap.get(dietOrderDetailGoodsAttribute.getLocalDietOrderDetailId());

                dietOrderDetailGoodsAttribute.setDietOrderId(dietOrderId);
                dietOrderDetailGoodsAttribute.setDietOrderGroupId(dietOrderGroup.getId());
                dietOrderDetailGoodsAttribute.setDietOrderDetailId(dietOrderDetail.getId());
            }
            DatabaseHelper.insertAll(dietOrderDetailGoodsAttributes);
        }

        List<DietOrderActivity> dietOrderActivities = null;
        if (StringUtils.isNotBlank(orderActivities)) {
            dietOrderActivities = JacksonUtils.readValueAsList(orderActivities, DietOrderActivity.class);
            for (DietOrderActivity dietOrderActivity : dietOrderActivities) {
                dietOrderActivity.setDietOrderId(dietOrderId);
            }
            DatabaseHelper.insertAll(dietOrderActivities);
        }

        Map<String, Object> dietOrderInfo = DietOrderUtils.buildDietOrderInfo(dietOrder, dietOrderGroups, dietOrderDetails, dietOrderDetailGoodsAttributes, dietOrderActivities);
        return ApiRest.builder().data(dietOrderInfo).message("获取POS订单成功！").successful(true).build();
    }

    /**
     * 组合付款
     *
     * @param doPayCombinedModel
     * @return
     */
    public ApiRest doPayCombined(DoPayCombinedModel doPayCombinedModel) {
        BigInteger tenantId = doPayCombinedModel.getTenantId();
        BigInteger branchId = doPayCombinedModel.getBranchId();
        BigInteger vipId = doPayCombinedModel.getVipId();
        BigInteger dietOrderId = doPayCombinedModel.getDietOrderId();
        List<DoPayCombinedModel.PaymentInfo> paymentInfos = doPayCombinedModel.getPaymentInfos();

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition(DietOrder.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        searchModel.addSearchCondition(DietOrder.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        searchModel.addSearchCondition(DietOrder.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, dietOrderId);
        DietOrder dietOrder = DatabaseHelper.find(DietOrder.class, searchModel);
        ValidateUtils.notNull(dietOrder, "订单不存在！");
        ValidateUtils.isTrue(dietOrder.getOrderStatus() == DietOrderConstants.ORDER_STATUS_PENDING && dietOrder.getOrderStatus() == DietOrderConstants.ORDER_STATUS_PENDING, "订单状态异常！");
        ValidateUtils.isTrue(new Date().getTime() - dietOrder.getCreatedTime().getTime() <= 15 * 60 * 1000, "订单已超时！");

        Vip vip = VipUtils.find(tenantId, vipId);
        ValidateUtils.notNull(vip, "会员不存在！");
        BigDecimal total = BigDecimal.ZERO;

        List<String> paymentCodes = new ArrayList<String>();
        for (DoPayCombinedModel.PaymentInfo paymentInfo : paymentInfos) {
            total = total.add(paymentInfo.getPaidAmount());
            paymentCodes.add(paymentInfo.getPaymentCode());
        }
        ValidateUtils.isTrue(total.compareTo(dietOrder.getPayableAmount()) == 0, "付款金额与订单金额不符！");
        SearchModel paymentSearchModel = new SearchModel(true);
        paymentSearchModel.addSearchCondition(Payment.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        paymentSearchModel.addSearchCondition(Payment.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        paymentSearchModel.addSearchCondition(Payment.ColumnName.CODE, Constants.SQL_OPERATION_SYMBOL_IN, paymentCodes);
        List<Payment> payments = DatabaseHelper.findAll(Payment.class, paymentSearchModel);
        Map<String, Payment> paymentMap = new HashMap<String, Payment>();
        for (Payment payment : payments) {
            paymentMap.put(payment.getCode(), payment);
        }

        Date now = new Date();
        for (DoPayCombinedModel.PaymentInfo paymentInfo : paymentInfos) {
            String paymentCode = paymentInfo.getPaymentCode();
            BigDecimal paidAmount = paymentInfo.getPaidAmount();
            if (Constants.PAYMENT_CODE_HYJF.equals(paymentCode)) {
                Tenant tenant = TenantUtils.obtainTenantInfo(tenantId);
                VipAccount vipAccount = VipUtils.obtainVipAccount(tenantId, branchId, vipId, tenant.getVipSharedType());
                VipType vipType = VipUtils.obtainVipType(tenantId, vipAccount.getVipTypeId());
                int bonusCoefficient = vipType.getBonusCoefficient();
                BigDecimal point = paidAmount.multiply(BigDecimal.valueOf(bonusCoefficient));
                VipUtils.deductingVipPoint(tenantId, branchId, vipId, point);

                Payment payment = paymentMap.get(paymentCode);
                DietOrderPayment dietOrderPayment = DietOrderPayment.builder()
                        .tenantId(tenantId)
                        .branchId(branchId)
                        .tenantCode(tenant.getCode())
                        .dietOrderId(dietOrderId)
                        .paymentId(payment.getId())
                        .paymentCode(payment.getCode())
                        .paymentName(payment.getName())
                        .paidAmount(paidAmount)
                        .occurrenceTime(now)
                        .extraInfo(String.valueOf(bonusCoefficient))
                        .build();
                DatabaseHelper.insert(dietOrderPayment);
            } else if (Constants.PAYMENT_CODE_HYQB.equals(paymentCode)) {
                VipUtils.deductingVipBalance(tenantId, branchId, vipId, paymentInfo.getPaidAmount());
                Payment payment = paymentMap.get(paymentCode);

                Tenant tenant = TenantUtils.obtainTenantInfo(tenantId);
                DietOrderPayment dietOrderPayment = DietOrderPayment.builder()
                        .tenantId(tenantId)
                        .branchId(branchId)
                        .tenantCode(tenant.getCode())
                        .dietOrderId(dietOrderId)
                        .paymentId(payment.getId())
                        .paymentCode(payment.getCode())
                        .paymentName(payment.getName())
                        .paidAmount(paidAmount)
                        .occurrenceTime(now)
                        .build();
                DatabaseHelper.insert(dietOrderPayment);
            } else if (Constants.PAYMENT_CODE_ALIPAY.equals(paymentCode)) {

            } else if (Constants.PAYMENT_CODE_WX.equals(paymentCode)) {

            }
        }
        return ApiRest.builder().build();
    }

    /**
     * 拉取订单信息
     *
     * @param pullOrderModel
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest pullOrder(PullOrderModel pullOrderModel) {
        BigInteger tenantId = pullOrderModel.obtainTenantId();
        BigInteger branchId = pullOrderModel.obtainBranchId();
        BigInteger orderId = pullOrderModel.getOrderId();

        SearchModel dietOrderSearchModel = SearchModel.builder()
                .equal(DietOrder.ColumnName.TENANT_ID, tenantId)
                .equal(DietOrder.ColumnName.BRANCH_ID, branchId)
                .equal(DietOrder.ColumnName.ID, orderId)
                .build();
        DietOrder dietOrder = DatabaseHelper.find(DietOrder.class, dietOrderSearchModel);
        ValidateUtils.notNull(dietOrder, "订单不存在！");

        SearchModel dietOrderGroupSearchModel = SearchModel.builder()
                .equal(DietOrderGroup.ColumnName.TENANT_ID, tenantId)
                .equal(DietOrderGroup.ColumnName.BRANCH_ID, branchId)
                .equal(DietOrderGroup.ColumnName.DIET_ORDER_ID, orderId)
                .build();
        List<DietOrderGroup> dietOrderGroups = DatabaseHelper.findAll(DietOrderGroup.class, dietOrderGroupSearchModel);

        SearchModel dietOrderDetailSearchModel = SearchModel.builder()
                .equal(DietOrderDetail.ColumnName.TENANT_ID, tenantId)
                .equal(DietOrderDetail.ColumnName.BRANCH_ID, branchId)
                .equal(DietOrderDetail.ColumnName.DIET_ORDER_ID, orderId)
                .build();
        List<DietOrderDetail> dietOrderDetails = DatabaseHelper.findAll(DietOrderDetail.class, dietOrderDetailSearchModel);

        SearchModel dietOrderDetailGoodsAttributeSearchModel = SearchModel.builder()
                .equal(DietOrderDetailGoodsAttribute.ColumnName.TENANT_ID, tenantId)
                .equal(DietOrderDetailGoodsAttribute.ColumnName.BRANCH_ID, branchId)
                .equal(DietOrderDetailGoodsAttribute.ColumnName.DIET_ORDER_ID, orderId)
                .build();
        List<DietOrderDetailGoodsAttribute> dietOrderDetailGoodsAttributes = DatabaseHelper.findAll(DietOrderDetailGoodsAttribute.class, dietOrderDetailGoodsAttributeSearchModel);

        SearchModel dietOrderActivityAttributeSearchModel = SearchModel.builder()
                .equal(DietOrderActivity.ColumnName.TENANT_ID, tenantId)
                .equal(DietOrderActivity.ColumnName.BRANCH_ID, branchId)
                .equal(DietOrderActivity.ColumnName.DIET_ORDER_ID, orderId)
                .build();
        List<DietOrderActivity> dietOrderActivities = DatabaseHelper.findAll(DietOrderActivity.class, dietOrderActivityAttributeSearchModel);

        SearchModel dietOrderPaymentAttributeSearchModel = SearchModel.builder()
                .equal(DietOrderPayment.ColumnName.TENANT_ID, tenantId)
                .equal(DietOrderPayment.ColumnName.BRANCH_ID, branchId)
                .equal(DietOrderPayment.ColumnName.DIET_ORDER_ID, orderId)
                .build();
        List<DietOrderPayment> dietOrderPayments = DatabaseHelper.findAll(DietOrderPayment.class, dietOrderPaymentAttributeSearchModel);

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("dietOrder", dietOrder);
        data.put("dietOrderGroups", dietOrderGroups);
        data.put("dietOrderDetails", dietOrderDetails);
        if (CollectionUtils.isNotEmpty(dietOrderDetailGoodsAttributes)) {
            data.put("dietOrderDetailGoodsAttributes", dietOrderDetailGoodsAttributes);
        }
        if (CollectionUtils.isNotEmpty(dietOrderActivities)) {
            data.put("dietOrderActivities", dietOrderActivities);
        }
        if (CollectionUtils.isNotEmpty(dietOrderPayments)) {
            data.put("dietOrderPayments", dietOrderPayments);
        }
        return ApiRest.builder().data(data).message("拉取订单成功！").successful(true).build();
    }
}
