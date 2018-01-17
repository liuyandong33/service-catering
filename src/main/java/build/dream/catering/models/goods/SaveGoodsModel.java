package build.dream.catering.models.goods;

import build.dream.common.models.BasicModel;
import build.dream.common.utils.ApplicationHandler;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.collections.CollectionUtils;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public class SaveGoodsModel extends BasicModel {
    private BigInteger id;

    @NotNull
    private String name;

    @NotNull
    private BigInteger tenantId;

    @NotNull
    private String tenantCode;

    @NotNull
    private BigInteger branchId;

    @NotNull
    private BigInteger userId;

    @NotNull
    private List<GoodsSpecificationModel> goodsSpecificationModels;

    private List<GoodsFlavorGroupModel> goodsFlavorGroupModels;

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

    public List<GoodsSpecificationModel> getGoodsSpecificationModels() {
        return goodsSpecificationModels;
    }

    public void setGoodsSpecificationModels(List<GoodsSpecificationModel> goodsSpecificationModels) {
        this.goodsSpecificationModels = goodsSpecificationModels;
    }

    public List<GoodsFlavorGroupModel> getGoodsFlavorGroupModels() {
        return goodsFlavorGroupModels;
    }

    public void setGoodsFlavorGroupModels(List<GoodsFlavorGroupModel> goodsFlavorGroupModels) {
        this.goodsFlavorGroupModels = goodsFlavorGroupModels;
    }

    @Override
    public boolean validate() {
        if (!super.validate()) {
            return false;
        }
        if (CollectionUtils.isNotEmpty(goodsFlavorGroupModels)) {
            for (GoodsFlavorGroupModel goodsFlavorGroupModel : goodsFlavorGroupModels) {
                if (!goodsFlavorGroupModel.validate()) {
                    return false;
                }
            }
        }
        if (CollectionUtils.isEmpty(goodsSpecificationModels)) {
            return false;
        }
        for (GoodsSpecificationModel goodsSpecificationModel : goodsSpecificationModels) {
            if (!goodsSpecificationModel.validate()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void validateAndThrow() {
        super.validateAndThrow();
        if (CollectionUtils.isNotEmpty(goodsFlavorGroupModels)) {
            for (GoodsFlavorGroupModel goodsFlavorGroupModel : goodsFlavorGroupModels) {
                ApplicationHandler.isTrue(goodsFlavorGroupModel.validate(), "goodsFlavorGroups");
            }
        }
        ApplicationHandler.isTrue(CollectionUtils.isNotEmpty(goodsSpecificationModels), "goodsSpecifications");
        for (GoodsSpecificationModel goodsSpecificationModel : goodsSpecificationModels) {
            goodsSpecificationModel.validateAndThrow();
        }
    }

    public static class GoodsSpecificationModel extends BasicModel {
        private BigInteger id;

        @NotNull
        private String name;

        @NotNull
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
        private String name;

        @SerializedName(value = "goodsFlavors")
        @NotNull
        private List<GoodsFlavorModel> goodsFlavorModels;

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

        @Override
        public boolean validate() {
            if (!super.validate()) {
                return false;
            }
            if (CollectionUtils.isNotEmpty(goodsFlavorModels)) {
                for (GoodsFlavorModel goodsFlavorModel : goodsFlavorModels) {
                    if (!goodsFlavorModel.validate()) {
                        return false;
                    }
                }
            }
            return true;
        }
    }

    public static class GoodsFlavorModel extends BasicModel {
        private BigInteger id;

        @NotNull
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
