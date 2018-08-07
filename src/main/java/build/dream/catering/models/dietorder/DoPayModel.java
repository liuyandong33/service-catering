package build.dream.catering.models.dietorder;

import build.dream.catering.constants.Constants;
import build.dream.common.models.BasicModel;
import build.dream.common.utils.ApplicationHandler;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

public class DoPayModel extends BasicModel {
    @NotNull
    private BigInteger tenantId;

    @NotNull
    private BigInteger branchId;

    @NotNull
    private BigInteger dietOrderId;

    @NotNull
    private Integer paidScene;

    @Length(max = 128)
    private String authCode;

    @Length(max = 128)
    private String openId;

    @Length(max = 128)
    private String subOpenId;

    @NotNull
    private String userId;

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

    public BigInteger getDietOrderId() {
        return dietOrderId;
    }

    public void setDietOrderId(BigInteger dietOrderId) {
        this.dietOrderId = dietOrderId;
    }

    public Integer getPaidScene() {
        return paidScene;
    }

    public void setPaidScene(Integer paidScene) {
        this.paidScene = paidScene;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getSubOpenId() {
        return subOpenId;
    }

    public void setSubOpenId(String subOpenId) {
        this.subOpenId = subOpenId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public void validateAndThrow() {
        super.validateAndThrow();
        ApplicationHandler.inArray(Constants.PAID_SCENES, paidScene, "paidScene");
    }
}
