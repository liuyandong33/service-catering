package build.dream.catering.models.eleme;

import build.dream.common.models.CateringBasicModel;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

public class DisagreeRefundLiteModel extends CateringBasicModel {
    @NotNull
    private BigInteger elemeOrderId;

    private String reason;

    public BigInteger getElemeOrderId() {
        return elemeOrderId;
    }

    public void setElemeOrderId(BigInteger elemeOrderId) {
        this.elemeOrderId = elemeOrderId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
