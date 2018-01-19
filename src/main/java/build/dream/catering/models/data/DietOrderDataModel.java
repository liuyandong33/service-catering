package build.dream.catering.models.data;

import build.dream.common.erp.catering.domains.DietOrder;
import build.dream.common.erp.catering.domains.DietOrderDetail;

import java.math.BigInteger;
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
        BigInteger dietOrderId = dietOrder.getId();
        for (DietOrderDetail dietOrderDetail : dietOrderDetails) {
            dietOrderDetail.setDietOrderId(dietOrderId);
        }
    }
}
