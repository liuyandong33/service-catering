package build.dream.catering.services;

import build.dream.catering.constants.Constants;
import build.dream.catering.models.requiregoods.ObtainRequireGoodsOrderModel;
import build.dream.catering.models.requiregoods.SaveRequireGoodsOrderModel;
import build.dream.catering.utils.SequenceUtils;
import build.dream.common.api.ApiRest;
import build.dream.common.catering.domains.RequireGoodsOrder;
import build.dream.common.catering.domains.RequireGoodsOrderDetail;
import build.dream.common.utils.DatabaseHelper;
import build.dream.common.utils.SearchModel;
import build.dream.common.utils.SerialNumberGenerator;
import build.dream.common.utils.ValidateUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
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
        BigInteger tenantId = saveRequireGoodsOrderModel.obtainTenantId();
        String tenantCode = saveRequireGoodsOrderModel.obtainTenantCode();
        BigInteger branchId = saveRequireGoodsOrderModel.obtainBranchId();
        BigInteger distributionCenterId = saveRequireGoodsOrderModel.getDistributionCenterId();
        BigInteger userId = saveRequireGoodsOrderModel.obtainUserId();
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
        BigInteger tenantId = obtainRequireGoodsOrderModel.obtainTenantId();
        BigInteger branchId = obtainRequireGoodsOrderModel.obtainBranchId();
        BigInteger requireGoodsOrderId = obtainRequireGoodsOrderModel.getRequireGoodsOrderId();

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
}
