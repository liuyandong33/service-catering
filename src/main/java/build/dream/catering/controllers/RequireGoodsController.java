package build.dream.catering.controllers;

import build.dream.catering.models.requiregoods.ObtainRequireGoodsOrderModel;
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
    /**
     * 保存要货单
     *
     * @return
     */
    @RequestMapping(value = "/saveRequireGoodsOrder", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = SaveRequireGoodsOrderModel.class, serviceClass = RequireGoodsService.class, serviceMethodName = "saveRequireGoodsOrder", error = "保存要货单失败")
    public String saveRequireGoodsOrder() {
        return null;
    }

    /**
     * 获取要货单
     *
     * @return
     */
    @RequestMapping(value = "/obtainRequireGoodsOrder", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = ObtainRequireGoodsOrderModel.class, serviceClass = RequireGoodsService.class, serviceMethodName = "obtainRequireGoodsOrder", error = "获取要货单失败")
    public String obtainRequireGoodsOrder() {
        return null;
    }
}
