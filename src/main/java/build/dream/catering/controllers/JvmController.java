package build.dream.catering.controllers;

import build.dream.common.annotations.PermitAll;
import build.dream.common.utils.JacksonUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import sun.management.VMManagement;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.reflect.Field;

@Controller
@RequestMapping(value = "/jvm")
@PermitAll
public class JvmController {
    @RequestMapping(value = "/getVmArguments")
    @ResponseBody
    public String getVmArguments() {
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        Field field = ReflectionUtils.findField(memoryMXBean.getClass(), "jvm");
        ReflectionUtils.makeAccessible(field);
        VMManagement vmManagement = (VMManagement) ReflectionUtils.getField(field, memoryMXBean);
        return JacksonUtils.writeValueAsString(vmManagement.getVmArguments());
    }
}
