package build.dream.catering.models.jddj;

import build.dream.common.models.CateringBasicModel;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

public class CheckSelfPickCodeModel extends CateringBasicModel {
    @NotNull
    private String selfPickCode;

    @NotNull
    private BigInteger orderId;

    public String getSelfPickCode() {
        return selfPickCode;
    }

    public void setSelfPickCode(String selfPickCode) {
        this.selfPickCode = selfPickCode;
    }

    public BigInteger getOrderId() {
        return orderId;
    }

    public void setOrderId(BigInteger orderId) {
        this.orderId = orderId;
    }
}
