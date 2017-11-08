package build.dream.erp.mappers;

import build.dream.common.erp.domains.Branch;
import build.dream.common.erp.domains.MeiTuanOrder;
import build.dream.common.utils.SearchModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MeiTuanOrderMapper {
    long insert(MeiTuanOrder meiTuanOrder);
    long update(MeiTuanOrder meiTuanOrder);
    Branch find(SearchModel searchModel);
    List<Branch> findAll(SearchModel searchModel);
    List<Branch> findAllPaged(SearchModel searchModel);
}
