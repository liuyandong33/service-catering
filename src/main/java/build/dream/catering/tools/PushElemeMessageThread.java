package build.dream.catering.tools;

import build.dream.common.utils.CacheUtils;
import build.dream.common.utils.LogUtils;
import build.dream.common.utils.WebUtils;
import org.apache.commons.lang.StringUtils;

public class PushElemeMessageThread implements Runnable {
    private boolean continued = true;
    private String uuid;
    private int count;
    private int interval;

    public PushElemeMessageThread(String message, String uuid, int count, int interval) {
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
                    String result = WebUtils.doGetWithRequestParameters("http://www.baidu.com", null);
                    LogUtils.info(result);
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
