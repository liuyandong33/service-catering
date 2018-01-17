package build.dream.catering.models.goods;

import build.dream.common.models.BasicModel;
import build.dream.common.utils.ApplicationHandler;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.List;

public class SavePackageModel extends BasicModel {
    @NotNull
    private BigInteger tenantId;

    @NotNull
    @Length(max = 20)
    private String tenantCode;

    @NotNull
    private BigInteger branchId;

    @NotNull
    private BigInteger userId;

    private BigInteger packageId;

    @NotNull
    @Length(max = 20)
    private String name;

    private List<PackageGroupModel> packageGroupModels;

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

    public BigInteger getPackageId() {
        return packageId;
    }

    public void setPackageId(BigInteger packageId) {
        this.packageId = packageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PackageGroupModel> getPackageGroupModels() {
        return packageGroupModels;
    }

    public void setPackageGroupModels(List<PackageGroupModel> packageGroupModels) {
        this.packageGroupModels = packageGroupModels;
    }

    @Override
    public boolean validate() {
        if (!super.validate()) {
            return false;
        }
        if (CollectionUtils.isEmpty(packageGroupModels)) {
            return false;
        }
        for (PackageGroupModel packageGroupModel : packageGroupModels) {
            if (!packageGroupModel.validate()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void validateAndThrow() {
        super.validateAndThrow();
        ApplicationHandler.notEmpty(packageGroupModels, "packageGroups");
        for (PackageGroupModel packageGroupModel : packageGroupModels) {
            ApplicationHandler.isTrue(packageGroupModel.validate(), "packageGroups");
        }
    }

    public static class PackageGroupModel extends BasicModel {
        private BigInteger id;
        @NotNull
        private Integer groupType;

        private Integer optionalQuantity;

        @SerializedName(value = "packageGroupGoodses")
        private List<PackageGroupGoodsModel> packageGroupGoodsModels;

        public BigInteger getId() {
            return id;
        }

        public void setId(BigInteger id) {
            this.id = id;
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

        public List<PackageGroupGoodsModel> getPackageGroupGoodsModels() {
            return packageGroupGoodsModels;
        }

        public void setPackageGroupGoodsModels(List<PackageGroupGoodsModel> packageGroupGoodsModels) {
            this.packageGroupGoodsModels = packageGroupGoodsModels;
        }

        @Override
        public boolean validate() {
            if (!super.validate()) {
                return false;
            }
            if (CollectionUtils.isEmpty(packageGroupGoodsModels)) {
                return false;
            }
            for (PackageGroupGoodsModel packageGroupGoodsModel : packageGroupGoodsModels) {
                if (!packageGroupGoodsModel.validate()) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public void validateAndThrow() {
            super.validateAndThrow();
            ApplicationHandler.notEmpty(packageGroupGoodsModels, "packageGroupGoodses");
            for (PackageGroupGoodsModel packageGroupGoodsModel : packageGroupGoodsModels) {
                ApplicationHandler.isTrue(packageGroupGoodsModel.validate(), "packageGroupGoodses");
            }
        }
    }

    public static class PackageGroupGoodsModel extends BasicModel {
        @NotNull
        private BigInteger goodsId;

        @NotNull
        private Integer quantity;

        public BigInteger getGoodsId() {
            return goodsId;
        }

        public void setGoodsId(BigInteger goodsId) {
            this.goodsId = goodsId;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
    }
}
