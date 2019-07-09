package build.dream.catering.models.jddj;

import build.dream.common.models.CateringBasicModel;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

public class AddTipsModel extends CateringBasicModel {
    @NotNull
    private BigInteger orderId;

    @NotNull
    private Integer tips;

    public BigInteger getOrderId() {
        return orderId;
    }

    public void setOrderId(BigInteger orderId) {
        this.orderId = orderId;
    }

    public Integer getTips() {
        return tips;
    }

    public void setTips(Integer tips) {
        this.tips = tips;
    }
}
