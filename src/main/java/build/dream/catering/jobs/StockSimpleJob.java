package build.dream.catering.jobs;

import build.dream.catering.utils.ThreadUtils;
import build.dream.common.utils.LogUtils;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;

public class StockSimpleJob implements SimpleJob {
    @Override
    public void execute(ShardingContext shardingContext) {
        LogUtils.info(String.format("------Thread ID: %s, 任务总片数: %s, " + "当前分片项: %s.当前参数: %s,"+ "当前任务名称: %s.当前任务参数: %s",
                Thread.currentThread().getId(),
                shardingContext.getShardingTotalCount(),
                shardingContext.getShardingItem(),
                shardingContext.getShardingParameter(),
                shardingContext.getJobName(),
                shardingContext.getJobParameter()
        ));
        ThreadUtils.sleepSafe(10000);
    }
}
