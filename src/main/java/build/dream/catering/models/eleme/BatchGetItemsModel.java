package build.dream.catering.models.eleme;

import build.dream.common.models.CateringBasicModel;

import javax.validation.constraints.NotEmpty;
import java.util.List;

public class BatchGetItemsModel extends CateringBasicModel {
    @NotEmpty
    private List<Long> itemIds;

    public List<Long> getItemIds() {
        return itemIds;
    }

    public void setItemIds(List<Long> itemIds) {
        this.itemIds = itemIds;
    }
}
