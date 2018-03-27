package build.dream.catering.tools;

import build.dream.catering.constants.Constants;
import build.dream.catering.services.MeiTuanService;
import build.dream.catering.utils.MeiTuanUtils;
import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.LogUtils;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

public class MeiTuanConsumerThread implements Runnable {
    private static final String MEI_TUAN_CONSUMER_THREAD_SIMPLE_NAME = "MeiTuanConsumerThread";

    private MeiTuanService meiTuanService = ApplicationHandler.getBean(MeiTuanService.class);

    @Override
    public void run() {
        while (true) {
            JSONObject callbackParametersJsonObject = null;
            String uuid = null;
            int count = 0;
            int type = 0;
            try {
                String meiTuanMessage = MeiTuanUtils.takeMeiTuanMessage();
                if (StringUtils.isBlank(meiTuanMessage)) {
                    continue;
                }

                if (!ApplicationHandler.isJson(meiTuanMessage)) {
                    continue;
                }

                if (!ApplicationHandler.isRightJson(meiTuanMessage, Constants.MEI_TUAN_MESSAGE_SCHEMA_FILE_PATH)) {
                    continue;
                }

                JSONObject meiTuanMessageJsonObject = JSONObject.fromObject(meiTuanMessage);

                callbackParametersJsonObject = meiTuanMessageJsonObject.getJSONObject("callbackParameters");
                uuid = meiTuanMessageJsonObject.getString("uuid");
                count = meiTuanMessageJsonObject.getInt("count");
                type = meiTuanMessageJsonObject.getInt("type");

                if (type == Constants.MEI_TUAN_CALLBACK_TYPE_ORDER_EFFECTIVE) {
                    meiTuanService.handleOrderEffectiveCallback(callbackParametersJsonObject, uuid, type);
                } else if (type == Constants.MEI_TUAN_CALLBACK_TYPE_ORDER_CANCEL) {
                    meiTuanService.handleOrderCancelCallback(callbackParametersJsonObject);
                } else if (type == Constants.MEI_TUAN_CALLBACK_TYPE_ORDER_REFUND) {
                    meiTuanService.handleOrderRefundCallback(callbackParametersJsonObject);
                }
            } catch (Exception e) {
                if (callbackParametersJsonObject != null) {
                    count = count - 1;
                    if (count > 0) {
                        MeiTuanUtils.addMeiTuanMessage(callbackParametersJsonObject, uuid, count, type);
                    }
                }
                LogUtils.error("处理美团消息失败", MEI_TUAN_CONSUMER_THREAD_SIMPLE_NAME, "run", e);
            }
        }
    }

    private void saveMeiTuanCallbackMessage(JSONObject callbackJsonObject) {

    }
}
