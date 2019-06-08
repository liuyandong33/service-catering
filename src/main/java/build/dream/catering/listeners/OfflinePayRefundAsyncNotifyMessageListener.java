package build.dream.catering.listeners;

import build.dream.catering.constants.Constants;
import build.dream.common.utils.ConfigurationUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.stereotype.Component;

@Component
public class OfflinePayRefundAsyncNotifyMessageListener implements MessageListener<String, String> {
    private static final String OFFLINE_PAY_REFUND_ALIPAY_ASYNC_NOTIFY_MESSAGE_TOPIC = ConfigurationUtils.getConfiguration(Constants.OFFLINE_PAY_REFUND_ALIPAY_ASYNC_NOTIFY_MESSAGE_TOPIC);
    private static final String OFFLINE_PAY_REFUND_WEI_XIN_ASYNC_NOTIFY_MESSAGE_TOPIC = ConfigurationUtils.getConfiguration(Constants.OFFLINE_PAY_REFUND_WEI_XIN_ASYNC_NOTIFY_MESSAGE_TOPIC);

    @KafkaListener(topics = {"${offline.pay.refund.alipay.async.notify.message.topic}", "${offline.pay.refund.wei.xin.async.notify.message.topic}"})
    @Override
    public void onMessage(ConsumerRecord<String, String> data) {
        String value = data.value();
        String topic = data.topic();
        if (OFFLINE_PAY_REFUND_ALIPAY_ASYNC_NOTIFY_MESSAGE_TOPIC.equals(topic)) {

        } else if (OFFLINE_PAY_REFUND_WEI_XIN_ASYNC_NOTIFY_MESSAGE_TOPIC.equals(topic)) {

        }
    }
}
