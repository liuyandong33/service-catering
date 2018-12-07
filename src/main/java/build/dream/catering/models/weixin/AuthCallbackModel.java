package build.dream.catering.models.weixin;

import build.dream.common.models.BasicModel;

import java.math.BigInteger;

public class AuthCallbackModel extends BasicModel {
    private BigInteger tenantId;
    private String componentAppId;
    private String authCode;

    public BigInteger getTenantId() {
        return tenantId;
    }

    public void setTenantId(BigInteger tenantId) {
        this.tenantId = tenantId;
    }

    public String getComponentAppId() {
        return componentAppId;
    }

    public void setComponentAppId(String componentAppId) {
        this.componentAppId = componentAppId;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }
}
