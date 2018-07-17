package build.dream.catering.configurations;

import build.dream.catering.constants.Constants;
import build.dream.common.utils.ConfigurationUtils;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class JobRegistryCenterConfiguration {
    @Bean(initMethod = "init")
    public ZookeeperRegistryCenter zookeeperRegistryCenter() throws IOException {
        String serverList = ConfigurationUtils.getConfiguration(Constants.ELASTICJOB_ZOOKEEPER_SERVER_LISTS);
        String namespace = ConfigurationUtils.getConfiguration(Constants.ELASTICJOB_ZOOKEEPER_NAMESPACE);
        return new ZookeeperRegistryCenter(new ZookeeperConfiguration(serverList, namespace));
    }
}
