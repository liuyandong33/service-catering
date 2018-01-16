package build.dream.catering.mappers;

import build.dream.common.erp.catering.domains.PackageGroup;
import build.dream.common.utils.SearchModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PackageGroupMapper {
    long insert(PackageGroup packageGroup);
    long insertAll(List<PackageGroup> packageGroups);
    long update(PackageGroup packageGroup);
    PackageGroup find(SearchModel searchModel);
    List<PackageGroup> findAll(SearchModel searchModel);
}
