package build.dream.catering.listeners;

import build.dream.catering.models.dietorder.CancelOrderModel;
import build.dream.catering.services.DietOrderService;
import build.dream.common.utils.JacksonUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.Map;

@Component
public class OrderInvalidMessageListener implements MessageListener<String, String> {
    @Autowired
    private DietOrderService dietOrderService;

    @KafkaListener(topics = "${order.invalid.message.topic}")
    @Override
    public void onMessage(ConsumerRecord<String, String> data) {
        String key = data.key();
        String value = data.value();

        Map<String, Object> info = JacksonUtils.readValueAsMap(value, String.class, Object.class);
        CancelOrderModel cancelOrderModel = new CancelOrderModel();
        cancelOrderModel.setOrderId(BigInteger.valueOf(MapUtils.getLongValue(info, "orderId")));
        try {
            dietOrderService.cancelOrder(cancelOrderModel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
