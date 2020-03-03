package build.dream.catering.models.vip;

import build.dream.common.models.CateringBasicModel;

import javax.validation.constraints.NotNull;

public class DeleteVipGroupModel extends CateringBasicModel {
    @NotNull
    private Long vipGroupId;

    public Long getVipGroupId() {
        return vipGroupId;
    }

    public void setVipGroupId(Long vipGroupId) {
        this.vipGroupId = vipGroupId;
    }
}
