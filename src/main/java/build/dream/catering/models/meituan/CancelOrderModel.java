package build.dream.catering.models.meituan;

import build.dream.common.models.CateringBasicModel;

import javax.validation.constraints.NotNull;

public class CancelOrderModel extends CateringBasicModel {
    @NotNull
    private Long dietOrderId;

    @NotNull
    private String reasonCode;

    @NotNull
    private String reason;

    public Long getDietOrderId() {
        return dietOrderId;
    }

    public void setDietOrderId(Long dietOrderId) {
        this.dietOrderId = dietOrderId;
    }

    public String getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(String reasonCode) {
        this.reasonCode = reasonCode;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
