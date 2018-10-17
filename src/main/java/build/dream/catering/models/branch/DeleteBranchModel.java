package build.dream.catering.models.branch;

import build.dream.common.models.BasicModel;
import build.dream.common.models.CateringBasicModel;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

public class DeleteBranchModel extends CateringBasicModel {
    @NotNull
    private BigInteger branchId;

    public BigInteger getBranchId() {
        return branchId;
    }

    public void setBranchId(BigInteger branchId) {
        this.branchId = branchId;
    }
}
