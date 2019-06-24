package build.dream.catering.controllers;

import build.dream.catering.models.dada.SyncShopModel;
import build.dream.catering.services.DadaService;
import build.dream.common.annotations.ApiRestAction;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/dada")
public class DadaController {
    /**
     * 同步门店信息
     *
     * @return
     */
    @RequestMapping(value = "/syncShop", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = SyncShopModel.class, serviceClass = DadaService.class, serviceMethodName = "syncShop", error = "同步门店失败")
    public String syncShop() {
        return null;
    }
}