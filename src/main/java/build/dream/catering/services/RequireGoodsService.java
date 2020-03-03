package build.dream.catering.services;

import build.dream.catering.constants.Constants;
import build.dream.catering.models.requiregoods.AuditRequireGoodsOrderModel;
import build.dream.catering.models.requiregoods.ObtainRequireGoodsOrderModel;
import build.dream.catering.models.requiregoods.SaveRequireGoodsOrderModel;
import build.dream.catering.utils.SequenceUtils;
import build.dream.common.api.ApiRest;
import build.dream.common.domains.catering.RequireGoodsOrder;
import build.dream.common.domains.catering.RequireGoodsOrderDetail;
import build.dream.common.utils.DatabaseHelper;
import build.dream.common.utils.SearchModel;
import build.dream.common.utils.SerialNumberGenerator;
import build.dream.common.utils.ValidateUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RequireGoodsService {
    /**
     * 保存要货单
     *
     * @param saveRequireGoodsOrderModel
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiRest saveRequireGoodsOrder(SaveRequireGoodsOrderModel saveRequireGoodsOrderModel) {
        Long tenantId = saveRequireGoodsOrderModel.obtainTenantId();
        String tenantCode = saveRequireGoodsOrderModel.obtainTenantCode();
        Long branchId = saveRequireGoodsOrderModel.obtainBranchId();
        Long distributionCenterId = saveRequireGoodsOrderModel.getDistributionCenterId();
        Long userId = saveRequireGoodsOrderModel.obtainUserId();
        String remark = saveRequireGoodsOrderModel.getRemark();

        String sequenceName = SerialNumberGenerator.generatorTodaySequenceName(tenantId, branchId, "require_goods_order_number");
        String orderNumber = SerialNumberGenerator.nextOrderNumber("YH", 8, SequenceUtils.nextValue(sequenceName));

        RequireGoodsOrder requireGoodsOrder = RequireGoodsOrder.builder()
                .tenantId(tenantId)
                .tenantCode(tenantCode)
                .branchId(branchId)
                .distributionCenterId(distributionCenterId)
                .orderNumber(orderNumber)
                .originatorUserId(userId)
                .auditorUserId(Constants.BIGINT_DEFAULT_VALUE)
                .auditTime(Constants.DATETIME_DEFAULT_VALUE)
                .remark(remark)
                .status(1)
                .createdUserId(userId)
                .updatedUserId(userId)
                .updatedRemark("创建要货单！")
                .build();
        DatabaseHelper.insert(requireGoodsOrder);

        return ApiRest.builder().data(requireGoodsOrder).message("保存要货单成功！").successful(true).build();
    }

    /**
     * 获取要货单
     *
     * @param obtainRequireGoodsOrderModel
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiRest obtainRequireGoodsOrder(ObtainRequireGoodsOrderModel obtainRequireGoodsOrderModel) {
        Long tenantId = obtainRequireGoodsOrderModel.obtainTenantId();
        Long branchId = obtainRequireGoodsOrderModel.obtainBranchId();
        Long requireGoodsOrderId = obtainRequireGoodsOrderModel.getRequireGoodsOrderId();

        SearchModel requireGoodsOrderSearchModel = new SearchModel(true);
        requireGoodsOrderSearchModel.addSearchCondition(RequireGoodsOrder.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        requireGoodsOrderSearchModel.addSearchCondition(RequireGoodsOrder.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        requireGoodsOrderSearchModel.addSearchCondition(RequireGoodsOrder.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, requireGoodsOrderId);
        RequireGoodsOrder requireGoodsOrder = DatabaseHelper.find(RequireGoodsOrder.class, requireGoodsOrderSearchModel);
        ValidateUtils.notNull(requireGoodsOrder, "要货单不存在！");


        SearchModel requireGoodsOrderDetailSearchModel = new SearchModel(true);
        requireGoodsOrderDetailSearchModel.addSearchCondition(RequireGoodsOrderDetail.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        requireGoodsOrderDetailSearchModel.addSearchCondition(RequireGoodsOrderDetail.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        requireGoodsOrderDetailSearchModel.addSearchCondition(RequireGoodsOrderDetail.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, requireGoodsOrderId);
        List<RequireGoodsOrderDetail> requireGoodsOrderDetails = DatabaseHelper.findAll(RequireGoodsOrderDetail.class, requireGoodsOrderDetailSearchModel);

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("requireGoodsOrder", requireGoodsOrder);
        data.put("requireGoodsOrderDetails", requireGoodsOrderDetails);
        return ApiRest.builder().data(data).message("获取要货单成功！").successful(true).build();
    }

    /**
     * 审核要货单
     *
     * @param auditRequireGoodsOrderModel
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiRest auditRequireGoodsOrder(AuditRequireGoodsOrderModel auditRequireGoodsOrderModel) {
        Long tenantId = auditRequireGoodsOrderModel.obtainTenantId();
        Long branchId = auditRequireGoodsOrderModel.obtainBranchId();
        Long userId = auditRequireGoodsOrderModel.obtainUserId();
        Long requireGoodsOrderId = auditRequireGoodsOrderModel.getRequireGoodsOrderId();


        SearchModel requireGoodsOrderSearchModel = new SearchModel(true);
        requireGoodsOrderSearchModel.addSearchCondition(RequireGoodsOrder.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        requireGoodsOrderSearchModel.addSearchCondition(RequireGoodsOrder.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        requireGoodsOrderSearchModel.addSearchCondition(RequireGoodsOrder.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, requireGoodsOrderId);
        RequireGoodsOrder requireGoodsOrder = DatabaseHelper.find(RequireGoodsOrder.class, requireGoodsOrderSearchModel);
        ValidateUtils.notNull(requireGoodsOrder, "要货单不存在！");
        ValidateUtils.isTrue(requireGoodsOrder.getStatus() == 1, "只有未审核状态的要货单才能进行审核操作！");

        requireGoodsOrder.setStatus(2);
        requireGoodsOrder.setAuditTime(new Date());
        requireGoodsOrder.setAuditorUserId(userId);
        requireGoodsOrder.setUpdatedUserId(userId);
        requireGoodsOrder.setUpdatedRemark("审核要货单！");

        DatabaseHelper.update(requireGoodsOrder);

        return ApiRest.builder().message("审核要货单成功！").successful(true).build();
    }
}
