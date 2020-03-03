package build.dream.catering.models.branch;

import build.dream.common.models.BasicModel;

import javax.validation.constraints.NotNull;

public class RenewCallbackModel extends BasicModel {
    @NotNull
    private Long tenantId;

    @NotNull
    private Long branchId;

    private String renewSql;

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

    public String getRenewSql() {
        return renewSql;
    }

    public void setRenewSql(String renewSql) {
        this.renewSql = renewSql;
    }
}
