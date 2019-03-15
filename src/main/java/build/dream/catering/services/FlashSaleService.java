package build.dream.catering.services;

import build.dream.catering.constants.Constants;
import build.dream.common.catering.domains.DietOrder;
import build.dream.common.catering.domains.DietOrderDetail;
import build.dream.common.catering.domains.DietOrderGroup;
import build.dream.common.constants.DietOrderConstants;
import build.dream.common.utils.DatabaseHelper;
import build.dream.common.utils.GsonUtils;
import build.dream.common.utils.RedisUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

@Service
public class FlashSaleService {
    @Transactional(rollbackFor = Exception.class)
    public void saveFlashSaleOrder(BigInteger tenantId, String tenantCode, BigInteger branchId, BigInteger vipId, BigInteger activityId, String uuid) {
        System.out.println("开始保存订单！");

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
