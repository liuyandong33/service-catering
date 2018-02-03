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
            String meiTuanMessage = null;
            try {
                meiTuanMessage = MeiTuanUtils.takeMeiTuanMessage();
                JSONObject meiTuanMessageJsonObject = JSONObject.fromObject(meiTuanMessage);

                JSONObject callbackParametersJsonObject = meiTuanMessageJsonObject.getJSONObject("callbackParameters");
                Integer type = callbackParametersJsonObject.getInt("type");

                if (type == Constants.MEI_TUAN_CALLBACK_TYPE_ORDER_EFFECTIVE) {
                    meiTuanService.handleOrderEffectiveCallback(callbackParametersJsonObject);
                } else if (type == Constants.MEI_TUAN_CALLBACK_TYPE_ORDER_CANCEL) {
                    meiTuanService.handleOrderCancelCallback(callbackParametersJsonObject);
                } else if (type == Constants.MEI_TUAN_CALLBACK_TYPE_ORDER_REFUND) {
                    meiTuanService.handleOrderRefundCallback(callbackParametersJsonObject);
                }
            } catch (Exception e) {
                if (StringUtils.isNotBlank(meiTuanMessage)) {
                    MeiTuanUtils.addMeiTuanMessage(meiTuanMessage);
                }
                LogUtils.error("保存饿了么消息失败", MEI_TUAN_CONSUMER_THREAD_SIMPLE_NAME, "run", e);
            }
        }
    }

    private void saveMeiTuanCallbackMessage(JSONObject callbackJsonObject) {

    }
}
