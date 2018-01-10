package build.dream.erp.mappers;

import build.dream.common.erp.catering.domains.GoodsFlavorGroup;
import build.dream.common.utils.SearchModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

@Mapper
public interface GoodsFlavorGroupMapper {
    long insert(GoodsFlavorGroup goodsFlavorGroup);
    long update(GoodsFlavorGroup goodsFlavorGroup);
    List<GoodsFlavorGroup> findAll(SearchModel searchModel);
    long insertAll(Collection<GoodsFlavorGroup> goodsFlavorGroups);
}
