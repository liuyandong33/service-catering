package build.dream.catering.listeners;

import build.dream.catering.services.PosService;
import build.dream.common.utils.JacksonUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class OfflinePayAsyncNotifyMessageListener implements MessageListener<String, String> {
    @Autowired
    private PosService posService;

    @KafkaListener(topics = "${offline.pay.async.notify.message.topic}")
    @Override
    public void onMessage(ConsumerRecord<String, String> data) {
        String key = data.key();
        String value = data.value();
        Map<String, String> params = JacksonUtils.readValueAsMap(value, String.class, String.class);
        posService.handleOfflinePayAsyncNotify(params);
    }
}
