package build.dream.catering.models.dada;

import build.dream.common.models.CateringBasicModel;

import javax.validation.constraints.NotEmpty;
import java.util.List;

public class SyncShopModel extends CateringBasicModel {
    @NotEmpty
    private List<Long> branchIds;

    public List<Long> getBranchIds() {
        return branchIds;
    }

    public void setBranchIds(List<Long> branchIds) {
        this.branchIds = branchIds;
    }
}
