package build.dream.erp.listeners;

import build.dream.common.utils.LogUtils;
import build.dream.erp.utils.ElemeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

@Component
public class ElemeCallbackMessageListener implements MessageListener {
    private static final String ELEME_CALLBACK_MESSAGE_LISTENER_SIMPLE_NAME = "ElemeCallbackMessageListener";
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        RedisSerializer<String> stringRedisSerializer = stringRedisTemplate.getStringSerializer();
        String body = stringRedisSerializer.deserialize(message.getBody());
        try {
            ElemeUtils.addElemeMessageBlockingQueue(body, 10);
        } catch (InterruptedException e) {
            LogUtils.error("处理饿了么回调信息失败", ELEME_CALLBACK_MESSAGE_LISTENER_SIMPLE_NAME, "onMessage", e);
        }
    }
}
