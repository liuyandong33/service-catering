package build.dream.catering.jobs;

import com.dangdang.ddframe.job.api.ShardingContext;

import java.util.UUID;

public class SimpleJob implements com.dangdang.ddframe.job.api.simple.SimpleJob {
    @Override
    public void execute(ShardingContext shardingContext) {
        System.out.println(UUID.randomUUID().toString());
    }
}
