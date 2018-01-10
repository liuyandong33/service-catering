package build.dream.erp.mappers;

import build.dream.common.erp.catering.domains.ElemeGroup;
import build.dream.common.utils.SearchModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ElemeGroupMapper {
    long insert(ElemeGroup elemeGroup);
    long update(ElemeGroup elemeGroup);
    ElemeGroup find(SearchModel searchModel);
    List<ElemeGroup> findAll(SearchModel searchModel);
}
