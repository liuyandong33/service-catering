package build.dream.catering.mappers;

import build.dream.common.utils.SearchModel;
import build.dream.common.utils.UpdateModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface UniversalMapper {
    long insert(Object domain);

    long insertAll(List<?> domains);

    long update(Object domain);

    long universalUpdate(UpdateModel updateModel);

    long executeUpdate(Map<String, Object> parameters);

    Map<String, Object> find(SearchModel searchModel);

    List<Map<String, Object>> findAll(SearchModel searchModel);

    long count(SearchModel searchModel);

    long universalCount(Map<String, Object> parameters);

    List<Map<String, Object>> findAllPaged(SearchModel searchModel);

    List<Map<String, Object>> executeQuery(Map<String, Object> parameters);

    Map<String, Object> executeUniqueResultQuery(Map<String, Object> parameters);
}
