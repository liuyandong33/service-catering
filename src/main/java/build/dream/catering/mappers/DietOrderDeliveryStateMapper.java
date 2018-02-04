package build.dream.catering.mappers;

import build.dream.common.erp.catering.domains.DietOrderDeliveryState;
import build.dream.common.utils.SearchModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DietOrderDeliveryStateMapper {
    long insert(DietOrderDeliveryState dietOrderDeliveryState);

    List<DietOrderDeliveryState> findAll(SearchModel searchModel);
}
