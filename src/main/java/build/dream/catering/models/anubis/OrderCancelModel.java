package build.dream.catering.models.anubis;

import build.dream.common.models.BasicModel;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

public class OrderCancelModel extends BasicModel {
    @NotNull
    private BigInteger tenantId;

    @NotNull
    private BigInteger branchId;

    @NotNull
    private BigInteger userId;

    @NotNull
    private BigInteger dietOrderId;

    @NotNull
    private Integer orderCancelCode;

    @Length(max = 128)
    private String orderCancelDescription;

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

    public Integer getOrderCancelCode() {
        return orderCancelCode;
    }

    public void setOrderCancelCode(Integer orderCancelCode) {
        this.orderCancelCode = orderCancelCode;
    }

    public String getOrderCancelDescription() {
        return orderCancelDescription;
    }

    public void setOrderCancelDescription(String orderCancelDescription) {
        this.orderCancelDescription = orderCancelDescription;
    }
}
