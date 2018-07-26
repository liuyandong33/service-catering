package build.dream.catering.models.vip;

import build.dream.common.models.BasicModel;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

public class ChangeVipIsolationLevelModel extends BasicModel {
    @NotNull
    private BigInteger tenantId;

    /**
     * 会员隔离级别，1-全部共享，2-全部独立
     */
    @NotNull
    private Integer vipIsolationLevel;

    public BigInteger getTenantId() {
        return tenantId;
    }

    public void setTenantId(BigInteger tenantId) {
        this.tenantId = tenantId;
    }

    public Integer getVipIsolationLevel() {
        return vipIsolationLevel;
    }

    public void setVipIsolationLevel(Integer vipIsolationLevel) {
        this.vipIsolationLevel = vipIsolationLevel;
    }
}
