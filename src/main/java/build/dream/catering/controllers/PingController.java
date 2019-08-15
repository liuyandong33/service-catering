package build.dream.catering.controllers;

import build.dream.catering.constants.Constants;
import build.dream.common.annotations.PermitAll;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/ping")
@PermitAll
public class PingController {
    @RequestMapping(value = "/ok")
    @ResponseBody
    public String ok() {
        return Constants.OK;
    }

    @RequestMapping(value = "/success")
    @ResponseBody
    public String success() {
        return Constants.SUCCESS;
    }

    @RequestMapping(value = "/pong")
    @ResponseBody
    public String pong() {
        return Constants.PONG;
    }
}
