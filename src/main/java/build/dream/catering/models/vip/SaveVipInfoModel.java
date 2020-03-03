package build.dream.catering.models.vip;

import build.dream.common.annotations.DateFormat;
import build.dream.common.models.CateringBasicModel;
import build.dream.common.utils.ApplicationHandler;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.util.Date;

public class SaveVipInfoModel extends CateringBasicModel {
    private Long vipId;

    @NotNull
    private Long vipTypeId;

    @NotNull
    @Length(max = 20)
    private String vipName;

    @NotNull
    @DateFormat(pattern = "yyyy-MM-dd")
    private Date birthday;

    @NotNull
    @Length(max = 20)
    private String phoneNumber;

    @Length(max = 50)
    private String openId;

    @Length(max = 50)
    private String mainOpenId;

    @Length(max = 50)
    private String alipayUserId;

    public Long getVipId() {
        return vipId;
    }

    public void setVipId(Long vipId) {
        this.vipId = vipId;
    }

    public Long getVipTypeId() {
        return vipTypeId;
    }

    public void setVipTypeId(Long vipTypeId) {
        this.vipTypeId = vipTypeId;
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
