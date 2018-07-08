package build.dream.catering.models.vip;

import build.dream.common.models.BasicModel;
import build.dream.common.utils.ApplicationHandler;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.BigInteger;

public class SaveVipTypeModel extends BasicModel {
    private static final Integer[] DISCOUNT_POLICIES = {1, 2, 3};
    private BigInteger id;
    @NotNull
    private BigInteger tenantId;

    @NotNull
    private String tenantCode;

    @NotNull
    private BigInteger branchId;

    @NotNull
    @Length(max = 20)
    private String name;

    private Integer discountPolicy;

    private BigDecimal discountRate;

    @NotNull
    private Boolean enableBonus;

    private Integer bonusCoefficient;

    @NotNull
    private BigInteger userId;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDiscountPolicy() {
        return discountPolicy;
    }

    public void setDiscountPolicy(Integer discountPolicy) {
        this.discountPolicy = discountPolicy;
    }

    public BigDecimal getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(BigDecimal discountRate) {
        this.discountRate = discountRate;
    }

    public Boolean getEnableBonus() {
        return enableBonus;
    }

    public void setEnableBonus(Boolean enableBonus) {
        this.enableBonus = enableBonus;
    }

    public Integer getBonusCoefficient() {
        return bonusCoefficient;
    }

    public void setBonusCoefficient(Integer bonusCoefficient) {
        this.bonusCoefficient = bonusCoefficient;
    }

    public BigInteger getUserId() {
        return userId;
    }

    public void setUserId(BigInteger userId) {
        this.userId = userId;
    }

    @Override
    public void validateAndThrow() {
        super.validateAndThrow();
        ApplicationHandler.inArray(DISCOUNT_POLICIES, discountPolicy, "discountPolicy");
        if (discountPolicy == 3) {
            ApplicationHandler.notNull(discountRate, "discountRate");
        }
        if (enableBonus) {
            ApplicationHandler.notNull(bonusCoefficient, "bonusCoefficient");
        }
    }
}
