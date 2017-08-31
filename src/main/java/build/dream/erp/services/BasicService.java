package build.dream.erp.services;

import build.dream.common.utils.ApplicationHandler;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.sql.DataSource;

public class BasicService {
    public DataSource obtainDataSource(String dataSourceName) {
        ServletContext servletContext = ApplicationHandler.getServletContext();
        WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        DataSource dataSource = (DataSource) webApplicationContext.getBean(dataSourceName);
        return dataSource;
    }
}
