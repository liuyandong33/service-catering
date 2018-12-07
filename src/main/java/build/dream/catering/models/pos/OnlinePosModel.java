package build.dream.catering.models.pos;

import build.dream.common.models.CateringBasicModel;

import javax.validation.constraints.NotNull;

public class OnlinePosModel extends CateringBasicModel {
    @NotNull
    private String deviceId;
    @NotNull
    private String type;
    @NotNull
    private String version;

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
}
