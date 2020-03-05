package build.dream.catering.domains;

import build.dream.catering.constants.Constants;
import build.dream.common.domains.catering.SaleDetail;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Date;

@Document(indexName = "branch", type = "branch")
public class ElasticSearchSaleDetail implements Serializable, Cloneable {
    @Id
    private Long id;

    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = Constants.DEFAULT_DATE_PATTERN)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DEFAULT_DATE_PATTERN)
    private Date createdTime;

    @Field(type = FieldType.Long)
    private Long createdUserId;

    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = Constants.DEFAULT_DATE_PATTERN)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DEFAULT_DATE_PATTERN)
    private Date updatedTime;

    @Field(type = FieldType.Long)
    private Long updatedUserId;

    @Field(type = FieldType.text)
    private String updatedRemark;

    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = Constants.DEFAULT_DATE_PATTERN)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DEFAULT_DATE_PATTERN)
    private Date deletedTime;

    @Field(type = FieldType.Boolean)
    private boolean deleted;

    /**
     * sale.id
     */
    @Field(type = FieldType.Long)
    private Long saleId;
    /**
     * 销售时间
     */
    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = Constants.DEFAULT_DATE_PATTERN)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DEFAULT_DATE_PATTERN)
    private Date saleTime;
    /**
     * 商户ID
     */
    @Field(type = FieldType.Long)
    private Long tenantId;
    /**
     * 商户编码
     */
    @Field(type = FieldType.keyword)
    private String tenantCode;
    /**
     * 门店ID
     */
    @Field(type = FieldType.Long)
    private Long branchId;
    /**
     * 产品ID，goods.id
     */
    @Field(type = FieldType.Long)
    private Long goodsId;
    /**
     * 产品名称，goods.name
     */
    @Field(type = FieldType.text)
    private String goodsName;
    /**
     * 商品规格ID，goodsSpecification.id
     */
    @Field(type = FieldType.Long)
    private Long goodsSpecificationId;
    /**
     * 商品规格名称，goodsSpecification.name
     */
    @Field(type = FieldType.text)
    private String goodsSpecificationName;
    /**
     * 商品分类ID
     */
    @Field(type = FieldType.Long)
    private Long categoryId;
    /**
     * 商品分类名称
     */
    @Field(type = FieldType.text)
    private String categoryName;
    /**
     * 单价
     */
    @Field(type = FieldType.Double)
    private Double price;
    /**
     * 总数量
     */
    @Field(type = FieldType.Double)
    private Double quantity;
    /**
     * 总金额
     */
    @Field(type = FieldType.Double)
    private Double totalAmount;
    /**
     * 优惠金额
     */
    @Field(type = FieldType.Double)
    private Double discountAmount;
    /**
     * 应付金额
     */
    @Field(type = FieldType.Double)
    private Double payableAmount;
    /**
     * 优惠分摊金额
     */
    @Field(type = FieldType.Double)
    private Double discountShare;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Long getCreatedUserId() {
        return createdUserId;
    }

    public void setCreatedUserId(Long createdUserId) {
        this.createdUserId = createdUserId;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    public Long getUpdatedUserId() {
        return updatedUserId;
    }

    public void setUpdatedUserId(Long updatedUserId) {
        this.updatedUserId = updatedUserId;
    }

    public String getUpdatedRemark() {
        return updatedRemark;
    }

    public void setUpdatedRemark(String updatedRemark) {
        this.updatedRemark = updatedRemark;
    }

    public Date getDeletedTime() {
        return deletedTime;
    }

    public void setDeletedTime(Date deletedTime) {
        this.deletedTime = deletedTime;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Long getSaleId() {
        return saleId;
    }

    public void setSaleId(Long saleId) {
        this.saleId = saleId;
    }

    public Date getSaleTime() {
        return saleTime;
    }

    public void setSaleTime(Date saleTime) {
        this.saleTime = saleTime;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getTenantCode() {
        return tenantCode;
    }

    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode;
    }

    public Long getBranchId() {
        return branchId;
    }

    public void setBranchId(Long branchId) {
        this.branchId = branchId;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public Long getGoodsSpecificationId() {
        return goodsSpecificationId;
    }

    public void setGoodsSpecificationId(Long goodsSpecificationId) {
        this.goodsSpecificationId = goodsSpecificationId;
    }

    public String getGoodsSpecificationName() {
        return goodsSpecificationName;
    }

    public void setGoodsSpecificationName(String goodsSpecificationName) {
        this.goodsSpecificationName = goodsSpecificationName;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(Double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public Double getPayableAmount() {
        return payableAmount;
    }

    public void setPayableAmount(Double payableAmount) {
        this.payableAmount = payableAmount;
    }

    public Double getDiscountShare() {
        return discountShare;
    }

    public void setDiscountShare(Double discountShare) {
        this.discountShare = discountShare;
    }

    public ElasticSearchSaleDetail build(SaleDetail saleDetail) {
        ElasticSearchSaleDetail elasticSearchSaleDetail = new ElasticSearchSaleDetail();
        elasticSearchSaleDetail.setId(saleDetail.getId());
        elasticSearchSaleDetail.setCreatedTime(saleDetail.getCreatedTime());
        elasticSearchSaleDetail.setCreatedUserId(saleDetail.getCreatedUserId());
        elasticSearchSaleDetail.setUpdatedTime(saleDetail.getUpdatedTime());
        elasticSearchSaleDetail.setUpdatedUserId(saleDetail.getUpdatedUserId());
        elasticSearchSaleDetail.setUpdatedRemark(saleDetail.getUpdatedRemark());
        elasticSearchSaleDetail.setDeletedTime(saleDetail.getDeletedTime());
        elasticSearchSaleDetail.setDeleted(saleDetail.isDeleted());
        elasticSearchSaleDetail.setSaleId(saleDetail.getSaleId());
        elasticSearchSaleDetail.setSaleTime(saleDetail.getSaleTime());
        elasticSearchSaleDetail.setTenantId(saleDetail.getTenantId());
        elasticSearchSaleDetail.setTenantCode(saleDetail.getTenantCode());
        elasticSearchSaleDetail.setBranchId(saleDetail.getBranchId());
        elasticSearchSaleDetail.setGoodsId(saleDetail.getGoodsId());
        elasticSearchSaleDetail.setGoodsName(saleDetail.getGoodsName());
        elasticSearchSaleDetail.setGoodsSpecificationId(saleDetail.getGoodsSpecificationId());
        elasticSearchSaleDetail.setGoodsSpecificationName(saleDetail.getGoodsSpecificationName());
        elasticSearchSaleDetail.setCategoryId(saleDetail.getCategoryId());
        elasticSearchSaleDetail.setCategoryName(saleDetail.getCategoryName());
        elasticSearchSaleDetail.setPrice(saleDetail.getPrice());
        elasticSearchSaleDetail.setQuantity(saleDetail.getQuantity());
        elasticSearchSaleDetail.setTotalAmount(saleDetail.getTotalAmount());
        elasticSearchSaleDetail.setDiscountAmount(saleDetail.getDiscountAmount());
        elasticSearchSaleDetail.setPayableAmount(saleDetail.getPayableAmount());
        elasticSearchSaleDetail.setDiscountShare(saleDetail.getDiscountShare());
        return elasticSearchSaleDetail;
    }
}
