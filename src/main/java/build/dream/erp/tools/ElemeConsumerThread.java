package build.dream.erp.tools;

import build.dream.common.utils.ApplicationHandler;
import build.dream.erp.services.ElemeService;
import build.dream.erp.utils.ElemeUtils;
import net.sf.json.JSONObject;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.math.BigInteger;
import java.util.List;

public class ElemeConsumerThread implements Runnable {
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
            Integer count = null;
            try {
                List<String> elemeMessageBody = ElemeUtils.takeElemeMessage();
                elemeMessage = elemeMessageBody.get(0);
                count = Integer.valueOf(elemeMessageBody.get(1));
                System.err.println(elemeMessage);

                JSONObject callbackJsonObject = JSONObject.fromObject(elemeMessage);

                BigInteger shopId = BigInteger.valueOf(callbackJsonObject.getLong("shopId"));
                String message = callbackJsonObject.getString("message");
                Integer type = callbackJsonObject.getInt("type");

                if (type == 10) {
                    elemeService.saveElemeOrder(shopId, message, type);
                } else if (ArrayUtils.contains(ELEME_ORDER_STATE_CHANGE_MESSAGE_TYPES, type)) {
                    elemeService.handleElemeOrderStateChangeMessage(shopId, message, type);
                } else if (ArrayUtils.contains(ELEME_REFUND_ORDER_MESSAGE_TYPES, type)) {
                    elemeService.handleElemeRefundOrderMessage(shopId, message, type);
                } else if (ArrayUtils.contains(ELEME_REMINDER_MESSAGE_TYPES, type)) {
                    elemeService.handleElemeReminderMessage(shopId, message, type);
                } else if (ArrayUtils.contains(ELEME_DELIVERY_ORDER_STATE_CHANGE_MESSAGE_TYPES, type)) {
                    elemeService.handleElemeDeliveryOrderStateChangeMessage(shopId, message, type);
                } else if (ArrayUtils.contains(ELEME_SHOP_STATE_CHANGE_MESSAGE_TYPES, type)) {
                    elemeService.handleElemeShopStateChangeMessage(shopId, message, type);
                } else if (type == 100) {
                    elemeService.handleAuthorizationStateChangeMessage(shopId, message, type);
                }
            } catch (Exception e) {
                if (StringUtils.isNotBlank(elemeMessage)) {
                    count = count - 1;
                    if (count > 0) {
                        try {
                            ElemeUtils.addElemeMessageBlockingQueue(elemeMessage, count);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    } else {
                        System.out.println(elemeMessage);
                    }
                }
            }
        }
    }
}
