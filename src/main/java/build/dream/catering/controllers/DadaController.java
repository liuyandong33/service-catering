package build.dream.catering.controllers;

import build.dream.catering.models.eleme.GetItemModel;
import build.dream.catering.services.ElemeService;
import build.dream.common.annotations.ApiRestAction;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/dada")
public class DadaController {
    @RequestMapping(value = "/syncShop", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = GetItemModel.class, serviceClass = ElemeService.class, serviceMethodName = "syncShop", error = "同步门店失败")
    public String syncShop() {
        return null;
    }
}
