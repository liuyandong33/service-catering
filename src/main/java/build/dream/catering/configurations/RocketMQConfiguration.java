package build.dream.catering.configurations;

import build.dream.common.rocketmq.RocketMQProperties;
import build.dream.common.utils.RocketMQUtils;
import com.aliyun.openservices.ons.api.Consumer;
import com.aliyun.openservices.ons.api.bean.ProducerBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class RocketMQConfiguration {
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private Environment environment;

    @Bean
    @ConfigurationProperties(prefix = "rocketmq")
    public RocketMQProperties rocketMQProperties() {
        return new RocketMQProperties();
    }

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public ProducerBean buildProducer() {
        return RocketMQUtils.buildProducer(rocketMQProperties());
    }

    @Bean
    public Consumer consumer() {
        return RocketMQUtils.buildConsumer(rocketMQProperties(), applicationContext, environment);
    }
}
