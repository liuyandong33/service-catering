package build.dream.catering.controllers;

import build.dream.catering.models.distributionCenter.SaveModel;
import build.dream.catering.services.DistributionCenterService;
import build.dream.common.annotations.ApiRestAction;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/distributionCenter")
public class DistributionCenterController {
    @RequestMapping(value = "/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = SaveModel.class, serviceClass = DistributionCenterService.class, serviceMethodName = "save", error = "保存失败！")
    public String save() {
        return null;
    }
}
