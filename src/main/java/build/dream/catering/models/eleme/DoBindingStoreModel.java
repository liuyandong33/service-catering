package build.dream.catering.models.eleme;

import build.dream.common.models.BasicModel;
import build.dream.common.models.CateringBasicModel;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

public class DoBindingStoreModel extends CateringBasicModel {
    @NotNull
    private BigInteger shopId;

    public BigInteger getShopId() {
        return shopId;
    }

    public void setShopId(BigInteger shopId) {
        this.shopId = shopId;
    }
}
