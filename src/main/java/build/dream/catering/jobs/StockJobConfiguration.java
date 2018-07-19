package build.dream.catering.jobs;

import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.lite.api.JobScheduler;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StockJobConfiguration {
    @Autowired
    private ZookeeperRegistryCenter zookeeperRegistryCenter;

    @Bean
    public SimpleJob simpleJob() {
        return new StockSimpleJob();
    }

    @Bean(initMethod = "init")
    public JobScheduler simpleJobScheduler(SimpleJob simpleJob) {
        String cron = "*/1 * * * * ?";
        int shardingTotalCount = 2;
        String shardingItemParameters = "0=aa,1=bb";
        return new SpringJobScheduler(simpleJob, zookeeperRegistryCenter, buildLiteJobConfiguration(simpleJob.getClass(), cron, shardingTotalCount, shardingItemParameters));
    }

    private LiteJobConfiguration buildLiteJobConfiguration(Class<? extends SimpleJob> jobClass, String cron, int shardingTotalCount, String shardingItemParameters) {
        JobCoreConfiguration jobCoreConfiguration = JobCoreConfiguration.newBuilder(jobClass.getName(), cron, shardingTotalCount).shardingItemParameters(shardingItemParameters).build();
        SimpleJobConfiguration simpleJobConfiguration = new SimpleJobConfiguration(jobCoreConfiguration, jobClass.getCanonicalName());
        LiteJobConfiguration liteJobConfiguration = LiteJobConfiguration.newBuilder(simpleJobConfiguration).overwrite(true).build();
        return liteJobConfiguration;
    }
}
