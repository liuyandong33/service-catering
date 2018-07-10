package build.dream.catering.tools;

import build.dream.catering.constants.Constants;
import build.dream.catering.services.ElemeService;
import build.dream.catering.utils.DingtalkUtils;
import build.dream.catering.utils.ThreadUtils;
import build.dream.common.erp.catering.domains.ElemeCallbackMessage;
import build.dream.common.utils.GsonUtils;
import org.apache.commons.lang.ArrayUtils;

public class HandleElemeMessageRunnable implements Runnable {
    private static final int[] ELEME_ORDER_STATE_CHANGE_MESSAGE_TYPES = {12, 14, 15, 17, 18};
    private static final int[] ELEME_CANCEL_ORDER_MESSAGE_TYPES = {20, 21, 22, 23, 24, 25, 26};
    private static final int[] ELEME_REFUND_ORDER_MESSAGE_TYPES = {30, 31, 32, 33, 34, 35, 36};
    private static final int[] ELEME_REMINDER_MESSAGE_TYPES = {45, 46};
    private static final int[] ELEME_DELIVERY_ORDER_STATE_CHANGE_MESSAGE_TYPES = {51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76};
    private static final int[] ELEME_SHOP_STATE_CHANGE_MESSAGE_TYPES = {91, 92};

    private ElemeService elemeService;
    private ElemeCallbackMessage elemeCallbackMessage;
    private boolean continued = true;
    private int count;
    private int interval;
    private String uuid;

    public HandleElemeMessageRunnable(ElemeService elemeService, ElemeCallbackMessage elemeCallbackMessage, int count, int interval, String uuid) {
        this.elemeService = elemeService;
        this.elemeCallbackMessage = elemeCallbackMessage;
        this.count = count;
        this.interval = interval;
        this.uuid = uuid;
    }

    @Override
    public void run() {
        while (continued) {
            boolean isNormal = true;
            try {
                int type = elemeCallbackMessage.getType();
                if (type == 10) {
                    elemeService.saveElemeOrder(elemeCallbackMessage, uuid);
                } else if (ArrayUtils.contains(ELEME_CANCEL_ORDER_MESSAGE_TYPES, type)) {
                    elemeService.handleElemeCancelOrderMessage(elemeCallbackMessage, uuid);
                } else if (ArrayUtils.contains(ELEME_ORDER_STATE_CHANGE_MESSAGE_TYPES, type)) {
                    elemeService.handleElemeOrderStateChangeMessage(elemeCallbackMessage, uuid);
                } else if (ArrayUtils.contains(ELEME_REFUND_ORDER_MESSAGE_TYPES, type)) {
                    elemeService.handleElemeRefundOrderMessage(elemeCallbackMessage, uuid);
                } else if (ArrayUtils.contains(ELEME_REMINDER_MESSAGE_TYPES, type)) {
                    elemeService.handleElemeReminderMessage(elemeCallbackMessage, uuid);
                } else if (ArrayUtils.contains(ELEME_DELIVERY_ORDER_STATE_CHANGE_MESSAGE_TYPES, type)) {
                    elemeService.handleElemeDeliveryOrderStateChangeMessage(elemeCallbackMessage, uuid);
                } else if (ArrayUtils.contains(ELEME_SHOP_STATE_CHANGE_MESSAGE_TYPES, type)) {
                    elemeService.handleElemeShopStateChangeMessage(elemeCallbackMessage, uuid);
                } else if (type == 100) {
                    elemeService.handleAuthorizationStateChangeMessage(elemeCallbackMessage, uuid);
                }
            } catch (Exception e) {
                isNormal = false;
                if (count == 1) {
                    DingtalkUtils.send(String.format(Constants.DINGTALK_ERROR_MESSAGE_FORMAT, "饿了么消息处理失败", GsonUtils.toJson(elemeCallbackMessage), e.getClass().getName(), e.getMessage()));
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
