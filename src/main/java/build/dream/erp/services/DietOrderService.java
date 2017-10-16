package build.dream.erp.services;

import build.dream.common.api.ApiRest;
import build.dream.common.erp.domains.*;
import build.dream.common.utils.*;
import build.dream.erp.constants.Constants;
import build.dream.erp.mappers.DietOrderMapper;
import build.dream.erp.mappers.GoodsFlavorMapper;
import build.dream.erp.mappers.GoodsMapper;
import build.dream.erp.mappers.GoodsSpecificationMapper;
import build.dream.erp.models.dietorder.DoPayModel;
import build.dream.erp.models.dietorder.SaveDietOrderModel;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
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
    private GoodsMapper goodsMapper;
    @Autowired
    private GoodsSpecificationMapper goodsSpecificationMapper;
    @Autowired
    private GoodsFlavorMapper goodsFlavorMapper;

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
        List<GoodsFlavor> goodsFlavors = goodsFlavorMapper.findAll(goodsSearchModel);
        Map<BigInteger, GoodsFlavor> goodsFlavorMap = new HashMap<BigInteger, GoodsFlavor>();
        for (GoodsFlavor goodsFlavor : goodsFlavors) {
            goodsFlavorMap.put(goodsFlavor.getId(), goodsFlavor);
        }

        DietOrder dietOrder = new DietOrder();
        for (SaveDietOrderModel.DietOrderModel dietOrderModel : dietOrderModels) {
            Goods goods = goodsMap.get(dietOrderModel.getGoodsId());
            Validate.notNull(goods, "菜品不存在！");

            GoodsSpecification goodsSpecification = goodsSpecificationMap.get(dietOrderModel.getGoodsSpecificationId());
            Validate.notNull(goodsSpecification, "菜品规格不存在！");

            List<GoodsFlavor> goodsFlavorList = new ArrayList<GoodsFlavor>();
            for (BigInteger goodsFlavorId : dietOrderModel.getGoodsFlavorIds()) {
                GoodsFlavor goodsFlavor = goodsFlavorMap.get(goodsFlavorId);
                Validate.notNull(goodsFlavor, "菜品口味不存在！");
                goodsFlavorList.add(goodsFlavor);
            }
            DietOrderDetail dietOrderDetail = new DietOrderDetail();
        }
        return null;
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
            if (paidScene == Constants.PAID_SCENE_WEI_XIN_PUBLIC_ACCOUNT) {
                doPayRequestParameters.put("tradeType", "JSAPI");
            } else if (paidScene == Constants.PAID_SCENE_WEI_XIN_H5) {
                doPayRequestParameters.put("tradeType", "MWEB");
                ApplicationHandler.notBlankAndPut(doPayRequestParameters, "openid", doPayModel.getOpenId(), ApplicationHandler.obtainParameterErrorMessage("openId"));
            } else if (paidScene == Constants.PAID_SCENE_WEI_XIN_APP) {
                doPayRequestParameters.put("tradeType", "APP");
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
