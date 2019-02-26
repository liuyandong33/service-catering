package build.dream.catering.services;

import build.dream.catering.constants.Constants;
import build.dream.catering.models.dietorder.*;
import build.dream.catering.utils.DietOrderUtils;
import build.dream.catering.utils.ThreadUtils;
import build.dream.common.api.ApiRest;
import build.dream.common.auth.AbstractUserDetails;
import build.dream.common.auth.CustomUserDetails;
import build.dream.common.auth.VipUserDetails;
import build.dream.common.catering.domains.*;
import build.dream.common.constants.DietOrderConstants;
import build.dream.common.models.alipay.AlipayTradeAppPayModel;
import build.dream.common.models.alipay.AlipayTradePagePayModel;
import build.dream.common.models.alipay.AlipayTradePayModel;
import build.dream.common.models.alipay.AlipayTradeWapPayModel;
import build.dream.common.models.aliyunpush.PushMessageToAndroidModel;
import build.dream.common.models.weixinpay.MicroPayModel;
import build.dream.common.models.weixinpay.UnifiedOrderModel;
import build.dream.common.saas.domains.SystemUser;
import build.dream.common.saas.domains.Tenant;
import build.dream.common.utils.*;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.DocumentException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
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
        dietOrderSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUAL, dietOrderId);
        dietOrderSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        dietOrderSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        DietOrder dietOrder = DatabaseHelper.find(DietOrder.class, dietOrderSearchModel);
        ValidateUtils.notNull(dietOrder, "订单不存在！");

        // 查询出订单组信息
        SearchModel dietOrderGroupSearchModel = new SearchModel(true);
        dietOrderGroupSearchModel.addSearchCondition("diet_order_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, dietOrderId);
        dietOrderGroupSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        dietOrderGroupSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        List<DietOrderGroup> dietOrderGroups = DatabaseHelper.findAll(DietOrderGroup.class, dietOrderGroupSearchModel);

        // 查询出订单详情信息
        SearchModel dietOrderDetailSearchModel = new SearchModel(true);
        dietOrderDetailSearchModel.addSearchCondition("diet_order_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, dietOrderId);
        dietOrderDetailSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        dietOrderDetailSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        List<DietOrderDetail> dietOrderDetails = DatabaseHelper.findAll(DietOrderDetail.class, dietOrderDetailSearchModel);

        // 查询出订单口味信息
        SearchModel dietOrderDetailGoodsAttributeSearchModel = new SearchModel(true);
        dietOrderDetailGoodsAttributeSearchModel.addSearchCondition("diet_order_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, dietOrderId);
        dietOrderDetailGoodsAttributeSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        dietOrderDetailGoodsAttributeSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        List<DietOrderDetailGoodsAttribute> dietOrderDetailGoodsAttributes = DatabaseHelper.findAll(DietOrderDetailGoodsAttribute.class, dietOrderDetailGoodsAttributeSearchModel);

        SearchModel dietOrderActivitySearchModel = new SearchModel(true);
        dietOrderActivitySearchModel.addSearchCondition("diet_order_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, dietOrderId);
        dietOrderActivitySearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        dietOrderActivitySearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
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
            CustomUserDetails customUserDetails = (CustomUserDetails) abstractUserDetails;
            Tenant tenant = customUserDetails.getTenant();
            SystemUser systemUser = customUserDetails.getSystemUser();
            saveDietOrderModel.setTenantId(tenant.getId());
            saveDietOrderModel.setTenantCode(tenant.getCode());
            saveDietOrderModel.setUserId(systemUser.getId());
        } else if (Constants.CLIENT_TYPE_O2O.equals(clientType)) {
            VipUserDetails vipUserDetails = (VipUserDetails) abstractUserDetails;
            Tenant tenant = vipUserDetails.getTenant();
            Vip vip = vipUserDetails.getVip();
            saveDietOrderModel.setTenantId(tenant.getId());
            saveDietOrderModel.setTenantCode(tenant.getCode());
            saveDietOrderModel.setVipId(vip.getId());
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
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        searchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        searchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUAL, orderId);
        DietOrder dietOrder = DatabaseHelper.find(DietOrder.class, searchModel);
        ValidateUtils.notNull(dietOrder, "订单不存在！");
        ValidateUtils.isTrue(dietOrder.getOrderStatus() == DietOrderConstants.ORDER_STATUS_UNPROCESSED, "只有未处理的订单才能进行接单操作！");

        dietOrder.setOrderStatus(DietOrderConstants.ORDER_STATUS_VALID);
        DatabaseHelper.update(dietOrder);

        return ApiRest.builder().message("接单成功！").successful(true).build();
    }

    @Transactional(rollbackFor = Exception.class)
    public ApiRest cancelOrder(CancelOrderModel cancelOrderModel) throws IOException, DocumentException {
        BigInteger tenantId = cancelOrderModel.obtainTenantId();
        BigInteger branchId = cancelOrderModel.obtainBranchId();
        BigInteger orderId = cancelOrderModel.getOrderId();

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        searchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        searchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUAL, orderId);
        DietOrder dietOrder = DatabaseHelper.find(DietOrder.class, searchModel);
        ValidateUtils.notNull(dietOrder, "订单不存在！");

        DietOrderUtils.recoveryStock(dietOrder);
        DietOrderUtils.refund(dietOrder);

        dietOrder.setOrderStatus(DietOrderConstants.ORDER_STATUS_INVALID);
        DatabaseHelper.update(dietOrder);

        return ApiRest.builder().message("取消订单成功").successful(true).build();
    }

    @Transactional(readOnly = true)
    public ApiRest doPay(DoPayModel doPayModel) throws DocumentException {
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
        ValidateUtils.isTrue(dietOrder.getOrderStatus() == DietOrderConstants.ORDER_STATUS_PENDING, "订单状态异常！");

        String orderNumber = dietOrder.getOrderNumber();
        BigDecimal payableAmount = dietOrder.getPayableAmount();
        String partitionCode = ConfigurationUtils.getConfiguration(Constants.PARTITION_CODE);
        String serviceDomain = CommonUtils.getServiceDomain(partitionCode, Constants.SERVICE_NAME_CATERING);

        Object result = null;
        if (paidScene == Constants.PAID_SCENE_WEI_XIN_MICROPAY) {
            MicroPayModel microPayModel = MicroPayModel.builder()
                    .tenantId(tenantId.toString())
                    .branchId(branchId.toString())
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
                tradeType = Constants.WEI_XIN_PAY_TRADE_TYPE_MINI_PROGRAM;
            }
            UnifiedOrderModel unifiedOrderModel = UnifiedOrderModel.builder()
                    .tenantId(tenantId.toString())
                    .branchId(branchId.toString())
                    .signType(Constants.MD5)
                    .body("订单支付")
                    .outTradeNo(orderNumber)
                    .totalFee(payableAmount.multiply(Constants.BIG_DECIMAL_ONE_HUNDRED).intValue())
                    .spbillCreateIp(ApplicationHandler.getRemoteAddress())
                    .notifyUrl(serviceDomain + "/dietOrder/weiXinPayCallback")
                    .tradeType(tradeType)
                    .openId(openId)
                    .subOpenId(subOpenId)
                    .build();
            result = WeiXinPayUtils.unifiedOrder(unifiedOrderModel);
        } else if (paidScene == Constants.PAID_SCENE_ALIPAY_MOBILE_WEBSITE) {
            String returnUrl = "";

            AlipayTradeWapPayModel alipayTradeWapPayModel = AlipayTradeWapPayModel.builder()
                    .tenantId(tenantId.toString())
                    .branchId(branchId.toString())
                    .returnUrl(returnUrl)
                    .notifyUrl(serviceDomain + "/dietOrder/alipayCallback")
                    .subject("订单支付")
                    .outTradeNo(orderNumber)
                    .totalAmount(payableAmount)
                    .productCode(orderNumber)
                    .build();
            result = AlipayUtils.alipayTradeWapPay(alipayTradeWapPayModel);
        } else if (paidScene == Constants.PAID_SCENE_ALIPAY_PC_WEBSITE) {
            String returnUrl = "";

            AlipayTradePagePayModel alipayTradePagePayModel = AlipayTradePagePayModel.builder()
                    .tenantId(tenantId.toString())
                    .branchId(branchId.toString())
                    .returnUrl(returnUrl)
                    .notifyUrl(serviceDomain + "/dietOrder/alipayCallback")
                    .outTradeNo(orderNumber)
                    .productCode(orderNumber)
                    .totalAmount(payableAmount)
                    .subject("订单支付")
                    .build();
            result = AlipayUtils.alipayTradePagePay(alipayTradePagePayModel);
        } else if (paidScene == Constants.PAID_SCENE_ALIPAY_APP) {
            AlipayTradeAppPayModel alipayTradeAppPayModel = AlipayTradeAppPayModel.builder()
                    .tenantId(tenantId.toString())
                    .branchId(branchId.toString())
                    .notifyUrl(serviceDomain + "/dietOrder/alipayCallback")
                    .outTradeNo(orderNumber)
                    .totalAmount(payableAmount)
                    .subject("订单支付")
                    .build();
            result = AlipayUtils.alipayTradeAppPay(alipayTradeAppPayModel);
        } else if (paidScene == Constants.PAID_SCENE_ALIPAY_FAC_TO_FACE) {
            AlipayTradePayModel alipayTradePayModel = AlipayTradePayModel.builder()
                    .tenantId(tenantId.toString())
                    .branchId(branchId.toString())
                    .notifyUrl(serviceDomain + "/dietOrder/alipayCallback")
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
        dietOrderSearchModel.addSearchCondition("order_number", Constants.SQL_OPERATION_SYMBOL_EQUAL, orderNumber);
        DietOrder dietOrder = DatabaseHelper.find(DietOrder.class, dietOrderSearchModel);
        ValidateUtils.notNull(dietOrder, "订单不存在！");
        if (dietOrder.getOrderStatus() == DietOrderConstants.PAY_STATUS_PAID) {
            return;
        }

        BigInteger tenantId = dietOrder.getTenantId();
        String tenantCode = dietOrder.getTenantCode();
        BigInteger branchId = dietOrder.getBranchId();

        SearchModel paymentSearchModel = new SearchModel(true);
        paymentSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        paymentSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        paymentSearchModel.addSearchCondition("code", Constants.SQL_OPERATION_SYMBOL_EQUAL, paymentCode);
        Payment payment = DatabaseHelper.find(Payment.class, paymentSearchModel);

        DietOrderPayment dietOrderPayment = DietOrderPayment.builder()
                .tenantId(tenantId)
                .tenantCode(tenantCode)
                .branchId(branchId)
                .dietOrderId(dietOrder.getId())
                .paymentId(payment.getId())
                .paymentCode(payment.getCode())
                .paymentName(payment.getName())
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
        DatabaseHelper.update(dietOrder);
    }

    /**
     * 获取POS订单
     *
     * @param obtainPosOrderModel
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiRest obtainPosOrder(ObtainPosOrderModel obtainPosOrderModel) throws IOException {
        BigInteger tenantId = obtainPosOrderModel.obtainTenantId();
        BigInteger branchId = obtainPosOrderModel.obtainBranchId();
        String tableCode = obtainPosOrderModel.obtainBranchCode();
        BigInteger vipId = obtainPosOrderModel.getVipId();
        PushMessageToAndroidModel pushMessageToAndroidModel = new PushMessageToAndroidModel();
        pushMessageToAndroidModel.setAppKey("");
        pushMessageToAndroidModel.setTarget(AliyunPushUtils.TAG);
        pushMessageToAndroidModel.setTargetValue("POS" + tenantId + "_" + branchId);
        pushMessageToAndroidModel.setTitle("获取POS订单");

        Map<String, Object> body = new HashMap<String, Object>();
        body.put("code", "");

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("tableCode", tableCode);

        String uuid = UUID.randomUUID().toString();
        map.put("uuid", uuid);

        body.put("data", map);

        pushMessageToAndroidModel.setBody(GsonUtils.toJson(body));
        Map<String, Object> result = AliyunPushUtils.pushMessageToAndroid(pushMessageToAndroidModel);

        String dataJson = null;
        int times = 0;
        while (times < 120) {
            times += 1;
            dataJson = CacheUtils.get(uuid);
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

        if (StringUtils.isNotBlank(orderDetailGoodsAttributes)) {
            List<DietOrderDetailGoodsAttribute> dietOrderDetailGoodsAttributes = JacksonUtils.readValueAsList(orderDetailGoodsAttributes, DietOrderDetailGoodsAttribute.class);
            for (DietOrderDetailGoodsAttribute dietOrderDetailGoodsAttribute : dietOrderDetailGoodsAttributes) {
                DietOrderGroup dietOrderGroup = dietOrderGroupMap.get(dietOrderDetailGoodsAttribute.getLocalDietOrderGroupId());
                DietOrderDetail dietOrderDetail = dietOrderDetailMap.get(dietOrderDetailGoodsAttribute.getLocalDietOrderDetailId());

                dietOrderDetailGoodsAttribute.setDietOrderId(dietOrderId);
                dietOrderDetailGoodsAttribute.setDietOrderGroupId(dietOrderGroup.getId());
                dietOrderDetailGoodsAttribute.setDietOrderDetailId(dietOrderDetail.getId());
            }
            DatabaseHelper.insertAll(dietOrderDetailGoodsAttributes);
        }

        return ApiRest.builder().data(dataMap).message("获取POS订单成功！").successful(true).build();
    }
}
