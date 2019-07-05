package build.dream.catering.controllers;

import build.dream.catering.constants.Constants;
import build.dream.common.annotations.PermitAll;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/jddj")
public class JDDJController {
    @PermitAll
    @RequestMapping(value = "/test")
    @ResponseBody
    public String test() {
        return Constants.SUCCESS;
    }
}
