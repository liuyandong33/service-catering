package build.dream.catering.mappers;

import build.dream.common.erp.catering.domains.MeiTuanOrderItem;
import build.dream.common.utils.SearchModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MeiTuanOrderItemMapper {
    long insert(MeiTuanOrderItem meiTuanItem);
    long update(MeiTuanOrderItem meiTuanItem);
    MeiTuanOrderItem find(SearchModel searchModel);
    List<MeiTuanOrderItem> findAll(SearchModel searchModel);
}
