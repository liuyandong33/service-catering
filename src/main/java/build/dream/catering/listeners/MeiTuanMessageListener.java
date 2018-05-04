package build.dream.catering.listeners;

import build.dream.common.utils.LogUtils;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class MeiTuanMessageListener {
    @KafkaListener(topics = "aa")
    public void listenMeiTuanMessage(String meiTuanMessage) {
        LogUtils.info(meiTuanMessage);
    }
}
