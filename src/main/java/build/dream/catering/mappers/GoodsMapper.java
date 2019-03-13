package build.dream.catering.mappers;

import build.dream.common.catering.domains.Goods;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@Mapper
public interface GoodsMapper {
    List<Map<String, Object>> listPackageInfos(@Param("tenantId") BigInteger tenantId,
                                               @Param("branchId") BigInteger branchId,
                                               @Param("packageIds") List<BigInteger> packageIds,
                                               @Param("groupType") Integer groupType);

    List<Goods> findAllByIdInList(@Param("tenantId") BigInteger tenantId, @Param("branchId") BigInteger branchId, @Param("goodsIds") List<BigInteger> goodsIds);

    List<Goods> findAllByCategoryId(@Param("tenantId") BigInteger tenantId, @Param("branchId") BigInteger branchId, @Param("categoryId") BigInteger categoryId);

    BigDecimal deductingGoodsStock(@Param("goodsId") BigInteger goodsId,
                                   @Param("goodsSpecificationId") BigInteger goodsSpecificationId,
                                   @Param("quantity") BigDecimal quantity);

    BigDecimal addGoodsStock(@Param("goodsId") BigInteger goodsId,
                             @Param("goodsSpecificationId") BigInteger goodsSpecificationId,
                             @Param("quantity") BigDecimal quantity);
}
