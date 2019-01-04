package build.dream.catering.models.weixin;

import build.dream.common.models.BasicModel;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

public class ObtainMessageContentModel extends BasicModel {
    @NotNull
    private BigInteger tenantId;

    @NotNull
    private BigInteger weiXinMenuId;

    public BigInteger getTenantId() {
        return tenantId;
    }

    public void setTenantId(BigInteger tenantId) {
        this.tenantId = tenantId;
    }

    public BigInteger getWeiXinMenuId() {
        return weiXinMenuId;
    }

    public void setWeiXinMenuId(BigInteger weiXinMenuId) {
        this.weiXinMenuId = weiXinMenuId;
    }
}
