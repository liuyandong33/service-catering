package build.dream.catering.models.table;

import build.dream.common.models.CateringBasicModel;

import javax.validation.constraints.NotNull;

public class DeleteTableAreaModel extends CateringBasicModel {
    @NotNull
    private Long tableAreaId;

    public Long getTableAreaId() {
        return tableAreaId;
    }

    public void setTableAreaId(Long tableAreaId) {
        this.tableAreaId = tableAreaId;
    }
}
