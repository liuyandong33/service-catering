package build.dream.catering.models.anubis;

import build.dream.common.models.BasicModel;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

public class OrderModel extends BasicModel {
    @NotNull
    private Long tenantId;

    @NotNull
    private Long branchId;

    @NotNull
    private Long userId;

    @NotNull
    private Long dietOrderId;

    @Length(max = 255)
    private String partnerRemark;

    @Length(max = 255)
    private String transportRemark;

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getBranchId() {
        return branchId;
    }

    public void setBranchId(Long branchId) {
        this.branchId = branchId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getDietOrderId() {
        return dietOrderId;
    }

    public void setDietOrderId(Long dietOrderId) {
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
