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
            elemeCallbackMessage.setMessage("{\"id\":\"1222477699114757177\",\"orderId\":\"1222477699114757177\",\"address\":\"东日天成投资咨询泉州南路枫情小镇小区26号网点\",\"createdAt\":\"2018-07-10T11:48:24\",\"activeAt\":\"2018-07-10T11:48:24\",\"deliverFee\":5.0,\"deliverTime\":null,\"description\":\"\",\"groups\":[{\"name\":\"用户 发起人1号... 的篮子\",\"type\":\"normal\",\"items\":[{\"id\":722083954,\"skuId\":339773968615,\"name\":\"台湾烤肠\",\"categoryId\":1,\"price\":2.5,\"quantity\":1,\"total\":2.5,\"additions\":[],\"newSpecs\":[],\"attributes\":[],\"extendCode\":\"\",\"barCode\":\"\",\"weight\":1.0,\"userPrice\":0.0,\"shopPrice\":0.0,\"vfoodId\":692674992},{\"id\":722063619,\"skuId\":339753145575,\"name\":\"甜不辣\",\"categoryId\":1,\"price\":3.0,\"quantity\":1,\"total\":3.0,\"additions\":[],\"newSpecs\":[],\"attributes\":[],\"extendCode\":\"\",\"barCode\":\"\",\"weight\":1.0,\"userPrice\":0.0,\"shopPrice\":0.0,\"vfoodId\":692649776},{\"id\":722099570,\"skuId\":339789959399,\"name\":\"牛肚\",\"categoryId\":1,\"price\":4.0,\"quantity\":1,\"total\":4.0,\"additions\":[],\"newSpecs\":[],\"attributes\":[],\"extendCode\":\"\",\"barCode\":\"\",\"weight\":1.0,\"userPrice\":0.0,\"shopPrice\":0.0,\"vfoodId\":692655573},{\"id\":727867374,\"skuId\":345696190695,\"name\":\"腊肠\",\"categoryId\":1,\"price\":3.0,\"quantity\":1,\"total\":3.0,\"additions\":[],\"newSpecs\":[],\"attributes\":[],\"extendCode\":\"\",\"barCode\":\"\",\"weight\":1.0,\"userPrice\":0.0,\"shopPrice\":0.0,\"vfoodId\":697977268},{\"id\":722179026,\"skuId\":339871322343,\"name\":\"宽粉\",\"categoryId\":1,\"price\":2.0,\"quantity\":1,\"total\":2.0,\"additions\":[],\"newSpecs\":[],\"attributes\":[],\"extendCode\":\"\",\"barCode\":\"\",\"weight\":1.0,\"userPrice\":0.0,\"shopPrice\":0.0,\"vfoodId\":692729318},{\"id\":725482462,\"skuId\":343254040807,\"name\":\"各位亲，套餐不包含米饭！需要米饭者请单点\",\"categoryId\":1,\"price\":2.0,\"quantity\":1,\"total\":2.0,\"additions\":[],\"newSpecs\":[],\"attributes\":[],\"extendCode\":\"\",\"barCode\":\"\",\"weight\":1.0,\"userPrice\":0.0,\"shopPrice\":0.0,\"vfoodId\":695792720},{\"id\":722081668,\"skuId\":339771627751,\"name\":\"金针菇\",\"categoryId\":1,\"price\":2.0,\"quantity\":1,\"total\":2.0,\"additions\":[],\"newSpecs\":[],\"attributes\":[],\"extendCode\":\"\",\"barCode\":\"\",\"weight\":1.0,\"userPrice\":0.0,\"shopPrice\":0.0,\"vfoodId\":692672862},{\"id\":722162144,\"skuId\":339854035175,\"name\":\"香菜\",\"categoryId\":1,\"price\":2.0,\"quantity\":1,\"total\":2.0,\"additions\":[],\"newSpecs\":[],\"attributes\":[],\"extendCode\":\"\",\"barCode\":\"\",\"weight\":1.0,\"userPrice\":0.0,\"shopPrice\":0.0,\"vfoodId\":692757378},{\"id\":722471104,\"skuId\":340170410215,\"name\":\"特辣，(附带打包盒)\",\"categoryId\":1,\"price\":0.0,\"quantity\":1,\"total\":0.0,\"additions\":[],\"newSpecs\":[],\"attributes\":[],\"extendCode\":\"\",\"barCode\":\"\",\"weight\":1.0,\"userPrice\":0.0,\"shopPrice\":0.0,\"vfoodId\":693023979}]},{\"name\":\"用户 发起人2号... 的篮子\",\"type\":\"normal\",\"items\":[{\"id\":725482462,\"skuId\":343254040807,\"name\":\"各位亲，套餐不包含米饭！需要米饭者请单点\",\"categoryId\":1,\"price\":2.0,\"quantity\":1,\"total\":2.0,\"additions\":[],\"newSpecs\":[],\"attributes\":[],\"extendCode\":\"\",\"barCode\":\"\",\"weight\":1.0,\"userPrice\":0.0,\"shopPrice\":0.0,\"vfoodId\":695792720},{\"id\":722088953,\"skuId\":339779087591,\"name\":\"鱼豆腐\",\"categoryId\":1,\"price\":2.5,\"quantity\":1,\"total\":2.5,\"additions\":[],\"newSpecs\":[],\"attributes\":[],\"extendCode\":\"\",\"barCode\":\"\",\"weight\":1.0,\"userPrice\":0.0,\"shopPrice\":0.0,\"vfoodId\":692679892},{\"id\":722476028,\"skuId\":340175452391,\"name\":\"酱香型，不辣(附带打包盒)\",\"categoryId\":1,\"price\":0.0,\"quantity\":1,\"total\":0.0,\"additions\":[],\"newSpecs\":[],\"attributes\":[],\"extendCode\":\"\",\"barCode\":\"\",\"weight\":1.0,\"userPrice\":0.0,\"shopPrice\":0.0,\"vfoodId\":693028757},{\"id\":722081668,\"skuId\":339771627751,\"name\":\"金针菇\",\"categoryId\":1,\"price\":2.0,\"quantity\":1,\"total\":2.0,\"additions\":[],\"newSpecs\":[],\"attributes\":[],\"extendCode\":\"\",\"barCode\":\"\",\"weight\":1.0,\"userPrice\":0.0,\"shopPrice\":0.0,\"vfoodId\":692672862},{\"id\":722158233,\"skuId\":339850030311,\"name\":\"平菇\",\"categoryId\":1,\"price\":2.0,\"quantity\":1,\"total\":2.0,\"additions\":[],\"newSpecs\":[],\"attributes\":[],\"extendCode\":\"\",\"barCode\":\"\",\"weight\":1.0,\"userPrice\":0.0,\"shopPrice\":0.0,\"vfoodId\":692748457},{\"id\":722138606,\"skuId\":339829932263,\"name\":\"木耳\",\"categoryId\":1,\"price\":2.0,\"quantity\":1,\"total\":2.0,\"additions\":[],\"newSpecs\":[],\"attributes\":[],\"extendCode\":\"\",\"barCode\":\"\",\"weight\":1.0,\"userPrice\":0.0,\"shopPrice\":0.0,\"vfoodId\":692728926},{\"id\":722063619,\"skuId\":339753145575,\"name\":\"甜不辣\",\"categoryId\":1,\"price\":3.0,\"quantity\":1,\"total\":3.0,\"additions\":[],\"newSpecs\":[],\"attributes\":[],\"extendCode\":\"\",\"barCode\":\"\",\"weight\":1.0,\"userPrice\":0.0,\"shopPrice\":0.0,\"vfoodId\":692649776},{\"id\":722189125,\"skuId\":339881663719,\"name\":\"鱼丸\",\"categoryId\":1,\"price\":2.5,\"quantity\":1,\"total\":2.5,\"additions\":[],\"newSpecs\":[],\"attributes\":[],\"extendCode\":\"\",\"barCode\":\"\",\"weight\":1.0,\"userPrice\":0.0,\"shopPrice\":0.0,\"vfoodId\":692744237},{\"id\":722163606,\"skuId\":339855532263,\"name\":\"午餐肉\",\"categoryId\":1,\"price\":3.0,\"quantity\":1,\"total\":3.0,\"additions\":[],\"newSpecs\":[],\"attributes\":[],\"extendCode\":\"\",\"barCode\":\"\",\"weight\":1.0,\"userPrice\":0.0,\"shopPrice\":0.0,\"vfoodId\":692758722}]},{\"name\":\"其它费用\",\"type\":\"extra\",\"items\":[{\"id\":-70000,\"skuId\":-1,\"name\":\"餐盒\",\"categoryId\":102,\"price\":3.0,\"quantity\":1,\"total\":3.0,\"additions\":[],\"newSpecs\":null,\"attributes\":null,\"extendCode\":\"\",\"barCode\":\"\",\"weight\":null,\"userPrice\":0.0,\"shopPrice\":0.0,\"vfoodId\":0}]}],\"invoice\":null,\"book\":false,\"onlinePaid\":true,\"railwayAddress\":null,\"phoneList\":[\"18266657352\"],\"shopId\":151805667,\"shopName\":\"辣五味麻辣香锅\",\"daySn\":6,\"status\":\"unprocessed\",\"refundStatus\":\"noRefund\",\"userId\":263377385,\"userIdStr\":\"263377385\",\"totalPrice\":22.5,\"originalPrice\":47.5,\"consignee\":\"赵**\",\"deliveryGeo\":\"120.04093895,36.28982906\",\"deliveryPoiAddress\":\"东日天成投资咨询泉州南路枫情小镇小区26号网点\",\"invoiced\":false,\"income\":20.91,\"serviceRate\":0.18,\"serviceFee\":-4.59,\"hongbao\":0.0,\"packageFee\":3.0,\"activityTotal\":-25.0,\"shopPart\":-17.0,\"elemePart\":-8.0,\"downgraded\":false,\"vipDeliveryFeeDiscount\":0.0,\"openId\":\"1Z1\",\"secretPhoneExpireTime\":\"2018-07-10T14:48:25\",\"orderActivities\":[{\"categoryId\":12,\"name\":\"在线支付立减优惠\",\"amount\":-20.0,\"elemePart\":-5.0,\"restaurantPart\":-15.0,\"familyPart\":0.0,\"id\":1291917226,\"orderAllPartiesPartList\":[{\"partName\":\"商家补贴\",\"partAmount\":\"15.0\"},{\"partName\":\"平台补贴\",\"partAmount\":\"5.0\"}]},{\"categoryId\":15,\"name\":\"商家代金券抵扣\",\"amount\":-5.0,\"elemePart\":-3.0,\"restaurantPart\":-2.0,\"familyPart\":0.0,\"id\":1126860207724,\"orderAllPartiesPartList\":[]}],\"invoiceType\":null,\"taxpayerId\":\"\",\"coldBoxFee\":0.0,\"cancelOrderDescription\":null,\"cancelOrderCreatedAt\":null,\"orderCommissions\":[],\"baiduWaimai\":false,\"userExtraInfo\":{\"giverPhone\":\"\",\"greeting\":\"\",\"remark\":\"\",\"invoiceTitle\":null},\"consigneePhones\":[]}");
            elemeCallbackMessage.setShopId(BigInteger.valueOf(1044143));
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
