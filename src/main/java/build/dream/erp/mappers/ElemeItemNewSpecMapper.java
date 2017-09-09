package build.dream.erp.mappers;

import build.dream.common.erp.domains.ElemeItemNewSpec;
import build.dream.common.utils.SearchModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ElemeItemNewSpecMapper {
    long insert(ElemeItemNewSpec elemeItemNewSpec);
    long update(ElemeItemNewSpec elemeItemNewSpec);
    ElemeItemNewSpec find(SearchModel searchModel);
    List<ElemeItemNewSpec> findAll(SearchModel searchModel);
}
