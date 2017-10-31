package build.dream.erp.mappers;

import build.dream.common.erp.domains.ElemeDeliveryOrderStateChangeMessage;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ElemeDeliveryOrderStateChangeMessageMapper {
    long insert(ElemeDeliveryOrderStateChangeMessage elemeDeliveryOrderStateChangeMessage);
    long update(ElemeDeliveryOrderStateChangeMessage elemeDeliveryOrderStateChangeMessage);
}
