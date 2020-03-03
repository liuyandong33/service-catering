package build.dream.catering.mappers;

import build.dream.common.domains.catering.VipAccount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface VipMapper {
    Double callProcedureDeductingVipPoint(@Param("tenantId") Long tenantId,
                                          @Param("vipId") Long vipId,
                                          @Param("vipAccountId") Long vipAccountId,
                                          @Param("point") Double point);

    Double callProcedureDeductingVipBalance(@Param("tenantId") Long tenantId,
                                            @Param("vipId") Long vipId,
                                            @Param("vipAccountId") Long vipAccountId,
                                            @Param("balance") Double balance);

    Double callProcedureAddVipPoint(@Param("tenantId") Long tenantId,
                                    @Param("vipId") Long vipId,
                                    @Param("vipAccountId") Long vipAccountId,
                                    @Param("point") Double point);

    Double callProcedureAddVipBalance(@Param("tenantId") Long tenantId,
                                      @Param("vipId") Long vipId,
                                      @Param("vipAccountId") Long vipAccountId,
                                      @Param("balance") Double balance);

    List<Map<String, Object>> listVipInfos(@Param("tenantId") Long tenantId,
                                           @Param("branchId") Long branchId,
                                           @Param("vipSharedType") Integer vipSharedType,
                                           @Param("offset") Integer offset,
                                           @Param("maxResults") Integer maxResults);

    long countVipInfos(@Param("tenantId") Long tenantId,
                       @Param("branchId") Long branchId,
                       @Param("vipSharedType") Integer vipSharedType);

    VipAccount obtainVipAccount(@Param("tenantId") Long tenantId,
                                @Param("branchId") Long branchId,
                                @Param("vipId") Long vipId,
                                @Param("vipSharedType") Integer vipSharedType);
}
