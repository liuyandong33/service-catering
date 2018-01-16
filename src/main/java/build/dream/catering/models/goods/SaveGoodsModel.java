package build.dream.catering.models.goods;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.List;

public class SaveGoodsModel {
    private BigInteger id;

    @NotNull
    private String name;

    @NotNull
    private BigInteger tenantId;

    @NotNull
    private String tenantCode;

    @NotNull
    private BigInteger branchId;

    @NotNull
    private BigInteger userId;

    private List<GoodsFlavorGroupModel> goodsFlavorGroupModels;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigInteger getTenantId() {
        return tenantId;
    }

    public void setTenantId(BigInteger tenantId) {
        this.tenantId = tenantId;
    }

    public String getTenantCode() {
        return tenantCode;
    }

    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode;
    }

    public BigInteger getBranchId() {
        return branchId;
    }

    public void setBranchId(BigInteger branchId) {
        this.branchId = branchId;
    }

    public BigInteger getUserId() {
        return userId;
    }

    public void setUserId(BigInteger userId) {
        this.userId = userId;
    }

    public List<GoodsFlavorGroupModel> getGoodsFlavorGroupModels() {
        return goodsFlavorGroupModels;
    }

    public void setGoodsFlavorGroupModels(List<GoodsFlavorGroupModel> goodsFlavorGroupModels) {
        this.goodsFlavorGroupModels = goodsFlavorGroupModels;
    }
}
