package build.dream.catering.models.branch;

import build.dream.common.models.BasicModel;

import javax.validation.constraints.NotNull;

public class ObtainHeadquartersInfoModel extends BasicModel {
    @NotNull
    private Long tenantId;

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }
}
