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
import java.text.ParseException;
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
    public String test() throws IOException, ParseException {
        ElemeCallbackMessage elemeCallbackMessage = new ElemeCallbackMessage();
        elemeCallbackMessage.setRequestId("200017223099345201");
        elemeCallbackMessage.setType(10);
        elemeCallbackMessage.setAppId(BigInteger.valueOf(65929831));
        elemeCallbackMessage.setMessage("{\"id\":\"3025881411983150156\",\"orderId\":\"3025881411983150156\",\"address\":\"重庆国瑞中心重庆万豪酒店3206\",\"createdAt\":\"2018-07-08T18:24:42\",\"activeAt\":\"2018-07-08T18:24:42\",\"deliverFee\":2.5,\"deliverTime\":null,\"description\":\"\",\"groups\":[{\"name\":\"1号篮子\",\"type\":\"normal\",\"items\":[{\"id\":102280962,\"skuId\":54120216881,\"name\":\"鲜肉骨汤包一笼\",\"categoryId\":1,\"price\":8.0,\"quantity\":1,\"total\":8.0,\"additions\":[],\"newSpecs\":[],\"attributes\":[],\"extendCode\":\"\",\"barCode\":\"\",\"weight\":1.0,\"userPrice\":0.0,\"shopPrice\":0.0,\"vfoodId\":55745095},{\"id\":102281945,\"skuId\":54120219953,\"name\":\"手工馒头一笼\",\"categoryId\":1,\"price\":4.0,\"quantity\":1,\"total\":4.0,\"additions\":[],\"newSpecs\":[],\"attributes\":[],\"extendCode\":\"\",\"barCode\":\"\",\"weight\":1.0,\"userPrice\":0.0,\"shopPrice\":0.0,\"vfoodId\":55745602},{\"id\":102309182,\"skuId\":54120347953,\"name\":\"拌核桃肉\",\"categoryId\":1,\"price\":12.0,\"quantity\":1,\"total\":12.0,\"additions\":[],\"newSpecs\":[],\"attributes\":[],\"extendCode\":\"\",\"barCode\":\"\",\"weight\":1.0,\"userPrice\":0.0,\"shopPrice\":0.0,\"vfoodId\":55765165},{\"id\":102310901,\"skuId\":54120359217,\"name\":\"拌空心菜\",\"categoryId\":1,\"price\":6.0,\"quantity\":1,\"total\":6.0,\"additions\":[],\"newSpecs\":[],\"attributes\":[],\"extendCode\":\"\",\"barCode\":\"\",\"weight\":1.0,\"userPrice\":0.0,\"shopPrice\":0.0,\"vfoodId\":55766379},{\"id\":102277770,\"skuId\":54120336689,\"name\":\"南瓜高梁粥\",\"categoryId\":1,\"price\":3.0,\"quantity\":1,\"total\":3.0,\"additions\":[],\"newSpecs\":[],\"attributes\":[],\"extendCode\":\"\",\"barCode\":\"\",\"weight\":1.0,\"userPrice\":0.0,\"shopPrice\":0.0,\"vfoodId\":55743525},{\"id\":102279285,\"skuId\":54120341809,\"name\":\"银耳汤\",\"categoryId\":1,\"price\":3.0,\"quantity\":1,\"total\":3.0,\"additions\":[],\"newSpecs\":[],\"attributes\":[],\"extendCode\":\"\",\"barCode\":\"\",\"weight\":1.0,\"userPrice\":0.0,\"shopPrice\":0.0,\"vfoodId\":55744205},{\"id\":527549289,\"skuId\":140570471729,\"name\":\"紫薯饼\",\"categoryId\":1,\"price\":1.5,\"quantity\":2,\"total\":3.0,\"additions\":[],\"newSpecs\":[],\"attributes\":[],\"extendCode\":\"\",\"barCode\":\"\",\"weight\":1.0,\"userPrice\":0.0,\"shopPrice\":0.0,\"vfoodId\":523070357},{\"id\":102842354,\"skuId\":54120386865,\"name\":\"凉糕\",\"categoryId\":1,\"price\":5.0,\"quantity\":1,\"total\":5.0,\"additions\":[],\"newSpecs\":[],\"attributes\":[],\"extendCode\":\"\",\"barCode\":\"\",\"weight\":1.0,\"userPrice\":0.0,\"shopPrice\":0.0,\"vfoodId\":55895196},{\"id\":102278570,\"skuId\":54120338737,\"name\":\"茶叶蛋\",\"categoryId\":1,\"price\":2.0,\"quantity\":1,\"total\":2.0,\"additions\":[],\"newSpecs\":[],\"attributes\":[],\"extendCode\":\"\",\"barCode\":\"\",\"weight\":1.0,\"userPrice\":0.0,\"shopPrice\":0.0,\"vfoodId\":55743921}]},{\"name\":\"其它费用\",\"type\":\"extra\",\"items\":[{\"id\":-70000,\"skuId\":-1,\"name\":\"餐盒\",\"categoryId\":102,\"price\":4.0,\"quantity\":1,\"total\":4.0,\"additions\":[],\"newSpecs\":null,\"attributes\":null,\"extendCode\":\"\",\"barCode\":\"\",\"weight\":null,\"userPrice\":0.0,\"shopPrice\":0.0,\"vfoodId\":0}]}],\"invoice\":null,\"book\":false,\"onlinePaid\":true,\"railwayAddress\":null,\"phoneList\":[\"13559345670\"],\"shopId\":1044143,\"shopName\":\"龙麦轩包粥坊（南坪东路店）\",\"daySn\":30,\"status\":\"unprocessed\",\"refundStatus\":\"noRefund\",\"userId\":440501554,\"userIdStr\":\"440501554\",\"totalPrice\":31.7,\"originalPrice\":52.5,\"consignee\":\"林**\",\"deliveryGeo\":\"106.58623891,29.53747499\",\"deliveryPoiAddress\":\"重庆国瑞中心重庆万豪酒店3206\",\"invoiced\":false,\"income\":24.4,\"serviceRate\":0.17,\"serviceFee\":-5.1,\"hongbao\":0.0,\"packageFee\":4.0,\"activityTotal\":-20.8,\"shopPart\":-20.0,\"elemePart\":-0.8,\"downgraded\":false,\"vipDeliveryFeeDiscount\":0.0,\"openId\":\"1Z1\",\"secretPhoneExpireTime\":\"2018-07-08T21:24:42\",\"orderActivities\":[{\"categoryId\":12,\"name\":\"在线支付立减优惠\",\"amount\":-18.0,\"elemePart\":0.0,\"restaurantPart\":-18.0,\"familyPart\":0.0,\"id\":1247288489,\"orderAllPartiesPartList\":[{\"partName\":\"商家补贴\",\"partAmount\":\"18.0\"}]},{\"categoryId\":15,\"name\":\"商家代金券抵扣\",\"amount\":-2.8,\"elemePart\":-0.8,\"restaurantPart\":-2.0,\"familyPart\":0.0,\"id\":400001190769859214,\"orderAllPartiesPartList\":[]}],\"invoiceType\":null,\"taxpayerId\":\"\",\"coldBoxFee\":0.0,\"cancelOrderDescription\":null,\"cancelOrderCreatedAt\":null,\"orderCommissions\":[]}");
        elemeCallbackMessage.setShopId(BigInteger.valueOf(1044143));
        elemeCallbackMessage.setTimestamp(new Date());
        elemeCallbackMessage.setSignature("74058D145AD4496ECEDB42149CE3EB22");
        elemeCallbackMessage.setUserId(BigInteger.valueOf(283166468060386111L));
        elemeService.saveElemeOrder(elemeCallbackMessage, UUID.randomUUID().toString());
        return Constants.SUCCESS;
    }
}
