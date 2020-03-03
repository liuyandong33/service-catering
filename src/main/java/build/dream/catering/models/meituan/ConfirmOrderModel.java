package build.dream.catering.models.meituan;

import build.dream.common.models.CateringBasicModel;

import javax.validation.constraints.NotNull;

public class ConfirmOrderModel extends CateringBasicModel {
    @NotNull
    private Long dietOrderId;

    public Long getDietOrderId() {
        return dietOrderId;
    }

    public void setDietOrderId(Long dietOrderId) {
        this.dietOrderId = dietOrderId;
    }
}
