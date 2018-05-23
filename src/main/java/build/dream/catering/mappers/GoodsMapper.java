package build.dream.catering.mappers;

import build.dream.common.erp.catering.domains.Goods;
import build.dream.common.utils.SearchModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@Mapper
public interface GoodsMapper {
    List<Map<String, Object>> listPackageInfos(@Param("packageIds") List<BigInteger> packageIds);
}
