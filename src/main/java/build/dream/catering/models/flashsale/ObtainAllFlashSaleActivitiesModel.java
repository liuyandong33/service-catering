package build.dream.catering.models.flashsale;

import build.dream.common.models.CateringBasicModel;

import javax.validation.constraints.NotNull;

/**
 * Created by liuyandong on 2019-03-14.
 */
public class ObtainAllFlashSaleActivitiesModel extends CateringBasicModel {
    @NotNull
    private Long vipId;

    public Long getVipId() {
        return vipId;
    }

    public void setVipId(Long vipId) {
        this.vipId = vipId;
    }
}
