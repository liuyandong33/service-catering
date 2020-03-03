package build.dream.catering.models.pos;

import build.dream.common.models.CateringBasicModel;

import javax.validation.constraints.NotNull;

public class ObtainMqttInfoModel extends CateringBasicModel {
    @NotNull
    private Long posId;

    public Long getPosId() {
        return posId;
    }

    public void setPosId(Long posId) {
        this.posId = posId;
    }
}
