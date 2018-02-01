package build.dream.catering.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
public class RedisMessageListenerConfiguration {
    @Autowired
    private JedisConnectionFactory jedisConnectionFactory;

    @Bean(destroyMethod = "destroy")
    public RedisMessageListenerContainer redisMessageListenerContainer() {
        RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
        redisMessageListenerContainer.setConnectionFactory(jedisConnectionFactory);
        return redisMessageListenerContainer;
    }
}
