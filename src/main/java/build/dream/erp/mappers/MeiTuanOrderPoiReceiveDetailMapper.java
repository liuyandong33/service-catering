package build.dream.erp.mappers;

import build.dream.common.erp.domains.MeiTuanOrderPoiReceiveDetail;
import build.dream.common.utils.SearchModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MeiTuanOrderPoiReceiveDetailMapper {
    long insert(MeiTuanOrderPoiReceiveDetail meiTuanOrderPoiReceiveDetail);
    long update(MeiTuanOrderPoiReceiveDetail meiTuanOrderPoiReceiveDetail);
    MeiTuanOrderPoiReceiveDetail find(SearchModel searchModel);
    List<MeiTuanOrderPoiReceiveDetail> findAll(SearchModel searchModel);
}
