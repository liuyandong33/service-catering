package build.dream.catering.controllers;

import build.dream.catering.models.activity.*;
import build.dream.catering.services.ActivityService;
import build.dream.common.annotations.ApiRestAction;
import build.dream.common.controllers.BasicController;
import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.MethodCaller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        MethodCaller methodCaller = () -> {
            SaveBuyGiveActivityModel saveBuyGiveActivityModel = ApplicationHandler.instantiateObject(SaveBuyGiveActivityModel.class, requestParameters);
            String buyGiveActivityInfos = requestParameters.get("buyGiveActivityInfos");
            saveBuyGiveActivityModel.setBuyGiveActivityInfos(buyGiveActivityInfos);
            saveBuyGiveActivityModel.validateAndThrow();

            return activityService.saveBuyGiveActivity(saveBuyGiveActivityModel);
        };
        return ApplicationHandler.callMethod(methodCaller, "保存买A赠B活动失败", requestParameters);
    }

    /**
     * 保存满减活动
     *
     * @return
     */
    @RequestMapping(value = "/saveFullReductionActivity")
    @ResponseBody
    public String saveFullReductionActivity() {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        MethodCaller methodCaller = () -> {
            SaveFullReductionActivityModel saveFullReductionActivityModel = ApplicationHandler.instantiateObject(SaveFullReductionActivityModel.class, requestParameters);
            saveFullReductionActivityModel.validateAndThrow();

            return activityService.saveFullReductionActivity(saveFullReductionActivityModel);
        };
        return ApplicationHandler.callMethod(methodCaller, "保存满减活动失败", requestParameters);
    }

    /**
     * 保存特价商品活动
     *
     * @return
     */
    @RequestMapping(value = "/saveSpecialGoodsActivity")
    @ResponseBody
    public String saveSpecialGoodsActivity() {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        MethodCaller methodCaller = () -> {
            SaveSpecialGoodsActivityModel saveSpecialGoodsActivityModel = ApplicationHandler.instantiateObject(SaveSpecialGoodsActivityModel.class, requestParameters);
            String specialGoodsActivityInfos = requestParameters.get("specialGoodsActivityInfos");
            saveSpecialGoodsActivityModel.setSpecialGoodsActivityInfos(specialGoodsActivityInfos);

            saveSpecialGoodsActivityModel.validateAndThrow();
            return activityService.saveSpecialGoodsActivity(saveSpecialGoodsActivityModel);
        };
        return ApplicationHandler.callMethod(methodCaller, "保存特价商品活动失败", requestParameters);
    }

    /**
     * 查询生效的活动
     *
     * @return
     */
    @RequestMapping(value = "/listEffectiveActivities", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = ListEffectiveActivitiesModel.class, serviceClass = ActivityService.class, serviceMethodName = "listEffectiveActivities", error = "查询生效的活动失败")
    public String listEffectiveActivities() {
        return null;
    }

    /**
     * 查询所有生效的整单满减活动
     *
     * @return
     */
    @RequestMapping(value = "/listFullReductionActivities")
    @ResponseBody
    public String listFullReductionActivities() {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        MethodCaller methodCaller = () -> {
            ListFullReductionActivitiesModel listFullReductionActivitiesModel = ApplicationHandler.instantiateObject(ListFullReductionActivitiesModel.class, requestParameters);
            listFullReductionActivitiesModel.validateAndThrow();
            return activityService.listFullReductionActivities(listFullReductionActivitiesModel);
        };
        return ApplicationHandler.callMethod(methodCaller, "查询所有生效的整单满减活动失败", requestParameters);
    }

    /**
     * 查询所有生效的支付促销活动
     *
     * @return
     */
    @RequestMapping(value = "/listPaymentActivities")
    @ResponseBody
    public String listPaymentActivities() {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        MethodCaller methodCaller = () -> {
            ListPaymentActivitiesModel listPaymentActivitiesModel = ApplicationHandler.instantiateObject(ListPaymentActivitiesModel.class, requestParameters);
            listPaymentActivitiesModel.validateAndThrow();
            return activityService.listPaymentActivities(listPaymentActivitiesModel);
        };
        return ApplicationHandler.callMethod(methodCaller, "查询所有生效的支付促销活动失败", requestParameters);
    }
}
