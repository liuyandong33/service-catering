package build.dream.erp.listeners;

import build.dream.common.listeners.BasicServletContextListener;
import build.dream.erp.utils.ElemeUtils;
import build.dream.erp.utils.MeiTuanUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.annotation.WebListener;

@WebListener
public class ErpChainServletContextListener extends BasicServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        super.contextInitialized(servletContextEvent);
        ElemeUtils.startElemeConsumerThread();
        MeiTuanUtils.startMeiTuanConsumerThread();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        super.contextDestroyed(sce);
    }
}
