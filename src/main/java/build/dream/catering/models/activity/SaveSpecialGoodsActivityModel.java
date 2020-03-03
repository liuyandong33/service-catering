package build.dream.catering.models.activity;

import build.dream.catering.constants.Constants;
import build.dream.common.constraints.VerifyJsonSchema;
import build.dream.common.models.CateringBasicModel;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

public class SaveSpecialGoodsActivityModel extends CateringBasicModel {
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

    @VerifyJsonSchema(value = Constants.SPECIAL_GOODS_ACTIVITY_INFOS_SCHEMA_FILE_PATH)
    private List<SpecialGoodsActivityInfo> specialGoodsActivityInfos;

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

    public List<SpecialGoodsActivityInfo> getSpecialGoodsActivityInfos() {
        return specialGoodsActivityInfos;
    }

    public void setSpecialGoodsActivityInfos(List<SpecialGoodsActivityInfo> specialGoodsActivityInfos) {
        this.specialGoodsActivityInfos = specialGoodsActivityInfos;
    }

    public static class SpecialGoodsActivityInfo {
        private Long goodsId;
        private Long goodsSpecificationId;
        private Integer discountType;
        private Double specialPrice;
        private Double discountRate;

        public Long getGoodsId() {
            return goodsId;
        }

        public void setGoodsId(Long goodsId) {
            this.goodsId = goodsId;
        }

        public Long getGoodsSpecificationId() {
            return goodsSpecificationId;
        }

        public void setGoodsSpecificationId(Long goodsSpecificationId) {
            this.goodsSpecificationId = goodsSpecificationId;
        }

        public Integer getDiscountType() {
            return discountType;
        }

        public void setDiscountType(Integer discountType) {
            this.discountType = discountType;
        }

        public Double getSpecialPrice() {
            return specialPrice;
        }

        public void setSpecialPrice(Double specialPrice) {
            this.specialPrice = specialPrice;
        }

        public Double getDiscountRate() {
            return discountRate;
        }

        public void setDiscountRate(Double discountRate) {
            this.discountRate = discountRate;
        }
    }
}
