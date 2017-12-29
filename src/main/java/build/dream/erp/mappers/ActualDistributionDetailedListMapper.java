package build.dream.erp.mappers;

import build.dream.common.erp.catering.domains.ActualDistributionDetailedList;
import build.dream.common.utils.SearchModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ActualDistributionDetailedListMapper {
    long insert(ActualDistributionDetailedList actualDistributionDetailedList);
    long insertAll(List<ActualDistributionDetailedList> actualDistributionDetailedLists);
    long update(ActualDistributionDetailedList actualDistributionDetailedList);
    ActualDistributionDetailedList find(SearchModel searchModel);
    List<ActualDistributionDetailedList> findAll(SearchModel searchModel);
}
