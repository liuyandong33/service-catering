package build.dream.catering.controllers;

import build.dream.catering.constants.Constants;
import build.dream.catering.services.BeElemeService;
import build.dream.common.utils.ApplicationHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "beEleme")
public class BeElemeController {
    @Autowired
    private BeElemeService beElemeService;

    @RequestMapping(value = "/orderCreate")
    @ResponseBody
    public String orderCreate() {
        beElemeService.handleOrderCreate(ApplicationHandler.getRequestParameters());
        return Constants.SUCCESS;
    }
}
