package build.dream.catering.models.activity;

import build.dream.common.models.CateringBasicModel;
import build.dream.common.utils.ApplicationHandler;
import org.apache.commons.lang.ArrayUtils;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public class SaveFullReductionActivityModel extends CateringBasicModel {
    private static Integer[] DISCOUNT_TYPES = new Integer[]{1, 2};
    @NotNull
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

    @NotNull
    private BigDecimal totalAmount;

    private Integer discountType;
    private BigDecimal discountRate;
    private BigDecimal discountAmount;

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

    public BigDecimal getTotalAmount() {
        return totalAmount;
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

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Integer getDiscountType() {
        return discountType;
    }

    public void setDiscountType(Integer discountType) {
        this.discountType = discountType;
    }

    public BigDecimal getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(BigDecimal discountRate) {
        this.discountRate = discountRate;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    @Override
    public boolean validate() {
        if (!super.validate()) {
            return false;
        }
        if (!ArrayUtils.contains(DISCOUNT_TYPES, discountType)) {
            return false;
        }
        if (discountType == 1 && discountAmount == null) {
            return false;
        }
        if (discountType == 2 && discountRate == null) {
            return false;
        }
        return true;
    }

    @Override
    public void validateAndThrow() {
        super.validateAndThrow();
        ApplicationHandler.inArray(new Integer[]{1, 2}, discountType, "discountType");
        if (discountType == 1) {
            ApplicationHandler.notNull(discountAmount, "discountAmount");
        } else if (discountType == 2) {
            ApplicationHandler.notNull(discountRate, "discountRate");
        }
    }
}
