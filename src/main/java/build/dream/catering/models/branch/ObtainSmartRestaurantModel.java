package build.dream.catering.models.branch;

import build.dream.common.models.CateringBasicModel;

import javax.validation.constraints.NotNull;

public class ObtainSmartRestaurantModel extends CateringBasicModel {
    @NotNull
    private Long branchId;

    public Long getBranchId() {
        return branchId;
    }

    public void setBranchId(Long branchId) {
        this.branchId = branchId;
    }
}
