package build.dream.catering.mappers;

import build.dream.common.utils.UpdateModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface UniversalMapper {
    long universalUpdate(UpdateModel updateModel);
    List<Map<String, Object>> executeQuery(Map<String, Object> parameters);
    long executeUpdate(Map<String, Object> parameters);
}
