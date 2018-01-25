package build.dream.catering.mappers;

import build.dream.common.erp.catering.domains.ElemeOrderActivity;
import build.dream.common.utils.SearchModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ElemeOrderActivityMapper {
    long insert(ElemeOrderActivity elemeOrderActivity);
    long update(ElemeOrderActivity elemeOrderActivity);
    ElemeOrderActivity find(SearchModel searchModel);
    List<ElemeOrderActivity> findAll(SearchModel searchModel);
}
