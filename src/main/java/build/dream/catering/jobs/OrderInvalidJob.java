package build.dream.catering.jobs;

import build.dream.common.utils.LogUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.UUID;

public class OrderInvalidJob implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        LogUtils.info(UUID.randomUUID().toString());
    }
}
