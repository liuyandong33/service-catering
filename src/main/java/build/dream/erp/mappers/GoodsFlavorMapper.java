package build.dream.erp.mappers;

import build.dream.common.erp.catering.domains.GoodsFlavor;
import build.dream.common.utils.SearchModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GoodsFlavorMapper {
    long insert(GoodsFlavor goodsFlavor);
    long update(GoodsFlavor goodsFlavor);
    GoodsFlavor find(SearchModel searchModel);
    List<GoodsFlavor> findAll(SearchModel searchModel);
    List<GoodsFlavor> findAllPaged(SearchModel searchModel);
}
