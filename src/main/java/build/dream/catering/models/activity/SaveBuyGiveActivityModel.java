package build.dream.catering.models.activity;

import build.dream.catering.constants.Constants;
import build.dream.common.constraints.VerifyJsonSchema;
import build.dream.common.models.CateringBasicModel;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

public class SaveBuyGiveActivityModel extends CateringBasicModel {
    @NotEmpty
    private List<Long> branchIds;

    @NotNull
    private Long userId;

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

    @NotNull
    private Integer weekSign;

    @VerifyJsonSchema(value = Constants.BUY_GIVE_ACTIVITY_INFOS_SCHEMA_FILE_PATH)
    private List<BuyGiveActivityInfo> buyGiveActivityInfos;

    public List<Long> getBranchIds() {
        return branchIds;
    }

    public void setBranchIds(List<Long> branchIds) {
        this.branchIds = branchIds;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
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

    public Integer getWeekSign() {
        return weekSign;
    }

    public void setWeekSign(Integer weekSign) {
        this.weekSign = weekSign;
    }

    public List<BuyGiveActivityInfo> getBuyGiveActivityInfos() {
        return buyGiveActivityInfos;
    }

    public void setBuyGiveActivityInfos(List<BuyGiveActivityInfo> buyGiveActivityInfos) {
        this.buyGiveActivityInfos = buyGiveActivityInfos;
    }

    public static class BuyGiveActivityInfo {
        private Long buyGoodsId;
        private Long buyGoodsSpecificationId;
        private Integer buyQuantity;
        private Long giveGoodsId;
        private Long giveGoodsSpecificationId;
        private Integer giveQuantity;

        public Long getBuyGoodsId() {
            return buyGoodsId;
        }

        public void setBuyGoodsId(Long buyGoodsId) {
            this.buyGoodsId = buyGoodsId;
        }

        public Long getBuyGoodsSpecificationId() {
            return buyGoodsSpecificationId;
        }

        public void setBuyGoodsSpecificationId(Long buyGoodsSpecificationId) {
            this.buyGoodsSpecificationId = buyGoodsSpecificationId;
        }

        public Integer getBuyQuantity() {
            return buyQuantity;
        }

        public void setBuyQuantity(Integer buyQuantity) {
            this.buyQuantity = buyQuantity;
        }

        public Long getGiveGoodsId() {
            return giveGoodsId;
        }

        public void setGiveGoodsId(Long giveGoodsId) {
            this.giveGoodsId = giveGoodsId;
        }

        public Long getGiveGoodsSpecificationId() {
            return giveGoodsSpecificationId;
        }

        public void setGiveGoodsSpecificationId(Long giveGoodsSpecificationId) {
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
