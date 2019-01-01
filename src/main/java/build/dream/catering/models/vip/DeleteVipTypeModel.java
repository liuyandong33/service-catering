package build.dream.catering.models.vip;

import build.dream.common.models.CateringBasicModel;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

public class DeleteVipTypeModel extends CateringBasicModel {
    @NotNull
    private BigInteger vipTypeId;

    public BigInteger getVipTypeId() {
        return vipTypeId;
    }

    public void setVipTypeId(BigInteger vipTypeId) {
        this.vipTypeId = vipTypeId;
    }
}
