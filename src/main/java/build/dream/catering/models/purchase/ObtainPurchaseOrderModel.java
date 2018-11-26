package build.dream.catering.models.purchase;

import build.dream.common.models.CateringBasicModel;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

public class ObtainPurchaseOrderModel extends CateringBasicModel {
    @NotNull
    private BigInteger purchaseOrderId;

    public BigInteger getPurchaseOrderId() {
        return purchaseOrderId;
    }

    public void setPurchaseOrderId(BigInteger purchaseOrderId) {
        this.purchaseOrderId = purchaseOrderId;
    }
}
