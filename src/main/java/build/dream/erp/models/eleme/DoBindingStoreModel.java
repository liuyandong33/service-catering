package build.dream.erp.models.eleme;

import build.dream.common.models.BasicModel;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

public class DoBindingStoreModel extends BasicModel {
    @NotNull
    private BigInteger tenantId;
    @NotNull
    private BigInteger branchId;
    @NotNull
    private BigInteger shopId;
    @NotNull
    private BigInteger userId;

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

    public BigInteger getShopId() {
        return shopId;
    }

    public void setShopId(BigInteger shopId) {
        this.shopId = shopId;
    }

    public BigInteger getUserId() {
        return userId;
    }

    public void setUserId(BigInteger userId) {
        this.userId = userId;
    }
}
