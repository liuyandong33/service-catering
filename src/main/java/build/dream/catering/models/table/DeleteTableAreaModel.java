package build.dream.catering.models.table;

import build.dream.common.models.CateringBasicModel;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

public class DeleteTableAreaModel extends CateringBasicModel {
    @NotNull
    private BigInteger tableAreaId;

    public BigInteger getTableAreaId() {
        return tableAreaId;
    }

    public void setTableAreaId(BigInteger tableAreaId) {
        this.tableAreaId = tableAreaId;
    }
}
