package build.dream.catering.controllers;

import build.dream.catering.models.message.ReceiptModel;
import build.dream.catering.services.MessageService;
import build.dream.common.annotations.ApiRestAction;
import build.dream.common.annotations.PermitAll;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/message")
public class MessageController {
    @PermitAll
    @RequestMapping(value = "/receipt")
    @ResponseBody
    @ApiRestAction(modelClass = ReceiptModel.class, serviceClass = MessageService.class, serviceMethodName = "receipt", error = "回执失败")
    public String receipt() {
        return null;
    }
}
