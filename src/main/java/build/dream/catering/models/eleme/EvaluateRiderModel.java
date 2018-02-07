package build.dream.catering.models.eleme;

import build.dream.common.constraints.ItemsInList;
import build.dream.common.models.BasicModel;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.List;

public class EvaluateRiderModel extends BasicModel {
    @NotNull
    private BigInteger tenantId;

    @NotNull
    private BigInteger branchId;

    @NotNull
    private BigInteger elemeOrderId;

    @NotNull
    @Min(value = 1)
    @Max(value = 5)
    private Integer level;

    @NotEmpty
    @ItemsInList(value = {"TAKE_MEAL_LONG_TIME", "FOOD_DELIVERY_SLOW", "NO_ONE_RESPONSE_PHONE", "SERVICE_BAD", "USER_NOT_RECEIVE_FOOD", "CONFIRM_ARRIVE_ADVANCE", "LACK_OF_FOOD", "INDUCE_USER_REFUND", "NOT_WEAR_UNIFORM", "IMAGE_UNTIDY", "TAKE_MEAL_ON_SCHEDULE", "FOOD_DELIVERY_FAST", "SERVICE_GOOD", "IMAGE_PRETTY", "CLEAR_UNIFORM", "EXTRA_HELP"})
    private List<String> tags;

    private String description;

    private String username;

    private String mobile;

    public BigInteger getTenantId() {
        return tenantId;
    }

    public void setTenantId(BigInteger tenantId) {
        this.tenantId = tenantId;
    }

    public BigInteger getBranchId() {
        return branchId;
    }

    public void setBranchId(BigInteger branchId) {
        this.branchId = branchId;
    }

    public BigInteger getElemeOrderId() {
        return elemeOrderId;
    }

    public void setElemeOrderId(BigInteger elemeOrderId) {
        this.elemeOrderId = elemeOrderId;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
