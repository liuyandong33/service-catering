package build.dream.catering.services;

import build.dream.catering.models.pos.InitPosModel;
import build.dream.common.api.ApiRest;
import build.dream.common.utils.GsonUtils;
import build.dream.common.utils.LogUtils;
import org.springframework.stereotype.Service;

@Service
public class PosService {
    public ApiRest initPos(InitPosModel initPosModel) {
        LogUtils.info(GsonUtils.toJson(initPosModel));
        ApiRest apiRest = new ApiRest();
        apiRest.setMessage("初始化POS成功！");
        apiRest.setSuccessful(true);
        return apiRest;
    }
}
