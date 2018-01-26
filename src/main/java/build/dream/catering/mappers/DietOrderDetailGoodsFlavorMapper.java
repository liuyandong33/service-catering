package build.dream.catering.mappers;

import build.dream.common.erp.catering.domains.DietOrderDetailGoodsFlavor;
import build.dream.common.utils.SearchModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DietOrderDetailGoodsFlavorMapper {
    long insert(DietOrderDetailGoodsFlavor dietOrderDetailGoodsFlavor);
    long insertAll(List<DietOrderDetailGoodsFlavor> dietOrderDetailGoodsFlavors);
    long update(DietOrderDetailGoodsFlavor dietOrderDetailGoodsFlavor);
    DietOrderDetailGoodsFlavor find(SearchModel searchModel);
    List<DietOrderDetailGoodsFlavor> findAll(SearchModel searchModel);
}
