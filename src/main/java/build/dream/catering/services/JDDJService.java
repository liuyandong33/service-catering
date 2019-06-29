package build.dream.catering.services;

import build.dream.common.catering.domains.DietOrder;
import build.dream.common.constants.DietOrderConstants;
import build.dream.common.utils.DatabaseHelper;
import build.dream.common.utils.ValidateUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@Service
public class JDDJService {
    @Transactional
    public void handleNewOrder() {
        Map<String, Object> resultMap = null;
        Map<String, Object> result = MapUtils.getMap(resultMap, "result");
        List<Map<String, Object>> resultList = (List<Map<String, Object>>) result.get("resultList");
        ValidateUtils.isTrue(resultList.size() == 1, "订单不存在！");

        Map<String, Object> orderInfo = resultList.get(0);

        DietOrder dietOrder = DietOrder.builder()
                .tenantId(BigInteger.ZERO)
                .tenantCode("")
                .branchId(BigInteger.ZERO)
                .orderNumber("JDDJ" + MapUtils.getLong(orderInfo, "orderId"))
                .orderType(5)
                .orderStatus(DietOrderConstants.ORDER_STATUS_UNPROCESSED)
                .payStatus(DietOrderConstants.PAY_STATUS_PAID)
                .refundStatus(DietOrderConstants.REFUND_STATUS_NO_REFUND)
                .build();

        DatabaseHelper.insert(dietOrder);
    }
}
