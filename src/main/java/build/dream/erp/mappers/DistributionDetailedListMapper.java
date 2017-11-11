package build.dream.erp.mappers;

import build.dream.common.erp.domains.DistributionDetailedList;
import build.dream.common.utils.SearchModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DistributionDetailedListMapper {
    long insert(DistributionDetailedList distributionDetailedList);
    long insertAll(List<DistributionDetailedList> distributionDetailedList);
    long update(DistributionDetailedList distributionDetailedList);
    DistributionDetailedList find(SearchModel searchModel);
    List<DistributionDetailedList> findAll(SearchModel searchModel);
}
