package build.dream.catering.services;

import build.dream.catering.utils.GoodsUtils;
import build.dream.catering.utils.SaleFlowUtils;
import build.dream.common.api.ApiRest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

@Service
public class DemoService {
    @Transactional(rollbackFor = Exception.class)
    public ApiRest writeSaleFlow(BigInteger dietOrderId) throws IOException {
        SaleFlowUtils.writeSaleFlow(dietOrderId);

        ApiRest apiRest = new ApiRest();
        apiRest.setMessage("写入流水成功！");
        apiRest.setSuccessful(true);
        return apiRest;
    }

    @Transactional(rollbackFor = Exception.class)
    public ApiRest deductingGoodsStock(BigInteger goodsId, BigInteger goodsSpecificationId, BigDecimal quantity) {
        BigDecimal stock = GoodsUtils.deductingGoodsStock(goodsId, goodsSpecificationId, quantity);
        return ApiRest.builder().data(stock).message("测试扣减库存成功！").successful(true).build();
    }

    @Transactional(rollbackFor = Exception.class)
    public ApiRest addGoodsStock(BigInteger goodsId, BigInteger goodsSpecificationId, BigDecimal quantity) {
        BigDecimal stock = GoodsUtils.addGoodsStock(goodsId, goodsSpecificationId, quantity);
        return ApiRest.builder().data(stock).message("测试增加库存成功！").successful(true).build();
    }
}
