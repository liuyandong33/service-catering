package build.dream.catering.tools;

import build.dream.catering.constants.Constants;
import build.dream.catering.services.ElemeService;
import build.dream.catering.utils.DingtalkUtils;
import build.dream.catering.utils.ElemeUtils;
import build.dream.common.api.ApiRest;
import build.dream.common.erp.catering.domains.ElemeCallbackMessage;
import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.LogUtils;
import build.dream.common.utils.ProxyUtils;
import net.sf.json.JSONObject;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ElemeConsumerThread implements Runnable {
    private static final String ELEME_CONSUMER_THREAD_SIMPLE_NAME = "ElemeConsumerThread";

    private static final int[] ELEME_ORDER_STATE_CHANGE_MESSAGE_TYPES = {12, 14, 15, 17, 18};
    private static final int[] ELEME_CANCEL_ORDER_MESSAGE_TYPES = {20, 21, 22, 23, 24, 25, 26};
    private static final int[] ELEME_REFUND_ORDER_MESSAGE_TYPES = {30, 31, 32, 33, 34, 35, 36};
    private static final int[] ELEME_REMINDER_MESSAGE_TYPES = {45, 46};
    private static final int[] ELEME_DELIVERY_ORDER_STATE_CHANGE_MESSAGE_TYPES = {51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76};
    private static final int[] ELEME_SHOP_STATE_CHANGE_MESSAGE_TYPES = {91, 92};

    private ElemeService elemeService = ApplicationHandler.getBean(ElemeService.class);

    @Override
    public void run() {
        while (true) {
            JSONObject callbackRequestBodyJsonObject = null;
            String uuid = null;
            int count = 0;
            try {
                String elemeMessage = ElemeUtils.takeElemeMessage();
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
                callbackRequestBodyJsonObject = elemeMessageJsonObject.getJSONObject("callbackRequestBody");
                uuid = elemeMessageJsonObject.getString("uuid");
                count = elemeMessageJsonObject.getInt("count");
                int type = callbackRequestBodyJsonObject.getInt("type");

                ElemeCallbackMessage elemeCallbackMessage = new ElemeCallbackMessage();
                elemeCallbackMessage.setRequestId(callbackRequestBodyJsonObject.getString("requestId"));
                elemeCallbackMessage.setType(type);
                elemeCallbackMessage.setAppId(BigInteger.valueOf(callbackRequestBodyJsonObject.getLong("appId")));
                elemeCallbackMessage.setMessage(callbackRequestBodyJsonObject.getString("message"));
                elemeCallbackMessage.setShopId(BigInteger.valueOf(callbackRequestBodyJsonObject.getLong("shopId")));

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(callbackRequestBodyJsonObject.getLong("timestamp"));
                elemeCallbackMessage.setTimestamp(calendar.getTime());
                elemeCallbackMessage.setSignature(callbackRequestBodyJsonObject.getString("signature"));
                elemeCallbackMessage.setUserId(BigInteger.valueOf(callbackRequestBodyJsonObject.getLong("userId")));

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
                if (callbackRequestBodyJsonObject != null) {
                    count = count - 1;
                    if (count > 0) {
                        ElemeUtils.addElemeMessage(callbackRequestBodyJsonObject, uuid, count);
                    } else {
                        markHandleFailureMessage(uuid);
                        DingtalkUtils.send(String.format(Constants.DINGTALK_ERROR_MESSAGE_FORMAT, "饿了么消息处理失败", callbackRequestBodyJsonObject.toString(), e.getClass().getSimpleName(), e.getMessage()));
                    }
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
