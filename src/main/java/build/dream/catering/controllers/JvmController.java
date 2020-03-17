package build.dream.catering.controllers;

import build.dream.common.annotations.PermitAll;
import build.dream.common.utils.JacksonUtils;
import build.dream.common.utils.JvmUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/jvm")
@PermitAll
public class JvmController {
    @RequestMapping(value = "/getProcessId")
    @ResponseBody
    public String getProcessId() {
        return String.valueOf(JvmUtils.getProcessId());
    }

    @RequestMapping(value = "/getVmArguments")
    @ResponseBody
    public String getVmArguments() {
        return JacksonUtils.writeValueAsString(JvmUtils.getInputArguments());
    }
}
