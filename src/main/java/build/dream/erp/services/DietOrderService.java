package build.dream.erp.services;

import build.dream.common.api.ApiRest;
import build.dream.common.erp.domains.DietOrder;
import build.dream.common.utils.*;
import build.dream.erp.constants.Constants;
import build.dream.erp.mappers.DietOrderMapper;
import build.dream.erp.models.dietorder.DoPayModel;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

@Service
public class DietOrderService {
    @Autowired
    private DietOrderMapper dietOrderMapper;

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
                doPayRequestParameters.put("tradeType", "MWEB");
            } else if (paidScene == Constants.PAID_SCENE_WEI_XIN_H5) {
                doPayRequestParameters.put("tradeType", "JSAPI");
                ApplicationHandler.notBlankAndPut(doPayRequestParameters, "openid", doPayModel.getOpenId(), ApplicationHandler.obtainParameterErrorMessage("openId"));
            } else if (paidScene == Constants.PAID_SCENE_WEI_XIN_APP) {
                doPayRequestParameters.put("tradeType", "APP");
            }
        } else if (paidType == Constants.PAID_TYPE_ALIPAY) {
            controllerName = "alipay";
            doPayRequestParameters.put("subject", "订单支付！");
            doPayRequestParameters.put("outTradeNo", dietOrder.getOrderNumber());
            doPayRequestParameters.put("productCode", "");
            ApplicationHandler.notBlankAndPut(doPayRequestParameters, "productCode", doPayModel.getProductCode(), ApplicationHandler.obtainParameterErrorMessage("productCode"));

            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            doPayRequestParameters.put("totalAmount", decimalFormat.format(dietOrder.getPayableAmount()));
            if (paidScene == Constants.PAID_SCENE_ALIPAY_MOBILE_WEBSITE) {
                actionName = "alipayTradeWapPay";
            } else if (paidScene == Constants.PAID_SCENE_ALIPAY_PC_WEBSITE) {
                actionName = "alipayTradePagePay";
            } else if (paidScene == Constants.PAID_SCENE_ALIPAY_APP) {

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
