package build.dream.catering.models.dietorder;

import build.dream.common.models.BasicModel;
import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.GsonUtils;
import com.google.gson.annotations.SerializedName;

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
    private Integer orderType;
    @NotNull
    private BigInteger userId;

    private List<GroupInfo> groupInfos;

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

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public BigInteger getUserId() {
        return userId;
    }

    public void setUserId(BigInteger userId) {
        this.userId = userId;
    }

    public List<GroupInfo> getGroupInfos() {
        return groupInfos;
    }

    public void setGroupInfos(List<GroupInfo> groupInfos) {
        this.groupInfos = groupInfos;
    }

    public void setGroupInfos(String groups) {
        ApplicationHandler.validateJson(groups, "build/dream/catering/schemas/groupsSchema.json", "groups");
        this.groupInfos = GsonUtils.jsonToList(groups, GroupInfo.class);
    }

    public static class GroupInfo {
        private String name;
        private String type;
        @SerializedName(value = "details", alternate = "detailInfos")
        private List<DetailInfo> detailInfos;

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

        public List<DetailInfo> getDetailInfos() {
            return detailInfos;
        }

        public void setDetailInfos(List<DetailInfo> detailInfos) {
            this.detailInfos = detailInfos;
        }
    }

    public static class DetailInfo {
        private BigInteger goodsId;
        private BigInteger goodsSpecificationId;
        private Integer quantity;
        @SerializedName(value = "flavors", alternate = "flavorInfos")
        private List<FlavorInfo> flavorInfos;

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

        public List<FlavorInfo> getFlavorInfos() {
            return flavorInfos;
        }

        public void setFlavorInfos(List<FlavorInfo> flavorInfos) {
            this.flavorInfos = flavorInfos;
        }
    }

    public static class FlavorInfo {
        private BigInteger flavorGroupId;
        private BigInteger flavorId;

        public BigInteger getFlavorGroupId() {
            return flavorGroupId;
        }

        public void setFlavorGroupId(BigInteger flavorGroupId) {
            this.flavorGroupId = flavorGroupId;
        }

        public BigInteger getFlavorId() {
            return flavorId;
        }

        public void setFlavorId(BigInteger flavorId) {
            this.flavorId = flavorId;
        }
    }
}
