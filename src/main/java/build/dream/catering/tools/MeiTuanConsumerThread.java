package build.dream.catering.tools;

import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.JacksonUtils;
import build.dream.catering.services.MeiTuanService;
import build.dream.catering.utils.MeiTuanUtils;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Map;

public class MeiTuanConsumerThread implements Runnable {
    private static final String ELEME_CONSUMER_THREAD_SIMPLE_NAME = "ElemeConsumerThread";

    private static final int[] ELEME_ORDER_STATE_CHANGE_MESSAGE_TYPES = {12, 14, 15, 17, 18};
    private static final int[] ELEME_REFUND_ORDER_MESSAGE_TYPES = {20, 21, 22, 23, 24, 25, 26, 30, 31, 32, 33, 34, 35, 36};
    private static final int[] ELEME_REMINDER_MESSAGE_TYPES = {45, 46};
    private static final int[] ELEME_DELIVERY_ORDER_STATE_CHANGE_MESSAGE_TYPES = {51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76};
    private static final int[] ELEME_SHOP_STATE_CHANGE_MESSAGE_TYPES = {91, 92};

    private MeiTuanService meiTuanService = ApplicationHandler.getBean(MeiTuanService.class);

    @Override
    public void run() {
        while (true) {
            String meiTuanCallbackMessage = null;
            Integer count = null;
            JSONObject meiTuanCallbackMessageJsonObject = null;
            try {
                List<String> meiTuanCallbackMessageBody = MeiTuanUtils.takeMeiTuanMessage();
                meiTuanCallbackMessage = meiTuanCallbackMessageBody.get(0);
                count = Integer.valueOf(meiTuanCallbackMessageBody.get(1));

                meiTuanCallbackMessageJsonObject = JSONObject.fromObject(meiTuanCallbackMessage);
                String message = meiTuanCallbackMessageJsonObject.getString("message");
                Integer type = meiTuanCallbackMessageJsonObject.getInt("type");

                Map<String, String> parameters = JacksonUtils.readValue(message, Map.class);
                if (type == 1) {
                    meiTuanService.handleOrderEffectiveCallback(parameters);
                } else if (type == 2) {
                    meiTuanService.handleOrderCancelCallback(parameters);
                } else if (type == 3) {
                    meiTuanService.handleOrderRefundCallback(parameters);
                }
                int a = 100;
            } catch (Exception e) {
                e.printStackTrace();
                if (StringUtils.isNotBlank(meiTuanCallbackMessage)) {
                    count = count - 1;
                    if (count > 0) {
                        try {
                            MeiTuanUtils.addMeiTuanMessageBlockingQueue(meiTuanCallbackMessage, count);
                        } catch (InterruptedException e1) {
                            saveMeiTuanCallbackMessage(meiTuanCallbackMessageJsonObject);
                        }
                    } else {
                        saveMeiTuanCallbackMessage(meiTuanCallbackMessageJsonObject);
                    }
                }
            }
        }
    }

    private void saveMeiTuanCallbackMessage(JSONObject callbackJsonObject) {

    }
}
