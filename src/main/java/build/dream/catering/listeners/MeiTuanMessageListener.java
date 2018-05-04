package build.dream.catering.listeners;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.stereotype.Component;

@Component
public class MeiTuanMessageListener implements MessageListener<String, String> {
    @KafkaListener(topics = "${mei.tuan.message.topic}")
    @Override
    public void onMessage(ConsumerRecord<String, String> data) {

    }
}
