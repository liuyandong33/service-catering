package build.dream.erp.listeners;

import build.dream.common.listeners.BasicServletContextListener;
import build.dream.erp.jobs.JobScheduler;
import build.dream.erp.utils.ElemeUtils;
import build.dream.erp.utils.MeiTuanUtils;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletContextEvent;
import javax.servlet.annotation.WebListener;
import java.io.IOException;

@WebListener
public class ErpChainServletContextListener extends BasicServletContextListener {
    @Autowired
    private JobScheduler jobScheduler;
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        super.contextInitialized(servletContextEvent);
        ElemeUtils.startElemeConsumerThread();
        MeiTuanUtils.startMeiTuanConsumerThread();
        try {
            jobScheduler.scheduler();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        super.contextDestroyed(sce);
    }
}
