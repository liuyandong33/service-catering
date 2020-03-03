package build.dream.catering.models.weixin;

import build.dream.common.models.CateringBasicModel;

import javax.validation.constraints.NotNull;

public class DeleteWeiXinMemberCardModel extends CateringBasicModel {
    @NotNull
    private Long weiXinCardId;

    public Long getWeiXinCardId() {
        return weiXinCardId;
    }

    public void setWeiXinCardId(Long weiXinCardId) {
        this.weiXinCardId = weiXinCardId;
    }
}
