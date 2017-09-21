package build.dream.erp.mappers;

import build.dream.common.erp.domains.GoodsSpecification;
import build.dream.common.utils.SearchModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GoodsSpecificationMapper {
    long insert(GoodsSpecification goodsSpecification);
    long update(GoodsSpecification goodsSpecification);
    GoodsSpecification find(SearchModel searchModel);
    List<GoodsSpecification> findAll(SearchModel searchModel);
    List<GoodsSpecification> findAllPaged(SearchModel searchModel);
}
