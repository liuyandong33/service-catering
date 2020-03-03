package build.dream.catering.models.purchase;

import build.dream.common.models.CateringBasicModel;

import javax.validation.constraints.NotNull;

public class AuditPurchaseOrderModel extends CateringBasicModel {
    @NotNull
    private Long purchaseOrderId;

    public Long getPurchaseOrderId() {
        return purchaseOrderId;
    }

    public void setPurchaseOrderId(Long purchaseOrderId) {
        this.purchaseOrderId = purchaseOrderId;
    }
}
