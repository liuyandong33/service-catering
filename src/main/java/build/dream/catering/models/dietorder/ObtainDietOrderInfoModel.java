package build.dream.catering.models.dietorder;

import build.dream.common.models.CateringBasicModel;

import javax.validation.constraints.NotNull;

public class ObtainDietOrderInfoModel extends CateringBasicModel {
    @NotNull
    private Long dietOrderId;

    public Long getDietOrderId() {
        return dietOrderId;
    }

    public void setDietOrderId(Long dietOrderId) {
        this.dietOrderId = dietOrderId;
    }
}
