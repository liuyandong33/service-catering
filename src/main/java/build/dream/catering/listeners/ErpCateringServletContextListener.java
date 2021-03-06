package build.dream.catering.listeners;

import build.dream.catering.jobs.JobScheduler;
import build.dream.common.listeners.BasicServletContextListener;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletContextEvent;
import javax.servlet.annotation.WebListener;
import java.io.IOException;

@WebListener
public class ErpCateringServletContextListener extends BasicServletContextListener {
    @Autowired
    private JobScheduler jobScheduler;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        super.contextInitialized(servletContextEvent);
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
