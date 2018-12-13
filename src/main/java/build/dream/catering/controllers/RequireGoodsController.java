package build.dream.catering.controllers;

import build.dream.catering.models.requiregoods.SaveRequireGoodsOrderModel;
import build.dream.catering.services.RequireGoodsService;
import build.dream.common.annotations.ApiRestAction;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller(value = "/requireGoods")
public class RequireGoodsController {
    @RequestMapping(value = "/saveRequireGoodsOrder", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = SaveRequireGoodsOrderModel.class, serviceClass = RequireGoodsService.class, serviceMethodName = "saveRequireGoodsOrder", error = "保存要货单失败")
    public String saveRequireGoodsOrder() {
        return null;
    }
}
