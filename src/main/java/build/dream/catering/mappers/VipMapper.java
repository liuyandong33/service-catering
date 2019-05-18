package build.dream.catering.mappers;

import build.dream.common.catering.domains.VipAccount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@Mapper
public interface VipMapper {
    BigDecimal callProcedureDeductingVipPoint(@Param("tenantId") BigInteger tenantId,
                                              @Param("vipId") BigInteger vipId,
                                              @Param("vipAccountId") BigInteger vipAccountId,
                                              @Param("point") BigDecimal point);

    BigDecimal callProcedureDeductingVipBalance(@Param("tenantId") BigInteger tenantId,
                                                @Param("vipId") BigInteger vipId,
                                                @Param("vipAccountId") BigInteger vipAccountId,
                                                @Param("balance") BigDecimal balance);

    BigDecimal callProcedureAddVipPoint(@Param("tenantId") BigInteger tenantId,
                                        @Param("vipId") BigInteger vipId,
                                        @Param("vipAccountId") BigInteger vipAccountId,
                                        @Param("point") BigDecimal point);

    BigDecimal callProcedureAddVipBalance(@Param("tenantId") BigInteger tenantId,
                                          @Param("vipId") BigInteger vipId,
                                          @Param("vipAccountId") BigInteger vipAccountId,
                                          @Param("balance") BigDecimal balance);

    List<Map<String, Object>> listVipInfos(@Param("tenantId") BigInteger tenantId,
                                           @Param("branchId") BigInteger branchId,
                                           @Param("vipSharedType") Integer vipSharedType,
                                           @Param("offset") Integer offset,
                                           @Param("maxResults") Integer maxResults);

    long countVipInfos(@Param("tenantId") BigInteger tenantId,
                       @Param("branchId") BigInteger branchId,
                       @Param("vipSharedType") Integer vipSharedType);

    VipAccount obtainVipAccount(@Param("tenantId") BigInteger tenantId,
                                @Param("branchId") BigInteger branchId,
                                @Param("vipId") BigInteger vipId,
                                @Param("vipSharedType") Integer vipSharedType);
}
