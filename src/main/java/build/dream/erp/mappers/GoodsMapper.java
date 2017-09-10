package build.dream.erp.mappers;

import build.dream.common.erp.domains.Goods;
import build.dream.common.utils.SearchModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GoodsMapper {
    long insert(Goods goods);
    long update(Goods goods);
    Goods find(@Param("goodsTableName") String goodsTableName, @Param("searchModel") SearchModel searchModel);
    List<Goods> findAll(@Param("goodsTableName") String goodsTableName, @Param("searchModel") SearchModel searchModel);
    List<Goods> findAllPaged(@Param("goodsTableName") String goodsTableName, @Param("searchModel") SearchModel searchModel);
}
