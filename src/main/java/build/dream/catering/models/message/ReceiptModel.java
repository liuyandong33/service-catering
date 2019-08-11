package build.dream.catering.models.message;

import build.dream.common.models.BasicModel;

import javax.validation.constraints.NotNull;

public class ReceiptModel extends BasicModel {
    @NotNull
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
