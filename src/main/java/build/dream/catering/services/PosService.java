package build.dream.catering.services;

import build.dream.catering.constants.Constants;
import build.dream.catering.models.pos.OfflinePayModel;
import build.dream.catering.models.pos.OfflinePosModel;
import build.dream.catering.models.pos.OnlinePosModel;
import build.dream.catering.models.pos.OrderQueryModel;
import build.dream.catering.utils.SequenceUtils;
import build.dream.common.api.ApiRest;
import build.dream.common.catering.domains.OfflinePayRecord;
import build.dream.common.catering.domains.Pos;
import build.dream.common.models.aggregatepay.ScanCodePayModel;
import build.dream.common.saas.domains.WeiXinPayAccount;
import build.dream.common.utils.*;
import org.apache.commons.lang.ArrayUtils;
import org.dom4j.DocumentException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import scala.Tuple2;

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
    public ApiRest offlinePay(OfflinePayModel offlinePayModel) throws DocumentException {
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
        if (ArrayUtils.contains(Constants.WEI_XIN_PAY_CODE_PREFIXES, payCodePrefix)) {
            channelType = Constants.CHANNEL_TYPE_WEI_XIN;
        } else if (ArrayUtils.contains(Constants.ALIPAY_PAY_CODE_PREFIXES, payCodePrefix)) {
            channelType = Constants.CHANNEL_TYPE_ALIPAY;
        } else if (ArrayUtils.contains(Constants.JING_DONG_PAY_CODE_PREFIXES, payCodePrefix)) {
            channelType = Constants.CHANNEL_TYPE_JING_DONG;
        }
        ValidateUtils.isTrue(channelType != 0, "支付码错误！");

        OfflinePayRecord offlinePayRecord = OfflinePayRecord.builder()
                .tenantId(tenantId)
                .tenantCode(tenantCode)
                .branchId(branchId)
                .userId(userId)
                .orderNumber(orderNumber)
                .channelType(channelType)
                .outTradeNo(outTradeNo)
                .totalAmount(totalAmount)
                .authCode(authCode)
                .status(Constants.OFFLINE_PAY_STATUS_UNPAID)
                .createdUserId(userId)
                .updatedUserId(userId)
                .build();
        DatabaseHelper.insert(offlinePayRecord);

        String ipAddress = ApplicationHandler.getRemoteAddress();
        ScanCodePayModel scanCodePayModel = ScanCodePayModel.builder()
                .tenantId(tenantId.toString())
                .branchId(branchId.toString())
                .channelType(channelType)
                .outTradeNo(outTradeNo)
                .authCode(authCode)
                .subject(subject)
                .totalAmount(totalAmount)
                .topic("")
                .ipAddress(ipAddress)
                .build();

        Map<String, ? extends Object> result = AggregatePayUtils.scanCodePay(scanCodePayModel);

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("outTradeNo", outTradeNo);
        return ApiRest.builder().data(data).message("扫码支付成功！").successful(true).build();
    }

    public <K, V> Map<K, V> buildMap(Tuple2<K, V>... tuple2s) {
        Map<K, V> map = new HashMap<K, V>();
        for (Tuple2<K, V> tuple2 : tuple2s) {
            map.put(tuple2._1(), tuple2._2());
        }
        return map;
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
        if (status == Constants.OFFLINE_PAY_STATUS_PAID) {
            return ApiRest.builder().data(buildMap(TupleUtils.buildTuple2("tradeState", Constants.OFFLINE_PAY_STATUS_PAID))).message("查询订单成功！").successful(true).build();
        }

        int channelType = offlinePayRecord.getChannelType();
        if (channelType == Constants.CHANNEL_TYPE_ALIPAY) {
            return ApiRest.builder().data(buildMap(TupleUtils.buildTuple2("tradeState", Constants.OFFLINE_PAY_STATUS_UNPAID))).message("查询订单成功！").successful(true).build();
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
        String tradeState = result.get("trade_state");
        if (Constants.SUCCESS.equals(tradeState)) {
            offlinePayRecord.setStatus(Constants.OFFLINE_PAY_STATUS_PAID);
            offlinePayRecord.setUpdatedUserId(userId);
            DatabaseHelper.update(offlinePayRecord);
            return ApiRest.builder().data(buildMap(TupleUtils.buildTuple2("tradeState", Constants.OFFLINE_PAY_STATUS_PAID))).message("查询订单成功！").successful(true).build();
        }

        return ApiRest.builder().data(buildMap(TupleUtils.buildTuple2("tradeState", Constants.OFFLINE_PAY_STATUS_UNPAID))).message("查询订单成功！").successful(true).build();
    }
}
