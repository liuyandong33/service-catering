package build.dream.catering.listeners;

import build.dream.catering.jobs.JobScheduler;
import build.dream.catering.services.FlashSaleService;
import build.dream.catering.tasks.SaveFlashSaleOrderTask;
import build.dream.common.listeners.BasicServletContextListener;
import build.dream.common.mappers.CommonMapper;
import build.dream.common.utils.MqttUtils;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletContextEvent;
import javax.servlet.annotation.WebListener;
import java.io.IOException;

@WebListener
public class CateringServletContextListener extends BasicServletContextListener {
    @Autowired
    private JobScheduler jobScheduler;
    @Autowired
    private FlashSaleService flashSaleService;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        super.contextInitialized(servletContextEvent);
        previousInjectionBean(servletContextEvent.getServletContext(), CommonMapper.class);
        try {
            jobScheduler.scheduler();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

//        new SaveFlashSaleOrderTask(flashSaleService).start();

//        new Thread(() -> MqttUtils.mqttConnect()).start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        super.contextDestroyed(sce);
    }
}
