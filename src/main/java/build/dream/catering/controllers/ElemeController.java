package build.dream.catering.controllers;

import build.dream.catering.constants.Constants;
import build.dream.catering.models.dietorder.CancelOrderModel;
import build.dream.catering.models.dietorder.ConfirmOrderModel;
import build.dream.catering.models.eleme.*;
import build.dream.catering.services.ElemeService;
import build.dream.common.annotations.ApiRestAction;
import build.dream.common.controllers.BasicController;
import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.CommonUtils;
import build.dream.common.utils.ConfigurationUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

@Controller
@RequestMapping(value = "/eleme")
public class ElemeController extends BasicController {
    @Autowired
    private ElemeService elemeService;

    @RequestMapping(value = "/tenantAuthorize", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = TenantAuthorizeModel.class, serviceClass = ElemeService.class, serviceMethodName = "tenantAuthorize", error = "生成授权链接失败")
    public String tenantAuthorize() {
        return null;
    }

    @RequestMapping(value = "/obtainElemeCallbackMessage", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = ObtainElemeCallbackMessageModel.class, serviceClass = ElemeService.class, serviceMethodName = "obtainElemeCallbackMessage", error = "获取饿了么回调消息失败")
    public String obtainElemeCallbackMessage() {
        return null;
    }

    @RequestMapping(value = "/obtainElemeOrder", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = ObtainElemeOrderModel.class, serviceClass = ElemeService.class, serviceMethodName = "obtainElemeOrder", error = "拉取饿了么订单失败")
    public String obtainElemeOrder() {
        return null;
    }

    @RequestMapping(value = "/bindingStore")
    @ResponseBody
    public String bindingStore() throws IOException {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        String result = readResource("bindingStore.html");
        result = result.replaceAll("\\$\\{serviceName}", ConfigurationUtils.getConfiguration(Constants.SERVICE_NAME));
        result = result.replaceAll("\\$\\{tenantId}", requestParameters.get("tenantId"));
        result = result.replaceAll("\\$\\{branchId}", requestParameters.get("branchId"));
        result = result.replaceAll("\\$\\{userId}", requestParameters.get("userId"));
        result = result.replaceAll("\\$\\{partitionCode}", ConfigurationUtils.getConfiguration(Constants.PARTITION_CODE));
        result = result.replaceAll("\\$\\{doBindingStoreUrl}", CommonUtils.getOutsideUrl(Constants.SERVICE_NAME_POSAPI, "proxy", "doPostPermit"));
        result = StringUtils.join(result.split("\\$\\{jquery-3\\.2\\.1\\.min.js}"), readResource("jquery-3.2.1.min.js"));
        return result;
    }

    private String readResource(String resourceName) throws IOException {
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        ClassLoader classLoader = this.getClass().getClassLoader();
        StringBuilder result = new StringBuilder();
        if ("bindingStore.html".equals(resourceName)) {
            inputStream = classLoader.getResourceAsStream("views/eleme/bindingStore.html");
        } else if ("jquery-3.2.1.min.js".equals(resourceName)) {
            inputStream = classLoader.getResourceAsStream("libraries/jquery/jquery-3.2.1.min.js");
        }
        inputStreamReader = new InputStreamReader(inputStream, Constants.CHARSET_NAME_UTF_8);
        int length = 0;
        char[] buffer = new char[1024];
        while ((length = inputStreamReader.read(buffer, 0, 1024)) != -1) {
            result.append(buffer, 0, length);
        }
        inputStreamReader.close();
        inputStream.close();
        return result.toString();
    }

    @RequestMapping(value = "/doBindingStore", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = DoBindingStoreModel.class, serviceClass = ElemeService.class, serviceMethodName = "doBindingStore", error = "绑定饿了么门店失败")
    public String doBindingStore() {
        return null;
    }

    @RequestMapping(value = "/getOrder", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = GetOrderModel.class, serviceClass = ElemeService.class, serviceMethodName = "getOrder", error = "获取订单失败")
    public String getOrder() {
        return null;
    }

    @RequestMapping(value = "/batchGetOrders", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = GetOrderModel.class, serviceClass = ElemeService.class, serviceMethodName = "batchGetOrders", error = "批量获取订单失败")
    public String batchGetOrders() {
        return null;
    }

    /**
     * 确认订单
     *
     * @return
     */
    @RequestMapping(value = "/confirmOrderLite", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = ConfirmOrderModel.class, serviceClass = ElemeService.class, serviceMethodName = "confirmOrderLite", error = "确认订单失败")
    public String confirmOrderLite() {
        return null;
    }

    /**
     * 取消订单
     *
     * @return
     */
    @RequestMapping(value = "/cancelOrderLite", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = CancelOrderModel.class, serviceClass = ElemeService.class, serviceMethodName = "cancelOrderLite", error = "取消订单失败")
    public String cancelOrderLite() {
        return null;
    }

    /**
     * 同意退单/同意取消单(推荐)
     *
     * @return
     */
    @RequestMapping(value = "/agreeRefundLite", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = AgreeRefundLiteModel.class, serviceClass = ElemeService.class, serviceMethodName = "agreeRefundLite", error = "同意退单/同意取消单失败")
    public String agreeRefundLite() {
        return null;
    }

    /**
     * 不同意退单/不同意取消单
     *
     * @return
     */
    @RequestMapping(value = "/disagreeRefundLite", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = DisagreeRefundLiteModel.class, serviceClass = ElemeService.class, serviceMethodName = "disagreeRefundLite", error = "不同意退单/不同意取消单失败")
    public String disagreeRefundLite() {
        return null;
    }

    /**
     * 配送异常或者物流拒单后选择自行配送
     *
     * @return
     */
    @RequestMapping(value = "/deliveryBySelfLite", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = DeliveryBySelfLiteModel.class, serviceClass = ElemeService.class, serviceMethodName = "deliveryBySelfLite", error = "配送异常或者物流拒单后选择自行配送失败")
    public String deliveryBySelfLite() {
        return null;
    }

    /**
     * 配送异常或者物流拒单后选择不再配送
     *
     * @return
     */
    @RequestMapping(value = "/noMoreDeliveryLite", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = NoMoreDeliveryLiteModel.class, serviceClass = ElemeService.class, serviceMethodName = "noMoreDeliveryLite", error = "配送异常或者物流拒单后选择不再配送失败")
    public String noMoreDeliveryLite() {
        return null;
    }

    /**
     * 订单确认送达
     *
     * @return
     */
    @RequestMapping(value = "/receivedOrderLite", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = ReceivedOrderLiteModel.class, serviceClass = ElemeService.class, serviceMethodName = "receivedOrderLite", error = "订单确认送达失败")
    public String receivedOrderLite() {
        return null;
    }

    /**
     * 回复催单
     *
     * @return
     */
    @RequestMapping(value = "/replyReminder", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = ReplyReminderModel.class, serviceClass = ElemeService.class, serviceMethodName = "replyReminder", error = "回复催单失败")
    public String replyReminder() {
        return null;
    }

    /**
     * 获取商户账号信息
     *
     * @return
     */
    @RequestMapping(value = "/getUser", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = GetUserModel.class, serviceClass = ElemeService.class, serviceMethodName = "getUser", error = "获取商户账号信息失败")
    public String getUser() {
        return null;
    }
}
