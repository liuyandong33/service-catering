package build.dream.catering.models.anubis;

import build.dream.common.models.CateringBasicModel;

import javax.validation.constraints.NotNull;

public class OrderCarrierModel extends CateringBasicModel {
    @NotNull
    private Long orderId;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
}
