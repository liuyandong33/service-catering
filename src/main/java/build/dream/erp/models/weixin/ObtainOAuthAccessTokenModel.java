package build.dream.erp.models.weixin;

import build.dream.common.models.BasicModel;

import javax.validation.constraints.NotNull;

public class ObtainOAuthAccessTokenModel extends BasicModel {
    @NotNull
    private String appId;
    @NotNull
    private String code;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
