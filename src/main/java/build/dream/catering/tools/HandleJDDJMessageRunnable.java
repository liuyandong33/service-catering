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
                }
            } catch (Exception e) {
                isNormal = false;
                if (count == 1) {
                    DingtalkUtils.send(ConfigurationUtils.getConfiguration(Constants.DINGTALK_ERROR_CHAT_ID), String.format(Constants.DINGTALK_ERROR_MESSAGE_FORMAT, "京东到家消息处理失败", GsonUtils.toJson(body), e.getClass().getName(), e.getMessage()));
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
