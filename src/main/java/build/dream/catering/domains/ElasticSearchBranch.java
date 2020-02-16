package build.dream.catering.domains;

import build.dream.catering.constants.Constants;
import build.dream.common.domains.catering.Branch;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Date;

@Document(indexName = "branch", type = "branch")
public class ElasticSearchBranch implements Serializable, Cloneable {
    @Id
    private Long id;

    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = Constants.DEFAULT_DATE_PATTERN)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DEFAULT_DATE_PATTERN)
    private Date createdTime;

    @Field(type = FieldType.Long)
    private Long createdUserId;

    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = Constants.DEFAULT_DATE_PATTERN)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DEFAULT_DATE_PATTERN)
    private Date updatedTime;

    @Field(type = FieldType.Long)
    private Long updatedUserId;

    @Field(type = FieldType.text)
    private String updatedRemark;

    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = Constants.DEFAULT_DATE_PATTERN)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DEFAULT_DATE_PATTERN)
    private Date deletedTime;

    @Field(type = FieldType.Boolean)
    private boolean deleted;

    /**
     * 商户id
     */
    @Field(type = FieldType.Long)
    private Long tenantId;
    /**
     * 商户编码
     */
    @Field(type = FieldType.keyword)
    private String tenantCode;
    /**
     * 门店编码
     */
    @Field(type = FieldType.keyword)
    private String code;
    /**
     * 门店名称
     */
    @Field(type = FieldType.text)
    private String name;
    /**
     * 门店类型，1-总部，2-直营店，3加盟店
     */
    @Field(type = FieldType.Integer)
    private Integer type;
    /**
     * 状态，1-启用，2-停用
     */
    @Field(type = FieldType.Integer)
    private Integer status;
    /**
     * 省编码
     */
    @Field(type = FieldType.keyword)
    private String provinceCode;
    /**
     * 省名称
     */
    @Field(type = FieldType.text)
    private String provinceName;
    /**
     * 市编码
     */
    @Field(type = FieldType.keyword)
    private String cityCode;
    /**
     * 市名称
     */
    @Field(type = FieldType.text)
    private String cityName;
    /**
     * 区编码
     */
    @Field(type = FieldType.keyword)
    private String districtCode;
    /**
     * 区名称
     */
    @Field(type = FieldType.text)
    private String districtName;
    /**
     * 门店详细地址
     */
    @Field(type = FieldType.text)
    private String address;
    /**
     * 经度
     */
    @Field(type = FieldType.text)
    private String longitude;
    /**
     * 纬度
     */
    @Field(type = FieldType.text)
    private String latitude;
    /**
     * 联系人
     */
    @Field(type = FieldType.text)
    private String linkman;
    /**
     * 联系电话
     */
    @Field(type = FieldType.text)
    private String contactPhone;
    /**
     * 饿了么账号类型，1-连锁账号，2-独立账号
     */
    @Field(type = FieldType.Integer)
    private Integer elemeAccountType;
    /**
     * 饿了么门店id
     */
    @Field(type = FieldType.Long)
    private Long shopId;
    /**
     * 微餐厅状态，1-正常，2-禁用
     */
    @Field(type = FieldType.Integer)
    private Integer smartRestaurantStatus;
    /**
     * 美团门店绑定的授权token
     */
    @Field(type = FieldType.text)
    private String appAuthToken;
    /**
     * 美团门店id
     */
    @Field(type = FieldType.text)
    private String poiId;
    /**
     * 美团门店名称
     */
    @Field(type = FieldType.text)
    private String poiName;
    /**
     * 会员分组ID
     */
    @Field(type = FieldType.Long)
    private Long vipGroupId;
    /**
     * 营业时间
     */
    @Field(type = FieldType.text)
    private String businessTimes;

    /**
     * 达达门店ID
     */
    @Field(type = FieldType.text)
    private String dadaOriginShopId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Long getCreatedUserId() {
        return createdUserId;
    }

    public void setCreatedUserId(Long createdUserId) {
        this.createdUserId = createdUserId;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    public Long getUpdatedUserId() {
        return updatedUserId;
    }

    public void setUpdatedUserId(Long updatedUserId) {
        this.updatedUserId = updatedUserId;
    }

    public String getUpdatedRemark() {
        return updatedRemark;
    }

    public void setUpdatedRemark(String updatedRemark) {
        this.updatedRemark = updatedRemark;
    }

    public Date getDeletedTime() {
        return deletedTime;
    }

    public void setDeletedTime(Date deletedTime) {
        this.deletedTime = deletedTime;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getTenantCode() {
        return tenantCode;
    }

    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Integer getElemeAccountType() {
        return elemeAccountType;
    }

    public void setElemeAccountType(Integer elemeAccountType) {
        this.elemeAccountType = elemeAccountType;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public Integer getSmartRestaurantStatus() {
        return smartRestaurantStatus;
    }

    public void setSmartRestaurantStatus(Integer smartRestaurantStatus) {
        this.smartRestaurantStatus = smartRestaurantStatus;
    }

    public String getAppAuthToken() {
        return appAuthToken;
    }

    public void setAppAuthToken(String appAuthToken) {
        this.appAuthToken = appAuthToken;
    }

    public String getPoiId() {
        return poiId;
    }

    public void setPoiId(String poiId) {
        this.poiId = poiId;
    }

    public String getPoiName() {
        return poiName;
    }

    public void setPoiName(String poiName) {
        this.poiName = poiName;
    }

    public Long getVipGroupId() {
        return vipGroupId;
    }

    public void setVipGroupId(Long vipGroupId) {
        this.vipGroupId = vipGroupId;
    }

    public String getBusinessTimes() {
        return businessTimes;
    }

    public void setBusinessTimes(String businessTimes) {
        this.businessTimes = businessTimes;
    }

    public String getDadaOriginShopId() {
        return dadaOriginShopId;
    }

    public void setDadaOriginShopId(String dadaOriginShopId) {
        this.dadaOriginShopId = dadaOriginShopId;
    }

    public static final class FieldName {
        public static final String ID = "id";
        public static final String CREATED_TIME = "createdTime";
        public static final String CREATED_USER_ID = "createdUserId";
        public static final String UPDATED_TIME = "updatedTime";
        public static final String UPDATED_USER_ID = "updatedUserId";
        public static final String UPDATED_REMARK = "updatedRemark";
        public static final String DELETED_TIME = "deletedTime";
        public static final String DELETED = "deleted";
        public static final String TENANT_ID = "tenantId";
        public static final String TENANT_CODE = "tenantCode";
        public static final String CODE = "code";
        public static final String NAME = "name";
        public static final String TYPE = "type";
        public static final String STATUS = "status";
        public static final String PROVINCE_CODE = "provinceCode";
        public static final String PROVINCE_NAME = "provinceName";
        public static final String CITY_CODE = "cityCode";
        public static final String CITY_NAME = "cityName";
        public static final String DISTRICT_CODE = "districtCode";
        public static final String DISTRICT_NAME = "districtName";
        public static final String ADDRESS = "address";
        public static final String LONGITUDE = "longitude";
        public static final String LATITUDE = "latitude";
        public static final String LINKMAN = "linkman";
        public static final String CONTACT_PHONE = "contactPhone";
        public static final String ELEME_ACCOUNT_TYPE = "elemeAccountType";
        public static final String SHOP_ID = "shopId";
        public static final String SMART_RESTAURANT_STATUS = "smartRestaurantStatus";
        public static final String APP_AUTH_TOKEN = "appAuthToken";
        public static final String POI_ID = "poiId";
        public static final String POI_NAME = "poiName";
        public static final String VIP_GROUP_ID = "vipGroupId";
        public static final String BUSINESS_TIMES = "businessTimes";
        public static final String DADA_ORIGIN_SHOP_ID = "dadaOriginShopId";
    }

    public static ElasticSearchBranch build(Branch branch) {
        ElasticSearchBranch elasticSearchBranch = new ElasticSearchBranch();
        elasticSearchBranch.setId(branch.getId().longValue());
        elasticSearchBranch.setCreatedTime(branch.getCreatedTime());
        elasticSearchBranch.setCreatedUserId(branch.getCreatedUserId().longValue());
        elasticSearchBranch.setUpdatedTime(branch.getUpdatedTime());
        elasticSearchBranch.setUpdatedUserId(branch.getUpdatedUserId().longValue());
        elasticSearchBranch.setUpdatedRemark(branch.getUpdatedRemark());
        elasticSearchBranch.setDeletedTime(branch.getDeletedTime());
        elasticSearchBranch.setDeleted(branch.isDeleted());
        elasticSearchBranch.setTenantId(branch.getTenantId().longValue());
        elasticSearchBranch.setTenantCode(branch.getTenantCode());
        elasticSearchBranch.setCode(branch.getCode());
        elasticSearchBranch.setName(branch.getName());
        elasticSearchBranch.setType(branch.getType());
        elasticSearchBranch.setStatus(branch.getStatus());
        elasticSearchBranch.setProvinceCode(branch.getProvinceCode());
        elasticSearchBranch.setProvinceName(branch.getProvinceName());
        elasticSearchBranch.setCityCode(branch.getCityCode());
        elasticSearchBranch.setCityName(branch.getCityName());
        elasticSearchBranch.setDistrictCode(branch.getDistrictCode());
        elasticSearchBranch.setDistrictName(branch.getDistrictName());
        elasticSearchBranch.setAddress(branch.getAddress());
        elasticSearchBranch.setLongitude(branch.getLongitude());
        elasticSearchBranch.setLatitude(branch.getLatitude());
        elasticSearchBranch.setLinkman(branch.getLinkman());
        elasticSearchBranch.setContactPhone(branch.getContactPhone());
        elasticSearchBranch.setElemeAccountType(branch.getElemeAccountType());
        elasticSearchBranch.setShopId(branch.getShopId().longValue());
        elasticSearchBranch.setSmartRestaurantStatus(branch.getSmartRestaurantStatus());
        elasticSearchBranch.setAppAuthToken(branch.getAppAuthToken());
        elasticSearchBranch.setPoiId(branch.getPoiId());
        elasticSearchBranch.setPoiName(branch.getPoiName());
        elasticSearchBranch.setVipGroupId(branch.getVipGroupId().longValue());
        elasticSearchBranch.setBusinessTimes(branch.getBusinessTimes());
        elasticSearchBranch.setDadaOriginShopId(branch.getDadaOriginShopId());
        return elasticSearchBranch;
    }
}
