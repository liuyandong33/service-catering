package build.dream.catering.mappers;

import build.dream.common.erp.catering.domains.ElemeDeliveryOrderStateChangeMessage;
import build.dream.common.utils.SearchModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ElemeDeliveryOrderStateChangeMessageMapper {
    long insert(ElemeDeliveryOrderStateChangeMessage elemeDeliveryOrderStateChangeMessage);
    long update(ElemeDeliveryOrderStateChangeMessage elemeDeliveryOrderStateChangeMessage);
    ElemeDeliveryOrderStateChangeMessage find(SearchModel searchModel);
    List<ElemeDeliveryOrderStateChangeMessage> findAll(SearchModel searchModel);
}
