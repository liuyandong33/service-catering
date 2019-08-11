package build.dream.catering.configurations;

import build.dream.catering.rocketmq.RocketMQProperties;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.bean.ProducerBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class RocketMQProducerConfiguration {
    @Autowired
    private RocketMQProperties rocketMQProperties;

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public ProducerBean buildProducer() {
        Properties properties = new Properties();
        properties.setProperty(PropertyKeyConst.AccessKey, rocketMQProperties.getAccessKey());
        properties.setProperty(PropertyKeyConst.SecretKey, rocketMQProperties.getSecretKey());
        properties.setProperty(PropertyKeyConst.NAMESRV_ADDR, rocketMQProperties.getNameSrvAddr());
        properties.setProperty(PropertyKeyConst.GROUP_ID, rocketMQProperties.getGroupId());
        ProducerBean producerBean = new ProducerBean();
        producerBean.setProperties(properties);
        return producerBean;
    }
}
