package build.dream.catering.jobs;

import build.dream.common.utils.ConfigurationUtils;
import build.dream.catering.constants.Constants;
import org.apache.commons.lang.StringUtils;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JobScheduler {
    @Autowired
    SchedulerFactoryBean schedulerFactoryBean;

    public void scheduler() throws IOException, SchedulerException {
        /*Scheduler scheduler = schedulerFactoryBean.getScheduler();
        String dataJobCronExpression = ConfigurationUtils.getConfiguration(Constants.DATA_JOB_CRON_EXPRESSION);
        if (StringUtils.isNotBlank(dataJobCronExpression)) {
            JobDetail dataJobDetail = JobBuilder.newJob(DataJob.class).withIdentity("dataJob", "cateringJobGroup").build();
            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(dataJobCronExpression);
            Trigger dataJobCronTrigger = TriggerBuilder.newTrigger().withIdentity("dataJobTrigger", "cateringJobGroup").withSchedule(cronScheduleBuilder).build();
            scheduler.scheduleJob(dataJobDetail, dataJobCronTrigger);
        }*/
    }
}
