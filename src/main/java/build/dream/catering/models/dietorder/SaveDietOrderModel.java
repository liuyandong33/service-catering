package build.dream.catering.models.dietorder;

import build.dream.common.constants.DietOrderConstants;
import build.dream.common.models.BasicModel;
import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.GsonUtils;
import build.dream.common.utils.ValidateUtils;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public class SaveDietOrderModel extends BasicModel {
    private static final String[] INVOICE_TYPES = {DietOrderConstants.INVOICE_TYPE_PERSONAL, DietOrderConstants.INVOICE_TYPE_COMPANY};

    @NotNull
    private BigInteger tenantId;

    @NotNull
    private String tenantCode;

    @NotNull
    private BigInteger branchId;

    @NotNull
    private Integer orderType;

    @NotNull
    private Boolean invoiced;

    private String invoiceType;

    @Length(max = 30)
    private String invoice;

    @NotNull
    private BigInteger userId;

    @NotEmpty
    private List<GoodsInfo> goodsInfos;

    public static String[] getInvoiceTypes() {
        return INVOICE_TYPES;
    }

    public BigInteger getTenantId() {
        return tenantId;
    }

    public void setTenantId(BigInteger tenantId) {
        this.tenantId = tenantId;
    }

    public String getTenantCode() {
        return tenantCode;
    }

    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode;
    }

    public BigInteger getBranchId() {
        return branchId;
    }

    public void setBranchId(BigInteger branchId) {
        this.branchId = branchId;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public Boolean getInvoiced() {
        return invoiced;
    }

    public void setInvoiced(Boolean invoiced) {
        this.invoiced = invoiced;
    }

    public String getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(String invoiceType) {
        this.invoiceType = invoiceType;
    }

    public String getInvoice() {
        return invoice;
    }

    public void setInvoice(String invoice) {
        this.invoice = invoice;
    }

    public BigInteger getUserId() {
        return userId;
    }

    public void setUserId(BigInteger userId) {
        this.userId = userId;
    }

    public List<GoodsInfo> getGoodsInfos() {
        return goodsInfos;
    }

    public void setGoodsInfos(List<GoodsInfo> goodsInfos) {
        this.goodsInfos = goodsInfos;
    }

    public static class GoodsInfo extends BasicModel {
        @NotNull
        private BigInteger goodsId;

        @NotNull
        private BigInteger goodsSpecificationId;

        @NotNull
        private BigDecimal quantity;

        private List<FlavorInfo> flavorInfos;

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

        public BigDecimal getQuantity() {
            return quantity;
        }

        public void setQuantity(BigDecimal quantity) {
            this.quantity = quantity;
        }

        public List<FlavorInfo> getFlavorInfos() {
            return flavorInfos;
        }

        public void setFlavorInfos(List<FlavorInfo> flavorInfos) {
            this.flavorInfos = flavorInfos;
        }

        @Override
        public boolean validate() {
            boolean isValidate = super.validate();
            if (!isValidate) {
                return false;
            }
            if (CollectionUtils.isNotEmpty(flavorInfos)) {
                for (FlavorInfo flavorInfo : flavorInfos) {
                    isValidate = isValidate && flavorInfo.validate();
                    if (!isValidate) {
                        return false;
                    }
                }
            }
            return true;
        }
    }

    public static class FlavorInfo extends BasicModel {
        @NotNull
        private BigInteger flavorGroupId;

        @NotNull
        private BigInteger flavorId;

        public BigInteger getFlavorGroupId() {
            return flavorGroupId;
        }

        public void setFlavorGroupId(BigInteger flavorGroupId) {
            this.flavorGroupId = flavorGroupId;
        }

        public BigInteger getFlavorId() {
            return flavorId;
        }

        public void setFlavorId(BigInteger flavorId) {
            this.flavorId = flavorId;
        }
    }

    @Override
    public void validateAndThrow() {
        super.validateAndThrow();
        for (GoodsInfo goodsInfo : goodsInfos) {
            ApplicationHandler.isTrue(goodsInfo.validate(), "goodsInfos");
        }
    }
}
