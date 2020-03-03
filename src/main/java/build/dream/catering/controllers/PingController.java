package build.dream.catering.controllers;

import build.dream.catering.constants.Constants;
import build.dream.common.annotations.PermitAll;
import build.dream.common.models.rocketmq.DelayedMessageModel;
import build.dream.common.models.rocketmq.DelayedType;
import build.dream.common.utils.JacksonUtils;
import build.dream.common.utils.RabbitUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "/ping")
@PermitAll
public class PingController {
    @RequestMapping(value = "/ok")
    @ResponseBody
    public String ok() {
        return Constants.OK;
    }

    @RequestMapping(value = "/success")
    @ResponseBody
    public String success() {
        return Constants.SUCCESS;
    }

    @RequestMapping(value = "/pong")
    @ResponseBody
    public String pong() {
        return Constants.PONG;
    }

    @RequestMapping(value = "/test")
    @ResponseBody
    public String test() {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("tenantId", 152120540646084608L);
        data.put("branchId", 152120540880965632L);
        data.put("orderId", 152120541002600448L);
        DelayedMessageModel delayedMessageModel = new DelayedMessageModel();
        delayedMessageModel.setType(DelayedType.DELAYED_TYPE_DIET_ORDER_INVALID);
        delayedMessageModel.setData(data);

        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setDelay(3000);
        Message message = new Message(JacksonUtils.writeValueAsString(delayedMessageModel).getBytes(Constants.CHARSET_UTF_8), messageProperties);
        RabbitUtils.send("development_zd1_catering_delayed_exchange", "c8168f230323822484b475d23e5fe88c", message);
        return new SimpleDateFormat(Constants.DEFAULT_DATE_PATTERN).format(new Date());
    }
}
