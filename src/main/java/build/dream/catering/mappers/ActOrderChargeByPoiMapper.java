package build.dream.catering.mappers;

import build.dream.common.erp.catering.domains.ActOrderChargeByPoi;
import build.dream.common.utils.SearchModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ActOrderChargeByPoiMapper {
    long insert(ActOrderChargeByPoi actOrderChargeByPoi);
    long update(ActOrderChargeByPoi actOrderChargeByPoi);
    ActOrderChargeByPoi find(SearchModel searchModel);
    List<ActOrderChargeByPoi> findAll(SearchModel searchModel);
}
