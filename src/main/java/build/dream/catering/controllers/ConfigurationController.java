package build.dream.catering.controllers;

import build.dream.common.annotations.PermitAll;
import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.ConfigurationUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping(value = "/configuration")
@PermitAll
public class ConfigurationController {
    @RequestMapping(value = "/getConfiguration")
    @ResponseBody
    public String getConfiguration() {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        String configurationKey = requestParameters.get("configurationKey");
        if (StringUtils.isBlank(configurationKey)) {
            return "";
        }
        return ConfigurationUtils.getConfiguration(configurationKey);
    }
}
