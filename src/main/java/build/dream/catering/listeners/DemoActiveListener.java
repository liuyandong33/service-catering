package build.dream.catering.listeners;

import build.dream.common.utils.LogUtils;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class DemoActiveListener {
    @JmsListener(destination = "hello.active")
    public void onMessage(String message) {
        LogUtils.info(message);
    }
}
