package build.dream.erp.mappers;

import build.dream.common.erp.catering.domains.ElemeOrderItem;
import build.dream.common.utils.SearchModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ElemeOrderItemMapper {
    long insert(ElemeOrderItem elemeOrderItem);
    long update(ElemeOrderItem elemeOrderItem);
    ElemeOrderItem find(SearchModel searchModel);
    List<ElemeOrderItem> findAll(SearchModel searchModel);
}
