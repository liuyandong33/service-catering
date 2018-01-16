package build.dream.catering.configurations;

import build.dream.common.utils.ConfigurationUtils;
import build.dream.catering.constants.Constants;
import build.dream.catering.listeners.ElemeCallbackMessageListener;
import build.dream.catering.listeners.MeiTuanCallbackMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import java.io.IOException;

@Configuration
public class RedisMessageListenerConfiguration {
    @Autowired
    private JedisConnectionFactory jedisConnectionFactory;
    @Autowired
    private ElemeCallbackMessageListener elemeCallbackMessageListener;
    @Autowired
    private MeiTuanCallbackMessageListener meiTuanCallbackMessageListener;

    @Bean(destroyMethod = "destroy")
    public RedisMessageListenerContainer redisMessageListenerContainer() throws IOException {
        RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
        redisMessageListenerContainer.setConnectionFactory(jedisConnectionFactory);
        String elemeCallbackMessageChannelTopic = ConfigurationUtils.getConfiguration(Constants.ELEME_CALLBACK_MESSAGE_CHANNEL_TOPIC) + "_" + ConfigurationUtils.getConfiguration(Constants.PARTITION_CODE);
        redisMessageListenerContainer.addMessageListener(elemeCallbackMessageListener, new ChannelTopic(elemeCallbackMessageChannelTopic));

        String meiTuanCallbackMessageChannelTopic = ConfigurationUtils.getConfiguration(Constants.MEI_TUAN_CALLBACK_MESSAGE_CHANNEL_TOPIC) + "_" + ConfigurationUtils.getConfiguration(Constants.PARTITION_CODE);
        redisMessageListenerContainer.addMessageListener(meiTuanCallbackMessageListener, new ChannelTopic(meiTuanCallbackMessageChannelTopic));
        return redisMessageListenerContainer;
    }
}
