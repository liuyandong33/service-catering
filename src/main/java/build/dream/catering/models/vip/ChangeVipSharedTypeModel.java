package build.dream.catering.models.vip;

import build.dream.common.models.CateringBasicModel;

import javax.validation.constraints.NotNull;

public class ChangeVipSharedTypeModel extends CateringBasicModel {
    /**
     * 会员共享类型，1-全部共享，2-全部独立，3-分组共享
     */
    @NotNull
    private Integer vipSharedType;

    public Integer getVipSharedType() {
        return vipSharedType;
    }

    public void setVipSharedType(Integer vipSharedType) {
        this.vipSharedType = vipSharedType;
    }
}
