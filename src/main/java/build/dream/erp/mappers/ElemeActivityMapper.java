package build.dream.erp.mappers;

import build.dream.common.erp.domains.ElemeActivity;
import build.dream.common.utils.SearchModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ElemeActivityMapper {
    long insert(ElemeActivity elemeActivity);
    long update(ElemeActivity elemeActivity);
    ElemeActivity find(SearchModel searchModel);
    List<ElemeActivity> findAll(SearchModel searchModel);
}
