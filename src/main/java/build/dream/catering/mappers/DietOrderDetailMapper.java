package build.dream.catering.mappers;

import build.dream.common.erp.catering.domains.DietOrderDetail;
import build.dream.common.utils.SearchModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DietOrderDetailMapper {
    long insert(DietOrderDetail dietOrderDetail);
    long insertAll(List<DietOrderDetail> dietOrderDetails);
    long update(DietOrderDetail dietOrderDetail);
    DietOrderDetail find(SearchModel searchModel);
    DietOrderDetail findAll(SearchModel searchModel);
}
