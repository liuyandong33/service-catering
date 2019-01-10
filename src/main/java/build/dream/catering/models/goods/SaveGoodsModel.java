package build.dream.catering.models.goods;

import build.dream.catering.constants.Constants;
import build.dream.common.annotations.JsonSchema;
import build.dream.common.models.CateringBasicModel;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@JsonSchema(value = Constants.SAVE_GOODS_SCHEMA_FILE_PATH)
public class SaveGoodsModel extends CateringBasicModel {
    private BigInteger id;
    private String name;
    private Integer type;
    private BigInteger categoryId;
    private String imageUrl;
    private Boolean stocked;

    private List<GoodsSpecificationInfo> goodsSpecificationInfos;

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
