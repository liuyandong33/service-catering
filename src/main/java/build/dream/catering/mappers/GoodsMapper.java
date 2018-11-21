package build.dream.catering.mappers;

import build.dream.common.erp.catering.domains.Goods;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@Mapper
public interface GoodsMapper {
    List<Map<String, Object>> listPackageInfos(@Param("packageIds") List<BigInteger> packageIds, @Param("groupType") Integer groupType);

    List<Goods> findAllGoodsInfos(@Param("tenantId") BigInteger tenantId, @Param("branchId") BigInteger branchId, @Param("goodsIds") List<BigInteger> goodsIds);

    BigDecimal deductingGoodsStock(@Param("goodsId") BigInteger goodsId,
                                                @Param("goodsSpecificationId") BigInteger goodsSpecificationId,
                                                @Param("quantity") BigDecimal quantity);

    BigDecimal addGoodsStock(@Param("goodsId") BigInteger goodsId,
                                          @Param("goodsSpecificationId") BigInteger goodsSpecificationId,
                                          @Param("quantity") BigDecimal quantity);
}
