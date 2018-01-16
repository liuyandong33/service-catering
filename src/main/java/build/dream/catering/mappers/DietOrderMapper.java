package build.dream.catering.mappers;

import build.dream.common.erp.catering.domains.DietOrder;
import build.dream.common.utils.SearchModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DietOrderMapper {
    long insert(DietOrder dietOrder);
    long update(DietOrder dietOrder);
    DietOrder find(SearchModel searchModel);
    List<DietOrder> findAll(SearchModel searchModel);
}
