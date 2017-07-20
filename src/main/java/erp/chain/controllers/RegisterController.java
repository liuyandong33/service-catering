package erp.chain.controllers;

import erp.chain.api.ApiRest;
import erp.chain.services.RegisterService;
import erp.chain.utils.ApplicationHandler;
import erp.chain.utils.GsonUtils;
import erp.chain.utils.LogUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * Created by liuyandong on 2017/7/18.
 */
@Controller
@RequestMapping(value = "/register")
public class RegisterController {
    private static final String REGISTER_CONTROLLER_SIMPLE_NAME = "RegisterController";
    @Autowired
    private RegisterService registerService;

    @RequestMapping(value = "/registerTenant", method = RequestMethod.POST)
    @ResponseBody
    public String registerTenant() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            ApplicationHandler.validateNotNull(requestParameters, "name", "mobile", "email", "linkman", "business");
        } catch (Exception e) {
            LogUtils.error("注册商户失败", REGISTER_CONTROLLER_SIMPLE_NAME, "registerTenant", e.getClass().getSimpleName(), e.getMessage(), requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }
}
