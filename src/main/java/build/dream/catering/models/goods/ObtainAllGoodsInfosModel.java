package build.dream.catering.models.goods;

import build.dream.common.models.CateringBasicModel;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

public class ObtainAllGoodsInfosModel extends CateringBasicModel {
    @NotNull
    private BigInteger categoryId;

    public BigInteger getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(BigInteger categoryId) {
        this.categoryId = categoryId;
    }
}
