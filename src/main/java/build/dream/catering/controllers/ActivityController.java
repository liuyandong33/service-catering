package build.dream.catering.controllers;

import build.dream.catering.models.activity.SaveBuyGiveActivityModel;
import build.dream.catering.models.activity.SaveFullReductionActivityModel;
import build.dream.catering.services.ActivityService;
import build.dream.common.api.ApiRest;
import build.dream.common.controllers.BasicController;
import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.GsonUtils;
import build.dream.common.utils.LogUtils;
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

    @RequestMapping(value = "/test")
    @ResponseBody
    public String test() {
        return GsonUtils.toJson(activityService.test());
    }

    /**
     * 保存买A赠B活动
     *
     * @return
     */
    @RequestMapping(value = "/saveBuyGiveActivity")
    @ResponseBody
    public String saveBuyGiveActivity() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            SaveBuyGiveActivityModel saveBuyGiveActivityModel = ApplicationHandler.instantiateObject(SaveBuyGiveActivityModel.class, requestParameters);
            String buyGiveActivityInfos = requestParameters.get("buyGiveActivityInfos");
            saveBuyGiveActivityModel.setBuyGiveActivityInfos(buyGiveActivityInfos);
            saveBuyGiveActivityModel.validateAndThrow();

            apiRest = activityService.saveBuyGiveActivity(saveBuyGiveActivityModel);
        } catch (Exception e) {
            LogUtils.error("保存买A赠B活动失败", controllerSimpleName, "saveBuyGiveActivity", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    /**
     * 保存满减活动
     *
     * @return
     */
    @RequestMapping(value = "/saveFullReductionActivity")
    @ResponseBody
    public String saveFullReductionActivity() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            SaveFullReductionActivityModel saveFullReductionActivityModel = ApplicationHandler.instantiateObject(SaveFullReductionActivityModel.class, requestParameters);
            saveFullReductionActivityModel.validateAndThrow();

            apiRest = activityService.saveFullReductionActivity(saveFullReductionActivityModel);
        } catch (Exception e) {
            LogUtils.error("保存满减活动失败", controllerSimpleName, "saveFullReductionActivity", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }
}
