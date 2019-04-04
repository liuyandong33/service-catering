package build.dream.catering.controllers;

import build.dream.catering.models.flashsale.SaveFlashSaleActivityModel;
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
}
