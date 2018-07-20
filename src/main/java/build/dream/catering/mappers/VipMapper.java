package build.dream.catering.mappers;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.math.BigInteger;

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
}
