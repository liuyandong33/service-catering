package build.dream.catering.listeners;

import build.dream.catering.jobs.JobScheduler;
import build.dream.catering.services.FlashSaleService;
import build.dream.common.listeners.BasicServletContextListener;
import build.dream.common.mappers.CommonMapper;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletContextEvent;
import javax.servlet.annotation.WebListener;

@WebListener
public class CateringServletContextListener extends BasicServletContextListener {
    @Autowired
    private FlashSaleService flashSaleService;
    @Autowired
    private JobScheduler jobScheduler;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        super.contextInitialized(servletContextEvent);
        previousInjectionBean(servletContextEvent.getServletContext(), CommonMapper.class);
//        new SaveFlashSaleOrderTask(flashSaleService).start();

//        new Thread(() -> MqttUtils.mqttConnect()).start();

        jobScheduler.scheduler();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        super.contextDestroyed(sce);
    }
}
