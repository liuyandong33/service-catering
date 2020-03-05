package build.dream.catering.domains;

import build.dream.catering.constants.Constants;
import build.dream.common.domains.catering.SalePayment;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Date;

@Document(indexName = "branch", type = "branch")
public class ElasticSearchSalePayment implements Serializable, Cloneable {
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
     * sale id
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
     * 支付方式id
     */
    @Field(type = FieldType.Long)
    private Long paymentId;
    /**
     * 支付方式编码
     */
    @Field(type = FieldType.keyword)
    private String paymentCode;
    /**
     * 支付方式名称
     */
    @Field(type = FieldType.text)
    private String paymentName;
    /**
     * 支付金额
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

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public String getPaymentCode() {
        return paymentCode;
    }

    public void setPaymentCode(String paymentCode) {
        this.paymentCode = paymentCode;
    }

    public String getPaymentName() {
        return paymentName;
    }

    public void setPaymentName(String paymentName) {
        this.paymentName = paymentName;
    }

    public Double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(Double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public ElasticSearchSalePayment build(SalePayment salePayment) {
        ElasticSearchSalePayment elasticSearchSalePayment = new ElasticSearchSalePayment();
        elasticSearchSalePayment.setId(salePayment.getId());
        elasticSearchSalePayment.setCreatedTime(salePayment.getCreatedTime());
        elasticSearchSalePayment.setCreatedUserId(salePayment.getCreatedUserId());
        elasticSearchSalePayment.setUpdatedTime(salePayment.getUpdatedTime());
        elasticSearchSalePayment.setUpdatedUserId(salePayment.getUpdatedUserId());
        elasticSearchSalePayment.setUpdatedRemark(salePayment.getUpdatedRemark());
        elasticSearchSalePayment.setDeletedTime(salePayment.getDeletedTime());
        elasticSearchSalePayment.setDeleted(salePayment.isDeleted());
        elasticSearchSalePayment.setSaleId(salePayment.getSaleId());
        elasticSearchSalePayment.setSaleTime(salePayment.getSaleTime());
        elasticSearchSalePayment.setTenantId(salePayment.getTenantId());
        elasticSearchSalePayment.setTenantCode(salePayment.getTenantCode());
        elasticSearchSalePayment.setBranchId(salePayment.getBranchId());
        elasticSearchSalePayment.setPaymentId(salePayment.getPaymentId());
        elasticSearchSalePayment.setPaymentCode(salePayment.getPaymentCode());
        elasticSearchSalePayment.setPaymentName(salePayment.getPaymentName());
        elasticSearchSalePayment.setPaidAmount(salePayment.getPaidAmount());
        return elasticSearchSalePayment;
    }
}
