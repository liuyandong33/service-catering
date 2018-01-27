package build.dream.catering.mappers;

import build.dream.common.erp.catering.domains.CanNotDeleteReason;
import build.dream.common.utils.SearchModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CanNotDeleteReasonMapper {
    long insert(CanNotDeleteReason canNotDeleteReason);
    long insertAll(List<CanNotDeleteReason> canNotDeleteReasons);
    CanNotDeleteReason find(SearchModel searchModel);
    long delete(SearchModel searchModel);
}
