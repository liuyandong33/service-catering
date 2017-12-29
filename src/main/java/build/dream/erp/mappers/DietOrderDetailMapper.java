package build.dream.erp.mappers;

import build.dream.common.erp.catering.domains.DietOrderDetail;
import build.dream.common.utils.SearchModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DietOrderDetailMapper {
    long insert(DietOrderDetail dietOrderDetail);
    long update(DietOrderDetail dietOrderDetail);
    DietOrderDetail find(SearchModel searchModel);
    DietOrderDetail findAll(SearchModel searchModel);
}
