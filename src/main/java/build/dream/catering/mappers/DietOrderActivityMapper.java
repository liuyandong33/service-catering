package build.dream.catering.mappers;

import build.dream.common.erp.catering.domains.DietOrderActivity;
import build.dream.common.utils.SearchModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DietOrderActivityMapper {
    long insert(DietOrderActivity dietOrderActivity);
    long insertAll(List<DietOrderActivity> dietOrderActivities);
    long update(DietOrderActivity dietOrderActivity);
    DietOrderActivity find(SearchModel searchModel);
    List<DietOrderActivity> findAll(SearchModel searchModel);
}
