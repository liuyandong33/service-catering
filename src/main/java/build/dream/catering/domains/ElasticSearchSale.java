package build.dream.catering.domains;

import build.dream.catering.constants.Constants;
import build.dream.common.domains.catering.Sale;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Date;

@Document(indexName = "branch", type = "branch")
public class ElasticSearchSale implements Serializable, Cloneable {
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
     * 商户ID
     */
    @Field(type = FieldType.Long)
    private Long tenantId;
    /**
     * 商户编号
     */
    @Field(type = FieldType.keyword)
    private String tenantCode;
    /**
     * 门店ID
     */
    @Field(type = FieldType.Long)
    private Long branchId;

    /**
     * 销售编号
     */
    @Field(type = FieldType.keyword)
    private String saleCode;
    /**
     * 销售时间
     */
    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = Constants.DEFAULT_DATE_PATTERN)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DEFAULT_DATE_PATTERN)
    private Date saleTime;
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
     * 实付金额
     */
    @Field(type = FieldType.Double)
    private Double paidAmount;

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

    public String getSaleCode() {
        return saleCode;
    }

    public void setSaleCode(String saleCode) {
        this.saleCode = saleCode;
    }

    public Date getSaleTime() {
        return saleTime;
    }

    public void setSaleTime(Date saleTime) {
        this.saleTime = saleTime;
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

    public Double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(Double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public static ElasticSearchSale build(Sale sale) {
        ElasticSearchSale elasticSearchSale = new ElasticSearchSale();
        elasticSearchSale.setId(sale.getId());
        elasticSearchSale.setCreatedTime(sale.getCreatedTime());
        elasticSearchSale.setCreatedUserId(sale.getCreatedUserId());
        elasticSearchSale.setUpdatedTime(sale.getUpdatedTime());
        elasticSearchSale.setUpdatedUserId(sale.getUpdatedUserId());
        elasticSearchSale.setUpdatedRemark(sale.getUpdatedRemark());
        elasticSearchSale.setDeletedTime(sale.getDeletedTime());
        elasticSearchSale.setDeleted(sale.isDeleted());
        elasticSearchSale.setTenantId(sale.getTenantId());
        elasticSearchSale.setTenantCode(sale.getTenantCode());
        elasticSearchSale.setBranchId(sale.getBranchId());
        elasticSearchSale.setSaleCode(sale.getSaleCode());
        elasticSearchSale.setSaleTime(sale.getSaleTime());
        elasticSearchSale.setTotalAmount(sale.getTotalAmount());
        elasticSearchSale.setDiscountAmount(sale.getDiscountAmount());
        elasticSearchSale.setPayableAmount(sale.getPayableAmount());
        elasticSearchSale.setPaidAmount(sale.getPaidAmount());
        return elasticSearchSale;
    }
}
