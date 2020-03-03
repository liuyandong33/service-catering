package build.dream.catering.models.purchase;

import build.dream.common.models.BasicModel;
import build.dream.common.models.CateringBasicModel;
import build.dream.common.utils.ApplicationHandler;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

public class SavePurchaseOrderModel extends CateringBasicModel {
    private String remark;

    @NotEmpty
    private List<Detail> details;

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
        private Long goodsId;

        @NotNull
        private Long goodsSpecificationId;

        @NotNull
        private Long unitId;

        @NotNull
        private Double purchasePrice;

        @NotNull
        private Double quantity;

        public Long getGoodsId() {
            return goodsId;
        }

        public void setGoodsId(Long goodsId) {
            this.goodsId = goodsId;
        }

        public Long getGoodsSpecificationId() {
            return goodsSpecificationId;
        }

        public void setGoodsSpecificationId(Long goodsSpecificationId) {
            this.goodsSpecificationId = goodsSpecificationId;
        }

        public Long getUnitId() {
            return unitId;
        }

        public void setUnitId(Long unitId) {
            this.unitId = unitId;
        }

        public Double getPurchasePrice() {
            return purchasePrice;
        }

        public void setPurchasePrice(Double purchasePrice) {
            this.purchasePrice = purchasePrice;
        }

        public Double getQuantity() {
            return quantity;
        }

        public void setQuantity(Double quantity) {
            this.quantity = quantity;
        }
    }
}
