package erp.chain.services;

import erp.chain.api.ApiRest;
import erp.chain.constants.Constants;
import erp.chain.domains.WeiXinPayConfiguration;
import erp.chain.mappers.WeiXinPayConfigurationMapper;
import erp.chain.utils.WeiXinUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

/**
 * Created by liuyandong on 2017/7/19.
 */
@Service
public class WeiXinPayService {
    @Autowired
    private WeiXinPayConfigurationMapper weiXinPayConfigurationMapper;

    @Transactional(readOnly = true)
    public ApiRest unifiedOrder(Map<String, String> parameters) throws IOException, DocumentException {
        String tenantId = parameters.get("tenantId");
        String branchId = parameters.get("branchId");
        WeiXinPayConfiguration weiXinPayConfiguration = weiXinPayConfigurationMapper.findByTenantIdAndBranchId(BigInteger.valueOf(Long.valueOf(tenantId)), BigInteger.valueOf(Long.valueOf(branchId)));
        Validate.notNull(weiXinPayConfiguration, "商户未配置微信支付账号！");
        Map<String, String> unifiedOrderRequestParameters = new TreeMap<String, String>();
        unifiedOrderRequestParameters.put("appid", weiXinPayConfiguration.getWeiXinPayAppId());
        unifiedOrderRequestParameters.put("mch_id", weiXinPayConfiguration.getWeiXinPayMchId());
        String deviceInfo = parameters.get("deviceInfo");
        if (StringUtils.isNotBlank(deviceInfo)) {
            unifiedOrderRequestParameters.put("device_info", deviceInfo);
        }
        unifiedOrderRequestParameters.put("nonce_str", UUID.randomUUID().toString().replaceAll("-", ""));
        unifiedOrderRequestParameters.put("sign_type", Constants.MD5);
        unifiedOrderRequestParameters.put("body", parameters.get("body"));
        String detail = parameters.get("detail");
        if (StringUtils.isNotBlank(detail)) {
            unifiedOrderRequestParameters.put("detail", detail);
        }
        String attach = parameters.get("attach");
        if (StringUtils.isNotBlank(attach)) {
            unifiedOrderRequestParameters.put("attach", attach);
        }
        unifiedOrderRequestParameters.put("out_trade_no", parameters.get("outTradeNo"));
        String feeType = parameters.get("feeType");
        if (StringUtils.isNotBlank(feeType)) {
            unifiedOrderRequestParameters.put("fee_type", feeType);
        }
        unifiedOrderRequestParameters.put("total_fee", parameters.get("totalFee"));
        unifiedOrderRequestParameters.put("spbill_create_ip", parameters.get("spbillCreateIp"));
        String timeStart = parameters.get("timeStart");
        if (StringUtils.isNotBlank(timeStart)) {
            unifiedOrderRequestParameters.put("time_start", timeStart);
        }
        String timeExpire = parameters.get("timeExpire");
        if (StringUtils.isNotBlank(timeExpire)) {
            unifiedOrderRequestParameters.put("time_expire", timeExpire);
        }
        String goodsTag = parameters.get("goodsTag");
        if (StringUtils.isNotBlank(goodsTag)) {
            unifiedOrderRequestParameters.put("goods_tag", goodsTag);
        }
        unifiedOrderRequestParameters.put("notify_url", parameters.get("notifyUrl"));
        unifiedOrderRequestParameters.put("trade_type", parameters.get("tradeType"));
        String productId = parameters.get("productId");
        if (StringUtils.isNotBlank(productId)) {
            unifiedOrderRequestParameters.put("product_id", productId);
        }
        String limitPay = parameters.get("limitPay");
        if (StringUtils.isNotBlank(limitPay)) {
            unifiedOrderRequestParameters.put("limit_pay", limitPay);
        }
        unifiedOrderRequestParameters.put("openid", parameters.get("openid"));
        String sceneInfo = parameters.get("sceneInfo");
        if (StringUtils.isNotBlank(sceneInfo)) {
            unifiedOrderRequestParameters.put("scene_info", sceneInfo);
        }
        unifiedOrderRequestParameters.put("sign", WeiXinUtils.generateUnifiedOrderSign(unifiedOrderRequestParameters, weiXinPayConfiguration.getWeiXinPayKey()));
        String unifiedOrderFinalData = WeiXinUtils.generateUnifiedOrderFinalData(unifiedOrderRequestParameters);
        Map<String, String> unifiedOrderResult = WeiXinUtils.doUnifiedOrder(Constants.WEI_XIN_PAY_UNIFIEDORDER_URL, unifiedOrderFinalData);
        String returnCode = unifiedOrderResult.get("return_code");
        String resultCode = unifiedOrderResult.get("result_code");
        ApiRest apiRest = new ApiRest();
        if (Constants.SUCCESS.equals(returnCode) && Constants.SUCCESS.equals(resultCode)) {
            apiRest.setData(unifiedOrderResult);
            apiRest.setMessage("微信下单成功！");
            apiRest.setSuccessful(true);
        } else {
            apiRest.setError(unifiedOrderResult.get("return_msg"));
            apiRest.setSuccessful(false);
        }
        return apiRest;
    }
}
