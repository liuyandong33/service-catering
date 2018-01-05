package build.dream.erp.services;

import build.dream.common.api.ApiRest;
import build.dream.common.utils.ConfigurationUtils;
import build.dream.common.utils.QueueUtils;
import build.dream.erp.constants.Constants;
import build.dream.erp.models.data.UploadDataModel;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class DataService {
    public ApiRest uploadData(UploadDataModel uploadDataModel) throws IOException {
        String deploymentEnvironment = ConfigurationUtils.getConfiguration(Constants.DEPLOYMENT_ENVIRONMENT);
        String partitionCode = ConfigurationUtils.getConfiguration(Constants.PARTITION_CODE);

        String key = null;
        String dataType = uploadDataModel.getDataType();
        if (Constants.DATA_TYPE_DIET_ORDER.equals(dataType)) {
            key = deploymentEnvironment + "_" + partitionCode + "_" + Constants.DIET_ORDER;
        }

        QueueUtils.rpush(key, uploadDataModel.getData());

        ApiRest apiRest = new ApiRest();
        apiRest.setMessage("数据上传成功！");
        apiRest.setSuccessful(true);
        return apiRest;
    }
}
