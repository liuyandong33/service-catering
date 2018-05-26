package build.dream.catering.services;

import build.dream.catering.utils.SaleFlowUtils;
import build.dream.common.api.ApiRest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;

@Service
public class DemoService {
    @Transactional(rollbackFor = Exception.class)
    public ApiRest writeSaleFlow(BigInteger dietOrderId) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        SaleFlowUtils.writeSaleFlow(dietOrderId);

        ApiRest apiRest = new ApiRest();
        apiRest.setMessage("写入流水成功！");
        apiRest.setSuccessful(true);
        return apiRest;
    }
}
