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
import build.dream.common.models.weixinpay.MicroPayModel;
import build.dream.common.saas.domains.AlipayAccount;
import build.dream.common.saas.domains.WeiXinPayAccount;
import build.dream.common.utils.*;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
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
        String notifyUrl = "";

        int payCodePrefix = Integer.parseInt(authCode.substring(0, 2));

        int channelType = 0;
        int paidScene = 0;
        if (ArrayUtils.contains(Constants.WEI_XIN_PAY_CODE_PREFIXES, payCodePrefix)) {
            paidScene = Constants.PAID_SCENE_WEI_XIN_MICROPAY;
            channelType = Constants.CHANNEL_TYPE_WEI_XIN;
        } else if (ArrayUtils.contains(Constants.ALIPAY_PAY_CODE_PREFIXES, payCodePrefix)) {
            paidScene = Constants.PAID_SCENE_ALIPAY_FAC_TO_FACE;
            channelType = Constants.CHANNEL_TYPE_ALIPAY;
        }
        ValidateUtils.isTrue(channelType != 0 && paidScene != 0, "支付码错误！");

        int status = 0;
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
                status = Constants.OFFLINE_PAY_STATUS_PAID_SUCCESS;
            } else {
                status = Constants.OFFLINE_PAY_STATUS_PAYING;
            }
        } else if (channelType == Constants.CHANNEL_TYPE_ALIPAY) {
            AlipayAccount alipayAccount = AlipayUtils.obtainAlipayAccount("2016121304213325");
            AlipayTradePayModel alipayTradePayModel = AlipayTradePayModel.builder()
                    .appId(alipayAccount.getAppId())
                    .appPrivateKey(alipayAccount.getAppPrivateKey())
                    .alipayPublicKey(alipayAccount.getAlipayPublicKey())
                    .topic("aaa")
                    .outTradeNo(outTradeNo)
                    .authCode(authCode)
                    .scene(build.dream.common.constants.Constants.SCENE_BAR_CODE)
                    .subject(subject)
                    .totalAmount(BigDecimal.valueOf(totalAmount).divide(Constants.BIG_DECIMAL_ONE_HUNDRED))
                    .build();
            channelResult = AlipayUtils.alipayTradePay(alipayTradePayModel);
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
                .status(status)
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
        data.put("status", status);

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

        int status = offlinePayRecord.getStatus();
        if (status == Constants.OFFLINE_PAY_STATUS_PAID_SUCCESS || status == Constants.OFFLINE_PAY_STATUS_PAID_FAILURE) {
            return ApiRest.builder().data(ApplicationHandler.buildHashMap(TupleUtils.buildTuple2("status", status))).message("查询订单成功！").successful(true).build();
        }

        int channelType = offlinePayRecord.getChannelType();
        if (channelType == Constants.CHANNEL_TYPE_ALIPAY) {
            return ApiRest.builder().data(ApplicationHandler.buildHashMap(TupleUtils.buildTuple2("status", Constants.OFFLINE_PAY_STATUS_PAYING))).message("查询订单成功！").successful(true).build();
        }

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

        int newStatus = 0;
        String tradeState = result.get("trade_state");
        if (Constants.SUCCESS.equals(tradeState)) {
            offlinePayRecord.setStatus(Constants.OFFLINE_PAY_STATUS_PAID_SUCCESS);
            offlinePayRecord.setUpdatedUserId(userId);
            DatabaseHelper.update(offlinePayRecord);
            newStatus = Constants.OFFLINE_PAY_STATUS_PAID_SUCCESS;
        } else if (Constants.USERPAYING.equals(tradeState)) {
            newStatus = Constants.OFFLINE_PAY_STATUS_PAYING;
        }
        return ApiRest.builder().data(ApplicationHandler.buildHashMap(TupleUtils.buildTuple2("status", newStatus))).message("查询订单成功！").successful(true).build();
    }

    @Transactional(rollbackFor = Exception.class)
    public void handleOfflinePayAsyncNotify(Map<String, String> params) {
        String outTradeNo = params.get("out_trade_no");
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

        offlinePayRecord.setStatus(Constants.OFFLINE_PAY_STATUS_PAID_SUCCESS);
        offlinePayRecord.setUpdatedUserId(BigInteger.ZERO);
        DatabaseHelper.update(offlinePayRecord);
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
        ValidateUtils.isTrue(offlinePayRecord.getStatus() == Constants.OFFLINE_PAY_STATUS_PAID_SUCCESS, "未支付不能退款！");

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
                    .operationCertificate(weiXinPayAccount.getOperationCertificate())
                    .operationCertificatePassword(weiXinPayAccount.getOperationCertificatePassword())
                    .outRefundNo("")
                    .totalFee(totalAmount)
                    .refundFee(refundAmount == null ? totalAmount : refundAmount)
                    .build();
            channelResult = WeiXinPayUtils.refund(weiXinRefundModel);
        } else if (channelType == Constants.CHANNEL_TYPE_ALIPAY) {
            AlipayAccount alipayAccount = AlipayUtils.obtainAlipayAccount("2016121304213325");
            ValidateUtils.notNull(alipayAccount, "未配置支付宝账号！");
            AlipayTradeRefundModel alipayTradeRefundModel = AlipayTradeRefundModel.builder()
                    .appId(alipayAccount.getAppId())
                    .appPrivateKey(alipayAccount.getAppPrivateKey())
                    .alipayPublicKey(alipayAccount.getAlipayPublicKey())
                    .refundAmount(refundAmount == null ? BigDecimal.valueOf(totalAmount).divide(Constants.BIG_DECIMAL_ONE_HUNDRED) : BigDecimal.valueOf(refundAmount).divide(Constants.BIG_DECIMAL_ONE_HUNDRED))
                    .outRequestNo(outTradeNo)
                    .tradeNo("")
                    .build();
            channelResult = AlipayUtils.alipayTradeRefund(alipayTradeRefundModel);
        }

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
