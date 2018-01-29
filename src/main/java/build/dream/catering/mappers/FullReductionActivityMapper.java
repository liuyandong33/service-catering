package build.dream.catering.mappers;

import build.dream.common.erp.catering.domains.FullReductionActivity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FullReductionActivityMapper {
    long insert(FullReductionActivity fullReductionActivity);
}
