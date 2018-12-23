package build.dream.catering.models.goods;

import build.dream.common.models.CateringBasicModel;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

public class DeleteGoodsSpecificationModel extends CateringBasicModel {
    @NotNull
    private BigInteger goodsSpecificationId;

    public BigInteger getGoodsSpecificationId() {
        return goodsSpecificationId;
    }

    public void setGoodsSpecificationId(BigInteger goodsSpecificationId) {
        this.goodsSpecificationId = goodsSpecificationId;
    }
}
