package build.dream.catering.tasks;

import build.dream.catering.constants.Constants;
import build.dream.catering.services.FlashSaleService;
import build.dream.common.utils.CommonRedisUtils;
import build.dream.common.utils.JacksonUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SaveFlashSaleOrderTask implements Runnable {
    private FlashSaleService flashSaleService;

    public SaveFlashSaleOrderTask(FlashSaleService flashSaleService) {
        this.flashSaleService = flashSaleService;
    }

    @Override
    public void run() {
        while (true) {
            try {
                String orderJson = CommonRedisUtils.brpop(Constants.KEY_FLASH_SALE_ORDERS, 60, TimeUnit.SECONDS);
                if (StringUtils.isNotBlank(orderJson)) {
                    Map<String, Object> orderInfo = JacksonUtils.readValueAsMap(orderJson, String.class, Object.class);
                    BigInteger tenantId = BigInteger.valueOf(MapUtils.getLongValue(orderInfo, "tenantId"));
                    String tenantCode = MapUtils.getString(orderInfo, "tenantCode");
                    BigInteger branchId = BigInteger.valueOf(MapUtils.getLongValue(orderInfo, "tenantId"));
                    BigInteger vipId = BigInteger.valueOf(MapUtils.getLongValue(orderInfo, "tenantId"));
                    BigInteger activityId = BigInteger.valueOf(MapUtils.getLongValue(orderInfo, "tenantId"));
                    String uuid = MapUtils.getString(orderInfo, "uuid");
                    flashSaleService.saveFlashSaleOrder(tenantId, tenantCode, branchId, vipId, activityId, uuid);
                    System.out.println(orderJson);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void start() {
        new Thread(this).start();
    }
}
