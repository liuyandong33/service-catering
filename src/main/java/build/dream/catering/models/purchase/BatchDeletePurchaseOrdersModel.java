package build.dream.catering.models.purchase;

import build.dream.common.models.CateringBasicModel;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.List;

public class BatchDeletePurchaseOrdersModel extends CateringBasicModel {
    @NotEmpty
    private List<BigInteger> purchaseOrderIds;

    public List<BigInteger> getPurchaseOrderIds() {
        return purchaseOrderIds;
    }

    public void setPurchaseOrderIds(List<BigInteger> purchaseOrderIds) {
        this.purchaseOrderIds = purchaseOrderIds;
    }
}
