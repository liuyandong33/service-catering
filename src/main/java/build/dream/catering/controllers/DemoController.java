package build.dream.catering.controllers;

import build.dream.catering.services.DemoService;
import build.dream.common.api.ApiRest;
import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.GsonUtils;
import build.dream.common.utils.MethodCaller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigInteger;
import java.util.Map;

@Controller
@RequestMapping(value = "/demo")
public class DemoController {
    @Autowired
    private DemoService demoService;
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @RequestMapping(value = "/index")
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("demo/index");
        return modelAndView;
    }

    @RequestMapping(value = "/testKafka")
    @ResponseBody
    public String testKafka() {
        kafkaTemplate.send("zd1_eleme_message_topic", "你好");
        return GsonUtils.toJson(new ApiRest());
    }


    @RequestMapping(value = "/writeSaleFlow", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String writeSaleFlow() {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        MethodCaller methodCaller = () -> {
            String dietOrderId = requestParameters.get("dietOrderId");
            ApplicationHandler.notNull(dietOrderId, "dietOrderId");
            return demoService.writeSaleFlow(BigInteger.valueOf(Long.valueOf(dietOrderId)));
        };
        return ApplicationHandler.callMethod(methodCaller, "写入流水失败", requestParameters);
    }
}
