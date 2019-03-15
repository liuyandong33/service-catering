package build.dream.catering.tools;

import build.dream.common.models.jpush.PushModel;
import build.dream.common.utils.GsonUtils;
import build.dream.common.utils.JPushUtils;
import build.dream.common.utils.RedisUtils;
import build.dream.common.utils.ThreadUtils;
import org.apache.commons.lang.StringUtils;

public class PushMessageThread implements Runnable {
    private boolean continued = true;
    private String uuid;
    private int count;
    private int interval;

    public PushMessageThread(PushModel pushModel, String uuid, int count, int interval) {
        pushModel.validateAndThrow();
        this.uuid = uuid;
        this.count = count;
        this.interval = interval;
        RedisUtils.set(uuid, GsonUtils.toJson(pushModel, false));
    }

    @Override
    public void run() {
        while (continued) {
            String message = RedisUtils.get(uuid);
            if (StringUtils.isBlank(message)) {
                continued = false;
            } else {
                try {
                    JPushUtils.push(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                count = count - 1;
                if (count <= 0) {
                    RedisUtils.delete(uuid);
                    continued = false;
                } else {
                    ThreadUtils.sleepSafe(interval);
                }
            }
        }
    }
}
