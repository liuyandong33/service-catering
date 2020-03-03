package build.dream.catering.models.weixin;

import build.dream.common.models.BasicModel;

import javax.validation.constraints.NotNull;

public class ObtainMessageContentModel extends BasicModel {
    @NotNull
    private Long tenantId;

    @NotNull
    private Long weiXinMenuId;

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getWeiXinMenuId() {
        return weiXinMenuId;
    }

    public void setWeiXinMenuId(Long weiXinMenuId) {
        this.weiXinMenuId = weiXinMenuId;
    }
}
