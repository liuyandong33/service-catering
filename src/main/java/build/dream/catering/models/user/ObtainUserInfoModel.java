package build.dream.catering.models.user;

import build.dream.common.models.BasicModel;

public class ObtainUserInfoModel extends BasicModel {
    private String loginName;

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }
}
