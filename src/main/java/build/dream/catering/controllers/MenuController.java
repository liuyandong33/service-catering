package build.dream.catering.controllers;

import build.dream.catering.models.goods.CountModel;
import build.dream.catering.models.menu.SaveMenuModel;
import build.dream.catering.services.GoodsService;
import build.dream.catering.services.MenuService;
import build.dream.common.annotations.ApiRestAction;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/menu")
public class MenuController {
    @RequestMapping(value = "/saveMenu", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = SaveMenuModel.class, serviceClass = MenuService.class, serviceMethodName = "saveMenu", error = "保存菜牌失败")
    public String saveMenu() {
        return null;
    }
}
