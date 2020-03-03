package build.dream.catering.models.dietorder;

import build.dream.catering.constants.Constants;
import build.dream.common.models.BasicModel;
import build.dream.common.utils.ApplicationHandler;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

public class DoPayModel extends BasicModel {
    @NotNull
    private Long tenantId;

    @NotNull
    private Long branchId;

    @NotNull
    private Long dietOrderId;

    @NotNull
    private Integer paidScene;

    @Length(max = 128)
    private String authCode;

    @Length(max = 128)
    private String openId;

    @Length(max = 128)
    private String subOpenId;

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getBranchId() {
        return branchId;
    }

    public void setBranchId(Long branchId) {
        this.branchId = branchId;
    }

    public Long getDietOrderId() {
        return dietOrderId;
    }

    public void setDietOrderId(Long dietOrderId) {
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

    @Override
    public void validateAndThrow() {
        super.validateAndThrow();
        ApplicationHandler.inArray(Constants.PAID_SCENES, paidScene, "paidScene");
        if (paidScene == Constants.PAID_SCENE_WEI_XIN_MICROPAY || paidScene == Constants.PAID_SCENE_ALIPAY_FAC_TO_FACE) {
            ApplicationHandler.notBlank(authCode, "authCode");
        }
    }
}
