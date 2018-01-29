package build.dream.catering.mappers;

import build.dream.common.erp.catering.domains.SpecialGoodsActivity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SpecialGoodsActivityMapper {
    long insert(SpecialGoodsActivity specialGoodsActivity);
    long insertAll(List<SpecialGoodsActivity> specialGoodsActivities);
}
