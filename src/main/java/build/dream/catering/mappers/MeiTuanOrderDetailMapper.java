package build.dream.catering.mappers;

import build.dream.common.erp.catering.domains.MeiTuanOrderDetail;
import build.dream.common.utils.SearchModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MeiTuanOrderDetailMapper {
    long insert(MeiTuanOrderDetail meiTuanOrderDetail);
    long update(MeiTuanOrderDetail meiTuanOrderDetail);
    MeiTuanOrderDetail find(SearchModel searchModel);
    List<MeiTuanOrderDetail> findAll(SearchModel searchModel);
}
