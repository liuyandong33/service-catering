package build.dream.catering.jobs;

import build.dream.catering.services.JDDJService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

public class CacheJDDJVenderInfoJob implements Job {
    @Autowired
    private JDDJService jddjService;

    @Override
    public void execute(JobExecutionContext context) {
        jddjService.cacheJDDJVenderInfo();
    }
}
