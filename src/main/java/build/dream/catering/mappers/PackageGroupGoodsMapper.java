package build.dream.catering.mappers;

import build.dream.common.erp.catering.domains.PackageGroupGoods;
import build.dream.common.utils.SearchModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PackageGroupGoodsMapper {
    long deleteAll(SearchModel searchModel);
}
