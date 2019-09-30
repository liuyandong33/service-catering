package build.dream.catering.domains;

import build.dream.catering.constants.Constants;
import build.dream.common.domains.catering.Goods;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Date;

@Document(indexName = "goods", type = "goods")
public class ElasticSearchGoods implements Serializable, Cloneable {
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
     * 商品名称
     */
    @Field(type = FieldType.text)
    private String name;
    /**
     * 商品名称，1-普通商品，2-套餐
     */
    @Field(type = FieldType.Integer)
    private Integer type;
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
     * 图片路径
     */
    @Field(type = FieldType.text)
    private String imageUrl;

    @Field(type = FieldType.Boolean)
    private boolean stocked;

    public ElasticSearchGoods() {

    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isStocked() {
        return stocked;
    }

    public void setStocked(boolean stocked) {
        this.stocked = stocked;
    }

    public static final class FieldName {
        public static final String ID = "id";
        public static final String CREATED_TIME = "createdTime";
        public static final String CREATED_USER_ID = "createdUserId";
        public static final String UPDATED_TIME = "updatedTime";
        public static final String UPDATED_USER_ID = "updatedUserId";
        public static final String UPDATED_REMARK = "updatedRemark";
        public static final String DELETED_TIME = "deletedTime";
        public static final String DELETED = "deleted";
        public static final String TENANT_ID = "tenantId";
        public static final String TENANT_CODE = "tenantCode";
        public static final String BRANCH_ID = "branchId";
        public static final String NAME = "name";
        public static final String TYPE = "type";
        public static final String CATEGORY_ID = "categoryId";
        public static final String CATEGORY_NAME = "categoryName";
        public static final String IMAGE_URL = "imageUrl";
        public static final String STOCKED = "stocked";
    }

    public static ElasticSearchGoods build(Goods goods) {
        ElasticSearchGoods elasticSearchGoods = new ElasticSearchGoods();
        elasticSearchGoods.setId(goods.getId().longValue());
        elasticSearchGoods.setCreatedTime(goods.getCreatedTime());
        elasticSearchGoods.setCreatedUserId(goods.getCreatedUserId().longValue());
        elasticSearchGoods.setUpdatedTime(goods.getUpdatedTime());
        elasticSearchGoods.setUpdatedUserId(goods.getUpdatedUserId().longValue());
        elasticSearchGoods.setUpdatedRemark(goods.getUpdatedRemark());
        elasticSearchGoods.setDeletedTime(goods.getDeletedTime());
        elasticSearchGoods.setDeleted(goods.isDeleted());
        elasticSearchGoods.setTenantId(goods.getTenantId().longValue());
        elasticSearchGoods.setTenantCode(goods.getTenantCode());
        elasticSearchGoods.setBranchId(goods.getBranchId().longValue());
        elasticSearchGoods.setName(goods.getName());
        elasticSearchGoods.setType(goods.getType());
        elasticSearchGoods.setCategoryId(goods.getCategoryId().longValue());
        elasticSearchGoods.setCategoryName(goods.getCategoryName());
        elasticSearchGoods.setImageUrl(goods.getImageUrl());
        elasticSearchGoods.setStocked(goods.isStocked());
        return elasticSearchGoods;
    }
}
