package build.dream.catering.services;

import build.dream.catering.models.purchase.SavePurchaseOrderModel;
import build.dream.catering.utils.SequenceUtils;
import build.dream.common.api.ApiRest;
import build.dream.common.catering.domains.PurchaseOrder;
import build.dream.common.catering.domains.PurchaseOrderDetail;
import build.dream.common.utils.DatabaseHelper;
import build.dream.common.utils.SerialNumberGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PurchaseService {
    @Transactional(rollbackFor = Exception.class)
    public ApiRest savePurchaseOrder(SavePurchaseOrderModel savePurchaseOrderModel) {
        BigInteger tenantId = savePurchaseOrderModel.obtainTenantId();
        String tenantCode = savePurchaseOrderModel.obtainTenantCode();
        BigInteger branchId = savePurchaseOrderModel.obtainBranchId();
        BigInteger userId = savePurchaseOrderModel.obtainUserId();
        String remark = savePurchaseOrderModel.getRemark();
        List<SavePurchaseOrderModel.Detail> details = savePurchaseOrderModel.getDetails();

        String sequenceName = SerialNumberGenerator.generatorTodaySequenceName(tenantId, branchId, "purchase_order");
        String orderNumber = SerialNumberGenerator.nextOrderNumber("JH", 4, SequenceUtils.nextValue(sequenceName));

        PurchaseOrder purchaseOrder = PurchaseOrder.builder()
                .tenantId(tenantId)
                .tenantCode(tenantCode)
                .branchId(branchId)
                .orderNumber(orderNumber)
                .originatorUserId(userId)
                .remark(remark)
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
                    .build();
            purchaseOrderDetails.add(purchaseOrderDetail);
        }
        DatabaseHelper.insertAll(purchaseOrderDetails);

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("purchaseOrder", purchaseOrder);
        data.put("purchaseOrderDetails", purchaseOrderDetails);
        return ApiRest.builder().data(data).message("保存进货单成功！").successful(true).build();
    }
}
