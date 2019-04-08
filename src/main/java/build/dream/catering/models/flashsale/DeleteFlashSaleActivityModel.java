package build.dream.catering.models.flashsale;

import build.dream.common.models.CateringBasicModel;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

public class DeleteFlashSaleActivityModel extends CateringBasicModel {
    @NotNull
    private BigInteger activityId;

    public BigInteger getActivityId() {
        return activityId;
    }

    public void setActivityId(BigInteger activityId) {
        this.activityId = activityId;
    }
}
