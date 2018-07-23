package build.dream.catering.controllers;

import build.dream.catering.constants.Constants;
import build.dream.catering.jobs.OrderInvalidJob;
import build.dream.catering.services.DemoService;
import build.dream.common.api.ApiRest;
import build.dream.common.models.weixin.RefundModel;
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
import java.util.HashMap;
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

    @RequestMapping(value = "/startJob", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String startJob() throws SchedulerException, ParseException {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        String orderCode = requestParameters.get("orderCode");
        ApplicationHandler.notBlank(orderCode, "orderCode");

        String startTime = requestParameters.get("startTime");
        ApplicationHandler.notBlank(startTime, "startTime");


        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        JobDetail dataJobDetail = JobBuilder.newJob(OrderInvalidJob.class).withIdentity(orderCode, "cateringJobGroup").build();
        SimpleScheduleBuilder simpleScheduleBuilder = SimpleScheduleBuilder.simpleSchedule();
        simpleScheduleBuilder.withRepeatCount(10000);
        simpleScheduleBuilder.withIntervalInSeconds(1);

        TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
        triggerBuilder.startAt(new SimpleDateFormat(Constants.DEFAULT_DATE_PATTERN).parse(startTime));
        triggerBuilder.withIdentity(orderCode, "cateringJobGroup");
        triggerBuilder.withSchedule(simpleScheduleBuilder);

        Trigger trigger = triggerBuilder.build();
        scheduler.scheduleJob(dataJobDetail, trigger);
        return Constants.SUCCESS;
    }

    @RequestMapping(value = "/stopJob", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String stopJob() throws SchedulerException {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        String orderCode = requestParameters.get("orderCode");
        ApplicationHandler.notBlank(orderCode, "orderCode");

        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        TriggerKey triggerKey = TriggerKey.triggerKey(orderCode, "cateringJobGroup");

        scheduler.pauseTrigger(triggerKey);
        scheduler.unscheduleJob(triggerKey);

        JobKey jobKey = JobKey.jobKey(orderCode, "cateringJobGroup");
        scheduler.deleteJob(jobKey);

        return Constants.SUCCESS;
    }

    @RequestMapping(value = "/order", method = RequestMethod.GET)
    public ModelAndView order() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("demo/order");
        Map<String, Object> modelMap = new HashMap<String, Object>();
        modelMap.put("appId", "wx63f5194332cc0f1b");
        modelMap.put("timeStamp", "1532072462");
        modelMap.put("nonceStr", "4ac1885c9a734f8c93574fb86a607419");
        modelMap.put("prepared", "prepay_id=wx201541024573150fa178a8901463747482");
        modelMap.put("signType", "RSA");
        modelMap.put("paySign", "0BPPkCrC4uw7N9TpbGskEMQTs78WfsQs75jBzT2u+7GFMU7Ouu71l7hnyaPc0NJThiaYEDQ6b0bP3irX1HjWcVEZkX8pUaB/mP4AqV9MveOE4ibAKRalO/iDFs8HUrBU1+5tLMwrV5UbeWhGMRmQ3yHK7TBMFSRjgBlr8ykA9J/WDJeSWqrNkGL2qq4FvXzVfFurvg83f6DC6MpP2MSgv2ZiYYNavzxFNxGdKh6DkomExoj4E5tTOUWQ/3zK9cEi5aLUilmDyxo6LV92ITlLx2c6qxfsW+Go15TmqSLIThO6R8vDdE1j42miOCPohIMA+DIU5hDwYTVKJAamPEqIgQ==");

        modelAndView.addAllObjects(modelMap);
        return modelAndView;
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
}
