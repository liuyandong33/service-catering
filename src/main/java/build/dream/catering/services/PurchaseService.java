package build.dream.catering.services;

import build.dream.catering.models.purchase.SavePurchaseOrderModel;
import build.dream.common.api.ApiRest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PurchaseService {
    @Transactional(rollbackFor = Exception.class)
    public ApiRest savePurchaseOrder(SavePurchaseOrderModel savePurchaseOrderModel) {
        return new ApiRest();
    }
}
