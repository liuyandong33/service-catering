package build.dream.catering.mappers;

import build.dream.common.erp.catering.domains.Vip;
import build.dream.common.utils.SearchModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface VipMapper {
    long insert(Vip vip);
    long update(Vip vip);
    Vip find(SearchModel searchModel);
    List<Vip> findAll(SearchModel searchModel);
    long count(SearchModel searchModel);
    List<Vip> findAllPaged(SearchModel searchModel);
}
