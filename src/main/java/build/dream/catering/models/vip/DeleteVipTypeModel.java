package build.dream.catering.models.vip;

import build.dream.common.models.CateringBasicModel;

import javax.validation.constraints.NotNull;

public class DeleteVipTypeModel extends CateringBasicModel {
    @NotNull
    private Long vipTypeId;

    public Long getVipTypeId() {
        return vipTypeId;
    }

    public void setVipTypeId(Long vipTypeId) {
        this.vipTypeId = vipTypeId;
    }
}
