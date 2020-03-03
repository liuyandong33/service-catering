package build.dream.catering.models.pos;

import build.dream.common.models.BasicModel;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class OfflinePayModel extends BasicModel {
    @NotNull
    private Long tenantId;

    @NotNull
    private String tenantCode;

    @NotNull
    private Long branchId;

    @NotNull
    private Long userId;

    @NotNull
    @Min(value = 0)
    private Integer totalAmount;

    @NotNull
    private String authCode;

    @NotNull
    private String subject;

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getTenantCode() {
        return tenantCode;
    }

    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode;
    }

    public Long getBranchId() {
        return branchId;
    }

    public void setBranchId(Long branchId) {
        this.branchId = branchId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Integer totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
