package build.dream.catering.models.branch;

import build.dream.common.models.BasicModel;

import javax.validation.constraints.NotNull;

public class DisableGoodsModel extends BasicModel {
    @NotNull
    private Long tenantId;

    @NotNull
    private Long branchId;

    @NotNull
    private String disableSql;

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getBranchId() {
        return branchId;
    }

    public void setBranchId(Long branchId) {
        this.branchId = branchId;
    }

    public String getDisableSql() {
        return disableSql;
    }

    public void setDisableSql(String disableSql) {
        this.disableSql = disableSql;
    }
}
