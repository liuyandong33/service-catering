package build.dream.catering.models.dietorder;

import build.dream.common.models.CateringBasicModel;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

public class ObtainDietOrderInfoModel extends CateringBasicModel {
    @NotNull
    private BigInteger dietOrderId;

    public BigInteger getDietOrderId() {
        return dietOrderId;
    }

    public void setDietOrderId(BigInteger dietOrderId) {
        this.dietOrderId = dietOrderId;
    }
}
