package build.dream.catering.controllers;

import build.dream.catering.models.activity.*;
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

    /**
     * 保存特价商品活动
     *
     * @return
     */
    @RequestMapping(value = "/saveSpecialGoodsActivity")
    @ResponseBody
    public String saveSpecialGoodsActivity() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            SaveSpecialGoodsActivityModel saveSpecialGoodsActivityModel = ApplicationHandler.instantiateObject(SaveSpecialGoodsActivityModel.class, requestParameters);
            String specialGoodsActivityInfos = requestParameters.get("specialGoodsActivityInfos");
            saveSpecialGoodsActivityModel.setSpecialGoodsActivityInfos(specialGoodsActivityInfos);

            saveSpecialGoodsActivityModel.validateAndThrow();
            apiRest = activityService.saveSpecialGoodsActivity(saveSpecialGoodsActivityModel);
        } catch (Exception e) {
            LogUtils.error("保存特价商品活动失败", controllerSimpleName, "saveSpecialGoodsActivity", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    /**
     * 查询生效的活动
     *
     * @return
     */
    @RequestMapping(value = "/listEffectiveActivities")
    @ResponseBody
    public String listEffectiveActivities() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            ListEffectiveActivitiesModel listEffectiveActivitiesModel = ApplicationHandler.instantiateObject(ListEffectiveActivitiesModel.class, requestParameters);
            listEffectiveActivitiesModel.validateAndThrow();
            apiRest = activityService.listEffectiveActivities(listEffectiveActivitiesModel);
        } catch (Exception e) {
            LogUtils.error("查询生效的活动失败", controllerSimpleName, "listEffectiveActivities", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    /**
     * 查询所有生效的整单满减活动
     *
     * @return
     */
    @RequestMapping(value = "/listFullReductionActivities")
    @ResponseBody
    public String listFullReductionActivities() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            ListFullReductionActivitiesModel listFullReductionActivitiesModel = ApplicationHandler.instantiateObject(ListFullReductionActivitiesModel.class, requestParameters);
            listFullReductionActivitiesModel.validateAndThrow();
            apiRest = activityService.listFullReductionActivities(listFullReductionActivitiesModel);
        } catch (Exception e) {
            LogUtils.error("查询所有生效的整单满减活动失败", controllerSimpleName, "listFullReductionActivities", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }
}
