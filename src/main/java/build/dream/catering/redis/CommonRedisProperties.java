package build.dream.catering.redis;

import build.dream.common.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "common.redis")
public class CommonRedisProperties extends RedisProperties {
}
