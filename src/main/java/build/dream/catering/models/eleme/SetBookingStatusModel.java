package build.dream.catering.models.eleme;

import build.dream.common.models.BasicModel;
import build.dream.common.utils.ApplicationHandler;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;

public class SetBookingStatusModel extends BasicModel {
    @NotNull
    private BigInteger tenantId;

    @NotNull
    private BigInteger branchId;

    @NotEmpty
    private Boolean enabled;

    private Integer maxBookingDays;

    public BigInteger getTenantId() {
        return tenantId;
    }

    public void setTenantId(BigInteger tenantId) {
        this.tenantId = tenantId;
    }

    public BigInteger getBranchId() {
        return branchId;
    }

    public void setBranchId(BigInteger branchId) {
        this.branchId = branchId;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Integer getMaxBookingDays() {
        return maxBookingDays;
    }

    public void setMaxBookingDays(Integer maxBookingDays) {
        this.maxBookingDays = maxBookingDays;
    }

    @Override
    public void validateAndThrow() {
        super.validateAndThrow();
        if (enabled) {
            ApplicationHandler.isTrue(maxBookingDays != null && maxBookingDays >= 0 && maxBookingDays <= 6, "maxBookingDays");
        }
    }
}
