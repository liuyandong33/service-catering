package build.dream.catering.controllers;

import build.dream.catering.constants.Constants;
import build.dream.catering.domains.MongoSaleDetail;
import build.dream.catering.repositories.MongoSaleDetailRepository;
import build.dream.common.annotations.PermitAll;
import build.dream.common.orm.SnowflakeIdGenerator;
import build.dream.common.utils.JacksonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping(value = "/test")
@PermitAll
public class TestController {
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    private MongoSaleDetailRepository mongoSaleDetailRepository;
    private SnowflakeIdGenerator snowflakeIdGenerator;

    @RequestMapping(value = "/testInsert")
    @ResponseBody
    public String testInsert() {
        if (Objects.isNull(snowflakeIdGenerator)) {
            snowflakeIdGenerator = new SnowflakeIdGenerator();
        }

        Long tenantId = snowflakeIdGenerator.nextId().longValue();
        Long branchId = snowflakeIdGenerator.nextId().longValue();
        System.out.println(tenantId);
        System.out.println(branchId);
        for (int index = 0; index < 1000; index++) {
            MongoSaleDetail mongoSaleDetail = new MongoSaleDetail();
            mongoSaleDetail.setId(snowflakeIdGenerator.nextId().longValue());
            mongoSaleDetail.setCreatedTime(new Date());
            mongoSaleDetail.setCreatedUserId(snowflakeIdGenerator.nextId().longValue());
            mongoSaleDetail.setUpdatedTime(new Date());
            mongoSaleDetail.setUpdatedUserId(snowflakeIdGenerator.nextId().longValue());
            mongoSaleDetail.setUpdatedRemark(UUID.randomUUID().toString());
            mongoSaleDetail.setDeleted(false);
            mongoSaleDetail.setDeletedTime(Constants.DATETIME_DEFAULT_VALUE);
            mongoSaleDetail.setSaleId(snowflakeIdGenerator.nextId().longValue());
            mongoSaleDetail.setSaleTime(new Date());
            mongoSaleDetail.setTenantId(tenantId);
            mongoSaleDetail.setTenantCode("61011888");
            mongoSaleDetail.setBranchId(branchId);
            mongoSaleDetail.setGoodsId(snowflakeIdGenerator.nextId().longValue());
            mongoSaleDetail.setGoodsName(UUID.randomUUID().toString());
            mongoSaleDetail.setGoodsSpecificationId(snowflakeIdGenerator.nextId().longValue());
            mongoSaleDetail.setGoodsSpecificationName(UUID.randomUUID().toString());
            mongoSaleDetail.setPrice(Math.random() * 1000);
            mongoSaleDetail.setQuantity(Math.random() * 1000);

            mongoSaleDetail.setTotalAmount(Math.random() * 1000);
            mongoSaleDetail.setDiscountAmount(0D);
            mongoSaleDetail.setPayableAmount(mongoSaleDetail.getTotalAmount());
            mongoSaleDetail.setDiscountShare(0D);
            mongoSaleDetailRepository.insert(mongoSaleDetail);
        }
        return Constants.SUCCESS;
    }

    @RequestMapping(value = "/testAggregate")
    @ResponseBody
    public String testAggregate() {
        List<AggregationOperation> aggregationOperations = new ArrayList<AggregationOperation>();
        AggregationOperation aggregationOperation = Aggregation.group("goodsId", "goodsSpecificationId")
                .sum("totalAmount").as("totalAmount")
                .sum("quantity").as("quantity")
                .sum("discountAmount").as("discountAmount");
        aggregationOperations.add(aggregationOperation);
//        aggregationOperations.add(Aggregation.match(Criteria.where("tenantId").is(154363472375713792L)));
//        aggregationOperations.add(Aggregation.match(Criteria.where("branchId").is(154363472375713793L)));
        aggregationOperations.add(Aggregation.skip(20L));
        aggregationOperations.add(Aggregation.limit(20));
        Aggregation aggregation = Aggregation.newAggregation(aggregationOperations);
        AggregationResults<Map> aggregationResults = mongoTemplate.aggregate(aggregation, "sale_detail", Map.class);
        return JacksonUtils.writeValueAsString(aggregationResults.getMappedResults());
    }
}
