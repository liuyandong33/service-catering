package build.dream.erp.listeners;

import build.dream.common.utils.LogUtils;
import build.dream.erp.utils.ElemeUtils;
import build.dream.erp.utils.MeiTuanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

@Component
public class MeiTuanCallbackMessageListener implements MessageListener {
    private static final String MEI_TUAN_CALLBACK_MESSAGE_LISTENER_SIMPLE_NAME = "MeiTuanCallbackMessageListener";
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        RedisSerializer<String> stringRedisSerializer = stringRedisTemplate.getStringSerializer();
        String body = stringRedisSerializer.deserialize(message.getBody());
        try {
            MeiTuanUtils.addMeiTuanMessageBlockingQueue(body, 10);
        } catch (InterruptedException e) {
            LogUtils.error("处理美团回调信息失败", MEI_TUAN_CALLBACK_MESSAGE_LISTENER_SIMPLE_NAME, "onMessage", e);
        }
    }
}
