package build.dream.catering.services;

import build.dream.catering.constants.Constants;
import build.dream.catering.models.pos.*;
import build.dream.catering.utils.SerialNumberGenerator;
import build.dream.common.api.ApiRest;
import build.dream.common.beans.AlipayAccount;
import build.dream.common.beans.MqConfig;
import build.dream.common.domains.catering.OfflinePayLog;
import build.dream.common.domains.catering.OfflinePayRecord;
import build.dream.common.domains.catering.Pos;
import build.dream.common.domains.saas.*;
import build.dream.common.models.alipay.AlipayTradePayModel;
import build.dream.common.models.alipay.AlipayTradeRefundModel;
import build.dream.common.models.miya.OrderPayModel;
import build.dream.common.models.mqtt.ApplyTokenModel;
import build.dream.common.models.mqtt.QueryTokenModel;
import build.dream.common.models.mqtt.RevokeTokenModel;
import build.dream.common.models.newland.BarcodePayModel;
import build.dream.common.models.newland.QryBarcodePayModel;
import build.dream.common.models.newland.RefundBarcodePayModel;
import build.dream.common.models.rocketmq.DelayedMessageModel;
import build.dream.common.models.rocketmq.DelayedType;
import build.dream.common.models.umpay.MerRefundModel;
import build.dream.common.models.umpay.PassiveScanCodePayModel;
import build.dream.common.models.weixinpay.MicroPayModel;
import build.dream.common.mqtt.MqttInfo;
import build.dream.common.tuples.Tuple2;
import build.dream.common.utils.*;
import com.aliyun.openservices.ons.api.Message;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

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
        Long tenantId = onlinePosModel.obtainTenantId();
        String tenantCode = onlinePosModel.obtainTenantCode();
        Long branchId = onlinePosModel.obtainBranchId();
        String branchCode = onlinePosModel.obtainBranchCode();
        Long userId = onlinePosModel.obtainUserId();
        String deviceId = onlinePosModel.getDeviceId();
        String type = onlinePosModel.getType();
        String version = onlinePosModel.getVersion();
        String cloudPushDeviceId = onlinePosModel.getCloudPushDeviceId();

        Map<String, Object> data = new HashMap<String, Object>();
        String mqttToken = Constants.VARCHAR_DEFAULT_VALUE;
        String mqttClientId = Constants.VARCHAR_DEFAULT_VALUE;
        long startDeliverTime = 0;
        if (Constants.POS_TYPE_WINDOWS.equals(type)) {
            Tuple2<String, MqttInfo> tuple2 = obtainMqttInfo();
            mqttToken = tuple2._1();
            MqttInfo mqttInfo = tuple2._2();

            mqttClientId = mqttInfo.getClientId();
            cloudPushDeviceId = Constants.VARCHAR_DEFAULT_VALUE;

            startDeliverTime = mqttInfo.getExpireTime().getTime() - 10 * 60 * 1000;

            data.put("mqttInfo", mqttInfo);
        }
        SearchModel searchModel = SearchModel.builder()
                .autoSetDeletedFalse()
                .addSearchCondition(Pos.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId)
                .addSearchCondition(Pos.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId)
                .addSearchCondition(Pos.ColumnName.USER_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, userId)
                .build();
        Pos pos = DatabaseHelper.find(Pos.class, searchModel);
        if (Objects.isNull(pos)) {
            pos = Pos.builder()
                    .tenantId(tenantId)
                    .tenantCode(tenantCode)
                    .branchId(branchId)
                    .branchCode(branchCode)
                    .userId(userId)
                    .deviceId(deviceId)
                    .type(type)
                    .version(version)
                    .online(true)
                    .cloudPushDeviceId(cloudPushDeviceId)
                    .mqttClientId(mqttClientId)
                    .mqttToken(mqttToken)
                    .createdUserId(userId)
                    .updatedUserId(userId)
                    .updatedRemark("POS不存在，新增POS并且设置为在线状态！")
                    .build();
            DatabaseHelper.insert(pos);
        } else {
            pos.setDeviceId(deviceId);
            pos.setType(type);
            pos.setVersion(version);
            pos.setOnline(true);
            pos.setCloudPushDeviceId(cloudPushDeviceId);
            pos.setMqttClientId(mqttClientId);
            pos.setMqttToken(mqttToken);
            pos.setUpdatedUserId(userId);
            pos.setUpdatedRemark("POS存在，设置为在线状态！");
            DatabaseHelper.update(pos);
        }

        if (Constants.POS_TYPE_WINDOWS.equals(type)) {
            sendPosTokenInvalidMessage(tenantId, branchId, pos.getId(), startDeliverTime);
        }

        data.put("pos", pos);
        return ApiRest.builder().data(data).message("上线POS成功！").successful(true).build();
    }

    /**
     * 获取mqtt连接信息
     *
     * @param obtainMqttInfoModel
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiRest obtainMqttInfo(ObtainMqttInfoModel obtainMqttInfoModel) {
        Long tenantId = obtainMqttInfoModel.obtainTenantId();
        Long branchId = obtainMqttInfoModel.obtainBranchId();
        Long userId = obtainMqttInfoModel.obtainUserId();
        Long posId = obtainMqttInfoModel.getPosId();

        SearchModel searchModel = SearchModel.builder()
                .autoSetDeletedFalse()
                .equal(Pos.ColumnName.TENANT_ID, tenantId)
                .equal(Pos.ColumnName.BRANCH_ID, branchId)
                .equal(Pos.ColumnName.ID, posId)
                .equal(Pos.ColumnName.USER_ID, userId)
                .build();
        Pos pos = DatabaseHelper.find(Pos.class, searchModel);
        ValidateUtils.notNull(pos, "POS 不存在！");

        Tuple2<String, MqttInfo> tuple2 = obtainMqttInfo();
        String mqttToken = tuple2._1();
        MqttInfo mqttInfo = tuple2._2();

        sendPosTokenInvalidMessage(tenantId, branchId, posId, mqttInfo.getExpireTime().getTime() - 10 * 60 - 1000);

        pos.setMqttToken(mqttToken);
        pos.setMqttClientId(mqttInfo.getClientId());
        pos.setUpdatedUserId(userId);
        DatabaseHelper.update(pos);

        return ApiRest.builder().data(mqttInfo).message("获取MQTT连接信息成功！").successful(true).build();
    }

    private Tuple2<String, MqttInfo> obtainMqttInfo() {
        MqttConfig mqttConfig = MqttUtils.obtainMqttConfig();

        Date expireTime = DateUtils.addDays(new Date(), 1);
        ApplyTokenModel applyTokenModel = ApplyTokenModel.builder()
                .actions("R")
                .resources(mqttConfig.getTopic() + "/#")
                .expireTime(expireTime.getTime())
                .proxyType("MQTT")
                .serviceName("mq")
                .instanceId(mqttConfig.getInstanceId())
                .build();
        String mqttToken = MqttUtils.applyToken(applyTokenModel);
        Map<String, String> tokenInfos = new HashMap<String, String>();
        tokenInfos.put("R", mqttToken);

        MqttInfo mqttInfo = MqttUtils.obtainMqttInfo(mqttConfig, tokenInfos);
        mqttInfo.setExpireTime(expireTime);
        return TupleUtils.buildTuple2(mqttToken, mqttInfo);
    }

    /**
     * 下线POS
     *
     * @param offlinePosModel
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiRest offlinePos(OfflinePosModel offlinePosModel) {
        Long tenantId = offlinePosModel.obtainTenantId();
        Long branchId = offlinePosModel.obtainBranchId();
        Long userId = offlinePosModel.obtainUserId();

        SearchModel searchModel = SearchModel.builder()
                .autoSetDeletedFalse()
                .addSearchCondition(Pos.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId)
                .addSearchCondition(Pos.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId)
                .addSearchCondition(Pos.ColumnName.USER_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, userId)
                .build();

        Pos pos = DatabaseHelper.find(Pos.class, searchModel);
        ValidateUtils.notNull(pos, "POS不存在！");

        String mqttToken = pos.getMqttToken();
        QueryTokenModel queryTokenModel = QueryTokenModel.builder()
                .token(mqttToken)
                .build();
        if (MqttUtils.queryToken(queryTokenModel)) {
            RevokeTokenModel revokeTokenModel = RevokeTokenModel.builder()
                    .token(mqttToken)
                    .build();
            MqttUtils.revokeToken(revokeTokenModel);
        }

        pos.setDeviceId(Constants.VARCHAR_DEFAULT_VALUE);
        pos.setType(Constants.VARCHAR_DEFAULT_VALUE);
        pos.setVersion(Constants.VARCHAR_DEFAULT_VALUE);
        pos.setOnline(false);
        pos.setCloudPushDeviceId(Constants.VARCHAR_DEFAULT_VALUE);
        pos.setMqttClientId(Constants.VARCHAR_DEFAULT_VALUE);
        pos.setMqttToken(Constants.VARCHAR_DEFAULT_VALUE);
        pos.setUpdatedRemark("下线POS");
        DatabaseHelper.update(pos);

        return ApiRest.builder().data(pos).message("下线POS成功！").successful(true).build();
    }

    /**
     * 扫码支付
     *
     * @param offlinePayModel
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiRest offlinePay(OfflinePayModel offlinePayModel) {
        Long tenantId = offlinePayModel.getTenantId();
        String tenantCode = offlinePayModel.getTenantCode();
        Long branchId = offlinePayModel.getBranchId();
        Long userId = offlinePayModel.getUserId();
        String authCode = offlinePayModel.getAuthCode();
        String subject = offlinePayModel.getSubject();
        int totalAmount = offlinePayModel.getTotalAmount();

        String outTradeNo = SerialNumberGenerator.generateSerialNumber();

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
        String tradeNo = null;
        if (channelType == Constants.CHANNEL_TYPE_WEI_XIN) {
            WeiXinPayAccount weiXinPayAccount = WeiXinPayUtils.obtainWeiXinPayAccount(tenantId, branchId);
            ValidateUtils.notNull(weiXinPayAccount, "商户未配置微信支付账号！");
            MicroPayModel microPayModel = MicroPayModel.builder()
                    .appId(weiXinPayAccount.getAppId())
                    .mchId(weiXinPayAccount.getMchId())
                    .apiKey(weiXinPayAccount.getApiKey())
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
            tradeNo = MapUtils.getString(channelResult, "transaction_id", "");
        } else if (channelType == Constants.CHANNEL_TYPE_ALIPAY) {
            AlipayAccount alipayAccount = AlipayUtils.obtainAlipayAccount(tenantId, branchId);
            ValidateUtils.notNull(alipayAccount, "未配置支付宝账号！");
            AlipayTradePayModel alipayTradePayModel = AlipayTradePayModel.builder()
                    .appId(alipayAccount.getAppId())
                    .appPrivateKey(alipayAccount.getAppPrivateKey())
                    .alipayPublicKey(alipayAccount.getAlipayPublicKey())
                    .mqConfig(MqConfig.builder().mqType(Constants.MQ_TYPE_KAFKA).topic(ConfigurationUtils.getConfiguration(Constants.OFFLINE_PAY_ALIPAY_ASYNC_NOTIFY_MESSAGE_TOPIC)).build())
                    .outTradeNo(outTradeNo)
                    .authCode(authCode)
                    .scene(build.dream.common.constants.Constants.SCENE_BAR_CODE)
                    .subject(subject)
                    .totalAmount(Double.valueOf(totalAmount) / 100)
                    .build();
            channelResult = AlipayUtils.alipayTradePay(alipayTradePayModel);
            tradeNo = MapUtils.getString(channelResult, "trade_no");
        } else if (channelType == Constants.CHANNEL_TYPE_MIYA) {
            MiyaAccount miyaAccount = MiyaUtils.obtainMiyaAccount(tenantId, branchId);
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
            tradeNo = MapUtils.getString(channelResult, "C6");
        } else if (channelType == Constants.CHANNEL_TYPE_NEW_LAND) {
            NewLandAccount newLandAccount = NewLandUtils.obtainNewLandAccount(tenantId, branchId);
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
                    .mercId(newLandAccount.getMercId())
                    .trmNo(newLandAccount.getTrmNo())
                    .tradeNo(outTradeNo)
                    .txnTime(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()))
                    .amount(totalAmount)
                    .totalAmount(totalAmount)
                    .authCode(authCode)
                    .payChannel(payChannel)
                    .build();
            channelResult = NewLandUtils.barcodePay(barcodePayModel);
            tradeNo = MapUtils.getString(channelResult, "orderNo");
        } else if (channelType == Constants.CHANNEL_TYPE_UMPAY) {
            UmPayAccount umPayAccount = UmPayUtils.obtainUmPayAccount(tenantId, branchId);
            ValidateUtils.notNull(umPayAccount, "未配置联动支付账号！");

            String scanCodeType = null;
            if (paidScene == Constants.PAID_SCENE_WEI_XIN_MICROPAY) {
                scanCodeType = Constants.UM_PAY_SCAN_CODE_TYPE_WECHAT;
            } else if (paidScene == Constants.PAID_SCENE_ALIPAY_FAC_TO_FACE) {
                scanCodeType = Constants.UM_PAY_SCAN_CODE_TYPE_ALIPAY;
            }
            PassiveScanCodePayModel passiveScanCodePayModel = PassiveScanCodePayModel.builder()
                    .merId(umPayAccount.getMerId())
                    .privateKey(umPayAccount.getPrivateKey())
                    .platformCertificate(umPayAccount.getPlatformCertificate())
                    .mqConfig(MqConfig.builder().mqType(Constants.MQ_TYPE_KAFKA).topic(ConfigurationUtils.getConfiguration(Constants.OFFLINE_PAY_UMPAY_ASYNC_NOTIFY_MESSAGE_TOPIC)).build())
                    .goodsInf("")
                    .orderId(outTradeNo)
                    .amount(totalAmount)
                    .authCode(authCode)
                    .useDesc("订单支付")
                    .scanCodeType(scanCodeType)
                    .build();
            channelResult = UmPayUtils.passiveScanCodePay(passiveScanCodePayModel);
        }

        OfflinePayRecord offlinePayRecord = OfflinePayRecord.builder()
                .tenantId(tenantId)
                .tenantCode(tenantCode)
                .branchId(branchId)
                .userId(userId)
                .paidScene(paidScene)
                .channelType(channelType)
                .tradeNo(tradeNo)
                .outTradeNo(outTradeNo)
                .refundNo(Constants.VARCHAR_DEFAULT_VALUE)
                .outRefundNo(Constants.VARCHAR_DEFAULT_VALUE)
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
        data.put("tradeNo", tradeNo);
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
        Long tenantId = orderQueryModel.getTenantId();
        String tenantCode = orderQueryModel.getTenantCode();
        Long branchId = orderQueryModel.getBranchId();
        Long userId = orderQueryModel.getUserId();
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
        if (paidStatus == Constants.OFFLINE_PAY_PAID_STATUS_PAYING && (channelType == Constants.CHANNEL_TYPE_WEI_XIN || channelType == Constants.CHANNEL_TYPE_MIYA || channelType == Constants.CHANNEL_TYPE_NEW_LAND)) {
            Map<String, ?> channelResult = null;
            if (channelType == Constants.CHANNEL_TYPE_WEI_XIN) {
                WeiXinPayAccount weiXinPayAccount = WeiXinPayUtils.obtainWeiXinPayAccount(tenantId, branchId);
                build.dream.common.models.weixinpay.OrderQueryModel weiXinOrderQueryModel = build.dream.common.models.weixinpay.OrderQueryModel.builder()
                        .appId(weiXinPayAccount.getAppId())
                        .mchId(weiXinPayAccount.getMchId())
                        .apiKey(weiXinPayAccount.getApiKey())
                        .subAppId(weiXinPayAccount.getSubPublicAccountAppId())
                        .subMchId(weiXinPayAccount.getSubMchId())
                        .acceptanceModel(weiXinPayAccount.isAcceptanceModel())
                        .outTradeNo(outTradeNo)
                        .build();
                channelResult = WeiXinPayUtils.orderQuery(weiXinOrderQueryModel);
                String tradeState = MapUtils.getString(channelResult, "trade_state");
                if (Constants.SUCCESS.equals(tradeState)) {
                    offlinePayRecord.setPaidStatus(Constants.OFFLINE_PAY_PAID_STATUS_SUCCESS);
                    offlinePayRecord.setUpdatedUserId(userId);
                    offlinePayRecord.setTradeNo(MapUtils.getString(channelResult, "transaction_id"));
                    DatabaseHelper.update(offlinePayRecord);
                    data.put("paidStatus", Constants.OFFLINE_PAY_PAID_STATUS_SUCCESS);
                } else if (Constants.USERPAYING.equals(tradeState)) {
                    data.put("paidStatus", Constants.OFFLINE_PAY_PAID_STATUS_PAYING);
                }
            } else if (channelType == Constants.CHANNEL_TYPE_MIYA) {
                MiyaAccount miyaAccount = MiyaUtils.obtainMiyaAccount(tenantId, branchId);
                ValidateUtils.notNull(miyaAccount, "未配置米雅支付账号！");
                build.dream.common.models.miya.OrderQueryModel miyaOrderQueryModel = build.dream.common.models.miya.OrderQueryModel.builder()
                        .build();
                channelResult = MiyaUtils.orderQuery(miyaOrderQueryModel);
            } else if (channelType == Constants.CHANNEL_TYPE_NEW_LAND) {
                NewLandAccount newLandAccount = NewLandUtils.obtainNewLandAccount(tenantId, branchId);
                ValidateUtils.notNull(newLandAccount, "未配置新大陆支付账号！");
                QryBarcodePayModel qryBarcodePayModel = QryBarcodePayModel.builder()
                        .build();
                channelResult = NewLandUtils.qryBarcodePay(qryBarcodePayModel);
            }

            OfflinePayLog offlinePayLog = OfflinePayLog.builder()
                    .tenantId(tenantId)
                    .tenantCode(tenantCode)
                    .branchId(branchId)
                    .offlinePayRecordId(offlinePayRecord.getId())
                    .type(Constants.OFFLINE_PAY_LOG_TYPE_QUERY)
                    .channelResult(JacksonUtils.writeValueAsString(channelResult))
                    .createdUserId(userId)
                    .updatedUserId(userId)
                    .build();
            DatabaseHelper.insert(offlinePayLog);
        } else {
            data.put("paidStatus", offlinePayRecord.getPaidStatus());
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
        } else if (channelType == Constants.CHANNEL_TYPE_UMPAY) {
            outTradeNo = params.get("order_id");
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
                .createdUserId(0L)
                .updatedUserId(0L)
                .build();
        DatabaseHelper.insert(offlinePayLog);

        offlinePayRecord.setPaidStatus(Constants.OFFLINE_PAY_PAID_STATUS_SUCCESS);
        offlinePayRecord.setUpdatedUserId(0L);
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
        offlinePayRecord.setUpdatedUserId(0L);
        DatabaseHelper.update(offlinePayRecord);

        OfflinePayLog offlinePayLog = OfflinePayLog.builder()
                .tenantId(offlinePayRecord.getTenantId())
                .tenantCode(offlinePayRecord.getTenantCode())
                .branchId(offlinePayRecord.getBranchId())
                .offlinePayRecordId(offlinePayRecord.getId())
                .type(Constants.OFFLINE_PAY_LOG_TYPE_PAID_CALLBACK)
                .channelResult(JacksonUtils.writeValueAsString(params))
                .createdUserId(0L)
                .updatedUserId(0L)
                .build();
        DatabaseHelper.insert(offlinePayLog);
    }

    /**
     * 退款
     *
     * @param refundModel
     */
    public ApiRest refund(RefundModel refundModel) {
        Long tenantId = refundModel.getTenantId();
        String tenantCode = refundModel.getTenantCode();
        Long branchId = refundModel.getBranchId();
        Long userId = refundModel.getUserId();
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

        String outRefundNo = SerialNumberGenerator.generateSerialNumber();
        Integer totalAmount = offlinePayRecord.getTotalAmount();
        Map<String, ?> channelResult = null;
        int channelType = offlinePayRecord.getChannelType();
        if (channelType == Constants.CHANNEL_TYPE_WEI_XIN) {
            WeiXinPayAccount weiXinPayAccount = WeiXinPayUtils.obtainWeiXinPayAccount(tenantId, branchId);
            ValidateUtils.notNull(weiXinPayAccount, "未配置微信支付账号！");

            build.dream.common.models.weixinpay.RefundModel weiXinRefundModel = build.dream.common.models.weixinpay.RefundModel.builder()
                    .appId(weiXinPayAccount.getAppId())
                    .mchId(weiXinPayAccount.getMchId())
                    .apiKey(weiXinPayAccount.getApiKey())
                    .apiV3Key(weiXinPayAccount.getApiV3Key())
                    .subAppId(weiXinPayAccount.getSubPublicAccountAppId())
                    .subMchId(weiXinPayAccount.getSubMchId())
                    .acceptanceModel(weiXinPayAccount.isAcceptanceModel())
                    .outTradeNo(outTradeNo)
                    .outRefundNo(outRefundNo)
                    .totalFee(totalAmount)
                    .refundFee(refundAmount == null ? totalAmount : refundAmount)
                    .mqConfig(MqConfig.builder().mqType(Constants.MQ_TYPE_KAFKA).topic(ConfigurationUtils.getConfiguration(Constants.OFFLINE_PAY_REFUND_WEI_XIN_ASYNC_NOTIFY_MESSAGE_TOPIC)).build())
                    .operationCertificate(weiXinPayAccount.getOperationCertificate())
                    .operationCertificatePassword(weiXinPayAccount.getOperationCertificatePassword())
                    .build();
            channelResult = WeiXinPayUtils.refund(weiXinRefundModel);
        } else if (channelType == Constants.CHANNEL_TYPE_ALIPAY) {
            AlipayAccount alipayAccount = AlipayUtils.obtainAlipayAccount(tenantId, branchId);
            ValidateUtils.notNull(alipayAccount, "未配置支付宝账号！");

            AlipayTradeRefundModel alipayTradeRefundModel = AlipayTradeRefundModel.builder()
                    .appId(alipayAccount.getAppId())
                    .mqConfig(MqConfig.builder().mqType(Constants.MQ_TYPE_KAFKA).topic(ConfigurationUtils.getConfiguration(Constants.OFFLINE_PAY_REFUND_WEI_XIN_ASYNC_NOTIFY_MESSAGE_TOPIC)).build())
                    .appPrivateKey(alipayAccount.getAppPrivateKey())
                    .alipayPublicKey(alipayAccount.getAlipayPublicKey())
                    .refundAmount(refundAmount == null ? Double.valueOf(totalAmount) / 100 : Double.valueOf(refundAmount) / 100)
                    .outTradeNo(outTradeNo)
                    .tradeNo(offlinePayRecord.getTradeNo())
                    .outRequestNo(outRefundNo)
                    .build();
            channelResult = AlipayUtils.alipayTradeRefund(alipayTradeRefundModel);
        } else if (channelType == Constants.CHANNEL_TYPE_MIYA) {
            MiyaAccount miyaAccount = MiyaUtils.obtainMiyaAccount(tenantId, branchId);
            ValidateUtils.notNull(miyaAccount, "未配置米雅支付账号！");
            build.dream.common.models.miya.RefundModel miyaRefundModel = build.dream.common.models.miya.RefundModel.builder()
                    .a2(miyaAccount.getMiyaMerchantCode())
                    .a3(miyaAccount.getMiyaBranchCode())
                    .a4("0000")
                    .a5("1111")
                    .miyaKey(miyaAccount.getMiyaKey())
                    .b1(outTradeNo)
                    .b2(outRefundNo)
                    .b4(String.valueOf(totalAmount))
                    .build();
            channelResult = MiyaUtils.refund(miyaRefundModel);
        } else if (channelType == Constants.CHANNEL_TYPE_NEW_LAND) {
            NewLandAccount newLandAccount = NewLandUtils.obtainNewLandAccount(tenantId, branchId);
            ValidateUtils.notNull(newLandAccount, "未配置新大陆支付账号！");
            RefundBarcodePayModel refundBarcodePayModel = RefundBarcodePayModel.builder()
                    .orgNo(newLandAccount.getOrgNo())
                    .mercId(newLandAccount.getMercId())
                    .trmNo(newLandAccount.getTrmNo())
                    .tradeNo(UUID.randomUUID().toString())
                    .txnTime(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()))
                    .secretKey(newLandAccount.getSecretKey())
                    .orderNo(outTradeNo)
                    .txnAmt(offlinePayRecord.getTotalAmount())
                    .build();
            channelResult = NewLandUtils.refundBarcodePay(refundBarcodePayModel);
        } else if (channelType == Constants.CHANNEL_TYPE_UMPAY) {
            UmPayAccount umPayAccount = UmPayUtils.obtainUmPayAccount(tenantId, branchId);
            ValidateUtils.notNull(umPayAccount, "未配置联动支付账号！");
            MerRefundModel merRefundModel = MerRefundModel.builder()
                    .merId(umPayAccount.getMerId())
                    .privateKey(umPayAccount.getPrivateKey())
                    .platformCertificate(umPayAccount.getPlatformCertificate())
                    .refundNo(outRefundNo)
                    .orderId(outTradeNo)
                    .merDate(new SimpleDateFormat("yyyyMMdd").format(offlinePayRecord.getCreatedTime()))
                    .refundAmount(offlinePayRecord.getTotalAmount())
                    .orgAmount(offlinePayRecord.getTotalAmount())
                    .build();
            channelResult = UmPayUtils.merRefund(merRefundModel);
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

    /**
     * 处理POS token 失效，通知POS重新申请token
     *
     * @param info
     */
    @Transactional(readOnly = true)
    public void handleMqttTokenInvalid(Map<String, Object> info) {
        Long tenantId = Long.valueOf(MapUtils.getLongValue(info, "tenantId"));
        Long branchId = Long.valueOf(MapUtils.getLongValue(info, "branchId"));
        Long posId = Long.valueOf(MapUtils.getLongValue(info, "posId"));

        SearchModel searchModel = SearchModel.builder()
                .autoSetDeletedFalse()
                .equal(Pos.ColumnName.TENANT_ID, tenantId)
                .equal(Pos.ColumnName.BRANCH_ID, branchId)
                .equal(Pos.ColumnName.ID, posId)
                .build();
        Pos pos = DatabaseHelper.find(Pos.class, searchModel);
        if (Objects.nonNull(pos) && pos.isOnline()) {
            PushUtils.pushMqttTokenInvalidMessage(pos, 10, 60000);
        }
    }

    public void sendPosTokenInvalidMessage(Long tenantId, Long branchId, Long posId, long startDeliverTime) {
        DelayedMessageModel delayedMessageModel = new DelayedMessageModel();
        delayedMessageModel.setType(DelayedType.DELAYED_TYPE_POS_MQTT_TOKEN_INVALID);

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("tenantId", tenantId);
        data.put("branchId", branchId);
        data.put("posId", posId);

        delayedMessageModel.setData(data);

        String body = JacksonUtils.writeValueAsString(data);

        Message message = new Message();
        message.setBody(body.getBytes(Constants.CHARSET_UTF_8));
        message.setStartDeliverTime(startDeliverTime);
        message.setTopic(ConfigurationUtils.getConfiguration("delayed.or.timed.rocket.mq.topic"));
        RocketMQUtils.send(message);
    }
}
