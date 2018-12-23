package build.dream.catering.models.weixin;

import build.dream.common.models.CateringBasicModel;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

public class DeleteWeiXinMemberCardModel extends CateringBasicModel {
    @NotNull
    private BigInteger weiXinCardId;

    public BigInteger getWeiXinCardId() {
        return weiXinCardId;
    }

    public void setWeiXinCardId(BigInteger weiXinCardId) {
        this.weiXinCardId = weiXinCardId;
    }
}
