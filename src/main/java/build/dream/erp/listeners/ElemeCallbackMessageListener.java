package build.dream.erp.listeners;

import build.dream.common.utils.CacheUtils;
import build.dream.common.utils.LogUtils;
import build.dream.erp.utils.ElemeUtils;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class ElemeCallbackMessageListener implements MessageListener {
    private static final String ELEME_CALLBACK_MESSAGE_LISTENER_SIMPLE_NAME = "ElemeCallbackMessageListener";
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        RedisSerializer<String> stringRedisSerializer = stringRedisTemplate.getStringSerializer();
        String body = stringRedisSerializer.deserialize(message.getBody());
        JSONObject messageJsonObject = JSONObject.fromObject(body);
        String uuid = messageJsonObject.getString("uuid");
        boolean setnxSuccessful = CacheUtils.setnx(uuid, uuid);
        if (setnxSuccessful) {
            CacheUtils.expire(uuid, 1800, TimeUnit.SECONDS);
            try {
                ElemeUtils.addElemeMessageBlockingQueue(messageJsonObject.getString("callbackRequestBody"), 10);
            } catch (InterruptedException e) {
                LogUtils.error("处理饿了么回调信息失败", ELEME_CALLBACK_MESSAGE_LISTENER_SIMPLE_NAME, "onMessage", e);
            }
        }
    }
}
