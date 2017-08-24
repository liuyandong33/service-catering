package build.dream.erp.mappers;

import build.dream.common.erp.domains.Branch;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BranchMapper {
    long insert(Branch branch);
    long update(Branch branch);
}
