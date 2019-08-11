package build.dream.catering.services;

import build.dream.catering.models.message.ReceiptModel;
import build.dream.common.api.ApiRest;
import build.dream.common.utils.CommonRedisUtils;
import org.springframework.stereotype.Service;

@Service
public class MessageService {
    public ApiRest receipt(ReceiptModel receiptModel) {
        String uuid = receiptModel.getUuid();
        CommonRedisUtils.del(uuid);
        return ApiRest.builder().message("回执成功！").successful(true).build();
    }
}
