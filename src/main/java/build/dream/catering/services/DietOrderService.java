package build.dream.catering.services;

import build.dream.catering.constants.Constants;
import build.dream.catering.mappers.*;
import build.dream.catering.models.dietorder.DoPayModel;
import build.dream.catering.models.dietorder.DoPayOfflineModel;
import build.dream.catering.models.dietorder.ObtainDietOrderInfoModel;
import build.dream.catering.models.dietorder.SaveDietOrderModel;
import build.dream.catering.utils.TenantSecretKeyUtils;
import build.dream.common.api.ApiRest;
import build.dream.common.constants.DietOrderConstants;
import build.dream.common.erp.catering.domains.*;
import build.dream.common.saas.domains.TenantSecretKey;
import build.dream.common.utils.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
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
    @Autowired
    private DietOrderGroupMapper dietOrderGroupMapper;
    @Autowired
    private DietOrderDetailGoodsFlavorMapper dietOrderDetailGoodsFlavorMapper;

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

                GoodsFlavorGroup goodsFlavorGroup = goodsFlavorGroupMap.get(goodsFlavor.getGoodsFlavorGroupId());
                Validate.notNull(goodsFlavorGroup, "口味组不存在！");

                DietOrderDetailGoodsFlavor dietOrderDetailGoodsFlavor = new DietOrderDetailGoodsFlavor();

                dietOrderDetailGoodsFlavor.setGoodsFlavorGroupId(goodsFlavorGroup.getId());
                dietOrderDetailGoodsFlavor.setGoodsFlavorGroupName(goodsFlavorGroup.getName());

                dietOrderDetailGoodsFlavor.setGoodsFlavorId(goodsFlavor.getId());
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
//            dietOrderDetail.setDietOrderId(dietOrder.getId());
            dietOrderDetail.setGoodsId(goods.getId());
            dietOrderDetail.setGoodsSpecificationId(goodsSpecification.getId());
            dietOrderDetail.setPrice(goodsSpecification.getPrice().add(goodsFlavorsTotalAmount));
//            dietOrderDetail.setQuantity(dietOrderModel.getAmount());
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

    public ApiRest doPayOffline(DoPayOfflineModel doPayOfflineModel, String bizContent, String signature) throws UnsupportedEncodingException, InvalidKeySpecException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        TenantSecretKey tenantSecretKey = TenantSecretKeyUtils.obtainTenantSecretKey(doPayOfflineModel.getTenantId());
        Validate.isTrue(SignatureUtils.verifySign(bizContent.getBytes(Constants.CHARSET_NAME_UTF_8), Base64.decodeBase64(tenantSecretKey.getPublicKey()), signature.getBytes(Constants.CHARSET_NAME_UTF_8), SignatureUtils.SIGNATURE_TYPE_SHA256_WITH_RSA), "签名校验失败！");

        ApiRest apiRest = new ApiRest();
        apiRest.setMessage("提交线下支付请求成功！");
        apiRest.setSuccessful(true);
        return apiRest;
    }

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
        List<BigInteger> dietOrderGroupIds = new ArrayList<BigInteger>();
        for (DietOrderGroup dietOrderGroup : dietOrderGroups) {
            dietOrderGroupIds.add(dietOrderGroup.getId());
        }

        SearchModel dietOrderDetailSearchModel = new SearchModel(true);
        dietOrderDetailSearchModel.addSearchCondition("diet_order_group_id", Constants.SQL_OPERATION_SYMBOL_IN, dietOrderGroupIds);
        List<DietOrderDetail> dietOrderDetails = dietOrderDetailMapper.findAll(dietOrderDetailSearchModel);
        Map<BigInteger, List<DietOrderDetail>> dietOrderDetailMap = new HashMap<BigInteger, List<DietOrderDetail>>();
        List<BigInteger> dietOrderDetailIds = new ArrayList<BigInteger>();
        for (DietOrderDetail dietOrderDetail : dietOrderDetails) {
            dietOrderDetailIds.add(dietOrderDetail.getId());
            List<DietOrderDetail> dietOrderDetailList = dietOrderDetailMap.get(dietOrderDetail.getDietOrderGroupId());
            if (dietOrderDetailList == null) {
                dietOrderDetailList = new ArrayList<DietOrderDetail>();
                dietOrderDetailMap.put(dietOrderDetail.getDietOrderGroupId(), dietOrderDetailList);
            }
            dietOrderDetailList.add(dietOrderDetail);
            dietOrderDetailIds.add(dietOrderDetail.getId());
        }

        SearchModel dietOrderDetailGoodsFlavorSearchModel = new SearchModel(true);
        dietOrderDetailGoodsFlavorSearchModel.addSearchCondition("diet_order_detail_id", Constants.SQL_OPERATION_SYMBOL_IN, dietOrderDetailIds);
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

    private List<Map<String, Object>> buildDetails(List<DietOrderDetail> dietOrderDetails, Map<BigInteger, List<DietOrderDetailGoodsFlavor>> dietOrderDetailGoodsFlavorMap) {
        List<Map<String, Object>> dietOrderDetailInfos = new ArrayList<Map<String, Object>>();
        for (DietOrderDetail dietOrderDetail : dietOrderDetails) {
            Map<String, Object> dietOrderDetailInfo = new HashMap<String, Object>();
            dietOrderDetailInfo.put("goodsId", dietOrderDetail.getGoodsId());
            dietOrderDetailInfo.put("goodsName", dietOrderDetail.getGoodsName());
            dietOrderDetailInfo.put("goodsSpecificationId", dietOrderDetail.getGoodsSpecificationId());
            dietOrderDetailInfo.put("goodsSpecificationName", dietOrderDetail.getGoodsSpecificationName());
            dietOrderDetailInfo.put("price", dietOrderDetail.getPrice());
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
}
