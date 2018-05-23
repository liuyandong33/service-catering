package build.dream.catering.mappers;

import build.dream.common.erp.catering.domains.CanNotOperateReason;
import build.dream.common.utils.SearchModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CanNotOperateReasonMapper {
    long delete(SearchModel searchModel);
}
