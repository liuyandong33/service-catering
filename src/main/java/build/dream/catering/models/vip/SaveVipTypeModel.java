package build.dream.catering.models.vip;

import build.dream.common.models.CateringBasicModel;
import build.dream.common.utils.ApplicationHandler;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.BigInteger;

public class SaveVipTypeModel extends CateringBasicModel {
    private static final Integer[] DISCOUNT_POLICIES = {1, 2, 3};
    private BigInteger id;

    @NotNull
    @Length(max = 20)
    private String name;

    private Integer discountPolicy;

    private BigDecimal discountRate;

    @NotNull
    private Boolean enableBonus;

    private Integer bonusCoefficient;

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
