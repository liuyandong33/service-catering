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
    private ZookeeperRegistryCenter regCenter;

    public StockJobConfiguration() {
    }

    @Bean
    public SimpleJob simpleJob() {
        return new StockSimpleJob();
    }


    @Bean(initMethod = "init")
    public JobScheduler simpleJobScheduler(final SimpleJob simpleJob) {
        String cron = "* * */1 * * ?";
        int shardingTotalCount = 2;
        String shardingItemParameters = "";
        return new SpringJobScheduler(simpleJob, regCenter, getLiteJobConfiguration(simpleJob.getClass(), cron, shardingTotalCount, shardingItemParameters));
    }

    /**
     * @Description 任务配置类
     */
    private LiteJobConfiguration getLiteJobConfiguration(final Class<? extends SimpleJob> jobClass,
                                                         final String cron,
                                                         final int shardingTotalCount,
                                                         final String shardingItemParameters) {


        return LiteJobConfiguration
                .newBuilder(
                        new SimpleJobConfiguration(
                                JobCoreConfiguration.newBuilder(
                                        jobClass.getName(), cron, shardingTotalCount)
                                        .shardingItemParameters(shardingItemParameters)
                                        .build()
                                , jobClass.getCanonicalName()
                        )
                )
                .overwrite(true)
                .build();

    }
}
