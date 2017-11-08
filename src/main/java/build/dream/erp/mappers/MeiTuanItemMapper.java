package build.dream.erp.mappers;

import build.dream.common.erp.domains.MeiTuanItem;
import build.dream.common.utils.SearchModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MeiTuanItemMapper {
    long insert(MeiTuanItem meiTuanItem);
    long update(MeiTuanItem meiTuanItem);
    MeiTuanItem find(SearchModel searchModel);
    List<MeiTuanItem> findAll(SearchModel searchModel);
}
