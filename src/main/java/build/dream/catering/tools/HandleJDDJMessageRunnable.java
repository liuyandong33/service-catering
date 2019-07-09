package build.dream.catering.tools;

import build.dream.catering.constants.Constants;
import build.dream.catering.services.JDDJService;
import build.dream.common.utils.ConfigurationUtils;
import build.dream.common.utils.DingtalkUtils;
import build.dream.common.utils.GsonUtils;
import build.dream.common.utils.ThreadUtils;
import org.apache.commons.collections.MapUtils;

import java.math.BigInteger;
import java.util.Map;

public class HandleJDDJMessageRunnable implements Runnable {
    private JDDJService jddjService;
    private BigInteger tenantId;
    private String tenantCode;
    private Map<String, Object> body;
    private boolean continued = true;
    private int count;
    private long interval;

    public HandleJDDJMessageRunnable(JDDJService jddjService, BigInteger tenantId, String tenantCode, Map<String, Object> body, int count, long interval) {
        this.jddjService = jddjService;
        this.tenantId = tenantId;
        this.tenantCode = tenantCode;
        this.body = body;
        this.count = count;
        this.interval = interval;
    }

    @Override
    public void run() {
        while (continued) {
            boolean isNormal = true;
            try {
                int type = MapUtils.getIntValue(body, "type");
                switch (type) {
                    case Constants.DJSW_TYPE_NEW_ORDER:
                        jddjService.handleNewOrder(tenantId, tenantCode, body);
                        break;
                    case Constants.DJSW_TYPE_ORDER_ADJUST:
                        jddjService.handleOrderAdjust(tenantId, tenantCode, body);
                        break;
                    case Constants.DJSW_TYPE_APPLY_CANCELORDER:
                        jddjService.handleApplyCancelOrder(tenantId, tenantCode, body);
                        break;
                    case Constants.DJSW_TYPE_ORDER_WAIT_OUT_STORE:
                        jddjService.handleOrderWaitOutStore(tenantId, tenantCode, body);
                        break;
                    case Constants.DJSW_TYPE_DELIVERY_ORDER:
                        jddjService.handleDeliveryOrder(tenantId, tenantCode, body);
                        break;
                    case Constants.DJSW_TYPE_PICK_FINISH_ORDER:
                        jddjService.handlePickFinishOrder(tenantId, tenantCode, body);
                        break;
                    case Constants.DJSW_TYPE_FINISH_ORDER:
                        jddjService.handleFinishOrder(tenantId, tenantCode, body);
                        break;
                    case Constants.DJSW_TYPE_LOCK_ORDER:
                        jddjService.handleLockOrder(tenantId, tenantCode, body);
                        break;
                    case Constants.DJSW_TYPE_UNLOCK_ORDER:
                        jddjService.handleUnlockOrder(tenantId, tenantCode, body);
                        break;
                    case Constants.DJSW_TYPE_USER_CANCEL_ORDER:
                        jddjService.handleUserCancelOrder(tenantId, tenantCode, body);
                        break;
                    case Constants.DJSW_TYPE_PUSH_DELIVERY_STATUS:
                        jddjService.handlePushDeliveryStatus(tenantId, tenantCode, body);
                        break;
                    case Constants.DJSW_TYPE_ORDER_INFO_CHANGE:
                        jddjService.handleOrderInfoChange(tenantId, tenantCode, body);
                        break;
                    case Constants.DJSW_TYPE_ORDER_ADD_TIPS:
                        jddjService.handleOrderAddTips(tenantId, tenantCode, body);
                        break;
                    case Constants.DJSW_TYPE_ORDER_ACCOUNTING:
                        jddjService.handleOrderAccounting(tenantId, tenantCode, body);
                        break;
                    case Constants.DJSW_TYPE_DELIVERY_CARRIER_MODIFY:
                        jddjService.handleDeliveryCarrierModify(tenantId, tenantCode, body);
                        break;

                }
            } catch (Exception e) {
                isNormal = false;
                if (count == 1) {
                    DingtalkUtils.send(ConfigurationUtils.getConfiguration(Constants.DINGTALK_ERROR_NOTIFY_CHAT_ID), String.format(Constants.DINGTALK_ERROR_NOTIFY_MESSAGE_FORMAT, "京东到家消息处理失败", GsonUtils.toJson(body), e.getClass().getName(), e.getMessage()));
                }
            }

            if (isNormal) {
                continued = false;
            } else {
                count = count - 1;
                if (count <= 0) {
                    continued = false;
                } else {
                    ThreadUtils.sleepSafe(interval);
                }
            }
        }
    }
}
