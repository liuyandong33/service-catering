package build.dream.catering.services;

import build.dream.catering.constants.Constants;
import build.dream.catering.models.pos.OfflinePayModel;
import build.dream.catering.models.pos.OfflinePosModel;
import build.dream.catering.models.pos.OnlinePosModel;
import build.dream.catering.utils.SequenceUtils;
import build.dream.common.api.ApiRest;
import build.dream.common.catering.domains.OfflinePayRecord;
import build.dream.common.catering.domains.Pos;
import build.dream.common.models.aggregatepay.ScanCodePayModel;
import build.dream.common.utils.*;
import org.apache.commons.lang.ArrayUtils;
import org.dom4j.DocumentException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        BigInteger tenantId = offlinePayModel.obtainTenantId();
        String tenantCode = offlinePayModel.obtainTenantCode();
        BigInteger branchId = offlinePayModel.obtainBranchId();
        BigInteger userId = offlinePayModel.obtainUserId();
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
                .status(1)
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
                .notifyUrl(notifyUrl)
                .ipAddress(ipAddress)
                .build();

        Map<String, ? extends Object> result = AggregatePayUtils.scanCodePay(scanCodePayModel);

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("outTradeNo", outTradeNo);
        return ApiRest.builder().data(data).message("扫码支付成功！").successful(true).build();
    }
}
