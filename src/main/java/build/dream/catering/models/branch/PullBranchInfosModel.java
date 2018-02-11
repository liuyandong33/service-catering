package build.dream.catering.models.branch;

import build.dream.common.models.BasicModel;

import javax.validation.constraints.NotNull;
import java.util.Date;

public class PullBranchInfosModel extends BasicModel {
    @NotNull
    private Date lastPullTime;
    @NotNull
    private Boolean reacquire;

    public Date getLastPullTime() {
        return lastPullTime;
    }

    public void setLastPullTime(Date lastPullTime) {
        this.lastPullTime = lastPullTime;
    }

    public Boolean getReacquire() {
        return reacquire;
    }

    public void setReacquire(Boolean reacquire) {
        this.reacquire = reacquire;
    }
}
