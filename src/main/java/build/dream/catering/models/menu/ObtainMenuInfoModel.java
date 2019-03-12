package build.dream.catering.models.menu;

import build.dream.common.models.CateringBasicModel;

import javax.validation.constraints.NotNull;

public class ObtainMenuInfoModel extends CateringBasicModel {
    @NotNull
    private Integer effectiveScope;

    public Integer getEffectiveScope() {
        return effectiveScope;
    }

    public void setEffectiveScope(Integer effectiveScope) {
        this.effectiveScope = effectiveScope;
    }
}
