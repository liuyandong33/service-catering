package build.dream.catering.listeners;

import build.dream.common.utils.GsonUtils;
import build.dream.common.utils.LogUtils;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RabbitListener(queues = "hello", group = "aaaaa")
public class DemoRabbitListener {
    @RabbitHandler
    public void onMessage(Map<String, String> message) {
        LogUtils.info(GsonUtils.toJson(message));
    }
}
