package build.dream.catering.models.goods;

import build.dream.catering.constants.Constants;
import build.dream.common.constraints.VerifyJsonSchema;
import build.dream.common.models.BasicModel;
import build.dream.common.models.CateringBasicModel;
import build.dream.common.utils.ApplicationHandler;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.util.List;

public class SavePackageModel extends CateringBasicModel {
    private static final Integer[] TYPES = {Constants.GOODS_TYPE_ORDINARY_GOODS, Constants.GOODS_TYPE_PACKAGE};
    private Long id;

    @NotNull
    @Length(max = 20)
    private String name;

    private Integer type;

    @NotNull
    private Double price;

    @NotNull
    private Long categoryId;

    @NotNull
    private String imageUrl;

    private List<Long> deleteGroupIds;

    @VerifyJsonSchema(value = Constants.GROUPS_SCHEMA_FILE_PATH)
    @NotNull
    private List<Group> groups;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<Long> getDeleteGroupIds() {
        return deleteGroupIds;
    }

    public void setDeleteGroupIds(List<Long> deleteGroupIds) {
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
        private Long id;
        private String groupName;
        private Integer groupType;
        private Integer optionalQuantity;
        private List<GroupDetail> groupDetails;
        private List<Long> deleteGroupDetailIds;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
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

        public List<Long> getDeleteGroupDetailIds() {
            return deleteGroupDetailIds;
        }

        public void setDeleteGroupDetailIds(List<Long> deleteGroupDetailIds) {
            this.deleteGroupDetailIds = deleteGroupDetailIds;
        }
    }

    public static class GroupDetail extends BasicModel {
        private Long goodsId;
        private Long goodsSpecificationId;
        private Integer quantity;

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

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
    }
}
