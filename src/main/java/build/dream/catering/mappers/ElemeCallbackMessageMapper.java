package build.dream.catering.mappers;

import build.dream.common.erp.catering.domains.ElemeCallbackMessage;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ElemeCallbackMessageMapper {
    long insert(ElemeCallbackMessage elemeCallbackMessage);
}
