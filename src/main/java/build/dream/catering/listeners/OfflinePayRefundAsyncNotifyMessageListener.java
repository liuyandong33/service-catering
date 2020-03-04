package build.dream.catering.listeners;

import build.dream.catering.constants.ConfigurationKeys;
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
public class OfflinePayRefundAsyncNotifyMessageListener implements MessageListener<String, String> {
    private static final String OFFLINE_PAY_REFUND_ALIPAY_ASYNC_NOTIFY_MESSAGE_TOPIC = ConfigurationUtils.getConfiguration(ConfigurationKeys.OFFLINE_PAY_REFUND_ALIPAY_ASYNC_NOTIFY_MESSAGE_TOPIC);
    private static final String OFFLINE_PAY_REFUND_WEI_XIN_ASYNC_NOTIFY_MESSAGE_TOPIC = ConfigurationUtils.getConfiguration(ConfigurationKeys.OFFLINE_PAY_REFUND_WEI_XIN_ASYNC_NOTIFY_MESSAGE_TOPIC);

    @Autowired
    private PosService posService;

    @KafkaListener(topics = {"${offline.pay.refund.alipay.async.notify.message.topic}", "${offline.pay.refund.wei.xin.async.notify.message.topic}"})
    @Override
    public void onMessage(ConsumerRecord<String, String> data) {
        String value = data.value();
        String topic = data.topic();
        Map<String, String> params = JacksonUtils.readValueAsMap(value, String.class, String.class);
        if (OFFLINE_PAY_REFUND_WEI_XIN_ASYNC_NOTIFY_MESSAGE_TOPIC.equals(topic)) {
            posService.handleOfflineRefundPayAsyncNotify(params, Constants.CHANNEL_TYPE_WEI_XIN);
        } else if (OFFLINE_PAY_REFUND_ALIPAY_ASYNC_NOTIFY_MESSAGE_TOPIC.equals(topic)) {
            posService.handleOfflineRefundPayAsyncNotify(params, Constants.CHANNEL_TYPE_ALIPAY);
        }
    }
}
