package build.dream.catering.models.goods;

import build.dream.common.models.CateringBasicModel;

import javax.validation.constraints.NotNull;

public class ObtainGoodsInfoModel extends CateringBasicModel {
    @NotNull
    private Long goodsId;

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }
}
