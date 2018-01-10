package build.dream.erp.mappers;

import build.dream.common.erp.catering.domains.ElemeOrderItemAttribute;
import build.dream.common.utils.SearchModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ElemeOrderItemAttributeMapper {
    long insert(ElemeOrderItemAttribute elemeOrderItemAttribute);
    long update(ElemeOrderItemAttribute elemeOrderItemAttribute);
    ElemeOrderItemAttribute find(SearchModel searchModel);
    List<ElemeOrderItemAttribute> findAll(SearchModel searchModel);
}
