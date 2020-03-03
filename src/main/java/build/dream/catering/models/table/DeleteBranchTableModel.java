package build.dream.catering.models.table;

import build.dream.common.models.CateringBasicModel;

import javax.validation.constraints.NotNull;

public class DeleteBranchTableModel extends CateringBasicModel {
    @NotNull
    private Long tableId;

    public Long getTableId() {
        return tableId;
    }

    public void setTableId(Long tableId) {
        this.tableId = tableId;
    }
}
