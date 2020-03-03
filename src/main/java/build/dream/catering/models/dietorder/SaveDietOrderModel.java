package build.dream.catering.models.dietorder;

import build.dream.catering.constants.Constants;
import build.dream.common.constants.DietOrderConstants;
import build.dream.common.constraints.VerifyJsonSchema;
import build.dream.common.models.VipBasicModel;
import build.dream.common.utils.ApplicationHandler;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

public class SaveDietOrderModel extends VipBasicModel {
    private static final String[] INVOICE_TYPES = {DietOrderConstants.INVOICE_TYPE_PERSONAL, DietOrderConstants.INVOICE_TYPE_COMPANY};

    @NotNull
    private Integer orderType;

    @NotNull
    private Boolean invoiced;

    private String invoiceType;

    @Length(max = 30)
    private String invoice;

    @NotEmpty
    @VerifyJsonSchema(value = Constants.GOODS_INFOS_SCHEMA_FILE_PATH)
    private List<GoodsInfo> goodsInfos;

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
        private Long goodsId;
        private Long goodsSpecificationId;
        private Integer goodsType;
        private Double quantity;

        private List<AttributeInfo> attributeInfos;

        private List<PackageInfo> packageInfos;

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

        public Integer getGoodsType() {
            return goodsType;
        }

        public void setGoodsType(Integer goodsType) {
            this.goodsType = goodsType;
        }

        public Double getQuantity() {
            return quantity;
        }

        public void setQuantity(Double quantity) {
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
        private Long attributeGroupId;
        private Long attributeId;

        public Long getAttributeGroupId() {
            return attributeGroupId;
        }

        public void setAttributeGroupId(Long attributeGroupId) {
            this.attributeGroupId = attributeGroupId;
        }

        public Long getAttributeId() {
            return attributeId;
        }

        public void setAttributeId(Long attributeId) {
            this.attributeId = attributeId;
        }
    }

    public static class PackageInfo {
        private Long groupId;
        private List<Detail> details;

        public Long getGroupId() {
            return groupId;
        }

        public void setGroupId(Long groupId) {
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
        private Long goodsId;
        private Long goodsSpecificationId;
        private Double quantity;

        public Long getGoodsId() {
            return goodsId;
        }

        public Long getGoodsSpecificationId() {
            return goodsSpecificationId;
        }

        public void setGoodsId(Long goodsId) {
            this.goodsId = goodsId;
        }

        public void setGoodsSpecificationId(Long goodsSpecificationId) {
            this.goodsSpecificationId = goodsSpecificationId;
        }

        public Double getQuantity() {
            return quantity;
        }

        public void setQuantity(Double quantity) {
            this.quantity = quantity;
        }
    }
}
