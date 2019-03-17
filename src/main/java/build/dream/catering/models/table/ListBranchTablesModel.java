package build.dream.catering.models.table;

import build.dream.common.models.CateringBasicModel;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;

public class ListBranchTablesModel extends CateringBasicModel {
    @NotNull
    private BigInteger tableAreaId;

    @NotNull
    @Min(value = 1)
    private Integer page;

    @NotNull
    @Min(value = 1)
    @Max(value = 1000)
    private Integer rows;

    public BigInteger getTableAreaId() {
        return tableAreaId;
    }

    public void setTableAreaId(BigInteger tableAreaId) {
        this.tableAreaId = tableAreaId;
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
