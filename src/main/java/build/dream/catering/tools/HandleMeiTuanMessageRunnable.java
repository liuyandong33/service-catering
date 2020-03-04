package build.dream.catering.tools;

import build.dream.catering.constants.ConfigurationKeys;
import build.dream.catering.constants.Constants;
import build.dream.catering.services.MeiTuanService;
import build.dream.catering.utils.ThreadUtils;
import build.dream.common.utils.ConfigurationUtils;
import build.dream.common.utils.DingtalkUtils;
import build.dream.common.utils.GsonUtils;

import java.util.Map;

public class HandleMeiTuanMessageRunnable implements Runnable {
    private MeiTuanService meiTuanService;
    private Map<String, String> callbackParameters;
    private boolean continued = true;
    private int count;
    private int interval;
    private String uuid;
    private int type;

    public HandleMeiTuanMessageRunnable(MeiTuanService meiTuanService, Map<String, String> callbackParameters, int count, int interval, String uuid, int type) {
        this.meiTuanService = meiTuanService;
        this.callbackParameters = callbackParameters;
        this.count = count;
        this.interval = interval;
        this.uuid = uuid;
        this.type = type;
    }

    @Override
    public void run() {
        while (continued) {
            boolean isNormal = true;
            try {
                if (type == Constants.MEI_TUAN_CALLBACK_TYPE_ORDER_EFFECTIVE) {
                    meiTuanService.handleOrderEffectiveCallback(callbackParameters, uuid, type);
                } else if (type == Constants.MEI_TUAN_CALLBACK_TYPE_ORDER_CANCEL) {
                    meiTuanService.handleOrderCancelCallback(callbackParameters, uuid, type);
                } else if (type == Constants.MEI_TUAN_CALLBACK_TYPE_ORDER_REFUND) {
                    meiTuanService.handleOrderRefundCallback(callbackParameters, uuid, type);
                } else if (type == Constants.MEI_TUAN_CALLBACK_TYPE_ORDER_CONFIRM) {
                    meiTuanService.handleOrderConfirmCallback(callbackParameters, uuid, type);
                } else if (type == Constants.MEI_TUAN_CALLBACK_TYPE_ORDER_SETTLED) {
                    meiTuanService.handleOrderSettledCallback(callbackParameters, uuid, type);
                } else if (type == Constants.MEI_TUAN_CALLBACK_TYPE_ORDER_SHIPPING_STATUS) {
                    meiTuanService.handleOrderShippingStatusCallback(callbackParameters, uuid, type);
                } else if (type == Constants.MEI_TUAN_CALLBACK_TYPE_POI_STATUS) {
                    meiTuanService.handlePoiStatusCallback(callbackParameters, uuid, type);
                } else if (type == Constants.MEI_TUAN_CALLBACK_TYPE_PART_ORDER_REFUND) {
                    meiTuanService.handlePartOrderRefundCallback(callbackParameters, uuid, type);
                } else if (type == Constants.MEI_TUAN_CALLBACK_TYPE_BINDING_STORE) {
                    meiTuanService.handleBindingStoreCallback(callbackParameters, uuid, type);
                }
            } catch (Exception e) {
                isNormal = false;
                if (count == 1) {
                    DingtalkUtils.send(ConfigurationUtils.getConfiguration(ConfigurationKeys.DINGTALK_ERROR_NOTIFY_CHAT_ID), String.format(Constants.DINGTALK_ERROR_NOTIFY_MESSAGE_FORMAT, "美团消息处理失败", GsonUtils.toJson(callbackParameters), e.getClass().getName(), e.getMessage()));
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
