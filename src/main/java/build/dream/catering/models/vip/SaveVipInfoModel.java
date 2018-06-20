package build.dream.catering.models.vip;

import build.dream.common.annotations.DateFormat;
import build.dream.common.models.BasicModel;
import build.dream.common.utils.ApplicationHandler;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.Date;

public class SaveVipInfoModel extends BasicModel {
    @NotNull
    private BigInteger tenantId;

    @NotNull
    @Length(max = 20)
    private String tenantCode;

    @NotNull
    private BigInteger branchId;

    private BigInteger vipId;

    @Length(max = 20)
    private String vipName;

    @DateFormat(pattern = "yyyy-MM-dd")
    private Date birthday;

    @Length(max = 20)
    private String phoneNumber;

    @Length(max = 50)
    private String openId;

    @Length(max = 50)
    private String mainOpenId;

    @Length(max = 50)
    private String alipayUserId;

    @NotNull
    private BigInteger userId;

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

    public BigInteger getVipId() {
        return vipId;
    }

    public void setVipId(BigInteger vipId) {
        this.vipId = vipId;
    }

    public String getVipName() {
        return vipName;
    }

    public void setVipName(String vipName) {
        this.vipName = vipName;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
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

    public BigInteger getUserId() {
        return userId;
    }

    public void setUserId(BigInteger userId) {
        this.userId = userId;
    }

    @Override
    public boolean validate() {
        if (vipId != null) {
            return super.validate();
        } else {
            return super.validate() && StringUtils.isNotBlank(vipName) && birthday != null && StringUtils.isNotBlank(phoneNumber);
        }
    }

    @Override
    public void validateAndThrow() {
        super.validateAndThrow();
        if (vipId == null) {
            ApplicationHandler.notEmpty(vipName, "vipName");
            ApplicationHandler.notNull(birthday, "birthday");
            ApplicationHandler.notEmpty(phoneNumber, "phoneNumber");
        }
    }
}
