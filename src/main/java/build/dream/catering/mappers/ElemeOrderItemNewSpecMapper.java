package build.dream.catering.mappers;

import build.dream.common.erp.catering.domains.ElemeOrderItemNewSpec;
import build.dream.common.utils.SearchModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ElemeOrderItemNewSpecMapper {
    long insert(ElemeOrderItemNewSpec elemeOrderItemNewSpec);
    long update(ElemeOrderItemNewSpec elemeOrderItemNewSpec);
    ElemeOrderItemNewSpec find(SearchModel searchModel);
    List<ElemeOrderItemNewSpec> findAll(SearchModel searchModel);
}
