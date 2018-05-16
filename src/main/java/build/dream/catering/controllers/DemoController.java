package build.dream.catering.controllers;

import build.dream.common.api.ApiRest;
import build.dream.common.utils.GsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/demo")
public class DemoController {
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
}
