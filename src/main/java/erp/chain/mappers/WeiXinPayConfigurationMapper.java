package erp.chain.mappers;

import erp.chain.domains.WeiXinPayConfiguration;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;

/**
 * Created by liuyandong on 2017/7/20.
 */
@Mapper
public interface WeiXinPayConfigurationMapper {
    int insert(WeiXinPayConfiguration weiXinPayConfiguration);
    int update(WeiXinPayConfiguration weiXinPayConfiguration);
    WeiXinPayConfiguration findByTenantIdAndBranchId(@Param("tenantId") BigInteger tenantId, @Param("branchId") BigInteger branchId);
}
