package build.dream.catering.models.branch;

import build.dream.common.models.BasicModel;

import java.math.BigInteger;

public class ListBranchesModel extends BasicModel {
    private BigInteger tenantId;
    private String name;
    private Integer page;
    private Integer rows;

    public BigInteger getTenantId() {
        return tenantId;
    }

    public void setTenantId(BigInteger tenantId) {
        this.tenantId = tenantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }
}
