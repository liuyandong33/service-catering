package build.dream.catering.models.anubis;

import build.dream.common.models.BasicModel;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

public class OrderModel extends BasicModel {
    @NotNull
    private BigInteger tenantId;

    @NotNull
    private BigInteger branchId;

    @NotNull
    private BigInteger userId;

    @NotNull
    private BigInteger dietOrderId;

    @Length(max = 255)
    private String partnerRemark;

    @Length(max = 255)
    private String transportRemark;

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

    public BigInteger getUserId() {
        return userId;
    }

    public void setUserId(BigInteger userId) {
        this.userId = userId;
    }

    public BigInteger getDietOrderId() {
        return dietOrderId;
    }

    public void setDietOrderId(BigInteger dietOrderId) {
        this.dietOrderId = dietOrderId;
    }

    public String getPartnerRemark() {
        return partnerRemark;
    }

    public void setPartnerRemark(String partnerRemark) {
        this.partnerRemark = partnerRemark;
    }

    public String getTransportRemark() {
        return transportRemark;
    }

    public void setTransportRemark(String transportRemark) {
        this.transportRemark = transportRemark;
    }
}
