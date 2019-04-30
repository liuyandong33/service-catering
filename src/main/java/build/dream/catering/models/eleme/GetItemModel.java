package build.dream.catering.models.eleme;

import build.dream.common.models.CateringBasicModel;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

public class GetItemModel extends CateringBasicModel {
    @NotNull
    private BigInteger itemId;

    public BigInteger getItemId() {
        return itemId;
    }

    public void setItemId(BigInteger itemId) {
        this.itemId = itemId;
    }
}
