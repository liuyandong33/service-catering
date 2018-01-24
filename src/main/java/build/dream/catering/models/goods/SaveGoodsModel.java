package build.dream.catering.models.goods;

import build.dream.catering.constants.Constants;
import build.dream.catering.utils.JsonSchemaValidateUtils;
import build.dream.common.models.BasicModel;
import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.GsonUtils;
import build.dream.common.utils.JacksonUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
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

    private List<FlavorGroupInfo> flavorGroupInfos;

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
        ApplicationHandler.isTrue(JsonSchemaValidateUtils.validate(goodsSpecificationInfos, "build/dream/catering/schemas/goodsSpecificationInfosSchema.json"), "goodsSpecificationInfos");
        this.goodsSpecificationInfos = GsonUtils.jsonToList(goodsSpecificationInfos, GoodsSpecificationInfo.class);
    }

    public List<FlavorGroupInfo> getFlavorGroupInfos() {
        return flavorGroupInfos;
    }

    public void setFlavorGroupInfos(List<FlavorGroupInfo> flavorGroupInfos) {
        this.flavorGroupInfos = flavorGroupInfos;
    }

    public void setFlavorGroupInfos(String flavorGroupInfos) throws IOException {
        ApplicationHandler.isTrue(JsonSchemaValidateUtils.validate(flavorGroupInfos, "build/dream/catering/schemas/flavorGroupInfosSchema.json"), "flavorGroupInfos");
        this.flavorGroupInfos = GsonUtils.jsonToList(flavorGroupInfos, FlavorGroupInfo.class);
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

    public static class FlavorGroupInfo {
        private BigInteger id;
        @NotNull
        @Length(max = 20)
        private String name;

        private List<FlavorInfo> flavorInfos;

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

        public List<FlavorInfo> getFlavorInfos() {
            return flavorInfos;
        }

        public List<BigInteger> getDeleteGoodsFlavorIds() {
            return deleteGoodsFlavorIds;
        }

        public void setDeleteGoodsFlavorIds(List<BigInteger> deleteGoodsFlavorIds) {
            this.deleteGoodsFlavorIds = deleteGoodsFlavorIds;
        }
    }

    public static class FlavorInfo {
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
