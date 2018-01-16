package build.dream.catering.services;

import build.dream.common.api.ApiRest;
import build.dream.common.utils.ConfigurationUtils;
import build.dream.common.utils.GsonUtils;
import build.dream.common.utils.QueueUtils;
import build.dream.catering.constants.Constants;
import build.dream.catering.mappers.DietOrderDetailMapper;
import build.dream.catering.mappers.DietOrderMapper;
import build.dream.catering.models.data.DietOrderDataModel;
import build.dream.catering.models.data.UploadDataModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
public class DataService {
    @Autowired
    private DietOrderMapper dietOrderMapper;
    @Autowired
    private DietOrderDetailMapper dietOrderDetailMapper;
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

    @Transactional(rollbackFor = Exception.class)
    public void saveDietOrder(String dietOrderData) {
        DietOrderDataModel dietOrderDataModel = GsonUtils.fromJson(dietOrderData, DietOrderDataModel.class);
        dietOrderDataModel.handleData();
        dietOrderMapper.insert(dietOrderDataModel.getDietOrder());
        dietOrderDetailMapper.insertAll(dietOrderDataModel.getDietOrderDetails());
    }
}
