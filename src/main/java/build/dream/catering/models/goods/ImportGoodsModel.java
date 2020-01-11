package build.dream.catering.models.goods;

import build.dream.common.models.CateringBasicModel;

import javax.validation.constraints.NotNull;

public class ImportGoodsModel extends CateringBasicModel {
    @NotNull
    private String zippedGoodsInfos;

    public String getZippedGoodsInfos() {
        return zippedGoodsInfos;
    }

    public void setZippedGoodsInfos(String zippedGoodsInfos) {
        this.zippedGoodsInfos = zippedGoodsInfos;
    }
}
