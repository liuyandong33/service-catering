package build.dream.erp.mappers;

import build.dream.common.erp.catering.domains.Goods;
import build.dream.common.utils.SearchModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GoodsMapper {
    long insert(Goods goods);
    long update(Goods goods);
    Goods find(SearchModel searchModel);
    List<Goods> findAll(SearchModel searchModel);
    List<Goods> findAllPaged(SearchModel searchModel);
}
