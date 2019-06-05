package build.dream.catering.listeners;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class TestKafkaMessageListener implements AcknowledgingMessageListener<String, String> {
    @KafkaListener(topics = "aaaa")
    @Override
    public void onMessage(ConsumerRecord<String, String> data, Acknowledgment acknowledgment) {
//        acknowledgment.acknowledge();
        System.out.println(data.key() + "======" + data.value());
        int a = 100;
    }
}
