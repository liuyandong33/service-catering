package erp.chain.mappers;

import erp.chain.domains.Tenant;
import org.apache.ibatis.annotations.Mapper;

/**
 * Created by liuyandong on 2017/7/18.
 */
@Mapper
public interface TenantMapper {
    int insert(Tenant tenant);
}
