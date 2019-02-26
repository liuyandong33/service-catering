package build.dream.catering.mappers;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@Mapper
public interface VipMapper {
    BigDecimal callProcedureDeductingVipPoint(@Param("tenantId") BigInteger tenantId,
                                              @Param("branchId") BigInteger branchId,
                                              @Param("vipId") BigInteger vipId,
                                              @Param("point") BigDecimal point);

    BigDecimal callProcedureDeductingVipBalance(@Param("tenantId") BigInteger tenantId,
                                                @Param("branchId") BigInteger branchId,
                                                @Param("vipId") BigInteger vipId,
                                                @Param("balance") BigDecimal balance);

    BigDecimal callProcedureAddVipPoint(@Param("tenantId") BigInteger tenantId,
                                        @Param("branchId") BigInteger branchId,
                                        @Param("vipId") BigInteger vipId,
                                        @Param("point") BigDecimal point);

    BigDecimal callProcedureAddVipBalance(@Param("tenantId") BigInteger tenantId,
                                          @Param("branchId") BigInteger branchId,
                                          @Param("vipId") BigInteger vipId,
                                          @Param("balance") BigDecimal balance);

    List<Map<String, Object>> listVipInfos(@Param("tenantId") BigInteger tenantId,
                                           @Param("branchId") BigInteger branchId,
                                           @Param("vipSharedType") Integer vipSharedType,
                                           @Param("offset") Integer offset,
                                           @Param("maxResults") Integer maxResults);

    long countVipInfos(@Param("tenantId") BigInteger tenantId,
                       @Param("branchId") BigInteger branchId,
                       @Param("vipSharedType") Integer vipSharedType);
}
