package build.dream.catering.receivers;

import build.dream.catering.constants.Constants;
import build.dream.catering.services.DietOrderService;
import build.dream.catering.services.PosService;
import build.dream.common.models.rocketmq.DelayedMessageModel;
import build.dream.common.models.rocketmq.DelayedType;
import build.dream.common.utils.JacksonUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DelayedRabbitMQMessageReceiver {
    @Autowired
    private DietOrderService dietOrderService;
    @Autowired
    private PosService posService;

    @RabbitListener(queues = "${delayed.rabbitmq.message.queue}")
    public void messageReceived(Message message) {
        String body = new String(message.getBody(), Constants.CHARSET_UTF_8);
        DelayedMessageModel delayedMessageModel = JacksonUtils.readValue(body, DelayedMessageModel.class);
        DelayedType delayedType = delayedMessageModel.getType();
        Map<String, Object> data = delayedMessageModel.getData();
        switch (delayedType) {
            case DELAYED_TYPE_DIET_ORDER_INVALID:
                dietOrderService.cancelOrder(data);
                break;
            case DELAYED_TYPE_POS_MQTT_TOKEN_INVALID:
                posService.handleMqttTokenInvalid(data);
                break;
        }
    }
}
