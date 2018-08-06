package build.dream.catering.controllers;

import build.dream.catering.constants.Constants;
import build.dream.catering.jobs.OrderInvalidJob;
import build.dream.catering.models.demo.DemoModel;
import build.dream.catering.services.DemoService;
import build.dream.common.api.ApiRest;
import build.dream.common.models.weixinpay.RefundModel;
import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.MethodCaller;
import build.dream.common.utils.WeiXinPayUtils;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping(value = "/demo")
public class DemoController {
    @Autowired
    private DemoService demoService;
    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

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

    @RequestMapping(value = "/refund", method = RequestMethod.GET)
    public ModelAndView refund() {
        ModelAndView modelAndView = new ModelAndView("demo/refund");
        return modelAndView;
    }

    @RequestMapping(value = "/doRefund", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String doRefund() {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        MethodCaller methodCaller = () -> {
            String tenantId = requestParameters.get("tenantId");
            ApplicationHandler.notBlank(tenantId, "tenantId");

            String branchId = requestParameters.get("branchId");
            ApplicationHandler.notBlank(branchId, "branchId");
            RefundModel refundModel = ApplicationHandler.instantiateObject(RefundModel.class, requestParameters);

            refundModel.setOutRefundNo(UUID.randomUUID().toString());
            return ApiRest.builder().data(WeiXinPayUtils.refund(tenantId, branchId, refundModel)).message("退款成功！").successful(true).build();
        };
        return ApplicationHandler.callMethod(methodCaller, "退款失败", requestParameters);
    }

    @RequestMapping(value = "/demo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String demo() throws Exception {
        DemoModel demoModel = ApplicationHandler.instantiateObject(DemoModel.class, ApplicationHandler.getRequestParameters());
        demoModel.validateAndThrow();
        return UUID.randomUUID().toString();
    }
}
