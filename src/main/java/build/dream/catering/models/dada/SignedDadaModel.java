package build.dream.catering.models.dada;

import build.dream.common.models.BasicModel;

import javax.validation.constraints.NotNull;

public class SignedDadaModel extends BasicModel {
    @NotNull
    private Long tenantId;

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }
}
