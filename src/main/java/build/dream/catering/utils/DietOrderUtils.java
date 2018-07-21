package build.dream.catering.utils;

import build.dream.common.erp.catering.domains.DietOrderDetail;
import org.apache.commons.collections.CollectionUtils;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DietOrderUtils {
    private static final ConcurrentHashMap<String, Timer> CONCURRENT_HASH_MAP = new ConcurrentHashMap<String, Timer>();

    public static void startTimer(String orderNumber) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                CONCURRENT_HASH_MAP.remove(orderNumber);
                timer.cancel();
            }
        }, 0, 1000);
        CONCURRENT_HASH_MAP.put(orderNumber, timer);
    }

    public static void stopTimer(String orderNumber) {
        Timer timer = CONCURRENT_HASH_MAP.remove(orderNumber);
        if (timer != null) {
            timer.cancel();
        }
    }

    public static Map<BigInteger, List<DietOrderDetail>> splitDietOrderDetails(List<DietOrderDetail> dietOrderDetails) {
        Map<BigInteger, List<DietOrderDetail>> dietOrderDetailMap = new HashMap<BigInteger, List<DietOrderDetail>>();
        for (DietOrderDetail dietOrderDetail : dietOrderDetails) {
            BigInteger dietOrderGroupId = dietOrderDetail.getDietOrderGroupId();
            List<DietOrderDetail> dietOrderDetailList = dietOrderDetailMap.get(dietOrderGroupId);
            if (CollectionUtils.isEmpty(dietOrderDetailList)) {
                dietOrderDetailList = new ArrayList<DietOrderDetail>();
                dietOrderDetailMap.put(dietOrderGroupId, dietOrderDetailList);
            }
            dietOrderDetailList.add(dietOrderDetail);
        }
        return dietOrderDetailMap;
    }
}
