package build.dream.catering.models.init;

import build.dream.common.models.BasicModel;

import javax.validation.constraints.NotNull;

public class InitTenantConfigModel extends BasicModel {
    @NotNull
    private Long tenantId;

    @NotNull
    private String tenantCode;

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
}
