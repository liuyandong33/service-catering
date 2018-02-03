package build.dream.catering.tools;

import build.dream.catering.constants.Constants;
import build.dream.catering.services.ElemeService;
import build.dream.catering.utils.ElemeUtils;
import build.dream.common.api.ApiRest;
import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.LogUtils;
import build.dream.common.utils.ProxyUtils;
import net.sf.json.JSONObject;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class ElemeConsumerThread implements Runnable {
    private static final String ELEME_CONSUMER_THREAD_SIMPLE_NAME = "ElemeConsumerThread";

    private static final int[] ELEME_ORDER_STATE_CHANGE_MESSAGE_TYPES = {12, 14, 15, 17, 18};
    private static final int[] ELEME_REFUND_ORDER_MESSAGE_TYPES = {20, 21, 22, 23, 24, 25, 26, 30, 31, 32, 33, 34, 35, 36};
    private static final int[] ELEME_REMINDER_MESSAGE_TYPES = {45, 46};
    private static final int[] ELEME_DELIVERY_ORDER_STATE_CHANGE_MESSAGE_TYPES = {51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76};
    private static final int[] ELEME_SHOP_STATE_CHANGE_MESSAGE_TYPES = {91, 92};

    private ElemeService elemeService = ApplicationHandler.getBean(ElemeService.class);

    @Override
    public void run() {
        while (true) {
            String elemeMessage = null;
            try {
                elemeMessage = ElemeUtils.takeElemeMessage();
                if (StringUtils.isBlank(elemeMessage)) {
                    continue;
                }

                if (!ApplicationHandler.isJson(elemeMessage)) {
                    continue;
                }

                if (!ApplicationHandler.isRightJson(elemeMessage, Constants.ELEME_MESSAGE_SCHEMA_FILE_PATH)) {
                    continue;
                }

                JSONObject elemeMessageJsonObject = JSONObject.fromObject(elemeMessage);
                JSONObject callbackRequestBodyJsonObject = elemeMessageJsonObject.getJSONObject("callbackRequestBody");
                String uuid = elemeMessageJsonObject.getString("uuid");
                int type = callbackRequestBodyJsonObject.getInt("type");
                BigInteger shopId = BigInteger.valueOf(callbackRequestBodyJsonObject.getLong("shopId"));
                String message = callbackRequestBodyJsonObject.getString("message");

                if (type == 10) {
                    elemeService.saveElemeOrder(shopId, message, type, uuid);
                } else if (ArrayUtils.contains(ELEME_ORDER_STATE_CHANGE_MESSAGE_TYPES, type)) {
                    elemeService.handleElemeOrderStateChangeMessage(shopId, message, type, uuid);
                } else if (ArrayUtils.contains(ELEME_REFUND_ORDER_MESSAGE_TYPES, type)) {
                    elemeService.handleElemeRefundOrderMessage(shopId, message, type, uuid);
                } else if (ArrayUtils.contains(ELEME_REMINDER_MESSAGE_TYPES, type)) {
                    elemeService.handleElemeReminderMessage(shopId, message, type, uuid);
                } else if (ArrayUtils.contains(ELEME_DELIVERY_ORDER_STATE_CHANGE_MESSAGE_TYPES, type)) {
                    elemeService.handleElemeDeliveryOrderStateChangeMessage(shopId, message, type, uuid);
                } else if (ArrayUtils.contains(ELEME_SHOP_STATE_CHANGE_MESSAGE_TYPES, type)) {
                    elemeService.handleElemeShopStateChangeMessage(shopId, message, type, uuid);
                } else if (type == 100) {
                    elemeService.handleAuthorizationStateChangeMessage(shopId, message, type, uuid);
                }
            } catch (Exception e) {
                if (StringUtils.isNotBlank(elemeMessage)) {
                    ElemeUtils.addElemeMessage(elemeMessage);
                }
                LogUtils.error("保存饿了么消息失败", ELEME_CONSUMER_THREAD_SIMPLE_NAME, "run", e);
            }
        }
    }

    private void markHandleFailureMessage(String uuid) {
        try {
            Map<String, String> markHandleFailureMessageRequestParameters = new HashMap<String, String>();
            markHandleFailureMessageRequestParameters.put("uuid", uuid);

            ApiRest saveElemeCallbackMessageApiRest = ProxyUtils.doPostWithRequestParameters(Constants.SERVICE_NAME_PLATFORM, "elemeCallbackMessage", "markHandleFailureMessage", markHandleFailureMessageRequestParameters);
            Validate.isTrue(saveElemeCallbackMessageApiRest.isSuccessful(), saveElemeCallbackMessageApiRest.getError());
        } catch (Exception e) {
            LogUtils.error("标记处理失败消息失败", ELEME_CONSUMER_THREAD_SIMPLE_NAME, "markHandleFailureMessage", e);
        }
    }
}
