package build.dream.erp.mappers;

import build.dream.common.erp.domains.ActOrderChargeByMt;
import build.dream.common.utils.SearchModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ActOrderChargeByMtMapper {
    long insert(ActOrderChargeByMt actOrderChargeByMt);
    long update(ActOrderChargeByMt actOrderChargeByMt);
    ActOrderChargeByMt find(SearchModel searchModel);
    List<ActOrderChargeByMt> findAll(SearchModel searchModel);
}
