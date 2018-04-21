package build.dream.catering.models.weixin;

import build.dream.common.models.BasicModel;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

public class DeleteWeiXinMemberCardModel extends BasicModel {
    @NotNull
    private BigInteger tenantId;

    @NotNull
    private BigInteger userId;

    @NotNull
    private BigInteger weiXinCardId;

    public BigInteger getTenantId() {
        return tenantId;
    }

    public void setTenantId(BigInteger tenantId) {
        this.tenantId = tenantId;
    }

    public BigInteger getUserId() {
        return userId;
    }

    public void setUserId(BigInteger userId) {
        this.userId = userId;
    }

    public BigInteger getWeiXinCardId() {
        return weiXinCardId;
    }

    public void setWeiXinCardId(BigInteger weiXinCardId) {
        this.weiXinCardId = weiXinCardId;
    }
}
