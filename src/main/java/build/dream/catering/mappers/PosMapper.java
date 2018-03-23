package build.dream.catering.mappers;

import build.dream.common.erp.catering.domains.Pos;
import build.dream.common.utils.SearchModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PosMapper {
    long insert(Pos pos);

    long update(Pos pos);

    Pos find(SearchModel searchModel);

    List<Pos> findAll(SearchModel searchModel);
}
