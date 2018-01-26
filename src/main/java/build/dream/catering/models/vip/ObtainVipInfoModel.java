package build.dream.catering.models.vip;

import build.dream.common.models.BasicModel;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

public class ObtainVipInfoModel extends BasicModel {
    @NotNull
    private BigInteger tenantId;
    @NotNull
    private BigInteger branchId;

    private BigInteger vipId;

    private String vipCode;

    private String phoneNumber;

    private String openId;

    private String alipayUserId;

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

    public BigInteger getVipId() {
        return vipId;
    }

    public void setVipId(BigInteger vipId) {
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

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getAlipayUserId() {
        return alipayUserId;
    }

    public void setAlipayUserId(String alipayUserId) {
        this.alipayUserId = alipayUserId;
    }

    @Override
    public boolean validate() {
        return super.validate() && (vipId != null || StringUtils.isNotBlank(vipCode) || StringUtils.isNotBlank(phoneNumber));
    }

    @Override
    public void validateAndThrow() {
        super.validateAndThrow();
        Validate.isTrue(vipId != null || StringUtils.isNotBlank(vipCode) || StringUtils.isNotBlank(phoneNumber) || StringUtils.isNotBlank(openId) || StringUtils.isNotBlank(alipayUserId), "参数【vipId、vipCode、phoneNumber、openId、alipayUserId】不能同时为空！");
    }
}
