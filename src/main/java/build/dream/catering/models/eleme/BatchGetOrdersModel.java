package build.dream.catering.models.eleme;

import build.dream.common.models.CateringBasicModel;

import javax.validation.constraints.NotEmpty;
import java.util.List;

public class BatchGetOrdersModel extends CateringBasicModel {
    @NotEmpty
    private List<Long> orderIds;

    public List<Long> getOrderIds() {
        return orderIds;
    }

    public void setOrderIds(List<Long> orderIds) {
        this.orderIds = orderIds;
    }
}
