package build.dream.catering.services;

import build.dream.catering.constants.Constants;
import build.dream.catering.models.pos.*;
import build.dream.catering.utils.SequenceUtils;
import build.dream.common.api.ApiRest;
import build.dream.common.catering.domains.OfflinePayLog;
import build.dream.common.catering.domains.OfflinePayRecord;
import build.dream.common.catering.domains.Pos;
import build.dream.common.models.alipay.AlipayTradePayModel;
import build.dream.common.models.alipay.AlipayTradeRefundModel;
import build.dream.common.models.miya.OrderPayModel;
import build.dream.common.models.newland.BarcodePayModel;
import build.dream.common.models.weixinpay.MicroPayModel;
import build.dream.common.saas.domains.*;
import build.dream.common.utils.*;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class PosService {
    /**
     * 上线POS
     *
     * @param onlinePosModel
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiRest onlinePos(OnlinePosModel onlinePosModel) {
        BigInteger tenantId = onlinePosModel.obtainTenantId();
        String tenantCode = onlinePosModel.obtainTenantCode();
        BigInteger branchId = onlinePosModel.obtainBranchId();
        String branchCode = onlinePosModel.obtainBranchCode();
        BigInteger userId = onlinePosModel.obtainUserId();
        String deviceId = onlinePosModel.getDeviceId();
        String type = onlinePosModel.getType();
        String version = onlinePosModel.getVersion();

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        searchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        searchModel.addSearchCondition("user_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, userId);
        Pos pos = DatabaseHelper.find(Pos.class, searchModel);
        if (pos == null) {
            pos = new Pos();
            pos.setTenantId(tenantId);
            pos.setTenantCode(tenantCode);
            pos.setBranchId(branchId);
            pos.setBranchCode(branchCode);
            pos.setUserId(userId);
            pos.setDeviceId(deviceId);
            pos.setType(type);
            pos.setVersion(version);
            pos.setOnline(true);
            pos.setCreatedUserId(userId);
            pos.setUpdatedUserId(userId);
            pos.setUpdatedRemark("POS不存在，新增POS并且设置为在线状态！");
            DatabaseHelper.insert(pos);
        } else {
            pos.setUserId(userId);
            pos.setDeviceId(deviceId);
            pos.setType(type);
            pos.setVersion(version);
            pos.setOnline(true);
            pos.setUpdatedUserId(userId);
            pos.setUpdatedRemark("POS存在，设置为在线状态！");
            DatabaseHelper.update(pos);
        }
        return ApiRest.builder().data(pos).message("上线POS成功！").successful(true).build();
    }

    /**
     * 下线POS
     *
     * @param offlinePosModel
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiRest offlinePos(OfflinePosModel offlinePosModel) {
        BigInteger tenantId = offlinePosModel.obtainTenantId();
        BigInteger branchId = offlinePosModel.obtainBranchId();
        BigInteger userId = offlinePosModel.obtainUserId();

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition(Pos.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        searchModel.addSearchCondition(Pos.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        searchModel.addSearchCondition(Pos.ColumnName.USER_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, userId);

        Pos pos = DatabaseHelper.find(Pos.class, searchModel);
        ValidateUtils.notNull(pos, "POS不存在！");

        pos.setDeviceId(Constants.VARCHAR_DEFAULT_VALUE);
        pos.setOnline(false);
        pos.setUpdatedRemark("下线POS");
        DatabaseHelper.update(pos);

        return ApiRest.builder().data(pos).message("下线POS成功！").build();
    }

    /**
     * 扫码支付
     *
     * @param offlinePayModel
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiRest offlinePay(OfflinePayModel offlinePayModel) {
        BigInteger tenantId = offlinePayModel.getTenantId();
        String tenantCode = offlinePayModel.getTenantCode();
        BigInteger branchId = offlinePayModel.getBranchId();
        BigInteger userId = offlinePayModel.getUserId();
        String orderNumber = offlinePayModel.getOrderNumber();
        String authCode = offlinePayModel.getAuthCode();
        String subject = offlinePayModel.getSubject();
        int totalAmount = offlinePayModel.getTotalAmount();

        String sequenceName = SerialNumberGenerator.generatorTodaySequenceName(tenantId, branchId, "offline_pay_out_trade_no");
        String outTradeNo = SerialNumberGenerator.nextOrderNumber("OP", 8, SequenceUtils.nextValue(sequenceName));

        Tenant tenant = TenantUtils.obtainTenantInfo(tenantId);
        Integer usedChannelType = tenant.getUsedChannelType();

        int payCodePrefix = Integer.parseInt(authCode.substring(0, 2));
        int paidScene = 0;
        if (ArrayUtils.contains(Constants.WEI_XIN_PAY_CODE_PREFIXES, payCodePrefix)) {
            paidScene = Constants.PAID_SCENE_WEI_XIN_MICROPAY;
        } else if (ArrayUtils.contains(Constants.ALIPAY_PAY_CODE_PREFIXES, payCodePrefix)) {
            paidScene = Constants.PAID_SCENE_ALIPAY_FAC_TO_FACE;
        }
        ValidateUtils.isTrue(paidScene != 0, "支付码错误！");

        int channelType = 0;
        if (usedChannelType == Constants.TENANT_USED_CHANNEL_TYPE_NATIVE) {
            if (paidScene == Constants.PAID_SCENE_WEI_XIN_MICROPAY) {
                channelType = Constants.CHANNEL_TYPE_WEI_XIN;
            } else if (paidScene == Constants.PAID_SCENE_ALIPAY_FAC_TO_FACE) {
                channelType = Constants.CHANNEL_TYPE_ALIPAY;
            }
        } else if (usedChannelType == Constants.TENANT_USED_CHANNEL_TYPE_MIYA) {
            channelType = Constants.CHANNEL_TYPE_MIYA;
        } else if (usedChannelType == Constants.TENANT_USED_CHANNEL_TYPE_NEW_LAND) {
            channelType = Constants.CHANNEL_TYPE_NEW_LAND;
        } else if (usedChannelType == Constants.TENANT_USED_CHANNEL_TYPE_UMPAY) {
            channelType = Constants.CHANNEL_TYPE_UMPAY;
        }

        int paidStatus = 0;
        Map<String, ?> channelResult = null;
        if (channelType == Constants.CHANNEL_TYPE_WEI_XIN) {
            WeiXinPayAccount weiXinPayAccount = WeiXinPayUtils.obtainWeiXinPayAccount(tenantId.toString(), branchId.toString());
            ValidateUtils.notNull(weiXinPayAccount, "商户未配置微信支付账号！");
            MicroPayModel microPayModel = MicroPayModel.builder()
                    .appId(weiXinPayAccount.getAppId())
                    .mchId(weiXinPayAccount.getMchId())
                    .apiSecretKey(weiXinPayAccount.getApiSecretKey())
                    .subAppId(weiXinPayAccount.getSubPublicAccountAppId())
                    .subMchId(weiXinPayAccount.getSubMchId())
                    .acceptanceModel(weiXinPayAccount.isAcceptanceModel())
                    .body("订单支付")
                    .outTradeNo(outTradeNo)
                    .totalFee(totalAmount)
                    .spbillCreateIp(ApplicationHandler.getRemoteAddress())
                    .authCode(authCode)
                    .build();
            channelResult = WeiXinPayUtils.microPay(microPayModel);
            String resultCode = MapUtils.getString(channelResult, "result_code");
            if (Constants.SUCCESS.equals(resultCode)) {
                paidStatus = Constants.OFFLINE_PAY_PAID_STATUS_SUCCESS;
            } else {
                paidStatus = Constants.OFFLINE_PAY_PAID_STATUS_PAYING;
            }
        } else if (channelType == Constants.CHANNEL_TYPE_ALIPAY) {
            AlipayAccount alipayAccount = AlipayUtils.obtainAlipayAccount("2016121304213325");
            AlipayTradePayModel alipayTradePayModel = AlipayTradePayModel.builder()
                    .appId(alipayAccount.getAppId())
                    .appPrivateKey(alipayAccount.getAppPrivateKey())
                    .alipayPublicKey(alipayAccount.getAlipayPublicKey())
                    .topic(ConfigurationUtils.getConfiguration(Constants.OFFLINE_PAY_ALIPAY_ASYNC_NOTIFY_MESSAGE_TOPIC))
                    .outTradeNo(outTradeNo)
                    .authCode(authCode)
                    .scene(build.dream.common.constants.Constants.SCENE_BAR_CODE)
                    .subject(subject)
                    .totalAmount(BigDecimal.valueOf(totalAmount).divide(Constants.BIG_DECIMAL_ONE_HUNDRED))
                    .build();
            channelResult = AlipayUtils.alipayTradePay(alipayTradePayModel);
        } else if (channelType == Constants.CHANNEL_TYPE_MIYA) {
            MiyaAccount miyaAccount = MiyaUtils.obtainMiyaAccount(tenantId.toString(), branchId.toString());
            ValidateUtils.notNull(miyaAccount, "未配置米雅账号！");

            OrderPayModel orderPayModel = OrderPayModel.builder()
                    .a2(miyaAccount.getMiyaMerchantCode())
                    .a3(miyaAccount.getMiyaBranchCode())
                    .a4("0000")
                    .a5("1111")
                    .miyaKey(miyaAccount.getMiyaKey())
                    .b1(outTradeNo)
                    .b2(authCode)
                    .b4(String.valueOf(totalAmount))
                    .build();
            channelResult = MiyaUtils.orderPay(orderPayModel);
        } else if (channelType == Constants.CHANNEL_TYPE_NEW_LAND) {
            NewLandAccount newLandAccount = NewLandUtils.obtainNewLandAccount(tenantId.toString(), branchId.toString());
            ValidateUtils.notNull(newLandAccount, "未配置新大陆账号！");

            String payChannel = null;
            if (paidScene == Constants.PAID_SCENE_WEI_XIN_MICROPAY) {
                payChannel = Constants.NEW_LAND_PAY_CHANNEL_ALIPAY;
            } else if (paidScene == Constants.PAID_SCENE_ALIPAY_FAC_TO_FACE) {
                payChannel = Constants.NEW_LAND_PAY_CHANNEL_WXPAY;
            }
            BarcodePayModel barcodePayModel = BarcodePayModel.builder()
                    .opSys(Constants.NEW_LAND_OP_SYS_ZHI_LIAN)
                    .orgNo(newLandAccount.getOrgNo())
                    .mercId(newLandAccount.getMchId())
                    .trmNo(newLandAccount.getTrmNo())
                    .tradeNo(outTradeNo)
                    .txnTime(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()))
                    .amount(totalAmount)
                    .totalAmount(totalAmount)
                    .authCode(authCode)
                    .payChannel(payChannel)
                    .build();
            channelResult = NewLandUtils.barcodePay(barcodePayModel);
        } else if (channelType == Constants.CHANNEL_TYPE_UMPAY) {

        }

        OfflinePayRecord offlinePayRecord = OfflinePayRecord.builder()
                .tenantId(tenantId)
                .tenantCode(tenantCode)
                .branchId(branchId)
                .userId(userId)
                .orderNumber(orderNumber)
                .paidScene(paidScene)
                .channelType(channelType)
                .outTradeNo(outTradeNo)
                .totalAmount(totalAmount)
                .authCode(authCode)
                .paidStatus(paidStatus)
                .refundStatus(Constants.OFFLINE_PAY_REFUND_STATUS_NO_REFUND)
                .createdUserId(userId)
                .updatedUserId(userId)
                .build();
        DatabaseHelper.insert(offlinePayRecord);

        OfflinePayLog offlinePayLog = OfflinePayLog.builder()
                .tenantId(tenantId)
                .tenantCode(tenantCode)
                .branchId(branchId)
                .offlinePayRecordId(offlinePayRecord.getId())
                .type(Constants.OFFLINE_PAY_LOG_TYPE_PAID)
                .channelResult(JacksonUtils.writeValueAsString(channelResult))
                .createdUserId(userId)
                .updatedUserId(userId)
                .build();
        DatabaseHelper.insert(offlinePayLog);

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("outTradeNo", outTradeNo);
        data.put("paidStatus", paidStatus);
        data.put("refundStatus", Constants.OFFLINE_PAY_REFUND_STATUS_NO_REFUND);

        return ApiRest.builder().data(data).message("扫码支付成功！").successful(true).build();
    }

    /**
     * 订单查询
     *
     * @param orderQueryModel
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiRest orderQuery(OrderQueryModel orderQueryModel) {
        BigInteger tenantId = orderQueryModel.getTenantId();
        String tenantCode = orderQueryModel.getTenantCode();
        BigInteger branchId = orderQueryModel.getBranchId();
        BigInteger userId = orderQueryModel.getUserId();
        String outTradeNo = orderQueryModel.getOutTradeNo();

        SearchModel searchModel = SearchModel.builder()
                .autoSetDeletedFalse()
                .addSearchCondition(OfflinePayRecord.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId)
                .addSearchCondition(OfflinePayRecord.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId)
                .addSearchCondition(OfflinePayRecord.ColumnName.OUT_TRADE_NO, Constants.SQL_OPERATION_SYMBOL_EQUAL, outTradeNo)
                .build();
        OfflinePayRecord offlinePayRecord = DatabaseHelper.find(OfflinePayRecord.class, searchModel);
        ValidateUtils.notNull(offlinePayRecord, "支付记录不存在！");

        Map<String, Object> data = new HashMap<String, Object>();
        int paidStatus = offlinePayRecord.getPaidStatus();
        int channelType = offlinePayRecord.getChannelType();
        if (paidStatus == Constants.OFFLINE_PAY_PAID_STATUS_PAYING && channelType == Constants.CHANNEL_TYPE_WEI_XIN) {
            WeiXinPayAccount weiXinPayAccount = WeiXinPayUtils.obtainWeiXinPayAccount(tenantId.toString(), branchId.toString());
            build.dream.common.models.weixinpay.OrderQueryModel model = build.dream.common.models.weixinpay.OrderQueryModel.builder()
                    .appId(weiXinPayAccount.getAppId())
                    .mchId(weiXinPayAccount.getMchId())
                    .apiSecretKey(weiXinPayAccount.getApiSecretKey())
                    .subAppId(weiXinPayAccount.getSubPublicAccountAppId())
                    .subMchId(weiXinPayAccount.getSubMchId())
                    .acceptanceModel(weiXinPayAccount.isAcceptanceModel())
                    .outTradeNo(outTradeNo)
                    .build();
            Map<String, String> result = WeiXinPayUtils.orderQuery(model);

            OfflinePayLog offlinePayLog = OfflinePayLog.builder()
                    .tenantId(tenantId)
                    .tenantCode(tenantCode)
                    .branchId(branchId)
                    .offlinePayRecordId(offlinePayRecord.getId())
                    .type(Constants.OFFLINE_PAY_LOG_TYPE_QUERY)
                    .channelResult(JacksonUtils.writeValueAsString(result))
                    .createdUserId(userId)
                    .updatedUserId(userId)
                    .build();
            DatabaseHelper.insert(offlinePayLog);

            int status = 0;
            String tradeState = result.get("trade_state");
            if (Constants.SUCCESS.equals(tradeState)) {
                offlinePayRecord.setPaidStatus(Constants.OFFLINE_PAY_PAID_STATUS_SUCCESS);
                offlinePayRecord.setUpdatedUserId(userId);
                DatabaseHelper.update(offlinePayRecord);
                status = Constants.OFFLINE_PAY_PAID_STATUS_SUCCESS;
            } else if (Constants.USERPAYING.equals(tradeState)) {
                status = Constants.OFFLINE_PAY_PAID_STATUS_PAYING;
            }
            data.put("paidStatus", status);
        } else {
            data.put("refundStatus", offlinePayRecord.getRefundStatus());
        }
        data.put("outTradeNo", outTradeNo);
        data.put("paidStatus", paidStatus);
        return ApiRest.builder().data(data).message("查询订单成功！").successful(true).build();
    }

    @Transactional(rollbackFor = Exception.class)
    public void handleOfflinePayAsyncNotify(Map<String, String> params, int channelType) {
        String outTradeNo = null;
        if (channelType == Constants.CHANNEL_TYPE_ALIPAY) {
            outTradeNo = params.get("out_trade_no");
        }

        SearchModel searchModel = SearchModel.builder()
                .autoSetDeletedFalse()
                .addSearchCondition(OfflinePayRecord.ColumnName.OUT_TRADE_NO, Constants.SQL_OPERATION_SYMBOL_EQUAL, outTradeNo)
                .build();
        OfflinePayRecord offlinePayRecord = DatabaseHelper.find(OfflinePayRecord.class, searchModel);
        ValidateUtils.notNull(offlinePayRecord, "支付记录不存在！");

        OfflinePayLog offlinePayLog = OfflinePayLog.builder()
                .tenantId(offlinePayRecord.getTenantId())
                .tenantCode(offlinePayRecord.getTenantCode())
                .branchId(offlinePayRecord.getBranchId())
                .offlinePayRecordId(offlinePayRecord.getId())
                .type(Constants.OFFLINE_PAY_LOG_TYPE_PAID_CALLBACK)
                .channelResult(JacksonUtils.writeValueAsString(params))
                .createdUserId(BigInteger.ZERO)
                .updatedUserId(BigInteger.ZERO)
                .build();
        DatabaseHelper.insert(offlinePayLog);

        offlinePayRecord.setPaidStatus(Constants.OFFLINE_PAY_PAID_STATUS_SUCCESS);
        offlinePayRecord.setUpdatedUserId(BigInteger.ZERO);
        DatabaseHelper.update(offlinePayRecord);
    }

    @Transactional(rollbackFor = Exception.class)
    public void handleOfflineRefundPayAsyncNotify(Map<String, String> params, int channelType) {
        String outTradeNo = null;
        if (channelType == Constants.CHANNEL_TYPE_WEI_XIN) {
            outTradeNo = params.get("out_trade_no");
        } else if (channelType == Constants.CHANNEL_TYPE_ALIPAY) {
            outTradeNo = params.get("out_trade_no");
        }

        SearchModel searchModel = SearchModel.builder()
                .autoSetDeletedFalse()
                .addSearchCondition(OfflinePayRecord.ColumnName.OUT_TRADE_NO, Constants.SQL_OPERATION_SYMBOL_EQUAL, outTradeNo)
                .build();
        OfflinePayRecord offlinePayRecord = DatabaseHelper.find(OfflinePayRecord.class, searchModel);
        ValidateUtils.notNull(offlinePayRecord, "支付记录不存在！");

        offlinePayRecord.setRefundStatus(Constants.OFFLINE_PAY_REFUND_STATUS_SUCCESS);
        offlinePayRecord.setUpdatedUserId(BigInteger.ZERO);
        DatabaseHelper.update(offlinePayRecord);

        OfflinePayLog offlinePayLog = OfflinePayLog.builder()
                .tenantId(offlinePayRecord.getTenantId())
                .tenantCode(offlinePayRecord.getTenantCode())
                .branchId(offlinePayRecord.getBranchId())
                .offlinePayRecordId(offlinePayRecord.getId())
                .type(Constants.OFFLINE_PAY_LOG_TYPE_PAID_CALLBACK)
                .channelResult(JacksonUtils.writeValueAsString(params))
                .createdUserId(BigInteger.ZERO)
                .updatedUserId(BigInteger.ZERO)
                .build();
        DatabaseHelper.insert(offlinePayLog);
    }

    /**
     * 退款
     *
     * @param refundModel
     */
    public ApiRest refund(RefundModel refundModel) {
        BigInteger tenantId = refundModel.getTenantId();
        String tenantCode = refundModel.getTenantCode();
        BigInteger branchId = refundModel.getBranchId();
        BigInteger userId = refundModel.getUserId();
        String outTradeNo = refundModel.getOutTradeNo();
        Integer refundAmount = refundModel.getRefundAmount();

        SearchModel searchModel = SearchModel.builder()
                .autoSetDeletedFalse()
                .addSearchCondition(OfflinePayRecord.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId)
                .addSearchCondition(OfflinePayRecord.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId)
                .addSearchCondition(OfflinePayRecord.ColumnName.OUT_TRADE_NO, Constants.SQL_OPERATION_SYMBOL_EQUAL, outTradeNo)
                .build();
        OfflinePayRecord offlinePayRecord = DatabaseHelper.find(OfflinePayRecord.class, searchModel);
        ValidateUtils.notNull(offlinePayRecord, "支付记录不存在！");
        ValidateUtils.isTrue(offlinePayRecord.getPaidStatus() == Constants.OFFLINE_PAY_PAID_STATUS_SUCCESS, "未支付不能退款！");

        Integer totalAmount = offlinePayRecord.getTotalAmount();
        Map<String, ?> channelResult = null;
        int channelType = offlinePayRecord.getChannelType();
        if (channelType == Constants.CHANNEL_TYPE_WEI_XIN) {
            WeiXinPayAccount weiXinPayAccount = WeiXinPayUtils.obtainWeiXinPayAccount(tenantId.toString(), branchId.toString());
            ValidateUtils.notNull(weiXinPayAccount, "未配置微信支付账号！");

            build.dream.common.models.weixinpay.RefundModel weiXinRefundModel = build.dream.common.models.weixinpay.RefundModel.builder()
                    .appId(weiXinPayAccount.getAppId())
                    .mchId(weiXinPayAccount.getMchId())
                    .apiSecretKey(weiXinPayAccount.getApiSecretKey())
                    .subAppId(weiXinPayAccount.getSubPublicAccountAppId())
                    .subMchId(weiXinPayAccount.getSubMchId())
                    .acceptanceModel(weiXinPayAccount.isAcceptanceModel())
                    .outTradeNo(outTradeNo)
                    .outRefundNo(outTradeNo)
                    .totalFee(totalAmount)
                    .refundFee(refundAmount == null ? totalAmount : refundAmount)
                    .topic(ConfigurationUtils.getConfiguration(Constants.OFFLINE_PAY_REFUND_WEI_XIN_ASYNC_NOTIFY_MESSAGE_TOPIC))
                    .operationCertificate(weiXinPayAccount.getOperationCertificate())
                    .operationCertificatePassword(weiXinPayAccount.getOperationCertificatePassword())
                    .build();
            channelResult = WeiXinPayUtils.refund(weiXinRefundModel);
        } else if (channelType == Constants.CHANNEL_TYPE_ALIPAY) {
            AlipayAccount alipayAccount = AlipayUtils.obtainAlipayAccount("2016121304213325");
            ValidateUtils.notNull(alipayAccount, "未配置支付宝账号！");
            AlipayTradeRefundModel alipayTradeRefundModel = AlipayTradeRefundModel.builder()
                    .appId(alipayAccount.getAppId())
                    .topic(ConfigurationUtils.getConfiguration(Constants.OFFLINE_PAY_REFUND_WEI_XIN_ASYNC_NOTIFY_MESSAGE_TOPIC))
                    .appPrivateKey(alipayAccount.getAppPrivateKey())
                    .alipayPublicKey(alipayAccount.getAlipayPublicKey())
                    .refundAmount(refundAmount == null ? BigDecimal.valueOf(totalAmount).divide(Constants.BIG_DECIMAL_ONE_HUNDRED) : BigDecimal.valueOf(refundAmount).divide(Constants.BIG_DECIMAL_ONE_HUNDRED))
                    .outTradeNo(outTradeNo)
//                    .tradeNo("")
                    .build();
            channelResult = AlipayUtils.alipayTradeRefund(alipayTradeRefundModel);
        }

        offlinePayRecord.setRefundStatus(Constants.OFFLINE_PAY_REFUND_STATUS_APPLIED);
        offlinePayRecord.setUpdatedUserId(userId);
        DatabaseHelper.update(offlinePayRecord);

        OfflinePayLog offlinePayLog = OfflinePayLog.builder()
                .tenantId(tenantId)
                .tenantCode(tenantCode)
                .branchId(branchId)
                .offlinePayRecordId(offlinePayRecord.getId())
                .type(Constants.OFFLINE_PAY_LOG_TYPE_REFUND)
                .channelResult(JacksonUtils.writeValueAsString(channelResult))
                .createdUserId(userId)
                .updatedUserId(userId)
                .build();
        DatabaseHelper.insert(offlinePayLog);
        return ApiRest.builder().message("退款成功！").successful(true).build();
    }
}
