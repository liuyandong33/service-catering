package build.dream.erp.mappers;

import build.dream.common.erp.catering.domains.MeiTuanOrderExtra;
import build.dream.common.utils.SearchModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MeiTuanOrderExtraMapper {
    long insert(MeiTuanOrderExtra meiTuanOrderExtra);
    long update(MeiTuanOrderExtra meiTuanOrderExtra);
    MeiTuanOrderExtra find(SearchModel searchModel);
    List<MeiTuanOrderExtra> findAll(SearchModel searchModel);
}
