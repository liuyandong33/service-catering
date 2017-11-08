package build.dream.erp.mappers;

import build.dream.common.erp.domains.MeiTuanOrderCancelMessage;
import build.dream.common.utils.SearchModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MeiTuanOrderCancelMessageMapper {
    long insert(MeiTuanOrderCancelMessage meiTuanOrderCancelMessage);
    long update(MeiTuanOrderCancelMessage meiTuanOrderCancelMessage);
    MeiTuanOrderCancelMessage find(SearchModel searchModel);
    List<MeiTuanOrderCancelMessage> findAll(SearchModel searchModel);
}
