package build.dream.catering.services;

import build.dream.catering.mappers.ReportMapper;
import build.dream.catering.models.report.CategorySummaryModel;
import build.dream.catering.models.report.PaymentSummaryModel;
import build.dream.catering.models.report.SingleSummaryModel;
import build.dream.common.api.ApiRest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.*;

@Service
public class ReportService {
    @Autowired
    private ReportMapper reportMapper;

    /**
     * 单品汇总
     *
     * @param singleSummaryModel
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest singleSummary(SingleSummaryModel singleSummaryModel) {
        BigInteger tenantId = singleSummaryModel.getTenantId();
        BigInteger branchId = singleSummaryModel.getBranchId();
        Date startTime = singleSummaryModel.getStartTime();
        Date endTime = singleSummaryModel.getEndTime();
        int page = singleSummaryModel.getPage();
        int rows = singleSummaryModel.getRows();

        Long count = reportMapper.countSingleSummary(tenantId, branchId, startTime, endTime);
        if (count == null) {
            count = 0L;
        }
        List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();

        if (count > 0) {
            results = reportMapper.singleSummary(tenantId, branchId, startTime, endTime, (page - 1) * rows, rows);
        }

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("total", count);
        data.put("rows", results);
        return new ApiRest(data, "查询单品汇总成功！");
    }

    /**
     * 分类汇总
     *
     * @param categorySummaryModel
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest categorySummary(CategorySummaryModel categorySummaryModel) {
        BigInteger tenantId = categorySummaryModel.getTenantId();
        BigInteger branchId = categorySummaryModel.getBranchId();
        Date startTime = categorySummaryModel.getStartTime();
        Date endTime = categorySummaryModel.getEndTime();
        int page = categorySummaryModel.getPage();
        int rows = categorySummaryModel.getRows();

        Long count = reportMapper.countCategorySummary(tenantId, branchId, startTime, endTime);
        if (count == null) {
            count = 0L;
        }
        List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();

        if (count > 0) {
            results = reportMapper.categorySummary(tenantId, branchId, startTime, endTime, (page - 1) * rows, rows);
        }

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("total", count);
        data.put("rows", results);
        return new ApiRest(data, "查询分类汇总成功！");
    }

    /**
     * 支付方式汇总
     *
     * @param paymentSummaryModel
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest paymentSummary(PaymentSummaryModel paymentSummaryModel) {
        BigInteger tenantId = paymentSummaryModel.getTenantId();
        BigInteger branchId = paymentSummaryModel.getBranchId();
        Date startTime = paymentSummaryModel.getStartTime();
        Date endTime = paymentSummaryModel.getEndTime();
        int page = paymentSummaryModel.getPage();
        int rows = paymentSummaryModel.getRows();

        Long count = reportMapper.countPaymentSummary(tenantId, branchId, startTime, endTime);
        if (count == null) {
            count = 0L;
        }
        List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();

        if (count > 0) {
            results = reportMapper.paymentSummary(tenantId, branchId, startTime, endTime, (page - 1) * rows, rows);
        }

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("total", count);
        data.put("rows", results);
        return new ApiRest(data, "查询支付方式汇总成功！");
    }
}
