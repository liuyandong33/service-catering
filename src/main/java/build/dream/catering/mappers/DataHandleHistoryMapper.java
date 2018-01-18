package build.dream.catering.mappers;

import build.dream.common.erp.catering.domains.DataHandleHistory;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DataHandleHistoryMapper {
    long insert(DataHandleHistory dataHandleHistory);
}
