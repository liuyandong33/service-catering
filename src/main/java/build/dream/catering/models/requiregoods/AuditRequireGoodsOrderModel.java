package build.dream.catering.models.requiregoods;

import build.dream.common.models.CateringBasicModel;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

public class AuditRequireGoodsOrderModel extends CateringBasicModel {
    @NotNull
    private BigInteger requireGoodsOrderId;

    public BigInteger getRequireGoodsOrderId() {
        return requireGoodsOrderId;
    }

    public void setRequireGoodsOrderId(BigInteger requireGoodsOrderId) {
        this.requireGoodsOrderId = requireGoodsOrderId;
    }
}
