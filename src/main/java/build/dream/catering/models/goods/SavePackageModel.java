package build.dream.catering.models.goods;

import build.dream.catering.constants.Constants;
import build.dream.common.annotations.JsonSchema;
import build.dream.common.constraints.InList;
import build.dream.common.models.BasicModel;
import build.dream.common.utils.ApplicationHandler;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public class SavePackageModel extends BasicModel {
    private static final Integer[] TYPES = {Constants.GOODS_TYPE_ORDINARY_GOODS, Constants.GOODS_TYPE_PACKAGE};
    @NotNull
    private BigInteger tenantId;

    @NotNull
    @Length(max = 20)
    private String tenantCode;

    @NotNull
    private BigInteger branchId;

    @NotNull
    private BigInteger userId;

    private BigInteger id;

    @NotNull
    @Length(max = 20)
    private String name;

    private Integer type;

    @NotNull
    private BigDecimal price;

    @NotNull
    private BigInteger categoryId;

    @NotNull
    private String imageUrl;

    private List<BigInteger> deleteGroupIds;

    @JsonSchema(value = Constants.GROUPS_SCHEMA_FILE_PATH)
    @NotNull
    private List<Group> groups;

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

    public BigInteger getUserId() {
        return userId;
    }

    public void setUserId(BigInteger userId) {
        this.userId = userId;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigInteger getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(BigInteger categoryId) {
        this.categoryId = categoryId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<BigInteger> getDeleteGroupIds() {
        return deleteGroupIds;
    }

    public void setDeleteGroupIds(List<BigInteger> deleteGroupIds) {
        this.deleteGroupIds = deleteGroupIds;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    @Override
    public void validateAndThrow() {
        super.validateAndThrow();
        ApplicationHandler.inArray(TYPES, type, "type");
    }

    public static class Group extends BasicModel {
        private BigInteger id;
        private String groupName;
        private Integer groupType;
        private Integer optionalQuantity;
        private List<GroupDetail> groupDetails;
        private List<BigInteger> deleteGroupDetailIds;

        public BigInteger getId() {
            return id;
        }

        public void setId(BigInteger id) {
            this.id = id;
        }

        public String getGroupName() {
            return groupName;
        }

        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }

        public Integer getGroupType() {
            return groupType;
        }

        public void setGroupType(Integer groupType) {
            this.groupType = groupType;
        }

        public Integer getOptionalQuantity() {
            return optionalQuantity;
        }

        public void setOptionalQuantity(Integer optionalQuantity) {
            this.optionalQuantity = optionalQuantity;
        }

        public List<GroupDetail> getGroupDetails() {
            return groupDetails;
        }

        public void setGroupDetails(List<GroupDetail> groupDetails) {
            this.groupDetails = groupDetails;
        }

        public List<BigInteger> getDeleteGroupDetailIds() {
            return deleteGroupDetailIds;
        }

        public void setDeleteGroupDetailIds(List<BigInteger> deleteGroupDetailIds) {
            this.deleteGroupDetailIds = deleteGroupDetailIds;
        }
    }

    public static class GroupDetail extends BasicModel {
        private BigInteger goodsId;
        private BigInteger goodsSpecificationId;
        private Integer quantity;

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

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
    }
}
