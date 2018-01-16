package build.dream.catering.models.dietorder;

import build.dream.common.models.BasicModel;
import build.dream.common.utils.ApplicationHandler;
import org.apache.commons.collections.CollectionUtils;
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

    @NotNull
    private Integer orderType;

    private String remark;
    private String deliveryAddress;
    private String deliveryLongitude;
    private String deliveryLatitude;
    private String telephoneNumber;
    private String consignee;

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

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getDeliveryLongitude() {
        return deliveryLongitude;
    }

    public void setDeliveryLongitude(String deliveryLongitude) {
        this.deliveryLongitude = deliveryLongitude;
    }

    public String getDeliveryLatitude() {
        return deliveryLatitude;
    }

    public void setDeliveryLatitude(String deliveryLatitude) {
        this.deliveryLatitude = deliveryLatitude;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    public String getConsignee() {
        return consignee;
    }

    public void setConsignee(String consignee) {
        this.consignee = consignee;
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
