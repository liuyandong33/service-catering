package build.dream.erp.models.dietorder;

import build.dream.common.models.BasicModel;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

public class DoPayModel extends BasicModel {
    @NotNull
    private BigInteger tenantId;

    @NotNull
    private BigInteger branchId;

    @NotNull
    private BigInteger dietOrderId;

    @NotNull
    private Integer paidType;

    @NotNull
    private Integer paidScene;
    private String spbillCreateIp;
    private String openId;
    private String subOpenId;
    private String productCode;

    @NotNull
    private String userId;

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

    public BigInteger getDietOrderId() {
        return dietOrderId;
    }

    public void setDietOrderId(BigInteger dietOrderId) {
        this.dietOrderId = dietOrderId;
    }

    public Integer getPaidType() {
        return paidType;
    }

    public void setPaidType(Integer paidType) {
        this.paidType = paidType;
    }

    public Integer getPaidScene() {
        return paidScene;
    }

    public void setPaidScene(Integer paidScene) {
        this.paidScene = paidScene;
    }

    public String getSpbillCreateIp() {
        return spbillCreateIp;
    }

    public void setSpbillCreateIp(String spbillCreateIp) {
        this.spbillCreateIp = spbillCreateIp;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getSubOpenId() {
        return subOpenId;
    }

    public void setSubOpenId(String subOpenId) {
        this.subOpenId = subOpenId;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
