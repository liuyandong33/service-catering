package build.dream.erp.mappers;

import build.dream.common.erp.catering.domains.Activity;
import build.dream.common.erp.catering.domains.BasicActivity;
import build.dream.common.utils.SearchModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ActivityMapper {
    Activity find(SearchModel searchModel);
    List<Activity> findAll(SearchModel searchModel);
    List<List<BasicActivity>> findAllActivities();
}
