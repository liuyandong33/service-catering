package build.dream.catering.models.anubis;

import build.dream.common.models.CateringBasicModel;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

public class OrderCarrierModel extends CateringBasicModel {
    @NotNull
    private BigInteger orderId;

    public BigInteger getOrderId() {
        return orderId;
    }

    public void setOrderId(BigInteger orderId) {
        this.orderId = orderId;
    }
}
