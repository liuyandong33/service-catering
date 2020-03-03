package build.dream.catering.models.purchase;

import build.dream.common.models.CateringBasicModel;

import javax.validation.constraints.NotEmpty;
import java.util.List;

public class BatchDeletePurchaseOrdersModel extends CateringBasicModel {
    @NotEmpty
    private List<Long> purchaseOrderIds;

    public List<Long> getPurchaseOrderIds() {
        return purchaseOrderIds;
    }

    public void setPurchaseOrderIds(List<Long> purchaseOrderIds) {
        this.purchaseOrderIds = purchaseOrderIds;
    }
}
