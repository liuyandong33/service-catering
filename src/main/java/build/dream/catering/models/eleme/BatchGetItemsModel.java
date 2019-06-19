package build.dream.catering.models.eleme;

import build.dream.common.models.CateringBasicModel;

import javax.validation.constraints.NotEmpty;
import java.math.BigInteger;
import java.util.List;

public class BatchGetItemsModel extends CateringBasicModel {
    @NotEmpty
    private List<BigInteger> itemIds;

    public List<BigInteger> getItemIds() {
        return itemIds;
    }

    public void setItemIds(List<BigInteger> itemIds) {
        this.itemIds = itemIds;
    }
}
