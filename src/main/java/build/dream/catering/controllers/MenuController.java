package build.dream.catering.controllers;

import build.dream.catering.models.menu.ObtainMenuInfoModel;
import build.dream.catering.models.menu.SaveMenuModel;
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
    /**
     * 保存菜牌信息
     *
     * @return
     */
    @RequestMapping(value = "/saveMenu", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = SaveMenuModel.class, serviceClass = MenuService.class, serviceMethodName = "saveMenu", error = "保存菜牌失败")
    public String saveMenu() {
        return null;
    }

    /**
     * 获取菜牌信息
     *
     * @return
     */
    @RequestMapping(value = "/obtainMenuInfo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = ObtainMenuInfoModel.class, serviceClass = MenuService.class, serviceMethodName = "obtainMenuInfo", error = "获取菜牌失败")
    public String obtainMenuInfo() {
        return null;
    }
}
