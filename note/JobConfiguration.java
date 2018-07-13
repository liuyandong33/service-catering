package build.dream.catering.configurations;

import build.dream.catering.jobs.SimpleJob;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.JobTypeConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.lite.api.JobScheduler;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JobConfiguration {
    @Autowired
    private ZookeeperRegistryCenter zookeeperRegistryCenter;

    @Bean
    public SimpleJob simpleJob() {
        return new SimpleJob();
    }

    @Bean(initMethod = "init")
    public JobScheduler simpleJobScheduler() {
        String cron = "*/1 * * * * ?";
        int shardingTotalCount = 1;
        String shardingItemParameters = "";
        SimpleJob simpleJob = simpleJob();
        return new SpringJobScheduler(simpleJob, zookeeperRegistryCenter, getLiteJobConfiguration(simpleJob.getClass(), cron, shardingTotalCount, shardingItemParameters));
    }

    private LiteJobConfiguration getLiteJobConfiguration(Class<? extends SimpleJob> jobClass, String cron, int shardingTotalCount, String shardingItemParameters) {
        JobCoreConfiguration jobCoreConfiguration = JobCoreConfiguration.newBuilder(jobClass.getName(), cron, shardingTotalCount)
                .shardingItemParameters(shardingItemParameters)
                .build();

        JobTypeConfiguration jobTypeConfiguration = new SimpleJobConfiguration(jobCoreConfiguration, jobClass.getCanonicalName());
        return LiteJobConfiguration.newBuilder(jobTypeConfiguration).overwrite(true).build();
    }
}
