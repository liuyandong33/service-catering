package build.dream.erp.controllers;

import build.dream.common.api.ApiRest;
import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.GsonUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.sql.DataSource;
import java.util.Map;

@Controller
@RequestMapping(value = "/demo")
public class DemoController {
    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    @ResponseBody
    public String index() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(ApplicationHandler.getServletContext());
            String[] beanDefinitionNames = webApplicationContext.getBeanDefinitionNames();

            DataSource dataSource = (DataSource) webApplicationContext.getBean("secondaryDataSource");
            apiRest = new ApiRest();
            apiRest.setData(beanDefinitionNames);
            apiRest.setMessage("操作成功！");
            apiRest.setSuccessful(true);
        } catch (Exception e) {
            apiRest = new ApiRest();
            apiRest.setError(e.getMessage());
            apiRest.setSuccessful(false);
        }
        return GsonUtils.toJson(apiRest);
    }
}
