package build.dream.catering.controllers;

import build.dream.catering.constants.Constants;
import build.dream.catering.services.DemoService;
import build.dream.common.api.ApiRest;
import build.dream.common.models.weixinpay.RefundModel;
import build.dream.common.utils.*;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping(value = "/demo")
public class DemoController {
    @Autowired
    private DemoService demoService;

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

            String paymentChannel = requestParameters.get("paymentChannel");

            Object data = null;
            if ("1".equals(paymentChannel)) {
                RefundModel refundModel = ApplicationHandler.instantiateObject(RefundModel.class, requestParameters);
                refundModel.setOutRefundNo(UUID.randomUUID().toString());
                data = WeiXinPayUtils.refund(refundModel);
            } else if ("2".equals(paymentChannel)) {

            } else if ("3".equals(paymentChannel)) {
                build.dream.common.models.miya.RefundModel refundModel = build.dream.common.models.miya.RefundModel.builder()
                        .a4(BigInteger.ONE.toString())
                        .a5(BigInteger.TEN.toString())
                        .b1("")
                        .b2(RandomStringUtils.randomAlphanumeric(32))
                        .build();
                data = MiyaUtils.refund(refundModel);
            } else if ("4".equals(paymentChannel)) {

            }
            return ApiRest.builder().data(data).message("退款成功！").successful(true).build();
        };
        return ApplicationHandler.callMethod(methodCaller, "退款失败", requestParameters);
    }

    @RequestMapping(value = "/deductingGoodsStock")
    @ResponseBody
    public String deductingGoodsStock(HttpServletRequest httpServletRequest) {
        BigInteger goodsId = NumberUtils.createBigInteger(ApplicationHandler.getRequestParameter(httpServletRequest, "goodsId"));
        BigInteger goodsSpecificationId = NumberUtils.createBigInteger(ApplicationHandler.getRequestParameter(httpServletRequest, "goodsSpecificationId"));
        BigDecimal quantity = NumberUtils.createBigDecimal(ApplicationHandler.getRequestParameter(httpServletRequest, "quantity"));
        return GsonUtils.toJson(demoService.deductingGoodsStock(goodsId, goodsSpecificationId, quantity));
    }

    @RequestMapping(value = "/addGoodsStock")
    @ResponseBody
    public String addGoodsStock(HttpServletRequest httpServletRequest) {
        BigInteger goodsId = NumberUtils.createBigInteger(ApplicationHandler.getRequestParameter(httpServletRequest, "goodsId"));
        BigInteger goodsSpecificationId = NumberUtils.createBigInteger(ApplicationHandler.getRequestParameter(httpServletRequest, "goodsSpecificationId"));
        BigDecimal quantity = NumberUtils.createBigDecimal(ApplicationHandler.getRequestParameter(httpServletRequest, "quantity"));
        return GsonUtils.toJson(demoService.addGoodsStock(goodsId, goodsSpecificationId, quantity));
    }

    @RequestMapping(value = "/testKafka")
    @ResponseBody
    public String testKafka() {
        KafkaUtils.send("aaaa", UUID.randomUUID().toString(), UUID.randomUUID().toString());
        return Constants.SUCCESS;
    }
}
