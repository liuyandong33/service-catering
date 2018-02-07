package build.dream.catering.models.eleme;

import build.dream.common.models.BasicModel;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

public class SetDeliveryTimeModel extends BasicModel {
    @NotNull
    private BigInteger tenantId;

    @NotNull
    private BigInteger branchId;

    @NotNull
    private Integer deliveryBasicMins;

    @NotNull
    private Integer deliveryAdjustMins;

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

    public Integer getDeliveryBasicMins() {
        return deliveryBasicMins;
    }

    public void setDeliveryBasicMins(Integer deliveryBasicMins) {
        this.deliveryBasicMins = deliveryBasicMins;
    }

    public Integer getDeliveryAdjustMins() {
        return deliveryAdjustMins;
    }

    public void setDeliveryAdjustMins(Integer deliveryAdjustMins) {
        this.deliveryAdjustMins = deliveryAdjustMins;
    }
}
