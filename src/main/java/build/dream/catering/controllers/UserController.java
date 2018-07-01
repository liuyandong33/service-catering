package build.dream.catering.controllers;

import build.dream.catering.models.user.ListUsersModel;
import build.dream.catering.services.UserService;
import build.dream.common.annotations.ApiRestAction;
import build.dream.common.controllers.BasicController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/user")
public class UserController extends BasicController {
    @RequestMapping(value = "/listUsers")
    @ResponseBody
    @ApiRestAction(modelClass = ListUsersModel.class, serviceClass = UserService.class, serviceMethodName = "listUsers", error = "查询员工列表失败")
    public String listUsers() {
        return null;
    }
}
