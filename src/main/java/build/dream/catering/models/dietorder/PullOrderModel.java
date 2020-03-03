package build.dream.catering.models.dietorder;

import build.dream.common.models.CateringBasicModel;

import javax.validation.constraints.NotNull;

public class PullOrderModel extends CateringBasicModel {
    @NotNull
    private Long orderId;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
}
