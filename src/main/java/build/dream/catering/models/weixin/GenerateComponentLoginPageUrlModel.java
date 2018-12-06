package build.dream.catering.models.weixin;

import build.dream.common.models.CateringBasicModel;

import javax.validation.constraints.NotNull;

public class GenerateComponentLoginPageUrlModel extends CateringBasicModel {
    @NotNull
    private String authType;

    public String getAuthType() {
        return authType;
    }

    public void setAuthType(String authType) {
        this.authType = authType;
    }
}
