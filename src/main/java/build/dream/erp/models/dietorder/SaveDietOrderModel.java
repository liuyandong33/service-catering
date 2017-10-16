package build.dream.erp.models.dietorder;

import build.dream.common.models.BasicModel;
import build.dream.common.utils.ApplicationHandler;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.Validate;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.List;

public class SaveDietOrderModel extends BasicModel {
    @NotNull
    private BigInteger tenantId;

    @NotNull
    private BigInteger branchId;

    @NotNull
    private BigInteger userId;

    private List<DietOrderModel> dietOrderModels;

    public BigInteger getTenantId() {
        return tenantId;
    }

    public void setTenantId(BigInteger tenantId) {
        this.tenantId = tenantId;
    }

    public BigInteger getBranchId() {
        return branchId;
    }

    public void setBranchId(BigInteger branchId) {
        this.branchId = branchId;
    }

    public BigInteger getUserId() {
        return userId;
    }

    public void setUserId(BigInteger userId) {
        this.userId = userId;
    }

    public List<DietOrderModel> getDietOrderModels() {
        return dietOrderModels;
    }

    public void setDietOrderModels(List<DietOrderModel> dietOrderModels) {
        this.dietOrderModels = dietOrderModels;
    }

    @Override
    public void validateAndThrow() throws NoSuchFieldException {
        Validate.isTrue(CollectionUtils.isNotEmpty(dietOrderModels), ApplicationHandler.obtainParameterErrorMessage("orderInfo"));
        for (DietOrderModel dietOrderModel : dietOrderModels) {
            Validate.isTrue(dietOrderModel.validate(), ApplicationHandler.obtainParameterErrorMessage("orderInfo"));
        }
        super.validateAndThrow();
    }

    public static class DietOrderModel extends BasicModel {
        @NotNull
        private BigInteger goodsId;

        @NotNull
        private BigInteger goodsSpecificationId;

        @NotNull
        private List<BigInteger> goodsFlavorIds;

        @NotNull
        private Integer amount;

        public BigInteger getGoodsId() {
            return goodsId;
        }

        public void setGoodsId(BigInteger goodsId) {
            this.goodsId = goodsId;
        }

        public BigInteger getGoodsSpecificationId() {
            return goodsSpecificationId;
        }

        public void setGoodsSpecificationId(BigInteger goodsSpecificationId) {
            this.goodsSpecificationId = goodsSpecificationId;
        }

        public List<BigInteger> getGoodsFlavorIds() {
            return goodsFlavorIds;
        }

        public void setGoodsFlavorIds(List<BigInteger> goodsFlavorIds) {
            this.goodsFlavorIds = goodsFlavorIds;
        }

        public Integer getAmount() {
            return amount;
        }

        public void setAmount(Integer amount) {
            this.amount = amount;
        }
    }
}
