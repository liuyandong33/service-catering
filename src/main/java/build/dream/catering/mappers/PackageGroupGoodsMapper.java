package build.dream.catering.mappers;

import build.dream.common.utils.SearchModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PackageGroupGoodsMapper {
    long deleteAll(SearchModel searchModel);
}
