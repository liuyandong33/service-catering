package build.dream.catering.models.goods;

import build.dream.catering.constants.Constants;
import build.dream.common.annotations.JsonSchema;
import build.dream.common.models.CateringBasicModel;

import java.util.List;

@JsonSchema(value = Constants.SAVE_GOODS_SCHEMA_FILE_PATH)
public class SaveGoodsModel extends CateringBasicModel {
    private Long id;
    private String name;
    private Integer type;
    private Long categoryId;
    private String imageUrl;
    private Boolean stocked;

    private List<GoodsSpecificationInfo> goodsSpecificationInfos;

    private List<AttributeGroupInfo> attributeGroupInfos;

    private List<Long> deleteGoodsSpecificationIds;

    private List<Long> deleteGoodsAttributeGroupIds;

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

    public List<Long> getDeleteGoodsSpecificationIds() {
        return deleteGoodsSpecificationIds;
    }

    public void setDeleteGoodsSpecificationIds(List<Long> deleteGoodsSpecificationIds) {
        this.deleteGoodsSpecificationIds = deleteGoodsSpecificationIds;
    }

    public List<Long> getDeleteGoodsAttributeGroupIds() {
        return deleteGoodsAttributeGroupIds;
    }

    public void setDeleteGoodsAttributeGroupIds(List<Long> deleteGoodsAttributeGroupIds) {
        this.deleteGoodsAttributeGroupIds = deleteGoodsAttributeGroupIds;
    }

    public static class GoodsSpecificationInfo {
        private Long id;
        private String name;
        private Double price;
        private Double stock;

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

        public Double getPrice() {
            return price;
        }

        public void setPrice(Double price) {
            this.price = price;
        }

        public Double getStock() {
            return stock;
        }

        public void setStock(Double stock) {
            this.stock = stock;
        }
    }

    public static class AttributeGroupInfo {
        private Long id;
        private String name;
        private List<AttributeInfo> attributeInfos;
        private List<Long> deleteGoodsAttributeIds;

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

        public List<AttributeInfo> getAttributeInfos() {
            return attributeInfos;
        }

        public void setAttributeInfos(List<AttributeInfo> attributeInfos) {
            this.attributeInfos = attributeInfos;
        }

        public List<Long> getDeleteGoodsAttributeIds() {
            return deleteGoodsAttributeIds;
        }

        public void setDeleteGoodsAttributeIds(List<Long> deleteGoodsAttributeIds) {
            this.deleteGoodsAttributeIds = deleteGoodsAttributeIds;
        }
    }

    public static class AttributeInfo {
        private Long id;
        private String name;
        private Double price;

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

        public Double getPrice() {
            return price;
        }

        public void setPrice(Double price) {
            this.price = price;
        }
    }
}
