package build.dream.catering.models.user;

import build.dream.common.models.CateringBasicModel;

import javax.validation.constraints.NotNull;

public class ListUsersModel extends CateringBasicModel {
    @NotNull
    private Integer page;

    @NotNull
    private Integer rows;

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
