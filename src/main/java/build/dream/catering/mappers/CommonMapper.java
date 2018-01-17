package build.dream.catering.mappers;

import build.dream.common.utils.UpdateModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommonMapper {
    long universalUpdate(UpdateModel updateModel);
}
