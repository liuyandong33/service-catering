package build.dream.catering.models.eleme;

import build.dream.common.models.BasicModel;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.List;

public class GetPreparedTimesByOrderIdsModel extends BasicModel {
    @NotNull
    private BigInteger tenantId;

    @NotNull
    private BigInteger branchId;

    @NotEmpty
    private List<BigInteger> elemeOrderIds;

    public BigInteger getTenantId() {
        return tenantId;
    }

    public void setTenantId(BigInteger tenantId) {
        this.tenantId = tenantId;
    }

    public BigInteger getBranchId() {
        return branchId;
    }

    public void setBranchId(BigInteger branchId) {
        this.branchId = branchId;
    }

    public List<BigInteger> getElemeOrderIds() {
        return elemeOrderIds;
    }

    public void setElemeOrderIds(List<BigInteger> elemeOrderIds) {
        this.elemeOrderIds = elemeOrderIds;
    }
}
