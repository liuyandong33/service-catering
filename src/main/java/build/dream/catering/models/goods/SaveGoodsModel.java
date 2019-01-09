package build.dream.catering.models.goods;

import build.dream.catering.constants.Constants;
import build.dream.common.annotations.JsonSchema;
import build.dream.common.models.CateringBasicModel;
import build.dream.common.utils.ApplicationHandler;
import org.apache.commons.lang.ArrayUtils;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public class SaveGoodsModel extends CateringBasicModel {
    private static final Integer[] TYPES = {Constants.GOODS_TYPE_ORDINARY_GOODS, Constants.GOODS_TYPE_PACKAGE};
    private BigInteger id;

    @NotNull
    @Length(max = 20)
    private String name;

    private Integer type;

    @NotNull
    private BigInteger categoryId;

    @NotNull
    private String imageUrl;

    @NotNull
    private Boolean stocked;

    @JsonSchema(value = Constants.GOODS_SPECIFICATION_INFOS_SCHEMA_FILE_PATH)
    private List<GoodsSpecificationInfo> goodsSpecificationInfos;

    @JsonSchema(value = Constants.ATTRIBUTE_GROUP_INFOS_SCHEMA_FILE_PATH)
    private List<AttributeGroupInfo> attributeGroupInfos;

    private List<BigInteger> deleteGoodsSpecificationIds;

    private List<BigInteger> deleteGoodsAttributeGroupIds;

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

    public Boolean getStocked() {
        return stocked;
    }

    public void setStocked(Boolean stocked) {
        this.stocked = stocked;
    }

    public List<GoodsSpecificationInfo> getGoodsSpecificationInfos() {
        return goodsSpecificationInfos;
    }

    public void setGoodsSpecificationInfos(List<GoodsSpecificationInfo> goodsSpecificationInfos) {
        this.goodsSpecificationInfos = goodsSpecificationInfos;
    }

    public List<AttributeGroupInfo> getAttributeGroupInfos() {
        return attributeGroupInfos;
    }

    public void setAttributeGroupInfos(List<AttributeGroupInfo> attributeGroupInfos) {
        this.attributeGroupInfos = attributeGroupInfos;
    }

    public List<BigInteger> getDeleteGoodsSpecificationIds() {
        return deleteGoodsSpecificationIds;
    }

    public void setDeleteGoodsSpecificationIds(List<BigInteger> deleteGoodsSpecificationIds) {
        this.deleteGoodsSpecificationIds = deleteGoodsSpecificationIds;
    }

    public List<BigInteger> getDeleteGoodsAttributeGroupIds() {
        return deleteGoodsAttributeGroupIds;
    }

    public void setDeleteGoodsAttributeGroupIds(List<BigInteger> deleteGoodsAttributeGroupIds) {
        this.deleteGoodsAttributeGroupIds = deleteGoodsAttributeGroupIds;
    }

    @Override
    public boolean validate() {
        if (!super.validate()) {
            return false;
        }
        if (!ArrayUtils.contains(TYPES, type)) {
            return false;
        }
        return true;
    }

    @Override
    public void validateAndThrow() {
        super.validateAndThrow();
        ApplicationHandler.notNull(type, "type");
        ApplicationHandler.inArray(TYPES, type, "type");
    }

    public static class GoodsSpecificationInfo {
        private BigInteger id;
        private String name;
        private BigDecimal price;
        private BigDecimal stock;

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

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }

        public BigDecimal getStock() {
            return stock;
        }

        public void setStock(BigDecimal stock) {
            this.stock = stock;
        }
    }

    public static class AttributeGroupInfo {
        private BigInteger id;
        @NotNull
        @Length(max = 20)
        private String name;

        private List<AttributeInfo> attributeInfos;

        private List<BigInteger> deleteGoodsAttributeIds;

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

        public List<AttributeInfo> getAttributeInfos() {
            return attributeInfos;
        }

        public void setAttributeInfos(List<AttributeInfo> attributeInfos) {
            this.attributeInfos = attributeInfos;
        }

        public List<BigInteger> getDeleteGoodsAttributeIds() {
            return deleteGoodsAttributeIds;
        }

        public void setDeleteGoodsAttributeIds(List<BigInteger> deleteGoodsAttributeIds) {
            this.deleteGoodsAttributeIds = deleteGoodsAttributeIds;
        }
    }

    public static class AttributeInfo {
        private BigInteger id;

        @NotNull
        @Length(max = 20)
        private String name;

        private BigDecimal price;

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

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }
    }
}
