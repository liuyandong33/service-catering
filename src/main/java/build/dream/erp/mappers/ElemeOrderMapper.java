package build.dream.erp.mappers;

import build.dream.common.erp.catering.domains.ElemeOrder;
import build.dream.common.utils.SearchModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ElemeOrderMapper {
    long insert(ElemeOrder elemeOrder);
    long update(ElemeOrder elemeOrder);
    ElemeOrder find(SearchModel searchModel);
    List<ElemeOrder> findAll(SearchModel searchModel);
    List<ElemeOrder> findAllPaged(SearchModel searchModel);
}
