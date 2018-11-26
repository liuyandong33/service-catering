package build.dream.catering.services;

import build.dream.catering.constants.Constants;
import build.dream.catering.models.purchase.ExaminePurchaseOrderModel;
import build.dream.catering.models.purchase.SavePurchaseOrderModel;
import build.dream.catering.utils.GoodsUtils;
import build.dream.catering.utils.SequenceUtils;
import build.dream.common.api.ApiRest;
import build.dream.common.catering.domains.PurchaseOrder;
import build.dream.common.catering.domains.PurchaseOrderDetail;
import build.dream.common.utils.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.*;

@Service
public class PurchaseService {
    /**
     * 保存进货单
     *
     * @param savePurchaseOrderModel
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiRest savePurchaseOrder(SavePurchaseOrderModel savePurchaseOrderModel) {
        BigInteger tenantId = savePurchaseOrderModel.obtainTenantId();
        String tenantCode = savePurchaseOrderModel.obtainTenantCode();
        BigInteger branchId = savePurchaseOrderModel.obtainBranchId();
        BigInteger userId = savePurchaseOrderModel.obtainUserId();
        String remark = savePurchaseOrderModel.getRemark();
        List<SavePurchaseOrderModel.Detail> details = savePurchaseOrderModel.getDetails();

        String sequenceName = SerialNumberGenerator.generatorTodaySequenceName(tenantId, branchId, "purchase_order");
        String orderNumber = SerialNumberGenerator.nextOrderNumber("JH", 8, SequenceUtils.nextValue(sequenceName));

        PurchaseOrder purchaseOrder = PurchaseOrder.builder()
                .tenantId(tenantId)
                .tenantCode(tenantCode)
                .branchId(branchId)
                .orderNumber(orderNumber)
                .originatorUserId(userId)
                .reviewerUserId(Constants.BIGINT_DEFAULT_VALUE)
                .reviewTime(Constants.DATETIME_DEFAULT_VALUE)
                .remark(StringUtils.isNotBlank(remark) ? remark : Constants.VARCHAR_DEFAULT_VALUE)
                .createUserId(userId)
                .lastUpdateUserId(userId)
                .build();

        DatabaseHelper.insert(purchaseOrder);

        BigInteger purchaseOrderId = purchaseOrder.getId();

        List<PurchaseOrderDetail> purchaseOrderDetails = new ArrayList<PurchaseOrderDetail>();
        for (SavePurchaseOrderModel.Detail detail : details) {
            PurchaseOrderDetail purchaseOrderDetail = PurchaseOrderDetail.builder()
                    .tenantId(tenantId)
                    .tenantCode(tenantCode)
                    .branchId(branchId)
                    .purchaseOrderId(purchaseOrderId)
                    .goodsId(detail.getGoodsId())
                    .goodsSpecificationId(detail.getGoodsSpecificationId())
                    .unitId(detail.getUnitId())
                    .purchasePrice(detail.getPurchasePrice())
                    .quantity(detail.getQuantity())
                    .createUserId(userId)
                    .lastUpdateUserId(userId)
                    .build();
            purchaseOrderDetails.add(purchaseOrderDetail);
        }
        DatabaseHelper.insertAll(purchaseOrderDetails);

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("purchaseOrder", purchaseOrder);
        data.put("purchaseOrderDetails", purchaseOrderDetails);
        return ApiRest.builder().data(data).message("保存进货单成功！").successful(true).build();
    }

    /**
     * 审核进货单
     *
     * @param examinePurchaseOrderModel
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiRest examinePurchaseOrder(ExaminePurchaseOrderModel examinePurchaseOrderModel) {
        BigInteger tenantId = examinePurchaseOrderModel.obtainTenantId();
        BigInteger branchId = examinePurchaseOrderModel.obtainBranchId();
        BigInteger userId = examinePurchaseOrderModel.obtainUserId();
        BigInteger purchaseOrderId = examinePurchaseOrderModel.getPurchaseOrderId();

        SearchModel searchModel = new SearchModel();
        searchModel.addSearchCondition(PurchaseOrder.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        searchModel.addSearchCondition(PurchaseOrder.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        searchModel.addSearchCondition(PurchaseOrder.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, purchaseOrderId);
        PurchaseOrder purchaseOrder = DatabaseHelper.find(PurchaseOrder.class, searchModel);
        ValidateUtils.notNull(purchaseOrder, "进货单不存在！");
        ValidateUtils.isTrue(purchaseOrder.getStatus() == 1, "只有未审核状态的进货单才能进行审核操作！");

        purchaseOrder.setReviewerUserId(userId);
        purchaseOrder.setReviewTime(new Date());
        purchaseOrder.setLastUpdateUserId(userId);
        DatabaseHelper.update(purchaseOrder);

        List<PurchaseOrderDetail> purchaseOrderDetails = DatabaseHelper.findAll(PurchaseOrderDetail.class, TupleUtils.buildTuple3(PurchaseOrderDetail.ColumnName.PURCHASE_ORDER_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, purchaseOrderId));
        for (PurchaseOrderDetail purchaseOrderDetail : purchaseOrderDetails) {
            GoodsUtils.addGoodsStock(purchaseOrderDetail.getGoodsId(), purchaseOrderDetail.getGoodsSpecificationId(), purchaseOrderDetail.getQuantity());
        }
        return ApiRest.builder().message("审核进货单成功！").successful(true).build();
    }
}
