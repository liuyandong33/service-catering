package build.dream.catering.models.pos;

import build.dream.common.models.CateringBasicModel;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

public class ObtainMqttInfoModel extends CateringBasicModel {
    @NotNull
    private BigInteger posId;

    public BigInteger getPosId() {
        return posId;
    }

    public void setPosId(BigInteger posId) {
        this.posId = posId;
    }
}
