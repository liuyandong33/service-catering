package build.dream.catering.mappers;

import build.dream.common.erp.catering.domains.ElemeCallbackMessage;
import build.dream.common.utils.SearchModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ElemeCallbackMessageMapper {
    long insert(ElemeCallbackMessage elemeCallbackMessage);
    ElemeCallbackMessage find(SearchModel searchModel);
}
