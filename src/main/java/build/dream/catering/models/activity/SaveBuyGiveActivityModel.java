package build.dream.catering.models.activity;

import build.dream.catering.constants.Constants;
import build.dream.common.models.BasicModel;
import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.GsonUtils;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.List;

public class SaveBuyGiveActivityModel extends BasicModel {
    @NotNull
    private BigInteger tenantId;

    @NotNull
    @Length(max = 20)
    private String tenantCode;

    @NotNull
    private BigInteger branchId;

    @NotNull
    private BigInteger userId;

    @NotNull
    @Length(max = 20)
    private String name;

    @NotNull
    @Length(min = 10, max = 10)
    private String startDate;

    @NotNull
    @Length(min = 5, max = 5)
    private String startTime;

    @NotNull
    @Length(min = 10, max = 10)
    private String endDate;

    @NotNull
    @Length(min = 5, max = 5)
    private String endTime;

    private List<BuyGiveActivityInfo> buyGiveActivityInfos;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public List<BuyGiveActivityInfo> getBuyGiveActivityInfos() {
        return buyGiveActivityInfos;
    }

    public void setBuyGiveActivityInfos(List<BuyGiveActivityInfo> buyGiveActivityInfos) {
        this.buyGiveActivityInfos = buyGiveActivityInfos;
    }

    public void setBuyGiveActivityInfos(String buyGiveActivityInfos) {
        ApplicationHandler.validateJson(buyGiveActivityInfos, Constants.BUY_GIVE_ACTIVITY_INFOS_SCHEMA_FILE_PATH, "buyGiveActivityInfos");
        this.buyGiveActivityInfos = GsonUtils.jsonToList(buyGiveActivityInfos, BuyGiveActivityInfo.class);
    }

    public static class BuyGiveActivityInfo {
        private BigInteger buyGoodsId;
        private BigInteger buyGoodsSpecificationId;
        private Integer buyQuantity;
        private BigInteger giveGoodsId;
        private BigInteger giveGoodsSpecificationId;
        private Integer giveQuantity;

        public BigInteger getBuyGoodsId() {
            return buyGoodsId;
        }

        public void setBuyGoodsId(BigInteger buyGoodsId) {
            this.buyGoodsId = buyGoodsId;
        }

        public BigInteger getBuyGoodsSpecificationId() {
            return buyGoodsSpecificationId;
        }

        public void setBuyGoodsSpecificationId(BigInteger buyGoodsSpecificationId) {
            this.buyGoodsSpecificationId = buyGoodsSpecificationId;
        }

        public Integer getBuyQuantity() {
            return buyQuantity;
        }

        public void setBuyQuantity(Integer buyQuantity) {
            this.buyQuantity = buyQuantity;
        }

        public BigInteger getGiveGoodsId() {
            return giveGoodsId;
        }

        public void setGiveGoodsId(BigInteger giveGoodsId) {
            this.giveGoodsId = giveGoodsId;
        }

        public BigInteger getGiveGoodsSpecificationId() {
            return giveGoodsSpecificationId;
        }

        public void setGiveGoodsSpecificationId(BigInteger giveGoodsSpecificationId) {
            this.giveGoodsSpecificationId = giveGoodsSpecificationId;
        }

        public Integer getGiveQuantity() {
            return giveQuantity;
        }

        public void setGiveQuantity(Integer giveQuantity) {
            this.giveQuantity = giveQuantity;
        }
    }
}
