package build.dream.erp.services;

import build.dream.common.api.ApiRest;
import build.dream.common.constants.DietOrderConstants;
import build.dream.common.erp.domains.*;
import build.dream.common.saas.domains.DietOrderDetailGoodsFlavor;
import build.dream.common.utils.*;
import build.dream.erp.constants.Constants;
import build.dream.erp.mappers.*;
import build.dream.erp.models.dietorder.DoPayModel;
import build.dream.erp.models.dietorder.SaveDietOrderModel;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
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

    @Transactional(rollbackFor = Exception.class)
    public ApiRest saveDietOrder(SaveDietOrderModel saveDietOrderModel) {
        List<SaveDietOrderModel.DietOrderModel> dietOrderModels = saveDietOrderModel.getDietOrderModels();
        List<BigInteger> goodsIds = new ArrayList<BigInteger>();
        List<BigInteger> goodsSpecificationIds = new ArrayList<BigInteger>();
        List<BigInteger> goodsFlavorIds = new ArrayList<BigInteger>();
        for (SaveDietOrderModel.DietOrderModel dietOrderModel : dietOrderModels) {
            goodsIds.add(dietOrderModel.getGoodsId());
            goodsSpecificationIds.add(dietOrderModel.getGoodsSpecificationId());
            goodsFlavorIds.addAll(dietOrderModel.getGoodsFlavorIds());
        }

        SearchModel goodsSearchModel = new SearchModel(true);
        goodsSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, saveDietOrderModel.getTenantId());
        goodsSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, saveDietOrderModel.getBranchId());
        goodsSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_IN, goodsIds);
        List<Goods> gooses = goodsMapper.findAll(goodsSearchModel);
        Map<BigInteger, Goods> goodsMap = new HashMap<BigInteger, Goods>();
        for (Goods goods : gooses) {
            goodsMap.put(goods.getId(), goods);
        }

        SearchModel goodsSpecificationSearchModel = new SearchModel(true);
        goodsSpecificationSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, saveDietOrderModel.getTenantId());
        goodsSpecificationSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, saveDietOrderModel.getBranchId());
        goodsSpecificationSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_IN, goodsSpecificationIds);
        List<GoodsSpecification> goodsSpecifications = goodsSpecificationMapper.findAll(goodsSpecificationSearchModel);
        Map<BigInteger, GoodsSpecification> goodsSpecificationMap = new HashMap<BigInteger, GoodsSpecification>();
        for (GoodsSpecification goodsSpecification : goodsSpecifications) {
            goodsSpecificationMap.put(goodsSpecification.getId(), goodsSpecification);
        }

        SearchModel goodsFlavorSearchModel = new SearchModel(true);
        goodsFlavorSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, saveDietOrderModel.getTenantId());
        goodsFlavorSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, saveDietOrderModel.getBranchId());
        goodsFlavorSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_IN, goodsFlavorIds);
        List<GoodsFlavor> goodsFlavors = goodsFlavorMapper.findAll(goodsFlavorSearchModel);
        Map<BigInteger, GoodsFlavor> goodsFlavorMap = new HashMap<BigInteger, GoodsFlavor>();
        List<BigInteger> goodsFlavorGroupIds = new ArrayList<BigInteger>();
        for (GoodsFlavor goodsFlavor : goodsFlavors) {
            goodsFlavorMap.put(goodsFlavor.getId(), goodsFlavor);
            goodsFlavorGroupIds.add(goodsFlavor.getGoodsFlavorGroupId());
        }

        SearchModel goodsFlavorGroupSearchModel = new SearchModel(true);
        goodsFlavorGroupSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_IN, goodsFlavorGroupIds);
        List<GoodsFlavorGroup> goodsFlavorGroups = goodsFlavorGroupMapper.findAll(goodsFlavorGroupSearchModel);
        Map<BigInteger, GoodsFlavorGroup> goodsFlavorGroupMap = new HashMap<BigInteger, GoodsFlavorGroup>();
        for (GoodsFlavorGroup goodsFlavorGroup : goodsFlavorGroups) {
            goodsFlavorGroupMap.put(goodsFlavorGroup.getId(), goodsFlavorGroup);
        }

        DietOrder dietOrder = new DietOrder();
        String prefix = null;
        Integer orderType = saveDietOrderModel.getOrderType();
        if (orderType == DietOrderConstants.ORDER_TYPE_SCAN_CODE_ORDER) {
            prefix = "SO";
        } else if (orderType == DietOrderConstants.ORDER_TYPE_ELEME_ORDER) {
            prefix = "EO";
        } else if (orderType == DietOrderConstants.ORDER_TYPE_MEI_TUAN_ORDER) {
            prefix = "MO";
        } else if (orderType == DietOrderConstants.ORDER_TYPE_WEI_XIN_ORDER) {
            prefix = "WO";
        }
        Integer daySerialNumber = sequenceMapper.nextValue(SerialNumberGenerator.generatorTodaySequenceName(saveDietOrderModel.getTenantId(), saveDietOrderModel.getBranchId(), "DO"));
        dietOrder.setOrderNumber(SerialNumberGenerator.nextOrderNumber(prefix, 6, daySerialNumber));
        dietOrder.setTenantId(saveDietOrderModel.getTenantId());
        dietOrder.setBranchId(saveDietOrderModel.getBranchId());
        dietOrder.setOrderType(orderType);
        dietOrder.setOrderStatus(DietOrderConstants.ORDER_STATUS_PENDING);
        dietOrder.setPayStatus(DietOrderConstants.PAY_STATUS_UNPAID);
        dietOrder.setRefundStatus(DietOrderConstants.REFUND_STATUS_NO_REFUND);
        dietOrder.setPaidAmount(BigDecimal.ZERO);
        dietOrder.setRemark(saveDietOrderModel.getRemark());
        dietOrder.setDeliveryAddress(saveDietOrderModel.getDeliveryAddress());
        dietOrder.setDeliveryLongitude(saveDietOrderModel.getDeliveryLongitude());
        dietOrder.setDeliveryLatitude(saveDietOrderModel.getDeliveryLatitude());
        dietOrder.setTelephoneNumber(saveDietOrderModel.getTelephoneNumber());
        dietOrder.setDaySerialNumber(daySerialNumber.toString());
        dietOrder.setConsignee(saveDietOrderModel.getConsignee());
        dietOrder.setCreateUserId(saveDietOrderModel.getUserId());
        dietOrder.setLastUpdateUserId(saveDietOrderModel.getUserId());
        dietOrder.setLastUpdateRemark("保存订单！");
        dietOrderMapper.insert(dietOrder);
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal discountAmount = BigDecimal.ZERO;
        BigDecimal payableAmount = BigDecimal.ZERO;

        List<Map<String, Object>> dietOrderDetailList = new ArrayList<Map<String, Object>>();
        for (SaveDietOrderModel.DietOrderModel dietOrderModel : dietOrderModels) {
            Goods goods = goodsMap.get(dietOrderModel.getGoodsId());
            Validate.notNull(goods, "菜品不存在！");

            GoodsSpecification goodsSpecification = goodsSpecificationMap.get(dietOrderModel.getGoodsSpecificationId());
            Validate.notNull(goodsSpecification, "菜品规格不存在！");

            totalAmount.add(goodsSpecification.getPrice().multiply(NumberUtils.createBigDecimal(dietOrderModel.getAmount().toString())));
            payableAmount.add(goodsSpecification.getPrice().multiply(NumberUtils.createBigDecimal(dietOrderModel.getAmount().toString())));

            BigDecimal goodsFlavorsTotalAmount = BigDecimal.ZERO;

            List<DietOrderDetailGoodsFlavor> dietOrderDetailGoodsFlavors = new ArrayList<DietOrderDetailGoodsFlavor>();
            for (BigInteger goodsFlavorId : dietOrderModel.getGoodsFlavorIds()) {
                GoodsFlavor goodsFlavor = goodsFlavorMap.get(goodsFlavorId);
                Validate.notNull(goodsFlavor, "菜品口味不存在！");
                DietOrderDetailGoodsFlavor dietOrderDetailGoodsFlavor = new DietOrderDetailGoodsFlavor();
                dietOrderDetailGoodsFlavor.setTenantId(saveDietOrderModel.getTenantId());
                dietOrderDetailGoodsFlavor.setBranchId(saveDietOrderModel.getBranchId());

                GoodsFlavorGroup goodsFlavorGroup = goodsFlavorGroupMap.get(goodsFlavor.getGoodsFlavorGroupId());
                Validate.notNull(goodsFlavorGroup, "口味组不存在！");
                dietOrderDetailGoodsFlavor.setGoodsFlavorGroupName(goodsFlavorGroup.getName());
                dietOrderDetailGoodsFlavor.setGoodsFlavorName(goodsFlavor.getName());
                dietOrderDetailGoodsFlavors.add(dietOrderDetailGoodsFlavor);

                if (goodsFlavor.getPrice() != null) {
                    goodsFlavorsTotalAmount.add(goodsFlavor.getPrice());
                    dietOrderDetailGoodsFlavor.setPrice(goodsFlavor.getPrice());
                }
            }
            totalAmount.add(goodsFlavorsTotalAmount);
            payableAmount.add(goodsFlavorsTotalAmount);
            DietOrderDetail dietOrderDetail = new DietOrderDetail();
            dietOrderDetail.setDietOrderId(dietOrder.getId());
            dietOrderDetail.setGoodsId(goods.getId());
            dietOrderDetail.setGoodsSpecificationId(goodsSpecification.getId());
            dietOrderDetail.setPrice(goodsSpecification.getPrice().add(goodsFlavorsTotalAmount));
            dietOrderDetail.setAmount(dietOrderModel.getAmount());
            dietOrderDetail.setTotalAmount(dietOrderDetail.getPrice().multiply(NumberUtils.createBigDecimal(dietOrderModel.getAmount().toString())));
            dietOrderDetail.setDiscountAmount(BigDecimal.ZERO);
            dietOrderDetail.setPayableAmount(dietOrderDetail.getPrice().multiply(NumberUtils.createBigDecimal(dietOrderModel.getAmount().toString())));
            dietOrderDetail.setCreateUserId(saveDietOrderModel.getUserId());
            dietOrderDetail.setLastUpdateUserId(saveDietOrderModel.getUserId());
            dietOrderDetail.setLastUpdateRemark("保存订单明细！");
            dietOrderDetailMapper.insert(dietOrderDetail);

            List<Map<String, String>> flavors = new ArrayList<Map<String, String>>();
            for (DietOrderDetailGoodsFlavor dietOrderDetailGoodsFlavor : dietOrderDetailGoodsFlavors) {
                dietOrderDetailGoodsFlavor.setDietOrderDetailId(dietOrderDetail.getId());
                Map<String, String> flavor = new HashMap<String, String>();
                flavor.put("name", dietOrderDetailGoodsFlavor.getGoodsFlavorGroupName());
                flavor.put("value", dietOrderDetailGoodsFlavor.getGoodsFlavorName());
                flavors.add(flavor);
            }

            Map<String, Object> dietOrderDetailMap = BeanUtils.beanToMap(dietOrderDetail);
            dietOrderDetailMap.put("goods", goods);
            dietOrderDetailMap.put("goodsSpecification", goodsSpecification);
            dietOrderDetailMap.put("goodsFlavors", flavors);
            dietOrderDetailList.add(dietOrderDetailMap);
        }
        dietOrder.setTotalAmount(totalAmount);
        dietOrder.setDiscountAmount(discountAmount);
        dietOrder.setPayableAmount(payableAmount);
        dietOrderMapper.update(dietOrder);

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("dietOrder", dietOrder);
        data.put("dietOrderDetails", dietOrderDetailList);
        ApiRest apiRest = new ApiRest();
        apiRest.setData(data);
        apiRest.setMessage("保存订单成功！");
        apiRest.setSuccessful(true);
        return apiRest;
    }

    @Transactional(readOnly = true)
    public ApiRest doPay(DoPayModel doPayModel) throws IOException {
        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUALS, doPayModel.getDietOrderId());
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, doPayModel.getTenantId());
        searchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, doPayModel.getBranchId());

        DietOrder dietOrder = dietOrderMapper.find(searchModel);
        Validate.notNull(dietOrder, "订单不存在！");
        Integer paidType = doPayModel.getPaidType();
        Integer paidScene = doPayModel.getPaidScene();
        String controllerName = null;
        String actionName = null;
        Map<String, String> doPayRequestParameters = new HashMap<String, String>();
        doPayRequestParameters.put("tenantId", doPayModel.getTenantId().toString());
        doPayRequestParameters.put("branchId", doPayModel.getBranchId().toString());
        String partitionCode = ConfigurationUtils.getConfiguration(Constants.PARTITION_CODE);
        String serviceName = ConfigurationUtils.getConfiguration(Constants.SERVICE_NAME);
        if (paidType == Constants.PAID_TYPE_WEI_XIN) {
            controllerName = "weiXinPay";
            actionName = "unifiedOrder";
            doPayRequestParameters.put("body", "订单支付！");
            doPayRequestParameters.put("outTradeNo", dietOrder.getOrderNumber());
            doPayRequestParameters.put("totalFee", String.valueOf(dietOrder.getPayableAmount().multiply(NumberUtils.createBigDecimal("100")).intValue()));
            ApplicationHandler.notBlankAndPut(doPayRequestParameters, "spbillCreateIp", doPayModel.getSpbillCreateIp(), ApplicationHandler.obtainParameterErrorMessage("spbillCreateIp"));
            doPayRequestParameters.put("notifyUrl", SystemPartitionUtils.getServiceDomain(partitionCode, serviceName) + "/dietOrder/weiXinPayCallback");
            doPayRequestParameters.put("userId", doPayModel.getUserId());
            if (paidScene == Constants.PAID_SCENE_WEI_XIN_PUBLIC_ACCOUNT) {
                doPayRequestParameters.put("tradeType", Constants.WEI_XIN_PAY_TRADE_TYPE_JSAPI);
                ApplicationHandler.notBlankAndPut(doPayRequestParameters, "openId", doPayModel.getOpenId(), ApplicationHandler.obtainParameterErrorMessage("openId"));
                ApplicationHandler.ifNotBlankPut(doPayRequestParameters, "subOpenId", doPayModel.getSubOpenId());
            } else if (paidScene == Constants.PAID_SCENE_WEI_XIN_H5) {
                doPayRequestParameters.put("tradeType", Constants.WEI_XIN_PAY_TRADE_TYPE_MWEB);
                ApplicationHandler.ifNotBlankPut(doPayRequestParameters, "openId", doPayModel.getOpenId());
                ApplicationHandler.ifNotBlankPut(doPayRequestParameters, "subOpenId", doPayModel.getSubOpenId());
            } else if (paidScene == Constants.PAID_SCENE_WEI_XIN_APP) {
                doPayRequestParameters.put("tradeType", Constants.WEI_XIN_PAY_TRADE_TYPE_APP);
            } else if (paidScene == Constants.PAID_SCENE_WEI_XIN_NATIVE) {
                doPayRequestParameters.put("tradeType", Constants.WEI_XIN_PAY_TRADE_TYPE_NATIVE);
                ApplicationHandler.ifNotBlankPut(doPayRequestParameters, "openId", doPayModel.getOpenId());
                ApplicationHandler.ifNotBlankPut(doPayRequestParameters, "subOpenId", doPayModel.getSubOpenId());
            }
        } else if (paidType == Constants.PAID_TYPE_ALIPAY) {
            controllerName = "alipay";
            doPayRequestParameters.put("subject", "订单支付！");
            doPayRequestParameters.put("outTradeNo", dietOrder.getOrderNumber());
            ApplicationHandler.notBlankAndPut(doPayRequestParameters, "productCode", doPayModel.getProductCode(), ApplicationHandler.obtainParameterErrorMessage("productCode"));
            doPayRequestParameters.put("totalAmount", new DecimalFormat("0.00").format(dietOrder.getPayableAmount()));
            if (paidScene == Constants.PAID_SCENE_ALIPAY_MOBILE_WEBSITE) {
                actionName = "alipayTradeWapPay";
            } else if (paidScene == Constants.PAID_SCENE_ALIPAY_PC_WEBSITE) {
                actionName = "alipayTradePagePay";
            } else if (paidScene == Constants.PAID_SCENE_ALIPAY_APP) {
                actionName = "alipayTradeAppPay";
            }
        }
        ApiRest doPayApiRest = ProxyUtils.doPostWithRequestParameters(Constants.SERVICE_NAME_OUT, controllerName, actionName, doPayRequestParameters);
        Validate.isTrue(doPayApiRest.isSuccessful(), doPayApiRest.getError());

        ApiRest apiRest = new ApiRest();
        apiRest.setData(doPayApiRest.getData());
        apiRest.setMessage("提交支付请求成功！");
        apiRest.setSuccessful(true);
        return apiRest;
    }
}
