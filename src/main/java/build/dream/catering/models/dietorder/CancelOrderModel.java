package build.dream.catering.models.dietorder;

import build.dream.common.models.BasicModel;
import build.dream.common.models.CateringBasicModel;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

public class CancelOrderModel extends CateringBasicModel {
    @NotNull
    private BigInteger orderId;

    public BigInteger getOrderId() {
        return orderId;
    }

    public void setOrderId(BigInteger orderId) {
        this.orderId = orderId;
    }
}
