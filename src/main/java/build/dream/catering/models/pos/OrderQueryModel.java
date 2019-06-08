package build.dream.catering.models.pos;

import build.dream.common.models.BasicModel;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

public class OrderQueryModel extends BasicModel {
    @NotNull
    private BigInteger tenantId;

    @NotNull
    private String tenantCode;

    @NotNull
    private BigInteger branchId;

    @NotNull
    private BigInteger userId;

    @NotNull
    private String outTradeNo;

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

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }
}
