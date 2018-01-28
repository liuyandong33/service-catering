package build.dream.catering.mappers;

import build.dream.common.erp.catering.domains.BuyGiveActivity;
import build.dream.common.utils.SearchModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BuyGiveActivityMapper {
    long insert(BuyGiveActivity buyGiveActivity);
    long insertAll(List<BuyGiveActivity> buyGiveActivities);
    List<BuyGiveActivity> findAll(SearchModel searchModel);
}
