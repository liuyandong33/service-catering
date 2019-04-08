package build.dream.catering.controllers;

import build.dream.catering.models.flashsale.DeleteFlashSaleActivityModel;
import build.dream.catering.models.flashsale.ObtainAllFlashSaleActivitiesModel;
import build.dream.catering.models.flashsale.SaveFlashSaleActivityModel;
import build.dream.catering.models.flashsale.StopFlashSaleActivityModel;
import build.dream.catering.services.FlashSaleService;
import build.dream.common.annotations.ApiRestAction;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "flashSale")
public class FlashSaleController {
    /**
     * 保存秒杀活动
     *
     * @return
     */
    @RequestMapping(value = "/saveFlashSaleActivity", method = RequestMethod.POST)
    @ResponseBody
    @ApiRestAction(modelClass = SaveFlashSaleActivityModel.class, serviceClass = FlashSaleService.class, serviceMethodName = "saveFlashSaleActivity", error = "保存秒杀活动失败", datePattern = "yyyy-MM-dd HH:mm")
    public String saveFlashSaleActivity() {
        return null;
    }

    /**
     * 获取所有秒杀活动
     *
     * @return
     */
    @RequestMapping(value = "/obtainAllFlashSaleActivities", method = RequestMethod.GET)
    @ResponseBody
    @ApiRestAction(modelClass = ObtainAllFlashSaleActivitiesModel.class, serviceClass = FlashSaleService.class, serviceMethodName = "obtainAllFlashSaleActivities", error = "获取所有秒杀活动失败")
    public String obtainAllFlashSaleActivities() {
        return null;
    }

    /**
     * 终止秒杀活动
     *
     * @return
     */
    @RequestMapping(value = "/stopFlashSaleActivity", method = RequestMethod.GET)
    @ResponseBody
    @ApiRestAction(modelClass = StopFlashSaleActivityModel.class, serviceClass = FlashSaleService.class, serviceMethodName = "stopFlashSaleActivity", error = "终止秒杀活动失败")
    public String stopFlashSaleActivity() {
        return null;
    }

    /**
     * 删除秒杀活动
     *
     * @return
     */
    @RequestMapping(value = "/deleteFlashSaleActivity", method = RequestMethod.GET)
    @ResponseBody
    @ApiRestAction(modelClass = DeleteFlashSaleActivityModel.class, serviceClass = FlashSaleService.class, serviceMethodName = "deleteFlashSaleActivity", error = "删除秒杀活动失败")
    public String deleteFlashSaleActivity() {
        return null;
    }
}
