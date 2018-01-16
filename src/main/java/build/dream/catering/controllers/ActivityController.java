package build.dream.catering.controllers;

import build.dream.common.api.ApiRest;
import build.dream.common.controllers.BasicController;
import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.GsonUtils;
import build.dream.common.utils.LogUtils;
import build.dream.catering.services.ActivityService;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping(value = "/activity")
public class ActivityController extends BasicController {
    @Autowired
    private ActivityService activityService;

    @RequestMapping(value = "/findAllActivities")
    @ResponseBody
    public String findAllActivities() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            String tenantId = requestParameters.get("tenantId");
            Validate.notNull(tenantId, ApplicationHandler.obtainParameterErrorMessage("tenantId"));

            String branchId = requestParameters.get("branchId");
            Validate.notNull(branchId, ApplicationHandler.obtainParameterErrorMessage("branchId"));

            apiRest = activityService.findAllActivities(NumberUtils.createBigInteger(tenantId), NumberUtils.createBigInteger(branchId));
        } catch (Exception e) {
            LogUtils.error("查询门店促销活动失败", controllerSimpleName, "findAllActivities", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }
}
