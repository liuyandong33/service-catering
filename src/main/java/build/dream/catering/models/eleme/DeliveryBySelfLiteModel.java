package build.dream.catering.models.eleme;

import build.dream.common.models.CateringBasicModel;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

public class DeliveryBySelfLiteModel extends CateringBasicModel {
    @NotNull
    private BigInteger elemeOrderId;

    public BigInteger getElemeOrderId() {
        return elemeOrderId;
    }

    public void setElemeOrderId(BigInteger elemeOrderId) {
        this.elemeOrderId = elemeOrderId;
    }
}
