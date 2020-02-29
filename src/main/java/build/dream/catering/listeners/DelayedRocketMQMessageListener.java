package build.dream.catering.listeners;

import build.dream.catering.constants.Constants;
import build.dream.catering.services.DietOrderService;
import build.dream.catering.services.PosService;
import build.dream.common.annotations.RocketMQMessageListener;
import build.dream.common.models.rocketmq.DelayedMessageModel;
import build.dream.common.models.rocketmq.DelayedType;
import build.dream.common.utils.JacksonUtils;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RocketMQMessageListener(topic = "${delayed.topic}")
public class DelayedRocketMQMessageListener implements MessageListener {
    @Autowired
    private DietOrderService dietOrderService;
    @Autowired
    private PosService posService;

    @Override
    public Action consume(Message message, ConsumeContext context) {
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
        return Action.CommitMessage;
    }
}
