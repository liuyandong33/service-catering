package build.dream.catering.models.jddj;

import build.dream.common.models.CateringBasicModel;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

public class CancelAndRefundModel extends CateringBasicModel {
    @NotNull
    private BigInteger orderId;

    @NotNull
    private String operRemark;

    public BigInteger getOrderId() {
        return orderId;
    }

    public void setOrderId(BigInteger orderId) {
        this.orderId = orderId;
    }

    public String getOperRemark() {
        return operRemark;
    }

    public void setOperRemark(String operRemark) {
        this.operRemark = operRemark;
    }
}
