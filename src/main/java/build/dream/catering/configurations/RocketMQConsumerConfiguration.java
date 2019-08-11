package build.dream.catering.configurations;

import build.dream.catering.rocketmq.RocketMQProperties;
import build.dream.common.annotations.RocketMQMessageListener;
import com.aliyun.openservices.ons.api.Consumer;
import com.aliyun.openservices.ons.api.MessageListener;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;

import java.util.Collection;
import java.util.Properties;

@Configuration
public class RocketMQConsumerConfiguration {
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private RocketMQProperties rocketMQProperties;

    @Bean
    public Consumer consumer() {
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.AccessKey, rocketMQProperties.getAccessKey());
        properties.put(PropertyKeyConst.SecretKey, rocketMQProperties.getSecretKey());
        properties.put(PropertyKeyConst.NAMESRV_ADDR, rocketMQProperties.getNameSrvAddr());
        properties.put(PropertyKeyConst.GROUP_ID, rocketMQProperties.getGroupId());
        Consumer consumer = ONSFactory.createConsumer(properties);

        Collection<MessageListener> messageListeners = applicationContext.getBeansOfType(MessageListener.class).values();
        for (MessageListener messageListener : messageListeners) {
            RocketMQMessageListener rocketMQMessageListener = AnnotationUtils.findAnnotation(messageListener.getClass(), RocketMQMessageListener.class);
            consumer.subscribe(rocketMQMessageListener.topic(), rocketMQMessageListener.subExpression(), messageListener);
        }
        consumer.start();
        return consumer;
    }
}
