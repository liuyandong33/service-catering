package build.dream.catering.models.eleme;

import build.dream.common.models.CateringBasicModel;

import javax.validation.constraints.NotEmpty;
import java.math.BigInteger;
import java.util.List;

public class BatchGetOrdersModel extends CateringBasicModel {
    @NotEmpty
    private List<BigInteger> elemeOrderIds;

    public List<BigInteger> getElemeOrderIds() {
        return elemeOrderIds;
    }

    public void setElemeOrderIds(List<BigInteger> elemeOrderIds) {
        this.elemeOrderIds = elemeOrderIds;
    }
}
