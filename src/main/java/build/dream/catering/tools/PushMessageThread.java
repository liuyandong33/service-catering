package build.dream.catering.tools;

import build.dream.catering.constants.Constants;
import build.dream.common.api.ApiRest;
import build.dream.common.utils.*;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class PushMessageThread implements Runnable {
    private boolean continued = true;
    private String uuid;
    private int count;
    private int interval;

    public PushMessageThread(String message, String uuid, int count, int interval) {
        this.uuid = uuid;
        this.count = count;
        this.interval = interval;
        CacheUtils.set(uuid, message);
    }

    @Override
    public void run() {
        while (continued) {
            String message = CacheUtils.get(uuid);
            if (StringUtils.isBlank(message)) {
                continued = false;
            } else {
                try {
                    Map<String, String> pushRequestParameters = new HashMap<String, String>();
                    pushRequestParameters.put("message", message);
                    ApiRest apiRest = ProxyUtils.doPostWithRequestParameters(Constants.SERVICE_NAME_OUT, "jpush", "push", pushRequestParameters);
                    LogUtils.info(GsonUtils.toJson(apiRest) + "-" + count);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                count = count - 1;
                if (count <= 0) {
                    CacheUtils.delete(uuid);
                    continued = false;
                } else {
                    try {
                        Thread.sleep(interval);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
