package build.dream.catering.models.data;

import build.dream.common.constraints.InList;
import build.dream.common.models.BasicModel;
import build.dream.catering.constants.Constants;

import javax.validation.constraints.NotNull;

public class UploadDataModel extends BasicModel {
    @NotNull
    private String data;

    @NotNull
    @InList(value = {Constants.DATA_TYPE_DIET_ORDER})
    private String dataType;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
}
