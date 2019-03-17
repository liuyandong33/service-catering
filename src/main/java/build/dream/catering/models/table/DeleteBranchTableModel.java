package build.dream.catering.models.table;

import build.dream.common.models.CateringBasicModel;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

public class DeleteBranchTableModel extends CateringBasicModel {
    @NotNull
    private BigInteger tableId;

    public BigInteger getTableId() {
        return tableId;
    }

    public void setTableId(BigInteger tableId) {
        this.tableId = tableId;
    }
}
