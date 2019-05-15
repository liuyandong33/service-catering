package build.dream.catering.controllers;

import build.dream.catering.models.assemble.SaveAssembleActivityModel;
import build.dream.catering.services.AssembleService;
import build.dream.common.annotations.ApiRestAction;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/assemble")
public class AssembleController {
    /**
     * 保存拼团活动
     *
     * @return
     */
    @RequestMapping(value = "/saveAssembleActivity", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = SaveAssembleActivityModel.class, serviceClass = AssembleService.class, serviceMethodName = "saveAssembleActivity", error = "保存拼团活动失败", datePattern = "yyyy-MM-dd HH:mm")
    public String saveAssembleActivity() {
        return null;
    }
}
