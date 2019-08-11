package build.dream.catering.listeners;

import build.dream.catering.constants.Constants;
import build.dream.catering.services.DietOrderService;
import build.dream.catering.services.PosService;
import build.dream.common.annotations.RocketMQMessageListener;
import build.dream.common.models.rocketmq.DelayedOrTimedModel;
import build.dream.common.models.rocketmq.DelayedOrTimedType;
import build.dream.common.utils.JacksonUtils;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RocketMQMessageListener(topic = "${delayed.or.timed.rocket.mq.topic}")
public class DelayedOrTimedRocketMQListener implements MessageListener {
    @Autowired
    private DietOrderService dietOrderService;
    @Autowired
    private PosService posService;

    @Override
    public Action consume(Message message, ConsumeContext context) {
        String body = new String(message.getBody(), Constants.CHARSET_UTF_8);
        DelayedOrTimedModel delayedOrTimedModel = JacksonUtils.readValue(body, DelayedOrTimedModel.class);
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
        return Action.CommitMessage;
    }
}
