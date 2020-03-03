package build.dream.catering.models.jddj;

import build.dream.common.models.CateringBasicModel;

import javax.validation.constraints.NotNull;

public class CancelAndRefundModel extends CateringBasicModel {
    @NotNull
    private Long orderId;

    @NotNull
    private String operRemark;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getOperRemark() {
        return operRemark;
    }

    public void setOperRemark(String operRemark) {
        this.operRemark = operRemark;
    }
}
