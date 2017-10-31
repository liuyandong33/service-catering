package build.dream.erp.models.eleme;

import build.dream.common.models.BasicModel;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

public class DisagreeRefundLiteModel extends BasicModel {
    @NotNull
    private BigInteger tenantId;

    @NotNull
    private BigInteger branchId;

    @NotNull
    private BigInteger elemeOrderId;

    private String reason;

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

    public BigInteger getElemeOrderId() {
        return elemeOrderId;
    }

    public void setElemeOrderId(BigInteger elemeOrderId) {
        this.elemeOrderId = elemeOrderId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
