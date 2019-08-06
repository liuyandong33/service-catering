package build.dream.catering.controllers;

import build.dream.catering.constants.Constants;
import build.dream.catering.models.eleme.*;
import build.dream.catering.services.ElemeService;
import build.dream.common.annotations.ApiRestAction;
import build.dream.common.controllers.BasicController;
import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.CommonUtils;
import build.dream.common.utils.ConfigurationUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "/eleme")
public class ElemeController extends BasicController {
    @Autowired
    private ElemeService elemeService;

    /**
     * 商户授权
     *
     * @return
     */
    @RequestMapping(value = "/tenantAuthorize", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = TenantAuthorizeModel.class, serviceClass = ElemeService.class, serviceMethodName = "tenantAuthorize", error = "生成授权链接失败")
    public String tenantAuthorize() {
        return null;
    }

    @RequestMapping(value = "/tenantAuthorizeCallback")
    public ModelAndView tenantAuthorizeCallback() {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        String clientType = requestParameters.get(Constants.CLIENT_TYPE);
        String code = requestParameters.get("code");
        String state = requestParameters.get("state");
        String[] array = state.split("Z");
        String tenantId = array[0];
        String branchId = array[1];
        String userId = array[2];
        String elemeAccountType = array[3];
        elemeService.handleTenantAuthorizeCallback(NumberUtils.createBigInteger(tenantId), NumberUtils.createBigInteger(branchId), NumberUtils.createBigInteger(userId), Integer.parseInt(elemeAccountType), code);
        return buildBindingStoreModelAndView(tenantId, branchId, userId, clientType);
    }

    private ModelAndView buildBindingStoreModelAndView(String tenantId, String branchId, String userId, String clientType) {
        String partitionCode = ConfigurationUtils.getConfiguration(Constants.PARTITION_CODE);
        String apiServiceName = CommonUtils.obtainApiServiceName(clientType);

        String proxyUrl = CommonUtils.getOutsideUrl(apiServiceName, "proxy", "doGetPermitWithUrl");
        String doBindingStoreUrl = CommonUtils.getOutsideUrl(apiServiceName, "proxy", "doPostPermit") + "/" + partitionCode + "/" + Constants.SERVICE_NAME_CATERING + "/eleme/doBindingStore";
        String baseUrl = CommonUtils.getServiceDomain(partitionCode, Constants.SERVICE_NAME_CATERING);

        Map<String, Object> model = new HashMap<String, Object>();

        model.put("tenantId", tenantId);
        model.put("branchId", branchId);
        model.put("userId", userId);
        model.put("proxyUrl", proxyUrl);
        model.put("baseUrl", baseUrl);
        model.put("doBindingStoreUrl", doBindingStoreUrl);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("eleme/bindingStore");
        modelAndView.addAllObjects(model);
        return modelAndView;
    }

    /**
     * 进入门店绑定门店
     *
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/bindingStore")
    public ModelAndView bindingStore() {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        String tenantId = requestParameters.get("tenantId");
        String branchId = requestParameters.get("branchId");
        String userId = requestParameters.get("userId");
        String clientType = requestParameters.get(Constants.CLIENT_TYPE);
        return buildBindingStoreModelAndView(tenantId, branchId, userId, clientType);
    }

    /**
     * 绑定门店
     *
     * @return
     */
    @RequestMapping(value = "/doBindingStore", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = DoBindingStoreModel.class, serviceClass = ElemeService.class, serviceMethodName = "doBindingStore", error = "绑定饿了么门店失败")
    public String doBindingStore() {
        return null;
    }

    /**
     * 获取订单
     *
     * @return
     */
    @RequestMapping(value = "/getOrder", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = GetOrderModel.class, serviceClass = ElemeService.class, serviceMethodName = "getOrder", error = "获取订单失败")
    public String getOrder() {
        return null;
    }

    /**
     * 批量获取订单
     *
     * @return
     */
    @RequestMapping(value = "/batchGetOrders", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = BatchGetOrdersModel.class, serviceClass = ElemeService.class, serviceMethodName = "batchGetOrders", error = "批量获取订单失败")
    public String batchGetOrders() {
        return null;
    }

    /**
     * 确认订单
     *
     * @return
     */
    @RequestMapping(value = "/confirmOrderLite", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = ConfirmOrderLiteModel.class, serviceClass = ElemeService.class, serviceMethodName = "confirmOrderLite", error = "确认订单失败")
    public String confirmOrderLite() {
        return null;
    }

    /**
     * 取消订单
     *
     * @return
     */
    @RequestMapping(value = "/cancelOrderLite", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = CancelOrderLiteModel.class, serviceClass = ElemeService.class, serviceMethodName = "cancelOrderLite", error = "取消订单失败")
    public String cancelOrderLite() {
        return null;
    }

    /**
     * 同意退单/同意取消单(推荐)
     *
     * @return
     */
    @RequestMapping(value = "/agreeRefundLite", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
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
    @RequestMapping(value = "/disagreeRefundLite", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
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
    @RequestMapping(value = "/deliveryBySelfLite", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
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
    @RequestMapping(value = "/noMoreDeliveryLite", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
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
    @RequestMapping(value = "/receivedOrderLite", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
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
    @RequestMapping(value = "/replyReminder", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
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
    @RequestMapping(value = "/getUser", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = GetUserModel.class, serviceClass = ElemeService.class, serviceMethodName = "getUser", error = "获取商户账号信息失败")
    public String getUser() {
        return null;
    }

    /**
     * 查询店铺信息
     *
     * @return
     */
    @RequestMapping(value = "/getShop", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = GetShopModel.class, serviceClass = ElemeService.class, serviceMethodName = "getShop", error = "查询店铺信息失败")
    public String getShop() {
        return null;
    }

    /**
     * 分页获取店铺下的商品
     *
     * @return
     */
    @RequestMapping(value = "/queryItemByPage", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = QueryItemByPageModel.class, serviceClass = ElemeService.class, serviceMethodName = "queryItemByPage", error = "分页获取店铺下的商品失败")
    public String queryItemByPage() {
        return null;
    }

    /**
     * 查询商品详情
     *
     * @return
     */
    @RequestMapping(value = "/getItem", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = GetItemModel.class, serviceClass = ElemeService.class, serviceMethodName = "getItem", error = "查询商品详情失败")
    public String getItem() {
        return null;
    }

    /**
     * 批量查询商品详情
     *
     * @return
     */
    @RequestMapping(value = "/batchGetItems", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = BatchGetItemsModel.class, serviceClass = ElemeService.class, serviceMethodName = "batchGetItems", error = "批量查询商品详情失败")
    public String batchGetItems() {
        return null;
    }
}
