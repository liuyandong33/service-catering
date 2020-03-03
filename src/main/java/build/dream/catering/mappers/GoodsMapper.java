package build.dream.catering.mappers;

import build.dream.catering.beans.PackageDetail;
import build.dream.common.domains.catering.Goods;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

@Mapper
public interface GoodsMapper {
    List<PackageDetail> listPackageInfos(@Param("tenantId") Long tenantId,
                                         @Param("branchId") Long branchId,
                                         @Param("packageIds") Collection<Long> packageIds,
                                         @Param("groupType") Integer groupType);

    List<Goods> findAllByIdInList(@Param("tenantId") Long tenantId, @Param("branchId") Long branchId, @Param("goodsIds") List<Long> goodsIds);

    List<Goods> findAllByCategoryId(@Param("tenantId") Long tenantId, @Param("branchId") Long branchId, @Param("categoryId") Long categoryId);

    Double deductingGoodsStock(@Param("goodsId") Long goodsId,
                               @Param("goodsSpecificationId") Long goodsSpecificationId,
                               @Param("quantity") Double quantity);

    Double addGoodsStock(@Param("goodsId") Long goodsId,
                         @Param("goodsSpecificationId") Long goodsSpecificationId,
                         @Param("quantity") Double quantity);

    List<Goods> findAll(@Param("tenantId") Long tenantId, @Param("branchId") Long branchId);
}
