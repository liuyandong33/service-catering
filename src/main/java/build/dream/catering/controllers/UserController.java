package build.dream.catering.controllers;

import build.dream.catering.models.user.ListUsersModel;
import build.dream.catering.services.UserService;
import build.dream.common.controllers.BasicController;
import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.MethodCaller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping(value = "/user")
public class UserController extends BasicController {
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/listUsers")
    @ResponseBody
    public String listUsers() {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        MethodCaller methodCaller = () -> {
            ListUsersModel listUsersModel = ApplicationHandler.instantiateObject(ListUsersModel.class, requestParameters);
            listUsersModel.validateAndThrow();
            return userService.listUsers(listUsersModel);
        };
        return ApplicationHandler.callMethod(methodCaller, "查询员工列表失败", requestParameters);
    }
}
