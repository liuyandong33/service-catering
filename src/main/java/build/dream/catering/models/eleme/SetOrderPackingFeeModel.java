package build.dream.catering.models.eleme;

import build.dream.common.models.BasicModel;
import build.dream.common.utils.ApplicationHandler;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;

public class SetOrderPackingFeeModel extends BasicModel {
    @NotNull
    private BigInteger tenantId;

    @NotNull
    private BigInteger branchId;

    @NotNull
    private Boolean status;

    @Min(0)
    @Max(8)
    private Double packingFee;

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

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Double getPackingFee() {
        return packingFee;
    }

    public void setPackingFee(Double packingFee) {
        this.packingFee = packingFee;
    }

    @Override
    public void validateAndThrow() {
        super.validateAndThrow();
        if (status) {
            ApplicationHandler.notNull(packingFee, "packingFee");
        }
    }
}
