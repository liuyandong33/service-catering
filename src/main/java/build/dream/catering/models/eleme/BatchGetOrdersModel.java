package build.dream.catering.models.eleme;

import build.dream.common.models.CateringBasicModel;

import javax.validation.constraints.NotEmpty;
import java.math.BigInteger;
import java.util.List;

public class BatchGetOrdersModel extends CateringBasicModel {
    @NotEmpty
    private List<BigInteger> orderIds;

    public List<BigInteger> getOrderIds() {
        return orderIds;
    }

    public void setOrderIds(List<BigInteger> orderIds) {
        this.orderIds = orderIds;
    }
}
