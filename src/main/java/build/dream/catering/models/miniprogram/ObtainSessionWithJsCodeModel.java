package build.dream.catering.models.miniprogram;

import build.dream.common.models.BasicModel;

import javax.validation.constraints.NotNull;

public class ObtainSessionWithJsCodeModel extends BasicModel {
    @NotNull
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
