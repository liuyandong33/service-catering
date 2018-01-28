package build.dream.catering.controllers;

import build.dream.catering.services.ActivityService;
import build.dream.common.controllers.BasicController;
import build.dream.common.utils.GsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/activity")
public class ActivityController extends BasicController {
    @Autowired
    private ActivityService activityService;

    @RequestMapping(value = "/test")
    @ResponseBody
    public String test() {
        return GsonUtils.toJson(activityService.test());
    }
}
