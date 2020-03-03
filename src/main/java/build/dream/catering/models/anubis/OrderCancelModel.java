package build.dream.catering.models.anubis;

import build.dream.common.models.BasicModel;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

public class OrderCancelModel extends BasicModel {
    @NotNull
    private Long tenantId;

    @NotNull
    private Long branchId;

    @NotNull
    private Long userId;

    @NotNull
    private Long dietOrderId;

    @NotNull
    private Integer orderCancelCode;

    @Length(max = 128)
    private String orderCancelDescription;

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
