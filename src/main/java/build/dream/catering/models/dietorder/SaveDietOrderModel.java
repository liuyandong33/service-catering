package build.dream.catering.models.dietorder;

import build.dream.catering.constants.Constants;
import build.dream.common.annotations.InstantiateObjectIgnore;
import build.dream.common.constants.DietOrderConstants;
import build.dream.common.constraints.VerifyJsonSchema;
import build.dream.common.models.CateringBasicModel;
import build.dream.common.utils.ApplicationHandler;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public class SaveDietOrderModel extends CateringBasicModel {
    private static final String[] INVOICE_TYPES = {DietOrderConstants.INVOICE_TYPE_PERSONAL, DietOrderConstants.INVOICE_TYPE_COMPANY};

    @InstantiateObjectIgnore
    private BigInteger tenantId;
    @InstantiateObjectIgnore
    private String tenantCode;
    private BigInteger branchId;

    @NotNull
    private Integer orderType;

    @NotNull
    private Boolean invoiced;

    private String invoiceType;

    @Length(max = 30)
    private String invoice;
    private BigInteger vipId;

    @InstantiateObjectIgnore
    private BigInteger userId;

    @NotEmpty
    @VerifyJsonSchema(value = Constants.GOODS_INFOS_SCHEMA_FILE_PATH)
    private List<GoodsInfo> goodsInfos;

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

    public BigInteger getVipId() {
        return vipId;
    }

    public void setVipId(BigInteger vipId) {
        this.vipId = vipId;
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

    @Override
    public void validateAndThrow() {
        super.validateAndThrow();
        if (invoiced) {
            ApplicationHandler.inArray(INVOICE_TYPES, invoiceType, "invoiceType");
            ApplicationHandler.notBlank(invoice, "invoice");
        }
    }

    public static class GoodsInfo {
        private BigInteger goodsId;
        private BigInteger goodsSpecificationId;
        private Integer goodsType;
        private BigDecimal quantity;

        private List<AttributeInfo> attributeInfos;

        private List<PackageInfo> packageInfos;

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

        public Integer getGoodsType() {
            return goodsType;
        }

        public void setGoodsType(Integer goodsType) {
            this.goodsType = goodsType;
        }

        public BigDecimal getQuantity() {
            return quantity;
        }

        public void setQuantity(BigDecimal quantity) {
            this.quantity = quantity;
        }

        public List<AttributeInfo> getAttributeInfos() {
            return attributeInfos;
        }

        public void setAttributeInfos(List<AttributeInfo> attributeInfos) {
            this.attributeInfos = attributeInfos;
        }

        public List<PackageInfo> getPackageInfos() {
            return packageInfos;
        }

        public void setPackageInfos(List<PackageInfo> packageInfos) {
            this.packageInfos = packageInfos;
        }

        public boolean isPackage() {
            return goodsType == Constants.GOODS_TYPE_PACKAGE;
        }

        public boolean isOrdinaryGoods() {
            return goodsType == Constants.GOODS_TYPE_ORDINARY_GOODS;
        }
    }

    public static class AttributeInfo {
        private BigInteger attributeGroupId;
        private BigInteger attributeId;

        public BigInteger getAttributeGroupId() {
            return attributeGroupId;
        }

        public void setAttributeGroupId(BigInteger attributeGroupId) {
            this.attributeGroupId = attributeGroupId;
        }

        public BigInteger getAttributeId() {
            return attributeId;
        }

        public void setAttributeId(BigInteger attributeId) {
            this.attributeId = attributeId;
        }
    }

    public static class PackageInfo {
        private BigInteger groupId;
        private List<Detail> details;

        public BigInteger getGroupId() {
            return groupId;
        }

        public void setGroupId(BigInteger groupId) {
            this.groupId = groupId;
        }

        public List<Detail> getDetails() {
            return details;
        }

        public void setDetails(List<Detail> details) {
            this.details = details;
        }
    }

    public static class Detail {
        private BigInteger goodsId;
        private BigInteger goodsSpecificationId;
        private BigDecimal quantity;

        public BigInteger getGoodsId() {
            return goodsId;
        }

        public BigInteger getGoodsSpecificationId() {
            return goodsSpecificationId;
        }

        public void setGoodsId(BigInteger goodsId) {
            this.goodsId = goodsId;
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
    }
}
