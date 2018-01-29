package build.dream.catering.mappers;

import build.dream.common.erp.catering.domains.CanNotOperateReason;
import build.dream.common.utils.SearchModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CanNotOperateReasonMapper {
    long insert(CanNotOperateReason canNotOperateReason);
    long insertAll(List<CanNotOperateReason> canNotOperateReasons);
    CanNotOperateReason find(SearchModel searchModel);
    long delete(SearchModel searchModel);
}
