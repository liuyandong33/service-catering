package build.dream.catering.models.requiregoods;

import build.dream.common.models.CateringBasicModel;

import javax.validation.constraints.NotNull;

public class AuditRequireGoodsOrderModel extends CateringBasicModel {
    @NotNull
    private Long requireGoodsOrderId;

    public Long getRequireGoodsOrderId() {
        return requireGoodsOrderId;
    }

    public void setRequireGoodsOrderId(Long requireGoodsOrderId) {
        this.requireGoodsOrderId = requireGoodsOrderId;
    }
}
