package build.dream.catering.jobs;

import build.dream.common.models.job.ScheduleCronJobModel;
import build.dream.common.utils.JobUtils;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.springframework.stereotype.Component;

@Component
public class JobScheduler {
    public void scheduler() {
//        ApplicationHandler.callMethodSuppressThrow(() -> startTestJob());
    }

    public void startTestJob() throws SchedulerException {
        JobKey jobKey = JobKey.jobKey("Test_Job", "Test_Job");
        TriggerKey triggerKey = TriggerKey.triggerKey("Test_Trigger", "Test_Trigger");
        if (JobUtils.checkExists(jobKey) || JobUtils.checkExists(triggerKey)) {
            JobUtils.pauseTrigger(triggerKey);
            JobUtils.unscheduleJob(triggerKey);
            JobUtils.deleteJob(jobKey);
        }

        ScheduleCronJobModel scheduleCronJobModel = ScheduleCronJobModel.builder()
                .jobName("Test_Job")
                .jobGroup("Test_Job")
                .jobClass(TestJob.class)
                .triggerName("Test_Trigger")
                .triggerGroup("Test_Trigger")
                .cronExpression("*/5 * * * * ?")
                .build();
        JobUtils.scheduleCronJob(scheduleCronJobModel);
    }
}
