package build.dream.erp.mappers;

import build.dream.common.erp.domains.ElemeItemAttribute;
import build.dream.common.utils.SearchModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ElemeItemAttributeMapper {
    long insert(ElemeItemAttribute elemeItemAttribute);
    long update(ElemeItemAttribute elemeItemAttribute);
    ElemeItemAttribute find(SearchModel searchModel);
    List<ElemeItemAttribute> findAll(SearchModel searchModel);
}
