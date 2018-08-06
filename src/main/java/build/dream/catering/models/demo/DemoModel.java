package build.dream.catering.models.demo;

import build.dream.common.constraints.InList;
import build.dream.common.models.BasicModel;

public class DemoModel extends BasicModel {
    @InList(value = {"a", "b"})
    private String aa;

    public String getAa() {
        return aa;
    }

    public void setAa(String aa) {
        this.aa = aa;
    }
}
