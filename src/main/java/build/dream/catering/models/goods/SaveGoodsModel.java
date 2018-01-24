package build.dream.catering.models.goods;

import build.dream.catering.constants.Constants;
import build.dream.catering.schemas.JsonSchemaValidateUtils;
import build.dream.common.models.BasicModel;
import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.GsonUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.Validate;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public class SaveGoodsModel extends BasicModel {
    @NotNull
    private BigInteger tenantId;

    @NotNull
    @Length(max = 20)
    private String tenantCode;

    @NotNull
    private BigInteger branchId;

    @NotNull
    private BigInteger userId;

    private BigInteger goodsId;

    @NotNull
    @Length(max = 20)
    private String goodsName;

    private Integer goodsType;

    @NotNull
    private BigInteger categoryId;

    private List<GoodsSpecificationInfo> goodsSpecificationInfos;

    private List<GoodsFlavorGroupModel> goodsFlavorGroupModels;

    private List<BigInteger> deleteGoodsSpecificationIds;

    private List<BigInteger> deleteGoodsFlavorGroupIds;

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

    public BigInteger getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(BigInteger goodsId) {
        this.goodsId = goodsId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public Integer getGoodsType() {
        return goodsType;
    }

    public void setGoodsType(Integer goodsType) {
        this.goodsType = goodsType;
    }

    public BigInteger getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(BigInteger categoryId) {
        this.categoryId = categoryId;
    }

    public List<GoodsSpecificationInfo> getGoodsSpecificationInfos() {
        return goodsSpecificationInfos;
    }

    public void setGoodsSpecificationInfos(List<GoodsSpecificationInfo> goodsSpecificationInfos) {
        this.goodsSpecificationInfos = goodsSpecificationInfos;
    }

    public void setGoodsSpecificationInfos(String goodsSpecificationInfos) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(goodsSpecificationInfos);
        ApplicationHandler.isTrue(JsonSchemaValidateUtils.validate(jsonNode, ""), "goodsSpecificationInfos");
        this.goodsSpecificationInfos = GsonUtils.jsonToList(goodsSpecificationInfos, GoodsSpecificationInfo.class);
    }

    public List<GoodsFlavorGroupModel> getGoodsFlavorGroupModels() {
        return goodsFlavorGroupModels;
    }

    public void setGoodsFlavorGroupModels(List<GoodsFlavorGroupModel> goodsFlavorGroupModels) {
        this.goodsFlavorGroupModels = goodsFlavorGroupModels;
    }

    public List<BigInteger> getDeleteGoodsSpecificationIds() {
        return deleteGoodsSpecificationIds;
    }

    public void setDeleteGoodsSpecificationIds(List<BigInteger> deleteGoodsSpecificationIds) {
        this.deleteGoodsSpecificationIds = deleteGoodsSpecificationIds;
    }

    public List<BigInteger> getDeleteGoodsFlavorGroupIds() {
        return deleteGoodsFlavorGroupIds;
    }

    public void setDeleteGoodsFlavorGroupIds(List<BigInteger> deleteGoodsFlavorGroupIds) {
        this.deleteGoodsFlavorGroupIds = deleteGoodsFlavorGroupIds;
    }

    @Override
    public boolean validate() {
        if (!super.validate()) {
            return false;
        }
        if (!ArrayUtils.contains(new Object[]{Constants.GOODS_TYPE_ORDINARY_GOODS, Constants.GOODS_TYPE_PACKAGE}, goodsType)) {
            return false;
        }
        return true;
    }

    @Override
    public void validateAndThrow() {
        super.validateAndThrow();
        ApplicationHandler.notNull(goodsType, "goodsType");
        ApplicationHandler.inArray(new Object[]{Constants.GOODS_TYPE_ORDINARY_GOODS, Constants.GOODS_TYPE_PACKAGE}, goodsType, "goodsType");
    }

    public static class GoodsSpecificationInfo {
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

    public static class GoodsFlavorGroupModel extends BasicModel {
        private BigInteger id;
        @NotNull
        @Length(max = 20)
        private String name;

        @SerializedName(value = "flavors", alternate = "goodsFlavorModels")
        private List<GoodsFlavorModel> goodsFlavorModels;

        private List<BigInteger> deleteGoodsFlavorIds;

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

        public List<GoodsFlavorModel> getGoodsFlavorModels() {
            return goodsFlavorModels;
        }

        public void setGoodsFlavorModels(List<GoodsFlavorModel> goodsFlavorModels) {
            this.goodsFlavorModels = goodsFlavorModels;
        }

        public List<BigInteger> getDeleteGoodsFlavorIds() {
            return deleteGoodsFlavorIds;
        }

        public void setDeleteGoodsFlavorIds(List<BigInteger> deleteGoodsFlavorIds) {
            this.deleteGoodsFlavorIds = deleteGoodsFlavorIds;
        }

        @Override
        public boolean validate() {
            if (!super.validate()) {
                return false;
            }
            if (CollectionUtils.isEmpty(goodsFlavorModels)) {
                return false;
            }
            for (GoodsFlavorModel goodsFlavorModel : goodsFlavorModels) {
                if (!goodsFlavorModel.validate()) {
                    return false;
                }
            }
            return true;
        }
    }

    public static class GoodsFlavorModel extends BasicModel {
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
