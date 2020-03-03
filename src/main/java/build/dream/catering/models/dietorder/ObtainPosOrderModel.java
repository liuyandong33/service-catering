package build.dream.catering.models.dietorder;

import build.dream.common.models.CateringBasicModel;

import javax.validation.constraints.NotNull;

public class ObtainPosOrderModel extends CateringBasicModel {
    @NotNull
    private Long vipId;

    public Long getVipId() {
        return vipId;
    }

    public void setVipId(Long vipId) {
        this.vipId = vipId;
    }
}
