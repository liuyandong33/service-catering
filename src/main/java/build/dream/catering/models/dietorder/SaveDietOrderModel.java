package build.dream.catering.models.dietorder;

import build.dream.common.models.BasicModel;
import build.dream.common.utils.ApplicationHandler;
import org.apache.commons.collections.CollectionUtils;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.List;

public class SaveDietOrderModel extends BasicModel {
    @NotNull
    private BigInteger tenantId;

    @NotNull
    private String tenantCode;

    @NotNull
    private BigInteger branchId;

    @NotNull
    private List<GroupModel> groupModels;

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

    public List<GroupModel> getGroupModels() {
        return groupModels;
    }

    public void setGroupModels(List<GroupModel> groupModels) {
        this.groupModels = groupModels;
    }

    @Override
    public boolean validate() {
        if (!super.validate()) {
            return false;
        }
        if (CollectionUtils.isEmpty(groupModels)) {
            return false;
        }
        for (GroupModel groupModel : groupModels) {
            if (!groupModel.validate()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void validateAndThrow() {
        super.validateAndThrow();
        ApplicationHandler.notEmpty(groupModels, "groups");
        for (GroupModel groupModel : groupModels) {
            ApplicationHandler.isTrue(groupModel.validate(), "groups");
        }
    }

    public static class GroupModel extends BasicModel {
        @NotNull
        private String name;
        @NotNull
        private String type;
        private List<DetailModel> detailModels;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public List<DetailModel> getDetailModels() {
            return detailModels;
        }

        public void setDetailModels(List<DetailModel> detailModels) {
            this.detailModels = detailModels;
        }

        @Override
        public boolean validate() {
            if (!super.validate()) {
                return false;
            }
            if (CollectionUtils.isEmpty(detailModels)) {
                return false;
            }
            for (DetailModel detailModel : detailModels) {
                if (!detailModel.validate()) {
                    return false;
                }
            }
            return true;
        }
    }

    public static class DetailModel extends BasicModel {
        @NotNull
        private BigInteger goodsId;
        @NotNull
        private BigInteger goodsSpecificationId;

        @NotNull
        private Integer quantity;

        private List<BigInteger> flavorIds;

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

        public List<BigInteger> getFlavorIds() {
            return flavorIds;
        }

        public void setFlavorIds(List<BigInteger> flavorIds) {
            this.flavorIds = flavorIds;
        }
    }
}
