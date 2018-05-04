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
    long insert(Goods goods);

    long insertAll(List<Goods> goodses);

    long update(Goods goods);

    Goods find(SearchModel searchModel);

    List<Goods> findAll(SearchModel searchModel);

    List<Goods> findAllPaged(SearchModel searchModel);

    List<Map<String, Object>> listPackageInfos(@Param("packageIds") List<BigInteger> packageIds);
}
