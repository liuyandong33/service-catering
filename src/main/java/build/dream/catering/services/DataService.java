package build.dream.catering.services;

import build.dream.catering.constants.Constants;
import build.dream.catering.mappers.DataHandleHistoryMapper;
import build.dream.catering.mappers.DietOrderDetailMapper;
import build.dream.catering.mappers.DietOrderMapper;
import build.dream.catering.models.data.DietOrderDataModel;
import build.dream.catering.models.data.UploadDataModel;
import build.dream.common.api.ApiRest;
import build.dream.common.erp.catering.domains.DataHandleHistory;
import build.dream.common.utils.CacheUtils;
import build.dream.common.utils.ConfigurationUtils;
import build.dream.common.utils.GsonUtils;
import org.apache.commons.codec.digest.DigestUtils;
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
    @Autowired
    private DataHandleHistoryMapper dataHandleHistoryMapper;

    public ApiRest uploadData(UploadDataModel uploadDataModel) throws IOException {
        String deploymentEnvironment = ConfigurationUtils.getConfiguration(Constants.DEPLOYMENT_ENVIRONMENT);
        String partitionCode = ConfigurationUtils.getConfiguration(Constants.PARTITION_CODE);

        String key = null;
        String dataType = uploadDataModel.getDataType();
        if (Constants.DATA_TYPE_DIET_ORDER.equals(dataType)) {
            key = deploymentEnvironment + "_" + partitionCode + "_" + Constants.DIET_ORDER;
        }

        String data = uploadDataModel.getData();
        String signature = DigestUtils.md5Hex(data);
        CacheUtils.hset(key, signature, data);

        ApiRest apiRest = new ApiRest();
        apiRest.setMessage("数据上传成功！");
        apiRest.setSuccessful(true);
        return apiRest;
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveDietOrder(String dietOrderData) {
        String signature = DigestUtils.md5Hex(dietOrderData);
        if (!CacheUtils.hexists(Constants.KEY_DATA_HANDLE_SIGNATURES, signature)) {
            DataHandleHistory dataHandleHistory = new DataHandleHistory();
            dataHandleHistory.setSignature(signature);
            dataHandleHistory.setDataType(Constants.DIET_ORDER);
            dataHandleHistory.setDataContent(dietOrderData);
            dataHandleHistoryMapper.insert(dataHandleHistory);
            CacheUtils.hset(Constants.KEY_DATA_HANDLE_SIGNATURES, signature, signature);
//            DietOrderDataModel dietOrderDataModel = GsonUtils.fromJson(dietOrderData, DietOrderDataModel.class);
//            dietOrderDataModel.handleData();
//            dietOrderMapper.insert(dietOrderDataModel.getDietOrder());
//            dietOrderDetailMapper.insertAll(dietOrderDataModel.getDietOrderDetails());
        }
    }
}
