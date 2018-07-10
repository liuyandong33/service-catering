package build.dream.catering.controllers;

import build.dream.catering.constants.Constants;
import build.dream.catering.models.dietorder.CancelOrderModel;
import build.dream.catering.models.dietorder.ConfirmOrderModel;
import build.dream.catering.models.eleme.*;
import build.dream.catering.services.ElemeService;
import build.dream.common.annotations.ApiRestAction;
import build.dream.common.controllers.BasicController;
import build.dream.common.erp.catering.domains.ElemeCallbackMessage;
import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.CommonUtils;
import build.dream.common.utils.ConfigurationUtils;
import build.dream.common.utils.ThreadUtils;
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
import java.math.BigInteger;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

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

    @RequestMapping(value = "/test")
    @ResponseBody
    public String test() {
        String returnValue = null;
        try {
            ElemeCallbackMessage elemeCallbackMessage = new ElemeCallbackMessage();
            elemeCallbackMessage.setRequestId("200017223099345201");
            elemeCallbackMessage.setType(10);
            elemeCallbackMessage.setAppId(BigInteger.valueOf(65929831));
            elemeCallbackMessage.setMessage("{\"id\":\"1222507828638127147\",\"orderId\":\"1222507828638127147\",\"address\":\"上海交通大学(闵行校区)1001\",\"createdAt\":\"2018-07-10T21:25:21\",\"activeAt\":\"2018-07-10T21:25:21\",\"deliverFee\":5.3,\"deliverTime\":null,\"description\":\"\",\"groups\":[{\"name\":\"1号篮子\",\"type\":\"normal\",\"items\":[{\"id\":1364161354,\"skuId\":200000168101182637,\"name\":\"中辣\",\"categoryId\":1,\"price\":10.0,\"quantity\":2,\"total\":20.0,\"additions\":[],\"newSpecs\":[],\"attributes\":[],\"extendCode\":\"\",\"barCode\":\"\",\"weight\":1.0,\"userPrice\":0.0,\"shopPrice\":0.0,\"vfoodId\":1343931759}]}],\"invoice\":null,\"book\":false,\"onlinePaid\":true,\"railwayAddress\":null,\"phoneList\":[\"13789871965\"],\"shopId\":150894532,\"shopName\":\"智汇_饿了么订单测试店铺\",\"daySn\":2,\"status\":\"unprocessed\",\"refundStatus\":\"noRefund\",\"userId\":195365009,\"userIdStr\":\"195365009\",\"totalPrice\":15.31,\"originalPrice\":25.3,\"consignee\":\"刘**\",\"deliveryGeo\":\"121.43521942,31.01870991\",\"deliveryPoiAddress\":\"上海交通大学(闵行校区)1001\",\"invoiced\":false,\"income\":5.01,\"serviceRate\":0.18,\"serviceFee\":-5.0,\"hongbao\":0.0,\"packageFee\":0.0,\"activityTotal\":-9.99,\"shopPart\":-9.99,\"elemePart\":-0.0,\"downgraded\":false,\"vipDeliveryFeeDiscount\":0.0,\"openId\":\"1Z1\",\"secretPhoneExpireTime\":\"2018-07-11T00:25:20\",\"orderActivities\":[{\"categoryId\":11,\"name\":\"单品定价\",\"amount\":-9.99,\"elemePart\":0.0,\"restaurantPart\":-9.99,\"familyPart\":0.0,\"id\":1293759106,\"orderAllPartiesPartList\":[{\"partName\":\"商家补贴\",\"partAmount\":\"9.99\"}]}],\"invoiceType\":null,\"taxpayerId\":\"\",\"coldBoxFee\":0.0,\"cancelOrderDescription\":null,\"cancelOrderCreatedAt\":null,\"orderCommissions\":[],\"baiduWaimai\":false,\"userExtraInfo\":{\"giverPhone\":\"\",\"greeting\":\"\",\"remark\":\"\",\"invoiceTitle\":null},\"consigneePhones\":[]}");
            elemeCallbackMessage.setShopId(BigInteger.valueOf(150894532));
            elemeCallbackMessage.setTimestamp(new Date());
            elemeCallbackMessage.setSignature("74058D145AD4496ECEDB42149CE3EB22");
            elemeCallbackMessage.setUserId(BigInteger.valueOf(283166468060386111L));
            elemeService.saveElemeOrder(elemeCallbackMessage, UUID.randomUUID().toString());

            returnValue = Constants.SUCCESS;
        } catch (Exception e) {
            returnValue = e.getMessage();
        }
        return returnValue;
    }

    @RequestMapping(value = "/demo")
    @ResponseBody
    public String demo() {
        ThreadUtils.sleepSafe(3000);
        return Constants.SUCCESS;
    }
}
