package build.dream.catering.mappers;

import build.dream.common.erp.catering.domains.Activity;
import build.dream.common.utils.SearchModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ActivityMapper {
    long insert(Activity activity);
    Activity find(SearchModel searchModel);
    long count(SearchModel searchModel);
    List<Activity> findAllPaged(SearchModel searchModel);
}
