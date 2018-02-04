package build.dream.catering.models.anubis;

import build.dream.common.models.BasicModel;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

public class OrderComplaintModel extends BasicModel {
    @NotNull
    private BigInteger tenantId;

    @NotNull
    private BigInteger branchId;

    @NotNull
    private BigInteger dietOrderId;

    @NotNull
    private Integer orderComplaintCode;

    @Length(max = 128)
    private String orderComplaintDesc;

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

    public BigInteger getDietOrderId() {
        return dietOrderId;
    }

    public void setDietOrderId(BigInteger dietOrderId) {
        this.dietOrderId = dietOrderId;
    }

    public Integer getOrderComplaintCode() {
        return orderComplaintCode;
    }

    public void setOrderComplaintCode(Integer orderComplaintCode) {
        this.orderComplaintCode = orderComplaintCode;
    }

    public String getOrderComplaintDesc() {
        return orderComplaintDesc;
    }

    public void setOrderComplaintDesc(String orderComplaintDesc) {
        this.orderComplaintDesc = orderComplaintDesc;
    }
}
