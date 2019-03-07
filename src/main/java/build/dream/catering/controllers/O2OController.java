package build.dream.catering.controllers;

import build.dream.catering.models.o2o.ObtainVipInfoModel;
import build.dream.catering.services.O2OService;
import build.dream.common.annotations.ApiRestAction;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/o2o")
public class O2OController {
    /**
     * 获取会员信息
     *
     * @return
     */
    @RequestMapping(value = "/obtainVipInfo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = ObtainVipInfoModel.class, serviceClass = O2OService.class, serviceMethodName = "obtainVipInfo", error = "获取会员信息失败")
    public String obtainVipInfo() {
        return null;
    }
}
