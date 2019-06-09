package build.dream.catering.listeners;

import build.dream.catering.constants.Constants;
import build.dream.catering.services.PosService;
import build.dream.common.utils.ConfigurationUtils;
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
    private static final String OFFLINE_PAY_ALIPAY_ASYNC_NOTIFY_MESSAGE_TOPIC = ConfigurationUtils.getConfiguration(Constants.OFFLINE_PAY_ALIPAY_ASYNC_NOTIFY_MESSAGE_TOPIC);
    private static final String OFFLINE_PAY_UMPAY_ASYNC_NOTIFY_MESSAGE_TOPIC = ConfigurationUtils.getConfiguration(Constants.OFFLINE_PAY_UMPAY_ASYNC_NOTIFY_MESSAGE_TOPIC);

    @KafkaListener(topics = "${offline.pay.alipay.async.notify.message.topic}")
    @Override
    public void onMessage(ConsumerRecord<String, String> data) {
        String value = data.value();
        String topic = data.topic();
        if (OFFLINE_PAY_ALIPAY_ASYNC_NOTIFY_MESSAGE_TOPIC.equals(topic)) {
            Map<String, String> params = JacksonUtils.readValueAsMap(value, String.class, String.class);
            posService.handleOfflinePayAsyncNotify(params, Constants.CHANNEL_TYPE_ALIPAY);
        } else if (OFFLINE_PAY_UMPAY_ASYNC_NOTIFY_MESSAGE_TOPIC.equals(topic)) {
            Map<String, String> params = JacksonUtils.readValueAsMap(value, String.class, String.class);
            posService.handleOfflinePayAsyncNotify(params, Constants.CHANNEL_TYPE_UMPAY);
        }
    }
}
