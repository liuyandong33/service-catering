package build.dream.catering.models.pos;

import build.dream.catering.constants.Constants;
import build.dream.common.constraints.InList;
import build.dream.common.models.CateringBasicModel;
import build.dream.common.utils.ApplicationHandler;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;

public class OnlinePosModel extends CateringBasicModel {
    /**
     * 设备ID，mac地址
     */
    @NotNull
    private String deviceId;

    /**
     * 类型，android-android pos, ios-ios pos, windows-windows pos
     */
    @NotNull
    @InList(value = {Constants.POS_TYPE_ANDROID, Constants.POS_TYPE_IOS, Constants.POS_TYPE_WINDOWS})
    private String type;

    /**
     * 版本
     */
    @NotNull
    private String version;

    /**
     * 阿里云推送服务设备ID
     */
    private String cloudPushDeviceId;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCloudPushDeviceId() {
        return cloudPushDeviceId;
    }

    public void setCloudPushDeviceId(String cloudPushDeviceId) {
        this.cloudPushDeviceId = cloudPushDeviceId;
    }

    @Override
    public boolean validate() {
        return Constants.POS_TYPE_WINDOWS.equals(type) ? super.validate() : StringUtils.isNotBlank(cloudPushDeviceId) && super.validate();
    }

    @Override
    public void validateAndThrow() {
        super.validateAndThrow();
        if (!Constants.POS_TYPE_WINDOWS.equals(type)) {
            ApplicationHandler.notBlank(cloudPushDeviceId, "cloudPushDeviceId");
        }
    }
}
