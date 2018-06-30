package build.dream.catering.models.activity;

import build.dream.catering.constants.Constants;
import build.dream.common.annotations.JsonSchema;
import build.dream.common.models.BasicModel;
import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.GsonUtils;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public class SaveSpecialGoodsActivityModel extends BasicModel {
    @NotNull
    private BigInteger tenantId;

    @NotNull
    @Length(max = 20)
    private String tenantCode;

    @NotEmpty
    private List<BigInteger> branchIds;

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

    @NotNull
    private Integer weekSign;

    @JsonSchema(value = Constants.SPECIAL_GOODS_ACTIVITY_INFOS_SCHEMA_FILE_PATH)
    private List<SpecialGoodsActivityInfo> specialGoodsActivityInfos;

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

    public List<BigInteger> getBranchIds() {
        return branchIds;
    }

    public void setBranchIds(List<BigInteger> branchIds) {
        this.branchIds = branchIds;
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
        private BigInteger goodsId;
        private BigInteger goodsSpecificationId;
        private Integer discountType;
        private BigDecimal specialPrice;
        private BigDecimal discountRate;

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

        public Integer getDiscountType() {
            return discountType;
        }

        public void setDiscountType(Integer discountType) {
            this.discountType = discountType;
        }

        public BigDecimal getSpecialPrice() {
            return specialPrice;
        }

        public void setSpecialPrice(BigDecimal specialPrice) {
            this.specialPrice = specialPrice;
        }

        public BigDecimal getDiscountRate() {
            return discountRate;
        }

        public void setDiscountRate(BigDecimal discountRate) {
            this.discountRate = discountRate;
        }
    }
}
