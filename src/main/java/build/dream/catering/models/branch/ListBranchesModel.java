package build.dream.catering.models.branch;

import build.dream.common.models.BasicModel;
import build.dream.common.models.CateringBasicModel;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class ListBranchesModel extends CateringBasicModel {
    private String searchString;

    @NotNull
    @Min(value = 1)
    private Integer page;

    @NotNull
    @Min(value = 1)
    @Max(value = 500)
    private Integer rows;

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
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
