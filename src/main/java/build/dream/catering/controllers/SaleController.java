package build.dream.catering.controllers;

import build.dream.catering.constants.Constants;
import build.dream.common.catering.domains.SaleDetail;
import build.dream.common.utils.DatabaseHelper;
import build.dream.common.utils.JacksonUtils;
import build.dream.common.utils.PagedSearchModel;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

@Controller
@RequestMapping(value = "/sale")
public class SaleController {
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @RequestMapping(value = "/index")
    @ResponseBody
    public String index() throws IOException {
        boolean proceed = true;
        BigInteger minId = BigInteger.valueOf(1074673661027422208L);
        while (proceed) {
            PagedSearchModel pagedSearchModel = new PagedSearchModel(true);
            pagedSearchModel.addSearchCondition(SaleDetail.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, BigInteger.valueOf(3));
            pagedSearchModel.addSearchCondition(SaleDetail.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_GREATER_THAN, minId);
            pagedSearchModel.setPage(1);
            pagedSearchModel.setRows(5000);

            List<SaleDetail> saleDetails = DatabaseHelper.findAllPaged(SaleDetail.class, pagedSearchModel);

            int size = saleDetails.size();
            if (size > 0) {
                for (SaleDetail saleDetail : saleDetails) {
                    IndexRequest indexRequest = new IndexRequest("zd1_catering_sale_detail");
                    indexRequest.id(saleDetail.getId().toString());
                    indexRequest.source(JacksonUtils.writeValueAsString(saleDetail), XContentType.JSON);
                    IndexResponse indexResponse = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);

                    if (indexResponse.getResult() == DocWriteResponse.Result.CREATED) {
                        System.out.println("新增文档成功!");
                    }
                }
                minId = saleDetails.get(size - 1).getId();
            } else {
                proceed = false;
            }
        }
        return Constants.SUCCESS;
    }
}
