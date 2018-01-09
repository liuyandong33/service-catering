package build.dream.erp.models.data;

import build.dream.common.erp.catering.domains.DietOrder;
import build.dream.common.erp.catering.domains.DietOrderDetail;

import java.util.List;

public class DietOrderDataModel {
    private DietOrder dietOrder;
    private List<DietOrderDetail> dietOrderDetails;

    public DietOrder getDietOrder() {
        return dietOrder;
    }

    public void setDietOrder(DietOrder dietOrder) {
        this.dietOrder = dietOrder;
    }

    public List<DietOrderDetail> getDietOrderDetails() {
        return dietOrderDetails;
    }

    public void setDietOrderDetails(List<DietOrderDetail> dietOrderDetails) {
        this.dietOrderDetails = dietOrderDetails;
    }

    public void handleData() {
        dietOrder.setLocalId(dietOrder.getId());
        dietOrder.setId(null);
        dietOrder.setLocalCreateTime(dietOrder.getCreateTime());
        dietOrder.setLocalCreateTime(null);
        dietOrder.setLocalLastUpdateTime(dietOrder.getLastUpdateTime());
        dietOrder.setLocalLastUpdateTime(null);

        for (DietOrderDetail dietOrderDetail : dietOrderDetails) {
            dietOrderDetail.setLocalId(dietOrder.getId());
            dietOrderDetail.setId(null);
            dietOrderDetail.setLocalCreateTime(dietOrder.getCreateTime());
            dietOrderDetail.setLocalCreateTime(null);
            dietOrderDetail.setLocalLastUpdateTime(dietOrder.getLastUpdateTime());
            dietOrderDetail.setLocalLastUpdateTime(null);
        }
    }
}
