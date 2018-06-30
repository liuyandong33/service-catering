package build.dream.catering.controllers;

import build.dream.catering.models.dietorder.CancelOrderModel;
import build.dream.catering.models.dietorder.ConfirmOrderModel;
import build.dream.catering.models.dietorder.ObtainDietOrderInfoModel;
import build.dream.catering.models.dietorder.SaveDietOrderModel;
import build.dream.catering.services.DietOrderService;
import build.dream.common.annotations.ApiRestAction;
import build.dream.common.controllers.BasicController;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/dietOrder")
public class DietOrderController extends BasicController {
    /**
     * 获取订单明细
     *
     * @return
     */
    @RequestMapping(value = "/obtainDietOrderInfo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = ObtainDietOrderInfoModel.class, serviceClass = DietOrderService.class, serviceMethodName = "obtainDietOrderInfo", error = "获取订单信息失败")
    public String obtainDietOrderInfo() {
        return null;
    }

    /**
     * 保存订单
     *
     * @return
     */
    @RequestMapping(value = "/saveDietOrder", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = SaveDietOrderModel.class, serviceClass = DietOrderService.class, serviceMethodName = "saveDietOrder", error = "保存订单失败")
    public String saveDietOrder() {
        return null;
    }

    /**
     * 确认订单
     *
     * @return
     */
    @RequestMapping(value = "/confirmOrder", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = ConfirmOrderModel.class, serviceClass = DietOrderService.class, serviceMethodName = "confirmOrder", error = "确认订单失败")
    public String confirmOrder() {
        return null;
    }

    /**
     * 取消订单
     *
     * @return
     */
    @RequestMapping(value = "/cancelOrder", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = CancelOrderModel.class, serviceClass = DietOrderService.class, serviceMethodName = "cancelOrder", error = "取消订单失败")
    public String cancelOrder() {
        return null;
    }
}
