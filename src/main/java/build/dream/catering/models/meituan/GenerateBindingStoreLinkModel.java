package build.dream.catering.models.meituan;

import build.dream.common.models.CateringBasicModel;

import javax.validation.constraints.NotNull;

public class GenerateBindingStoreLinkModel extends CateringBasicModel {
    @NotNull
    private String businessId;

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }
}
