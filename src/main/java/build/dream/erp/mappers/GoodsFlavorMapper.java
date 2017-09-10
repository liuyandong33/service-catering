package build.dream.erp.mappers;

import build.dream.common.erp.domains.GoodsFlavor;
import build.dream.common.utils.SearchModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GoodsFlavorMapper {
    long insert(GoodsFlavor goodsFlavor);
    long update(GoodsFlavor goodsFlavor);
    GoodsFlavor find(SearchModel searchModel);
    List<GoodsFlavor> findAll(@Param("goodsFlavorTableName") String goodsFlavorTableName, @Param("searchModel") SearchModel searchModel);
    List<GoodsFlavor> findAllPaged(@Param("goodsFlavorTableName") String goodsFlavorTableName, @Param("searchModel") SearchModel searchModel);
}
