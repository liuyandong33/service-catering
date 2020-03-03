package build.dream.catering.models.jddj;

import build.dream.common.models.CateringBasicModel;

import javax.validation.constraints.NotNull;

public class CheckSelfPickCodeModel extends CateringBasicModel {
    @NotNull
    private String selfPickCode;

    @NotNull
    private Long orderId;

    public String getSelfPickCode() {
        return selfPickCode;
    }

    public void setSelfPickCode(String selfPickCode) {
        this.selfPickCode = selfPickCode;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
}
