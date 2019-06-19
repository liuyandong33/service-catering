package build.dream.catering.listeners;

import build.dream.catering.services.BeElemeService;
import build.dream.common.utils.JacksonUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class BeElemeListener implements MessageListener<String, String> {
    @Autowired
    private BeElemeService beElemeService;

    @KafkaListener(topics = "${be.eleme.message.topic}")
    @Override
    public void onMessage(ConsumerRecord<String, String> data) {
        String message = data.value();

        Map<String, Object> messageMap = JacksonUtils.readValueAsMap(message, String.class, Object.class);
        String paramsJson = MapUtils.getString(messageMap, "params");
        Map<String, String> params = JacksonUtils.readValueAsMap(paramsJson, String.class, String.class);
        int type = MapUtils.getIntValue(messageMap, "type");
        if (type == 1) {
            beElemeService.handleOrderClaimPush(params);
        } else if (type == 2) {
            beElemeService.handleOrderCreate(params);
        }
    }
}
