package build.dream.catering.mappers;

import build.dream.common.erp.catering.domains.GoodsFlavorGroup;
import build.dream.common.utils.SearchModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GoodsFlavorGroupMapper {
    long insert(GoodsFlavorGroup goodsFlavorGroup);
    long insertAll(List<GoodsFlavorGroup> goodsFlavorGroups);
    long update(GoodsFlavorGroup goodsFlavorGroup);
    GoodsFlavorGroup find(SearchModel searchModel);
    List<GoodsFlavorGroup> findAll(SearchModel searchModel);
    List<GoodsFlavorGroup> findAllPaged(SearchModel searchModel);
}
