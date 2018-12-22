package build.dream.catering.models.eleme;

import build.dream.common.models.CateringBasicModel;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

public class ObtainElemeCallbackMessageModel extends CateringBasicModel {
    @NotNull
    private BigInteger elemeCallbackMessageId;

    public BigInteger getElemeCallbackMessageId() {
        return elemeCallbackMessageId;
    }

    public void setElemeCallbackMessageId(BigInteger elemeCallbackMessageId) {
        this.elemeCallbackMessageId = elemeCallbackMessageId;
    }
}
