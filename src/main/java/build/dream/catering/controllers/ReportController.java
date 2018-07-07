package build.dream.catering.controllers;

import build.dream.catering.models.report.CategorySummaryModel;
import build.dream.catering.models.report.PaymentSummaryModel;
import build.dream.catering.models.report.SingleSummaryModel;
import build.dream.catering.services.ReportService;
import build.dream.common.annotations.ApiRestAction;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/report")
public class ReportController {
    /**
     * 单品汇总
     *
     * @return
     */
    @RequestMapping(value = "/singleSummary", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = SingleSummaryModel.class, serviceClass = ReportService.class, serviceMethodName = "singleSummary", error = "查询单品汇总失败")
    public String singleSummary() {
        return null;
    }

    /**
     * 分类汇总
     *
     * @return
     */
    @RequestMapping(value = "/categorySummary", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = CategorySummaryModel.class, serviceClass = ReportService.class, serviceMethodName = "categorySummary", error = "查询分类汇总失败")
    public String categorySummary() {
        return null;
    }

    /**
     * 支付方式汇总
     *
     * @return
     */
    @RequestMapping(value = "/paymentSummary", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = PaymentSummaryModel.class, serviceClass = ReportService.class, serviceMethodName = "paymentSummary", error = "查询支付方式汇总失败")
    public String paymentSummary() {
        return null;
    }
}
