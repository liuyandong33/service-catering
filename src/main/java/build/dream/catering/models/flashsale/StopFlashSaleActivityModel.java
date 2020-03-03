package build.dream.catering.models.flashsale;

import build.dream.common.models.CateringBasicModel;

import javax.validation.constraints.NotNull;

public class StopFlashSaleActivityModel extends CateringBasicModel {
    @NotNull
    private Long activityId;

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }
}
