package build.dream.erp.mappers;

import build.dream.common.erp.catering.domains.MeiTuanOrder;
import build.dream.common.utils.SearchModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MeiTuanOrderMapper {
    long insert(MeiTuanOrder meiTuanOrder);
    long update(MeiTuanOrder meiTuanOrder);
    MeiTuanOrder find(SearchModel searchModel);
    List<MeiTuanOrder> findAll(SearchModel searchModel);
    List<MeiTuanOrder> findAllPaged(SearchModel searchModel);
}
