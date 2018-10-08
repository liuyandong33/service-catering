package build.dream.catering.models.user;

import build.dream.common.models.BasicModel;

import javax.validation.constraints.NotNull;

public class ObtainUserInfoModel extends BasicModel {
    @NotNull
    private String loginName;

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }
}
