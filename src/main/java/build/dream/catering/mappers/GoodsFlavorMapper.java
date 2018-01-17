package build.dream.catering.mappers;

import build.dream.common.erp.catering.domains.GoodsFlavor;
import build.dream.common.utils.SearchModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;

@Mapper
public interface GoodsFlavorMapper {
    long insert(GoodsFlavor goodsFlavor);
    long insertAll(Collection<GoodsFlavor> goodsFlavors);
    long update(GoodsFlavor goodsFlavor);
    GoodsFlavor find(SearchModel searchModel);
    List<GoodsFlavor> findAll(SearchModel searchModel);
    List<GoodsFlavor> findAllPaged(SearchModel searchModel);
    long deleteAllByGoodsId(@Param("goodsId") BigInteger goodsId, @Param("userId") BigInteger userId, @Param("lastUpdateRemark") String lastUpdateRemark);
}
