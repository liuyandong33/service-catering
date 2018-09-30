package build.dream.catering.controllers;

import build.dream.catering.models.user.ListUsersModel;
import build.dream.catering.models.user.ObtainUserInfoModel;
import build.dream.catering.services.UserService;
import build.dream.common.annotations.ApiRestAction;
import build.dream.common.controllers.BasicController;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/user")
public class UserController extends BasicController {
    /**
     * 查询用户列表
     *
     * @return
     */
    @RequestMapping(value = "/listUsers", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = ListUsersModel.class, serviceClass = UserService.class, serviceMethodName = "listUsers", error = "查询员工列表失败")
    public String listUsers() {
        return null;
    }

    /**
     * 获取用户信息
     *
     * @return
     */
    @RequestMapping(value = "/obtainUserInfo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = ObtainUserInfoModel.class, serviceClass = UserService.class, serviceMethodName = "obtainUserInfo", error = "获取员工信息失败")
    public String obtainUserInfo() {
        return null;
    }
}
