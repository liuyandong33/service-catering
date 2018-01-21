package build.dream.catering.services;

import build.dream.catering.constants.Constants;
import build.dream.catering.mappers.*;
import build.dream.catering.models.dietorder.ObtainDietOrderInfoModel;
import build.dream.catering.models.dietorder.SaveDietOrderModel;
import build.dream.common.api.ApiRest;
import build.dream.common.erp.catering.domains.DietOrder;
import build.dream.common.erp.catering.domains.DietOrderDetail;
import build.dream.common.erp.catering.domains.DietOrderDetailGoodsFlavor;
import build.dream.common.erp.catering.domains.DietOrderGroup;
import build.dream.common.utils.SearchModel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DietOrderService {
    @Autowired
    private DietOrderMapper dietOrderMapper;
    @Autowired
    private DietOrderDetailMapper dietOrderDetailMapper;
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private GoodsSpecificationMapper goodsSpecificationMapper;
    @Autowired
    private GoodsFlavorMapper goodsFlavorMapper;
    @Autowired
    private SequenceMapper sequenceMapper;
    @Autowired
    private GoodsFlavorGroupMapper goodsFlavorGroupMapper;
    @Autowired
    private DietOrderGroupMapper dietOrderGroupMapper;
    @Autowired
    private DietOrderDetailGoodsFlavorMapper dietOrderDetailGoodsFlavorMapper;

    /**
     * 获取订单信息
     * @param obtainDietOrderInfoModel
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest obtainDietOrderInfo(ObtainDietOrderInfoModel obtainDietOrderInfoModel) {
        SearchModel dietOrderSearchModel = new SearchModel(true);
        dietOrderSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUALS, obtainDietOrderInfoModel.getDietOrderId());
        dietOrderSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, obtainDietOrderInfoModel.getTenantId());
        dietOrderSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, obtainDietOrderInfoModel.getBranchId());
        DietOrder dietOrder = dietOrderMapper.find(dietOrderSearchModel);
        Validate.notNull(dietOrder, "订单不存在！");

        SearchModel dietOrderGroupSearchModel = new SearchModel(true);
        dietOrderGroupSearchModel.addSearchCondition("diet_order_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, dietOrder.getId());
        List<DietOrderGroup> dietOrderGroups = dietOrderGroupMapper.findAll(dietOrderGroupSearchModel);

        SearchModel dietOrderDetailSearchModel = new SearchModel(true);
        dietOrderDetailSearchModel.addSearchCondition("diet_order_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, dietOrder.getId());
        List<DietOrderDetail> dietOrderDetails = dietOrderDetailMapper.findAll(dietOrderDetailSearchModel);
        Map<BigInteger, List<DietOrderDetail>> dietOrderDetailMap = new HashMap<BigInteger, List<DietOrderDetail>>();
        for (DietOrderDetail dietOrderDetail : dietOrderDetails) {
            List<DietOrderDetail> dietOrderDetailList = dietOrderDetailMap.get(dietOrderDetail.getDietOrderGroupId());
            if (dietOrderDetailList == null) {
                dietOrderDetailList = new ArrayList<DietOrderDetail>();
                dietOrderDetailMap.put(dietOrderDetail.getDietOrderGroupId(), dietOrderDetailList);
            }
            dietOrderDetailList.add(dietOrderDetail);
        }

        SearchModel dietOrderDetailGoodsFlavorSearchModel = new SearchModel(true);
        dietOrderDetailGoodsFlavorSearchModel.addSearchCondition("diet_order_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, dietOrder.getId());
        List<DietOrderDetailGoodsFlavor> dietOrderDetailGoodsFlavors = dietOrderDetailGoodsFlavorMapper.findAll(dietOrderDetailGoodsFlavorSearchModel);
        Map<BigInteger, List<DietOrderDetailGoodsFlavor>> dietOrderDetailGoodsFlavorMap = new HashMap<BigInteger, List<DietOrderDetailGoodsFlavor>>();
        for (DietOrderDetailGoodsFlavor dietOrderDetailGoodsFlavor : dietOrderDetailGoodsFlavors) {
            List<DietOrderDetailGoodsFlavor> dietOrderDetailGoodsFlavorList = dietOrderDetailGoodsFlavorMap.get(dietOrderDetailGoodsFlavor.getDietOrderDetailId());
            if (dietOrderDetailGoodsFlavorList == null) {
                dietOrderDetailGoodsFlavorList = new ArrayList<DietOrderDetailGoodsFlavor>();
                dietOrderDetailGoodsFlavorMap.put(dietOrderDetailGoodsFlavor.getDietOrderDetailId(), dietOrderDetailGoodsFlavorList);
            }
            dietOrderDetailGoodsFlavorList.add(dietOrderDetailGoodsFlavor);
        }

        ApiRest apiRest = new ApiRest(buildDietOrderInfo(dietOrder, dietOrderGroups, dietOrderDetailMap, dietOrderDetailGoodsFlavorMap), "获取订单信息成功！");
        return apiRest;
    }

    /**
     * 构建订单信息
     * @param dietOrder
     * @param dietOrderGroups
     * @param dietOrderDetailMap
     * @param dietOrderDetailGoodsFlavorMap
     * @return
     */
    private Map<String, Object> buildDietOrderInfo(DietOrder dietOrder, List<DietOrderGroup> dietOrderGroups, Map<BigInteger, List<DietOrderDetail>> dietOrderDetailMap, Map<BigInteger, List<DietOrderDetailGoodsFlavor>> dietOrderDetailGoodsFlavorMap) {
        Map<String, Object> dietOrderInfo = new HashMap<String, Object>();
        dietOrderInfo.put("id", dietOrder.getId());
        dietOrderInfo.put("orderNumber", dietOrder.getOrderNumber());
        dietOrderInfo.put("tenantId", dietOrder.getTenantId());
        dietOrderInfo.put("tenantCode", dietOrder.getTenantCode());
        dietOrderInfo.put("branchId", dietOrder.getBranchId());
        dietOrderInfo.put("orderType", dietOrder.getOrderType());
        dietOrderInfo.put("orderStatus", dietOrder.getOrderStatus());
        dietOrderInfo.put("payStatus", dietOrder.getPayStatus());
        dietOrderInfo.put("refundStatus", dietOrder.getRefundStatus());
        dietOrderInfo.put("totalAmount", dietOrder.getTotalAmount());
        dietOrderInfo.put("discountAmount", dietOrder.getDiscountAmount());
        dietOrderInfo.put("payableAmount", dietOrder.getPayableAmount());
        dietOrderInfo.put("paidAmount", dietOrder.getPaidAmount());
        dietOrderInfo.put("paidType", dietOrder.getPaidType());
        dietOrderInfo.put("remark", dietOrder.getRemark());
        dietOrderInfo.put("deliveryAddress", dietOrder.getDeliveryAddress());
        dietOrderInfo.put("deliveryLongitude", dietOrder.getDeliveryLongitude());
        dietOrderInfo.put("deliveryLatitude", dietOrder.getDeliveryLatitude());
        dietOrderInfo.put("deliverTime", dietOrder.getDeliverTime());
        dietOrderInfo.put("activeTime", dietOrder.getActiveTime());
        dietOrderInfo.put("deliverFee", dietOrder.getDeliverFee());
        dietOrderInfo.put("telephoneNumber", dietOrder.getTelephoneNumber());
        dietOrderInfo.put("daySerialNumber", dietOrder.getDaySerialNumber());
        dietOrderInfo.put("consignee", dietOrder.getConsignee());
        dietOrderInfo.put("groups", buildGroups(dietOrderGroups, dietOrderDetailMap, dietOrderDetailGoodsFlavorMap));
        return dietOrderInfo;
    }

    /**
     * 构建订单组信息
     * @param dietOrderGroups
     * @param dietOrderDetailMap
     * @param dietOrderDetailGoodsFlavorMap
     * @return
     */
    private List<Map<String,Object>> buildGroups(List<DietOrderGroup> dietOrderGroups, Map<BigInteger, List<DietOrderDetail>> dietOrderDetailMap, Map<BigInteger, List<DietOrderDetailGoodsFlavor>> dietOrderDetailGoodsFlavorMap) {
        List<Map<String, Object>> groups = new ArrayList<Map<String, Object>>();
        for (DietOrderGroup dietOrderGroup : dietOrderGroups) {
            Map<String, Object> group = new HashMap<String, Object>();
            group.put("id", dietOrderGroup.getId());
            group.put("name", dietOrderGroup.getName());
            group.put("type", dietOrderGroup.getType());
            group.put("details", buildDetails(dietOrderDetailMap.get(dietOrderGroup.getId()), dietOrderDetailGoodsFlavorMap));
            groups.add(group);
        }
        return groups;
    }

    /**
     * 构建订单详情信息
     * @param dietOrderDetails
     * @param dietOrderDetailGoodsFlavorMap
     * @return
     */
    private List<Map<String, Object>> buildDetails(List<DietOrderDetail> dietOrderDetails, Map<BigInteger, List<DietOrderDetailGoodsFlavor>> dietOrderDetailGoodsFlavorMap) {
        List<Map<String, Object>> dietOrderDetailInfos = new ArrayList<Map<String, Object>>();
        for (DietOrderDetail dietOrderDetail : dietOrderDetails) {
            Map<String, Object> dietOrderDetailInfo = new HashMap<String, Object>();
            dietOrderDetailInfo.put("goodsId", dietOrderDetail.getGoodsId());
            dietOrderDetailInfo.put("goodsName", dietOrderDetail.getGoodsName());
            dietOrderDetailInfo.put("goodsSpecificationId", dietOrderDetail.getGoodsSpecificationId());
            dietOrderDetailInfo.put("goodsSpecificationName", dietOrderDetail.getGoodsSpecificationName());
            dietOrderDetailInfo.put("price", dietOrderDetail.getPrice());
            dietOrderDetailInfo.put("flavorIncrease", dietOrderDetail.getFlavorIncrease());
            dietOrderDetailInfo.put("quantity", dietOrderDetail.getQuantity());
            dietOrderDetailInfo.put("totalAmount", dietOrderDetail.getTotalAmount());
            dietOrderDetailInfo.put("discountAmount", dietOrderDetail.getDiscountAmount());
            dietOrderDetailInfo.put("payableAmount", dietOrderDetail.getPayableAmount());

            List<DietOrderDetailGoodsFlavor> dietOrderDetailGoodsFlavors = dietOrderDetailGoodsFlavorMap.get(dietOrderDetail.getId());
            if (CollectionUtils.isNotEmpty(dietOrderDetailGoodsFlavors)) {
                List<Map<String, Object>> flavors = new ArrayList<Map<String, Object>>();
                for (DietOrderDetailGoodsFlavor dietOrderDetailGoodsFlavor : dietOrderDetailGoodsFlavors) {
                    Map<String, Object> flavor = new HashMap<String, Object>();
                    flavor.put("flavorGroupId", dietOrderDetailGoodsFlavor.getGoodsFlavorGroupId());
                    flavor.put("flavorGroupName", dietOrderDetailGoodsFlavor.getGoodsFlavorGroupName());
                    flavor.put("flavorId", dietOrderDetailGoodsFlavor.getGoodsFlavorId());
                    flavor.put("flavorName", dietOrderDetailGoodsFlavor.getGoodsFlavorName());
                    flavor.put("price", dietOrderDetailGoodsFlavor.getPrice());
                    flavors.add(flavor);
                }
                dietOrderDetailInfo.put("flavors", flavors);
            }
            dietOrderDetailInfos.add(dietOrderDetailInfo);
        }
        return dietOrderDetailInfos;
    }

    @Transactional(rollbackFor = Exception.class)
    public ApiRest saveDietOrder(SaveDietOrderModel saveDietOrderModel) {
        return new ApiRest();
    }
}
