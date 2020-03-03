package build.dream.catering.tasks;

import build.dream.catering.services.FlashSaleService;

public class SaveFlashSaleOrderTask implements Runnable {
    private FlashSaleService flashSaleService;

    public SaveFlashSaleOrderTask(FlashSaleService flashSaleService) {
        this.flashSaleService = flashSaleService;
    }

    @Override
    public void run() {
        /*while (true) {
            try {
                String orderJson = CommonRedisUtils.brpop(Constants.KEY_FLASH_SALE_ORDERS, 60, TimeUnit.SECONDS);
                if (StringUtils.isNotBlank(orderJson)) {
                    Map<String, Object> orderInfo = JacksonUtils.readValueAsMap(orderJson, String.class, Object.class);
                    Long tenantId = MapUtils.getLongValue(orderInfo, "tenantId");
                    String tenantCode = MapUtils.getString(orderInfo, "tenantCode");
                    Long branchId = MapUtils.getLongValue(orderInfo, "tenantId");
                    Long vipId = MapUtils.getLongValue(orderInfo, "tenantId");
                    Long activityId = MapUtils.getLongValue(orderInfo, "tenantId");
                    String uuid = MapUtils.getString(orderInfo, "uuid");
                    flashSaleService.saveFlashSaleOrder(tenantId, tenantCode, branchId, vipId, activityId, uuid);
                    System.out.println(orderJson);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
    }

    public void start() {
        new Thread(this).start();
    }
}
