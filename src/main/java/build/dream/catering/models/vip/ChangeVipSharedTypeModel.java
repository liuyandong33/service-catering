package build.dream.catering.models.vip;

import build.dream.common.models.BasicModel;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

public class ChangeVipSharedTypeModel extends BasicModel {
    @NotNull
    private BigInteger tenantId;

    /**
     * 会员共享类型，1-全部共享，2-全部独立，3-分组共享
     */
    @NotNull
    private Integer vipSharedType;

    public BigInteger getTenantId() {
        return tenantId;
    }

    public void setTenantId(BigInteger tenantId) {
        this.tenantId = tenantId;
    }

    public Integer getVipSharedType() {
        return vipSharedType;
    }

    public void setVipSharedType(Integer vipSharedType) {
        this.vipSharedType = vipSharedType;
    }
}
