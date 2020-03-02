package build.dream.catering.controllers;

import build.dream.catering.models.init.InitTenantConfigModel;
import build.dream.catering.services.InitService;
import build.dream.common.annotations.ApiRestAction;
import build.dream.common.annotations.PermitAll;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/init")
public class InitController {
    @RequestMapping(value = "/initTenantConfig")
    @ResponseBody
    @PermitAll
    @ApiRestAction(modelClass = InitTenantConfigModel.class, serviceClass = InitService.class, serviceMethodName = "initTenantConfig", error = "初始化商户配置失败")
    public String initTenantConfig() {
        return null;
    }
}
