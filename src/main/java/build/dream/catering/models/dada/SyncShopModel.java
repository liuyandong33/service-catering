package build.dream.catering.models.dada;

import build.dream.common.models.CateringBasicModel;

import javax.validation.constraints.NotEmpty;
import java.math.BigInteger;
import java.util.List;

public class SyncShopModel extends CateringBasicModel {
    @NotEmpty
    private List<BigInteger> branchIds;

    public List<BigInteger> getBranchIds() {
        return branchIds;
    }

    public void setBranchIds(List<BigInteger> branchIds) {
        this.branchIds = branchIds;
    }
}
