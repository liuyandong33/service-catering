package build.dream.erp.mappers;

import build.dream.common.erp.domains.ElemeItem;
import build.dream.common.utils.SearchModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ElemeItemMapper {
    long insert(ElemeItem elemeItem);
    long update(ElemeItem elemeItem);
    ElemeItem find(SearchModel searchModel);
    List<ElemeItem> findAll(SearchModel searchModel);
}
