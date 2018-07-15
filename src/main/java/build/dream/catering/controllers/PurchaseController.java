package build.dream.catering.controllers;

import build.dream.catering.models.purchase.SavePurchaseOrderModel;
import build.dream.catering.services.PurchaseService;
import build.dream.common.annotations.ApiRestAction;
import build.dream.common.controllers.BasicController;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/purchase")
public class PurchaseController extends BasicController {
    @RequestMapping(value = "/savePurchaseOrder", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = SavePurchaseOrderModel.class, serviceClass = PurchaseService.class, serviceMethodName = "savePurchaseOrder", error = "保存进货单失败")
    public String savePurchaseOrder() {
        return null;
    }
}
