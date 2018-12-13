package build.dream.catering.models.requiregoods;

import build.dream.catering.models.purchase.SavePurchaseOrderModel;
import build.dream.common.models.BasicModel;
import build.dream.common.models.CateringBasicModel;
import build.dream.common.utils.ApplicationHandler;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public class SaveRequireGoodsOrderModel extends CateringBasicModel {
    @NotNull
    private BigInteger distributionCenterId;

    private String remark;

    @NotEmpty
    private List<Detail> details;

    public BigInteger getDistributionCenterId() {
        return distributionCenterId;
    }

    public void setDistributionCenterId(BigInteger distributionCenterId) {
        this.distributionCenterId = distributionCenterId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<Detail> getDetails() {
        return details;
    }

    public void setDetails(List<Detail> details) {
        this.details = details;
    }

    @Override
    public boolean validate() {
        boolean isOk = super.validate();
        if (!isOk) {
            return false;
        }
        for (Detail detail : details) {
            isOk = isOk && detail.validate();
            if (!isOk) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void validateAndThrow() {
        super.validateAndThrow();
        for (Detail detail : details) {
            ApplicationHandler.isTrue(detail.validate(), "details");
        }
    }

    public static class Detail extends BasicModel {
        @NotNull
        private BigInteger goodsId;

        @NotNull
        private BigInteger goodsSpecificationId;

        @NotNull
        private BigInteger unitId;

        @NotNull
        private BigDecimal purchasePrice;

        @NotNull
        private BigDecimal quantity;

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

        public BigInteger getUnitId() {
            return unitId;
        }

        public void setUnitId(BigInteger unitId) {
            this.unitId = unitId;
        }

        public BigDecimal getPurchasePrice() {
            return purchasePrice;
        }

        public void setPurchasePrice(BigDecimal purchasePrice) {
            this.purchasePrice = purchasePrice;
        }

        public BigDecimal getQuantity() {
            return quantity;
        }

        public void setQuantity(BigDecimal quantity) {
            this.quantity = quantity;
        }
    }
}
