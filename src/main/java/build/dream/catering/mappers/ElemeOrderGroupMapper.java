package build.dream.catering.mappers;

import build.dream.common.erp.catering.domains.ElemeOrderGroup;
import build.dream.common.utils.SearchModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ElemeOrderGroupMapper {
    long insert(ElemeOrderGroup elemeOrderGroup);
    long update(ElemeOrderGroup elemeOrderGroup);
    ElemeOrderGroup find(SearchModel searchModel);
    List<ElemeOrderGroup> findAll(SearchModel searchModel);
}
