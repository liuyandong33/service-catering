package build.dream.catering.listeners;

import build.dream.catering.services.DietOrderService;
import build.dream.catering.services.PosService;
import build.dream.common.models.rocketmq.DelayedOrTimedModel;
import build.dream.common.models.rocketmq.DelayedOrTimedType;
import build.dream.common.utils.JacksonUtils;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RocketMQMessageListener(topic = "${delayed.or.timed.rocket.mq.topic}", consumerGroup = "${rocketmq.consumer.group}")
public class DelayedOrTimedRocketMQListener implements RocketMQListener<String> {
    @Autowired
    private DietOrderService dietOrderService;
    @Autowired
    private PosService posService;

    @Override
    public void onMessage(String message) {
        DelayedOrTimedModel delayedOrTimedModel = JacksonUtils.readValue(message, DelayedOrTimedModel.class);
        DelayedOrTimedType delayedOrTimedType = delayedOrTimedModel.getType();
        Map<String, Object> data = delayedOrTimedModel.getData();

        switch (delayedOrTimedType) {
            case DELAYED_OR_TIMED_TYPE_DIET_ORDER_INVALID:
                dietOrderService.cancelOrder(data);
                break;
            case DELAYED_OR_TIMED_TYPE_POS_MQTT_TOKEN_INVALID:
                posService.tokenInvalid(data);
                break;
        }
    }
}
