package build.dream.catering.services;

import build.dream.catering.constants.Constants;
import build.dream.catering.models.flashsale.ObtainAllFlashSaleActivitiesModel;
import build.dream.catering.models.flashsale.SaveFlashSaleActivityModel;
import build.dream.common.api.ApiRest;
import build.dream.common.catering.domains.DietOrder;
import build.dream.common.catering.domains.DietOrderDetail;
import build.dream.common.catering.domains.DietOrderGroup;
import build.dream.common.catering.domains.FlashSaleActivity;
import build.dream.common.constants.DietOrderConstants;
import build.dream.common.utils.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class FlashSaleService {
    /**
     * 保存秒杀活动
     *
     * @param saveFlashSaleActivityModel
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiRest saveFlashSaleActivity(SaveFlashSaleActivityModel saveFlashSaleActivityModel) {
        BigInteger tenantId = saveFlashSaleActivityModel.obtainTenantId();
        String tenantCode = saveFlashSaleActivityModel.obtainTenantCode();
        BigInteger branchId = saveFlashSaleActivityModel.obtainBranchId();
        BigInteger userId = saveFlashSaleActivityModel.obtainUserId();
        BigInteger goodsId = saveFlashSaleActivityModel.getGoodsId();
        String goodsName = saveFlashSaleActivityModel.getGoodsName();
        String imageUrl = saveFlashSaleActivityModel.getImageUrl();
        String name = saveFlashSaleActivityModel.getName();
        Date startTime = saveFlashSaleActivityModel.getStartTime();
        Date endTime = saveFlashSaleActivityModel.getEndTime();
        boolean limited = saveFlashSaleActivityModel.getLimited();
        BigDecimal limitQuantity = saveFlashSaleActivityModel.getLimitQuantity();
        Integer beforeShowTime = saveFlashSaleActivityModel.getBeforeShowTime();
        Integer timeUnit = saveFlashSaleActivityModel.getTimeUnit();
        BigDecimal originalPrice = saveFlashSaleActivityModel.getOriginalPrice();
        BigDecimal flashSalePrice = saveFlashSaleActivityModel.getFlashSalePrice();
        BigDecimal flashSaleStock = saveFlashSaleActivityModel.getFlashSaleStock();
        String description = saveFlashSaleActivityModel.getDescription();

        FlashSaleActivity flashSaleActivity = FlashSaleActivity.builder()
                .tenantId(tenantId)
                .tenantCode(tenantCode)
                .branchId(branchId)
                .name(name)
                .status(0)
                .startTime(startTime)
                .endTime(endTime)
                .limited(limited)
                .limitQuantity(limitQuantity)
                .beforeShowTime(beforeShowTime)
                .timeUnit(timeUnit)
                .goodsId(goodsId)
                .goodsName(goodsName)
                .imageUrl(imageUrl)
                .originalPrice(originalPrice)
                .flashSalePrice(flashSalePrice)
                .flashSaleStock(flashSaleStock)
                .description(description)
                .createdUserId(userId)
                .updatedUserId(userId)
                .updatedRemark("保存秒杀活动")
                .build();
        DatabaseHelper.insert(flashSaleActivity);

        BigInteger flashSaleActivityId = flashSaleActivity.getId();
        String flashSaleActivityKey = Constants.KEY_FLASH_SALE_ACTIVITY + "_" + tenantId + "_" + branchId + "_" + flashSaleActivityId;
        long timeout = (endTime.getTime() - new Date().getTime()) / 1000;

        String flashSaleStockKey = Constants.KEY_FLASH_SALE_STOCK + "_" + tenantId + "_" + branchId + "_" + flashSaleActivityId;

        RedisUtils.setex(flashSaleActivityKey, GsonUtils.toJson(flashSaleActivity), timeout, TimeUnit.SECONDS);
        RedisUtils.setex(flashSaleStockKey, flashSaleStock.toString(), timeout, TimeUnit.SECONDS);
        RedisUtils.hset(Constants.KEY_FLASH_SALE_ACTIVITY_IDS + "_" + tenantId + "_" + branchId, flashSaleActivityId.toString(), flashSaleActivityId.toString());

        return ApiRest.builder().data(flashSaleActivity).message("保存秒杀活动成功！").successful(true).build();
    }

    /**
     * 获取所有秒杀活动
     *
     * @param obtainAllFlashSaleActivitiesModel
     * @return
     */
    public ApiRest obtainAllFlashSaleActivities(ObtainAllFlashSaleActivitiesModel obtainAllFlashSaleActivitiesModel) {
        BigInteger tenantId = obtainAllFlashSaleActivitiesModel.obtainTenantId();
        BigInteger branchId = obtainAllFlashSaleActivitiesModel.obtainBranchId();
        BigInteger vipId = obtainAllFlashSaleActivitiesModel.getVipId();

        Map<String, String> flashSaleActivityIdsMap = RedisUtils.hgetAll(Constants.KEY_FLASH_SALE_ACTIVITY_IDS + "_" + tenantId + "_" + branchId);
        Set<String> flashSaleActivityIds = flashSaleActivityIdsMap.keySet();

        List<FlashSaleActivity> flashSaleActivities = new ArrayList<FlashSaleActivity>();
        Map<String, Object> flashSaleStocks = new HashMap<String, Object>();
        for (String flashSaleActivityId : flashSaleActivityIds) {
            String flashSaleActivityJson = RedisUtils.get(Constants.KEY_FLASH_SALE_ACTIVITY + "_" + tenantId + "_" + branchId + "_" + flashSaleActivityId);
            String flashSaleStock = RedisUtils.get(Constants.KEY_FLASH_SALE_STOCK + "_" + tenantId + "_" + branchId + "_" + flashSaleActivityId);
            if (StringUtils.isBlank(flashSaleActivityJson)) {
                RedisUtils.hdel(Constants.KEY_FLASH_SALE_ACTIVITY_IDS + "_" + tenantId + "_" + branchId, flashSaleActivityId);
            } else {
                flashSaleActivities.add(JacksonUtils.readValue(flashSaleActivityJson, FlashSaleActivity.class));
                flashSaleStocks.put("_" + flashSaleActivityId, flashSaleStock);
            }
        }

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("flashSaleActivities", flashSaleActivities);
        data.put("flashSaleStocks", flashSaleStocks);
        return ApiRest.builder().data(data).message("获取秒杀活动列表成功！").successful(true).build();
    }

    /**
     * 保存秒杀订单
     *
     * @param tenantId
     * @param tenantCode
     * @param branchId
     * @param vipId
     * @param activityId
     * @param uuid
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveFlashSaleOrder(BigInteger tenantId, String tenantCode, BigInteger branchId, BigInteger vipId, BigInteger activityId, String uuid) {
        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition(FlashSaleActivity.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        searchModel.addSearchCondition(FlashSaleActivity.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        searchModel.addSearchCondition(FlashSaleActivity.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, activityId);
        FlashSaleActivity flashSaleActivity = DatabaseHelper.find(FlashSaleActivity.class, searchModel);
        ValidateUtils.notNull(flashSaleActivity, "秒杀活动不存在！");

        String orderNumber = "";
        int orderType = 1;
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal discountAmount = BigDecimal.ZERO;
        BigDecimal payableAmount = BigDecimal.ZERO;
        BigDecimal paidAmount = BigDecimal.ZERO;
        BigDecimal deliverFee = BigDecimal.ZERO;
        String daySerialNumber = "";
        BigInteger userId = BigInteger.ZERO;
        BigInteger goodsId = BigInteger.ZERO;
        String goodsName = "";
        BigInteger goodsSpecificationId = BigInteger.ZERO;
        String goodsSpecificationName = "";
        BigInteger categoryId = BigInteger.ZERO;
        String categoryName = "";

        DietOrder dietOrder = DietOrder.builder()
                .tenantId(tenantId)
                .branchId(branchId)
                .tenantCode(tenantCode)
                .orderNumber(orderNumber)
                .orderType(orderType)
                .orderStatus(DietOrderConstants.ORDER_STATUS_PENDING)
                .payStatus(DietOrderConstants.PAY_STATUS_UNPAID)
                .refundStatus(DietOrderConstants.REFUND_STATUS_NO_REFUND)
                .totalAmount(totalAmount)
                .discountAmount(discountAmount)
                .payableAmount(payableAmount)
                .paidAmount(paidAmount)
                .paidType(Constants.TINYINT_DEFAULT_VALUE)
                .remark(Constants.VARCHAR_DEFAULT_VALUE)
                .deliveryAddress(Constants.VARCHAR_DEFAULT_VALUE)
                .deliveryLongitude(Constants.VARCHAR_DEFAULT_VALUE)
                .deliveryLatitude(Constants.VARCHAR_DEFAULT_VALUE)
                .deliverTime(Constants.DATETIME_DEFAULT_VALUE)
                .activeTime(Constants.DATETIME_DEFAULT_VALUE)
                .deliverFee(deliverFee)
                .telephoneNumber(Constants.VARCHAR_DEFAULT_VALUE)
                .daySerialNumber(daySerialNumber)
                .consignee(Constants.VARCHAR_DEFAULT_VALUE)
                .invoiced(false)
                .invoiceType(Constants.VARCHAR_DEFAULT_VALUE)
                .invoice(Constants.VARCHAR_DEFAULT_VALUE)
                .vipId(vipId)
                .localId(Constants.VARCHAR_DEFAULT_VALUE)
                .localCreatedTime(Constants.DATETIME_DEFAULT_VALUE)
                .localUpdatedTime(Constants.DATETIME_DEFAULT_VALUE)
                .createdUserId(userId)
                .updatedUserId(userId)
                .build();
        DatabaseHelper.insert(dietOrder);

        BigInteger dietOrderId = dietOrder.getId();
        DietOrderGroup dietOrderGroup = DietOrderGroup.builder()
                .tenantId(tenantId)
                .tenantCode(tenantCode)
                .branchId(branchId)
                .dietOrderId(dietOrderId)
                .name("普通菜品")
                .type(DietOrderConstants.GROUP_TYPE_NORMAL)
                .localId(Constants.VARCHAR_DEFAULT_VALUE)
                .localDietOrderId(Constants.VARCHAR_DEFAULT_VALUE)
                .localCreatedTime(Constants.DATETIME_DEFAULT_VALUE)
                .localUpdatedTime(Constants.DATETIME_DEFAULT_VALUE)
                .createdUserId(userId)
                .updatedUserId(userId)
                .build();
        DatabaseHelper.insert(dietOrderGroup);

        DietOrderDetail dietOrderDetail = DietOrderDetail.builder()
                .tenantId(tenantId)
                .tenantCode(tenantCode)
                .branchId(branchId)
                .dietOrderId(dietOrderId)
                .dietOrderGroupId(dietOrderGroup.getId())
                .goodsType(Constants.GOODS_TYPE_ORDINARY_GOODS)
                .goodsId(goodsId)
                .goodsName(goodsName)
                .goodsSpecificationId(goodsSpecificationId)
                .goodsSpecificationName(goodsSpecificationName)
                .packageId(Constants.BIGINT_DEFAULT_VALUE)
                .packageGroupId(Constants.BIGINT_DEFAULT_VALUE)
                .packageGroupName(Constants.VARCHAR_DEFAULT_VALUE)
                .categoryId(categoryId)
                .categoryName(categoryName)
                .price(BigDecimal.ZERO)
                .attributeIncrease(Constants.DECIMAL_DEFAULT_VALUE)
                .quantity(Constants.BIG_DECIMAL_ONE)
                .totalAmount(Constants.BIG_DECIMAL_ONE)
                .discountAmount(Constants.BIG_DECIMAL_ONE)
                .payableAmount(Constants.BIG_DECIMAL_ONE)
                .localId(Constants.VARCHAR_DEFAULT_VALUE)
                .localDietOrderId(Constants.VARCHAR_DEFAULT_VALUE)
                .localDietOrderGroupId(Constants.VARCHAR_DEFAULT_VALUE)
                .localCreatedTime(Constants.DATETIME_DEFAULT_VALUE)
                .localUpdatedTime(Constants.DATETIME_DEFAULT_VALUE)
                .createdUserId(userId)
                .updatedUserId(userId)
                .build();
        DatabaseHelper.insert(dietOrderDetail);
        RedisUtils.setex(uuid, GsonUtils.toJson(dietOrder), 10, TimeUnit.MINUTES);
    }
}
