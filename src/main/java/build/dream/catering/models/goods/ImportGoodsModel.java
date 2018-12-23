package build.dream.catering.models.goods;

import build.dream.common.models.CateringBasicModel;

import javax.validation.constraints.NotNull;

public class ImportGoodsModel extends CateringBasicModel {
    @NotNull
    private String zipGoodsInfos;

    public String getZipGoodsInfos() {
        return zipGoodsInfos;
    }

    public void setZipGoodsInfos(String zipGoodsInfos) {
        this.zipGoodsInfos = zipGoodsInfos;
    }
}
