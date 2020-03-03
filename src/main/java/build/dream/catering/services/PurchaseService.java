package build.dream.catering.services;

import build.dream.catering.constants.Constants;
import build.dream.catering.models.purchase.*;
import build.dream.catering.utils.GoodsUtils;
import build.dream.catering.utils.SequenceUtils;
import build.dream.common.api.ApiRest;
import build.dream.common.domains.catering.PurchaseOrder;
import build.dream.common.domains.catering.PurchaseOrderDetail;
import build.dream.common.domains.catering.StockFlow;
import build.dream.common.tuples.Tuple3;
import build.dream.common.utils.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        Long tenantId = savePurchaseOrderModel.obtainTenantId();
        String tenantCode = savePurchaseOrderModel.obtainTenantCode();
        Long branchId = savePurchaseOrderModel.obtainBranchId();
        Long userId = savePurchaseOrderModel.obtainUserId();
        String remark = savePurchaseOrderModel.getRemark();
        List<SavePurchaseOrderModel.Detail> details = savePurchaseOrderModel.getDetails();

        String sequenceName = SerialNumberGenerator.generatorTodaySequenceName(tenantId, branchId, "purchase_order_number");
        String orderNumber = SerialNumberGenerator.nextOrderNumber("JH", 8, SequenceUtils.nextValue(sequenceName));

        PurchaseOrder purchaseOrder = PurchaseOrder.builder()
                .tenantId(tenantId)
                .tenantCode(tenantCode)
                .branchId(branchId)
                .orderNumber(orderNumber)
                .originatorUserId(userId)
                .auditorUserId(Constants.BIGINT_DEFAULT_VALUE)
                .auditTime(Constants.DATETIME_DEFAULT_VALUE)
                .remark(StringUtils.isNotBlank(remark) ? remark : Constants.VARCHAR_DEFAULT_VALUE)
                .status(Constants.PURCHASE_ORDER_STATUS_NOT_EXAMINED)
                .createdUserId(userId)
                .updatedUserId(userId)
                .build();

        DatabaseHelper.insert(purchaseOrder);

        Long purchaseOrderId = purchaseOrder.getId();

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
                    .createdUserId(userId)
                    .updatedUserId(userId)
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
     * @param auditPurchaseOrderModel
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiRest auditPurchaseOrder(AuditPurchaseOrderModel auditPurchaseOrderModel) {
        Long tenantId = auditPurchaseOrderModel.obtainTenantId();
        Long branchId = auditPurchaseOrderModel.obtainBranchId();
        Long userId = auditPurchaseOrderModel.obtainUserId();
        Long purchaseOrderId = auditPurchaseOrderModel.getPurchaseOrderId();

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition(PurchaseOrder.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        searchModel.addSearchCondition(PurchaseOrder.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        searchModel.addSearchCondition(PurchaseOrder.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, purchaseOrderId);
        PurchaseOrder purchaseOrder = DatabaseHelper.find(PurchaseOrder.class, searchModel);
        ValidateUtils.notNull(purchaseOrder, "进货单不存在！");
        ValidateUtils.isTrue(purchaseOrder.getStatus() == Constants.PURCHASE_ORDER_STATUS_NOT_EXAMINED, "只有未审核状态的进货单才能进行审核操作！");

        Date date = new Date();

        purchaseOrder.setAuditorUserId(userId);
        purchaseOrder.setAuditTime(date);
        purchaseOrder.setStatus(Constants.PURCHASE_ORDER_STATUS_EXAMINED);
        purchaseOrder.setUpdatedUserId(userId);
        purchaseOrder.setUpdatedRemark("审核进货单！");
        DatabaseHelper.update(purchaseOrder);

        List<PurchaseOrderDetail> purchaseOrderDetails = DatabaseHelper.findAll(PurchaseOrderDetail.class, TupleUtils.buildTuple3(PurchaseOrderDetail.ColumnName.PURCHASE_ORDER_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, purchaseOrderId));
        String tenantCode = purchaseOrder.getTenantCode();
        List<StockFlow> stockFlows = new ArrayList<StockFlow>();

        for (PurchaseOrderDetail purchaseOrderDetail : purchaseOrderDetails) {
            Long goodsId = purchaseOrderDetail.getGoodsId();
            Long goodsSpecificationId = purchaseOrderDetail.getGoodsSpecificationId();
            Long unitId = purchaseOrderDetail.getUnitId();
            Double quantity = purchaseOrderDetail.getQuantity();
            GoodsUtils.addGoodsStock(goodsId, goodsSpecificationId, quantity);

            StockFlow stockFlow = StockFlow.builder()
                    .tenantId(tenantId)
                    .tenantCode(tenantCode)
                    .branchId(branchId)
                    .goodsId(goodsId)
                    .goodsSpecificationId(goodsSpecificationId)
                    .unitId(unitId)
                    .type(Constants.STOCK_FLOW_TYPE_PURCHASE)
                    .occurrenceTime(date)
                    .quantity(quantity)
                    .createdUserId(userId)
                    .updatedUserId(userId)
                    .build();
            stockFlows.add(stockFlow);
        }
        DatabaseHelper.insertAll(stockFlows);
        return ApiRest.builder().message("审核进货单成功！").successful(true).build();
    }

    /**
     * 删除进货单
     *
     * @param deletePurchaseOrderModel
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiRest deletePurchaseOrder(DeletePurchaseOrderModel deletePurchaseOrderModel) {
        Long tenantId = deletePurchaseOrderModel.obtainTenantId();
        Long branchId = deletePurchaseOrderModel.obtainBranchId();
        Long purchaseOrderId = deletePurchaseOrderModel.getPurchaseOrderId();
        Long userId = deletePurchaseOrderModel.obtainUserId();

        DatabaseHelper.markedDelete(PurchaseOrder.class, purchaseOrderId, userId, "删除进货单！");

        Tuple3[] searchConditions = new Tuple3[]{
                TupleUtils.buildTuple3(PurchaseOrderDetail.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId),
                TupleUtils.buildTuple3(PurchaseOrderDetail.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId),
                TupleUtils.buildTuple3(PurchaseOrderDetail.ColumnName.PURCHASE_ORDER_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, purchaseOrderId),
        };

        DatabaseHelper.markedDelete(PurchaseOrderDetail.class, userId, "删除进货单明细！", searchConditions);

        return ApiRest.builder().message("删除进货单成功！").successful(true).build();
    }

    @Transactional(rollbackFor = Exception.class)
    public ApiRest batchDeletePurchaseOrders(BatchDeletePurchaseOrdersModel batchDeletePurchaseOrdersModel) {
        Long tenantId = batchDeletePurchaseOrdersModel.obtainTenantId();
        Long branchId = batchDeletePurchaseOrdersModel.obtainBranchId();
        Long userId = batchDeletePurchaseOrdersModel.obtainUserId();
        List<Long> purchaseOrderIds = batchDeletePurchaseOrdersModel.getPurchaseOrderIds();

        Tuple3[] purchaseOrderSearchConditions = new Tuple3[]{
                TupleUtils.buildTuple3(PurchaseOrder.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId),
                TupleUtils.buildTuple3(PurchaseOrder.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId),
                TupleUtils.buildTuple3(PurchaseOrder.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_IN, purchaseOrderIds)
        };
        DatabaseHelper.markedDelete(PurchaseOrder.class, userId, "删除进货单！", purchaseOrderSearchConditions);

        Tuple3[] purchaseOrderDetailSearchConditions = new Tuple3[]{
                TupleUtils.buildTuple3(PurchaseOrderDetail.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId),
                TupleUtils.buildTuple3(PurchaseOrderDetail.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId),
                TupleUtils.buildTuple3(PurchaseOrderDetail.ColumnName.PURCHASE_ORDER_ID, Constants.SQL_OPERATION_SYMBOL_IN, purchaseOrderIds)
        };
        DatabaseHelper.markedDelete(PurchaseOrderDetail.class, userId, "删除进货单明细！", purchaseOrderDetailSearchConditions);
        return ApiRest.builder().message("批量删除进货单成功！").successful(true).build();
    }

    /**
     * 获取进货单列表
     *
     * @param listPurchaseOrdersModel
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest listPurchaseOrders(ListPurchaseOrdersModel listPurchaseOrdersModel) {
        Long tenantId = listPurchaseOrdersModel.obtainTenantId();
        Long branchId = listPurchaseOrdersModel.obtainBranchId();
        int page = listPurchaseOrdersModel.getPage();
        int rows = listPurchaseOrdersModel.getRows();
        String sort = listPurchaseOrdersModel.getSort();
        String order = listPurchaseOrdersModel.getOrder();

        if (StringUtils.isBlank(sort)) {
            sort = PurchaseOrder.ColumnName.UPDATED_TIME;
        }

        if (StringUtils.isBlank(order)) {
            order = Constants.DESC;
        }

        List<SearchCondition> searchConditions = new ArrayList<SearchCondition>();
        searchConditions.add(new SearchCondition(PurchaseOrder.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId));
        searchConditions.add(new SearchCondition(PurchaseOrder.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId));
        searchConditions.add(new SearchCondition(PurchaseOrder.ColumnName.DELETED, Constants.SQL_OPERATION_SYMBOL_EQUAL, 0));

        SearchModel searchModel = new SearchModel();
        searchModel.setSearchConditions(searchConditions);
        long count = DatabaseHelper.count(PurchaseOrder.class, searchModel);

        List<PurchaseOrder> purchaseOrders = null;
        if (count > 0) {
            PagedSearchModel pagedSearchModel = new PagedSearchModel();
            pagedSearchModel.setSearchConditions(searchConditions);
            pagedSearchModel.setOrderBy(sort + " " + order);
            pagedSearchModel.setPage(page);
            pagedSearchModel.setRows(rows);
            purchaseOrders = DatabaseHelper.findAllPaged(PurchaseOrder.class, pagedSearchModel);
        } else {
            purchaseOrders = new ArrayList<PurchaseOrder>();
        }

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("total", count);
        data.put("rows", purchaseOrders);

        return ApiRest.builder().data(data).message("获取进货单列表成功！").successful(true).build();
    }

    /**
     * 获取进货单信息
     *
     * @param obtainPurchaseOrderModel
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest obtainPurchaseOrder(ObtainPurchaseOrderModel obtainPurchaseOrderModel) {
        Long tenantId = obtainPurchaseOrderModel.obtainTenantId();
        Long branchId = obtainPurchaseOrderModel.obtainBranchId();
        Long purchaseOrderId = obtainPurchaseOrderModel.getPurchaseOrderId();

        SearchModel purchaseOrderSearchModel = new SearchModel(true);
        purchaseOrderSearchModel.addSearchCondition(PurchaseOrder.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        purchaseOrderSearchModel.addSearchCondition(PurchaseOrder.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        purchaseOrderSearchModel.addSearchCondition(PurchaseOrder.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, purchaseOrderId);

        PurchaseOrder purchaseOrder = DatabaseHelper.find(PurchaseOrder.class, purchaseOrderSearchModel);
        ValidateUtils.notNull(purchaseOrder, "进货单不存在！");

        SearchModel purchaseOrderDetailSearchModel = new SearchModel(true);
        purchaseOrderDetailSearchModel.addSearchCondition(PurchaseOrderDetail.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        purchaseOrderDetailSearchModel.addSearchCondition(PurchaseOrderDetail.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        purchaseOrderDetailSearchModel.addSearchCondition(PurchaseOrderDetail.ColumnName.PURCHASE_ORDER_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, purchaseOrderId);
        List<PurchaseOrderDetail> purchaseOrderDetails = DatabaseHelper.findAll(PurchaseOrderDetail.class, purchaseOrderDetailSearchModel);

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("purchaseOrder", purchaseOrder);
        data.put("purchaseOrderDetails", purchaseOrderDetails);
        return ApiRest.builder().data(data).message("获取进货单成功！").successful(true).build();
    }
}
