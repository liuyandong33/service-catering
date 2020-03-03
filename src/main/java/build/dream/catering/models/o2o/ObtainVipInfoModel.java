package build.dream.catering.models.o2o;

import build.dream.common.models.BasicModel;
import build.dream.common.utils.ValidateUtils;
import org.apache.commons.lang.StringUtils;

import javax.validation.constraints.NotNull;

public class ObtainVipInfoModel extends BasicModel {
    @NotNull
    private Long tenantId;

    private Long vipId;

    private String vipCode;

    private String phoneNumber;

    private String mainOpenId;

    private String alipayUserId;

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getVipId() {
        return vipId;
    }

    public void setVipId(Long vipId) {
        this.vipId = vipId;
    }

    public String getVipCode() {
        return vipCode;
    }

    public void setVipCode(String vipCode) {
        this.vipCode = vipCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getMainOpenId() {
        return mainOpenId;
    }

    public void setMainOpenId(String mainOpenId) {
        this.mainOpenId = mainOpenId;
    }

    public String getAlipayUserId() {
        return alipayUserId;
    }

    public void setAlipayUserId(String alipayUserId) {
        this.alipayUserId = alipayUserId;
    }

    @Override
    public void validateAndThrow() {
        super.validateAndThrow();
        ValidateUtils.isTrue(vipId != null || StringUtils.isNotBlank(vipCode) || StringUtils.isNotBlank(phoneNumber) || StringUtils.isNotBlank(mainOpenId) || StringUtils.isNotBlank(alipayUserId), "参数【vipId、vipCode、phoneNumber、mainOpenId、alipayUserId】不能同时为空！");
    }
}
