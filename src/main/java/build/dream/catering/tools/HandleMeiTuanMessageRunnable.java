package build.dream.catering.tools;

import build.dream.catering.constants.Constants;
import build.dream.catering.services.MeiTuanService;
import build.dream.catering.utils.DingtalkUtils;
import build.dream.catering.utils.ThreadUtils;
import build.dream.common.utils.GsonUtils;
import net.sf.json.JSONObject;

public class HandleMeiTuanMessageRunnable implements Runnable {
    private MeiTuanService meiTuanService;
    private JSONObject callbackParametersJsonObject;
    private boolean continued = true;
    private int count;
    private int interval;
    private String uuid;
    private int type;

    public HandleMeiTuanMessageRunnable(MeiTuanService meiTuanService, JSONObject callbackParametersJsonObject, int count, int interval, String uuid, int type) {
        this.meiTuanService = meiTuanService;
        this.callbackParametersJsonObject = callbackParametersJsonObject;
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
                    meiTuanService.handleOrderEffectiveCallback(callbackParametersJsonObject, uuid, type);
                } else if (type == Constants.MEI_TUAN_CALLBACK_TYPE_ORDER_CANCEL) {
                    meiTuanService.handleOrderCancelCallback(callbackParametersJsonObject, uuid, type);
                } else if (type == Constants.MEI_TUAN_CALLBACK_TYPE_ORDER_REFUND) {
                    meiTuanService.handleOrderRefundCallback(callbackParametersJsonObject, uuid, type);
                } else if (type == 8) {
                    meiTuanService.handleBindingStoreCallback(callbackParametersJsonObject, uuid, type);
                }
            } catch (Exception e) {
                isNormal = false;
                if (count == 1) {
                    DingtalkUtils.send(String.format(Constants.DINGTALK_ERROR_MESSAGE_FORMAT, "饿了么消息处理失败", GsonUtils.toJson(callbackParametersJsonObject), e.getClass().getName(), e.getMessage()));
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
