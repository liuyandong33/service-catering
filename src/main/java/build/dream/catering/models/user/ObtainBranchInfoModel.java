package build.dream.catering.models.user;

import build.dream.common.models.BasicModel;

import javax.validation.constraints.NotNull;

public class ObtainBranchInfoModel extends BasicModel {
    @NotNull
    private Long tenantId;
    @NotNull
    private Long userId;

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
