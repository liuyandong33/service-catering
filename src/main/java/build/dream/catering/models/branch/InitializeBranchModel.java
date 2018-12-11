package build.dream.catering.models.branch;

import build.dream.catering.constants.Constants;
import build.dream.common.models.BasicModel;
import build.dream.common.utils.ApplicationHandler;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

public class InitializeBranchModel extends BasicModel {
    private static final Integer[] TYPES = {Constants.BRANCH_TYPE_HEADQUARTERS, Constants.BRANCH_TYPE_DIRECT_SALE_STORE, Constants.BRANCH_TYPE_FRANCHISE_STORE};
    private static final Integer[] BRANCH_STATUS = {Constants.BRANCH_STATUS_ENABLED, Constants.BRANCH_STATUS_DISABLED};
    @NotNull
    private BigInteger tenantId;

    @NotNull
    @Length(max = 20)
    private String tenantCode;

    private Integer type;

    private Integer status;

    @NotNull
    @Length(max = 20)
    private String name;

    @NotNull
    @Length(max = 10)
    private String provinceCode;

    @NotNull
    @Length(max = 10)
    private String provinceName;

    @NotNull
    @Length(max = 10)
    private String cityCode;

    @NotNull
    @Length(max = 10)
    private String cityName;

    @NotNull
    @Length(max = 10)
    private String districtCode;

    @NotNull
    @Length(max = 10)
    private String districtName;

    @NotNull
    @Length(max = 255)
    private String address;

    @NotNull
    @Length(max = 20)
    private String longitude;

    @NotNull
    @Length(max = 20)
    private String latitude;

    @NotNull
    @Length(max = 20)
    private String linkman;

    @NotNull
    @Length(max = 20)
    private String contactPhone;

    @NotNull
    private Integer smartRestaurantStatus;

    @NotNull
    private BigInteger currentUserId;

    @NotNull
    private BigInteger userId;

    public static Integer[] getBranchStatus() {
        return BRANCH_STATUS;
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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getDistrictCode() {
        return districtCode;
    }

    public void setDistrictCode(String districtCode) {
        this.districtCode = districtCode;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLinkman() {
        return linkman;
    }

    public void setLinkman(String linkman) {
        this.linkman = linkman;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public Integer getSmartRestaurantStatus() {
        return smartRestaurantStatus;
    }

    public void setSmartRestaurantStatus(Integer smartRestaurantStatus) {
        this.smartRestaurantStatus = smartRestaurantStatus;
    }

    public BigInteger getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(BigInteger currentUserId) {
        this.currentUserId = currentUserId;
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
        ApplicationHandler.inArray(TYPES, type, "type");
        ApplicationHandler.inArray(BRANCH_STATUS, status, "status");
    }
}
