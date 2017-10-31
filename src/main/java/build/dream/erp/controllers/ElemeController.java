package build.dream.erp.controllers;

import build.dream.common.api.ApiRest;
import build.dream.common.controllers.BasicController;
import build.dream.common.erp.domains.Branch;
import build.dream.common.erp.domains.ElemeOrder;
import build.dream.common.erp.domains.GoodsCategory;
import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.ConfigurationUtils;
import build.dream.common.utils.GsonUtils;
import build.dream.common.utils.LogUtils;
import build.dream.erp.constants.Constants;
import build.dream.erp.models.eleme.AgreeRefundLiteModel;
import build.dream.erp.models.eleme.CancelOrderLiteModel;
import build.dream.erp.models.eleme.DisagreeRefundLiteModel;
import build.dream.erp.services.BranchService;
import build.dream.erp.services.ElemeService;
import build.dream.erp.utils.ElemeUtils;
import net.sf.json.JSONObject;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "/eleme")
public class ElemeController extends BasicController {
    private static final int[] ELEME_ORDER_STATE_CHANGE_MESSAGE_TYPES = {12, 14, 15, 17, 18};
    private static final int[] ELEME_REFUND_ORDER_MESSAGE_TYPES = {20, 21, 22, 23, 24, 25, 26, 30, 31, 32, 33, 34, 35, 36};
    private static final int[] ELEME_DELIVERY_ORDER_STATE_CHANGE_MESSAGE_TYPES = {51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76};
    private static final int[] ELEME_SHOP_STATE_CHANGE_MESSAGE_TYPES = {91, 92, 100};
    @Autowired
    private ElemeService elemeService;
    @Autowired
    private BranchService branchService;

    @RequestMapping(value = "/tenantAuthorize")
    @ResponseBody
    public String tenantAuthorize() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            String tenantId = requestParameters.get("tenantId");
            Validate.notNull(tenantId, "参数(tenantId)不能为空！");

            String branchId = requestParameters.get("branchId");
            Validate.notNull(branchId, "参数(branchId)不能为空！");

            apiRest = elemeService.tenantAuthorize(BigInteger.valueOf(Long.valueOf(tenantId)), BigInteger.valueOf(Long.valueOf(branchId)));
        } catch (Exception e) {
            LogUtils.error("生成授权链接失败", controllerSimpleName, "tenantAuthorize", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    @RequestMapping(value = "/handleOrderCallback")
    @ResponseBody
    public String handleOrderCallback() {
        String returnValue = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            String orderCallbackRequestBody = requestParameters.get("orderCallbackRequestBody");
            orderCallbackRequestBody = "{\"requestId\":\"200007211657667333\",\"type\":10,\"appId\":65929831,\"message\":\"{\\\"id\\\":\\\"3014728511382982849\\\",\\\"orderId\\\":\\\"3014728511382982849\\\",\\\"address\\\":\\\"南昌大学共青学院-学生公寓一栋111\\\",\\\"createdAt\\\":\\\"2017-10-30T20:08:07\\\",\\\"activeAt\\\":\\\"2017-10-30T20:08:07\\\",\\\"deliverFee\\\":3.0,\\\"deliverTime\\\":null,\\\"description\\\":\\\"\\\",\\\"groups\\\":[{\\\"name\\\":\\\"1号篮子\\\",\\\"type\\\":\\\"normal\\\",\\\"items\\\":[{\\\"id\\\":1298577560,\\\"skuId\\\":200000100943422213,\\\"name\\\":\\\"台湾烤肠\\\",\\\"categoryId\\\":1,\\\"price\\\":5.0,\\\"quantity\\\":2,\\\"total\\\":10.0,\\\"additions\\\":[],\\\"newSpecs\\\":[],\\\"attributes\\\":[],\\\"extendCode\\\":\\\"\\\",\\\"barCode\\\":\\\"\\\",\\\"weight\\\":1.0,\\\"userPrice\\\":0.0,\\\"shopPrice\\\":0.0,\\\"vfoodId\\\":1290927110},{\\\"id\\\":1288279667,\\\"skuId\\\":200000090398379781,\\\"name\\\":\\\"烧仙草奶茶-大杯热\\\",\\\"categoryId\\\":1,\\\"price\\\":14.0,\\\"quantity\\\":1,\\\"total\\\":14.0,\\\"additions\\\":[],\\\"newSpecs\\\":[],\\\"attributes\\\":[],\\\"extendCode\\\":\\\"\\\",\\\"barCode\\\":\\\"\\\",\\\"weight\\\":1.0,\\\"userPrice\\\":0.0,\\\"shopPrice\\\":0.0,\\\"vfoodId\\\":671477772}]}],\\\"invoice\\\":null,\\\"book\\\":false,\\\"onlinePaid\\\":true,\\\"railwayAddress\\\":null,\\\"phoneList\\\":[\\\"15079241539\\\"],\\\"shopId\\\":156898280,\\\"shopName\\\":\\\"Honey cup哈尼卡布\\\",\\\"daySn\\\":6,\\\"status\\\":\\\"unprocessed\\\",\\\"refundStatus\\\":\\\"noRefund\\\",\\\"userId\\\":201506988,\\\"totalPrice\\\":17.0,\\\"originalPrice\\\":27.0,\\\"consignee\\\":\\\"马成鹏(先生)\\\",\\\"deliveryGeo\\\":\\\"115.80731995,29.22636934\\\",\\\"deliveryPoiAddress\\\":\\\"南昌大学共青学院-学生公寓一栋111\\\",\\\"invoiced\\\":false,\\\"income\\\":13.6,\\\"serviceRate\\\":0.15,\\\"serviceFee\\\":-2.4,\\\"hongbao\\\":0.0,\\\"packageFee\\\":0.0,\\\"activityTotal\\\":-10.0,\\\"shopPart\\\":-8.0,\\\"elemePart\\\":-2.0,\\\"downgraded\\\":false,\\\"vipDeliveryFeeDiscount\\\":0.0,\\\"openId\\\":\\\"3028Z73140\\\",\\\"secretPhoneExpireTime\\\":null,\\\"orderActivities\\\":[{\\\"categoryId\\\":12,\\\"name\\\":\\\"在线支付立减优惠\\\",\\\"amount\\\":-10.0,\\\"elemePart\\\":0.0,\\\"restaurantPart\\\":0.0,\\\"id\\\":141587914}],\\\"invoiceType\\\":null,\\\"taxpayerId\\\":\\\"\\\",\\\"coldBoxFee\\\":0.0}\",\"shopId\":156898280,\"timestamp\":1509365287868,\"signature\":\"0A1D9E427F0AE7008BA8728E93E30162\",\"userId\":283166468248413847}";
            Validate.notNull(orderCallbackRequestBody, "参数(orderCallbackRequestBody)不能为空！");

            JSONObject orderCallbackJsonObject = JSONObject.fromObject(orderCallbackRequestBody);
            Validate.isTrue(ElemeUtils.checkSignature(orderCallbackJsonObject, ConfigurationUtils.getConfiguration(Constants.ELEME_APP_SECRET)), "签名校验未通过！");

            BigInteger shopId = BigInteger.valueOf(orderCallbackJsonObject.getLong("shopId"));
            String message = orderCallbackJsonObject.getString("message");
            Integer type = orderCallbackJsonObject.getInt("type");

            ApiRest apiRest = null;
            if (type == 10) {
                apiRest = elemeService.saveElemeOrder(shopId, message, type);
            } else if (ArrayUtils.contains(ELEME_ORDER_STATE_CHANGE_MESSAGE_TYPES, type)) {
                apiRest = elemeService.handleElemeOrderStateChangeMessage(shopId, message, type);
            } else if (ArrayUtils.contains(ELEME_REFUND_ORDER_MESSAGE_TYPES, type)) {
                apiRest = elemeService.handleElemeRefundOrderMessage(shopId, message, type);
            } else if (ArrayUtils.contains(ELEME_DELIVERY_ORDER_STATE_CHANGE_MESSAGE_TYPES, type)) {
                apiRest = elemeService.handleElemeDeliveryOrderStateChangeMessage(shopId, message, type);
            } else if (ArrayUtils.contains(ELEME_SHOP_STATE_CHANGE_MESSAGE_TYPES, type)) {
                apiRest = elemeService.handleElemeShopStateChangeMessage(shopId, message, type);
            }
            Validate.isTrue(apiRest.isSuccessful(), apiRest.getError());
            returnValue = Constants.ELEME_ORDER_CALLBACK_SUCCESS_RETURN_VALUE;
        } catch (Exception e) {
            LogUtils.error("订单回调处理失败", controllerSimpleName, "handleOrderCallback", e, requestParameters);
            returnValue = e.getMessage();
        }
        return returnValue;
    }

    @RequestMapping(value = "/uploadImage")
    @ResponseBody
    public String uploadImage() {
        return null;
    }

    @RequestMapping(value = "/uploadImageWithRemoteUrl ")
    @ResponseBody
    public String uploadImageWithRemoteUrl() {
        return null;
    }

    @RequestMapping(value = "/getUploadedUrl")
    @ResponseBody
    public String getUploadedUrl() {
        return null;
    }

    @RequestMapping(value = "/getShopCategories")
    @ResponseBody
    public String getShopCategories() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            String tenantId = requestParameters.get("tenantId");
            Validate.notNull(tenantId, "参数(tenantId)不能为空！");

            String branchId = requestParameters.get("branchId");
            Validate.notNull(branchId, "参数(branchId)不能为空！");

            Branch branch = elemeService.findBranchInfo(BigInteger.valueOf(Long.valueOf(tenantId)), BigInteger.valueOf(Long.valueOf(branchId)));
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("shopId", branch.getShopId());
            apiRest = ElemeUtils.callElemeSystem("1", tenantId.toString(), branch.getType().toString(), branchId.toString(), "eleme.product.category.getShopCategories", params);
        } catch (Exception e) {
            LogUtils.error("查询店铺商品分类失败", controllerSimpleName, "getShopCategories", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    @RequestMapping(value = "/getShopCategoriesWithChildren")
    @ResponseBody
    public String getShopCategoriesWithChildren() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            String tenantId = requestParameters.get("tenantId");
            Validate.notNull(tenantId, "参数(tenantId)不能为空！");

            String branchId = requestParameters.get("branchId");
            Validate.notNull(branchId, "参数(branchId)不能为空！");

            Branch branch = elemeService.findBranchInfo(BigInteger.valueOf(Long.valueOf(tenantId)), BigInteger.valueOf(Long.valueOf(branchId)));
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("shopId", branch.getShopId());
            apiRest = ElemeUtils.callElemeSystem("1", tenantId, branch.getType().toString(), branchId, "eleme.product.category.getShopCategoriesWithChildren", params);
        } catch (Exception e) {
            LogUtils.error("查询店铺商品分类失败", controllerSimpleName, "getShopCategoriesWithChildren", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    @RequestMapping(value = "/getCategory")
    @ResponseBody
    public String getCategory() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            String tenantId = requestParameters.get("tenantId");
            Validate.notNull(tenantId, "参数(tenantId)不能为空！");

            String branchId = requestParameters.get("branchId");
            Validate.notNull(branchId, "参数(branchId)不能为空！");
        } catch (Exception e) {
            LogUtils.error("查询商品分类详情失败", controllerSimpleName, "getCategory", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    @RequestMapping(value = "/getCategoryWithChildren")
    @ResponseBody
    public String getCategoryWithChildren() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            String tenantId = requestParameters.get("tenantId");
            Validate.notNull(tenantId, "参数(tenantId)不能为空！");

            String branchId = requestParameters.get("branchId");
            Validate.notNull(branchId, "参数(branchId)不能为空！");
        } catch (Exception e) {
            LogUtils.error("查询商品分类详情失败", controllerSimpleName, "getCategoryWithChildren", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    @RequestMapping(value = "/createCategory")
    @ResponseBody
    public String createCategory() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            String tenantId = requestParameters.get("tenantId");
            Validate.notNull(tenantId, "参数(tenantId)不能为空！");

            String branchId = requestParameters.get("branchId");
            Validate.notNull(branchId, "参数(branchId)不能为空！");

            String categoryId = requestParameters.get("categoryId");
            Validate.notNull(categoryId, ApplicationHandler.obtainParameterErrorMessage("categoryId"));

            BigInteger bigIntegerTenantId = NumberUtils.createBigInteger(tenantId);
            BigInteger bigIntegerBranchId = NumberUtils.createBigInteger(branchId);
            BigInteger bigIntegerCategoryId = NumberUtils.createBigInteger(categoryId);

            Branch branch = elemeService.findBranchInfo(bigIntegerTenantId, bigIntegerBranchId);
            GoodsCategory goodsCategory = elemeService.findGoodsCategoryInfo(bigIntegerTenantId, bigIntegerBranchId, bigIntegerCategoryId);
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("shopId", branch.getShopId());
            params.put("name", goodsCategory.getName());
            params.put("description", goodsCategory.getDescription());
            apiRest = ElemeUtils.callElemeSystem("1", tenantId, branch.getType().toString(), branchId, "eleme.product.category.createCategory", params);
        } catch (Exception e) {
            LogUtils.error("添加商品分类失败", controllerSimpleName, "createCategory", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    @RequestMapping(value = "/createCategoryWithChildren")
    @ResponseBody
    public String createCategoryWithChildren() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            String tenantId = requestParameters.get("tenantId");
            Validate.notNull(tenantId, "参数(tenantId)不能为空！");

            String branchId = requestParameters.get("branchId");
            Validate.notNull(branchId, "参数(branchId)不能为空！");

            String categoryId = requestParameters.get("categoryId");
            Validate.notNull(categoryId, ApplicationHandler.obtainParameterErrorMessage("categoryId"));

            BigInteger bigIntegerTenantId = NumberUtils.createBigInteger(tenantId);
            BigInteger bigIntegerBranchId = NumberUtils.createBigInteger(branchId);
            BigInteger bigIntegerCategoryId = NumberUtils.createBigInteger(categoryId);

            Branch branch = elemeService.findBranchInfo(bigIntegerTenantId, bigIntegerBranchId);
            GoodsCategory goodsCategory = elemeService.findGoodsCategoryInfo(bigIntegerTenantId, bigIntegerBranchId, bigIntegerCategoryId);
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("shopId", branch.getShopId());
            params.put("name", goodsCategory.getName());
            params.put("parentId", goodsCategory.getParentId());
            params.put("description", goodsCategory.getDescription());
            apiRest = ElemeUtils.callElemeSystem("1", tenantId, branch.getType().toString(), branchId, "eleme.product.category.createCategoryWithChildren", params);
        } catch (Exception e) {
            LogUtils.error("添加商品分类失败", controllerSimpleName, "createCategoryWithChildren", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    @RequestMapping(value = "/updateCategory")
    @ResponseBody
    public String updateCategory() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            String tenantId = requestParameters.get("tenantId");
            Validate.notNull(tenantId, "参数(tenantId)不能为空！");

            String branchId = requestParameters.get("branchId");
            Validate.notNull(branchId, "参数(branchId)不能为空！");

            String categoryId = requestParameters.get("categoryId");
            Validate.notNull(categoryId, ApplicationHandler.obtainParameterErrorMessage("categoryId"));

            BigInteger bigIntegerTenantId = NumberUtils.createBigInteger(tenantId);
            BigInteger bigIntegerBranchId = NumberUtils.createBigInteger(branchId);
            BigInteger bigIntegerCategoryId = NumberUtils.createBigInteger(categoryId);

            Branch branch = elemeService.findBranchInfo(bigIntegerTenantId, bigIntegerBranchId);
            GoodsCategory goodsCategory = elemeService.findGoodsCategoryInfo(bigIntegerTenantId, bigIntegerBranchId, bigIntegerCategoryId);
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("categoryId", goodsCategory.getId());
            params.put("name", goodsCategory.getName());
            params.put("description", goodsCategory.getDescription());
            apiRest = ElemeUtils.callElemeSystem("1", tenantId, branch.getType().toString(), branchId, "eleme.product.category.updateCategory", params);
        } catch (Exception e) {
            LogUtils.error("更新商品分类失败", controllerSimpleName, "updateCategory", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    @RequestMapping(value = "/updateCategoryWithChildren")
    @ResponseBody
    public String updateCategoryWithChildren() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            String tenantId = requestParameters.get("tenantId");
            Validate.notNull(tenantId, "参数(tenantId)不能为空！");

            String branchId = requestParameters.get("branchId");
            Validate.notNull(branchId, "参数(branchId)不能为空！");

            String categoryId = requestParameters.get("categoryId");
            Validate.notNull(categoryId, ApplicationHandler.obtainParameterErrorMessage("categoryId"));

            BigInteger bigIntegerTenantId = NumberUtils.createBigInteger(tenantId);
            BigInteger bigIntegerBranchId = NumberUtils.createBigInteger(branchId);
            BigInteger bigIntegerCategoryId = NumberUtils.createBigInteger(categoryId);

            Branch branch = elemeService.findBranchInfo(bigIntegerTenantId, bigIntegerBranchId);
            GoodsCategory goodsCategory = elemeService.findGoodsCategoryInfo(bigIntegerTenantId, bigIntegerBranchId, bigIntegerCategoryId);
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("categoryId", goodsCategory.getId());
            params.put("name", goodsCategory.getName());
            params.put("parentId", goodsCategory.getParentId());
            params.put("description", goodsCategory.getDescription());
            apiRest = ElemeUtils.callElemeSystem("1", tenantId, branch.getType().toString(), branchId, "eleme.product.category.updateCategoryWithChildren", params);
        } catch (Exception e) {
            LogUtils.error("更新商品分类失败", controllerSimpleName, "updateCategoryWithChildren", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    @RequestMapping(value = "/removeCategory")
    @ResponseBody
    public String removeCategory() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            String tenantId = requestParameters.get("tenantId");
            Validate.notNull(tenantId, "参数(tenantId)不能为空！");

            String branchId = requestParameters.get("branchId");
            Validate.notNull(branchId, "参数(branchId)不能为空！");

            String categoryId = requestParameters.get("categoryId");
            Validate.notNull(categoryId, ApplicationHandler.obtainParameterErrorMessage("categoryId"));

            BigInteger bigIntegerTenantId = NumberUtils.createBigInteger(tenantId);
            BigInteger bigIntegerBranchId = NumberUtils.createBigInteger(branchId);
            BigInteger bigIntegerCategoryId = NumberUtils.createBigInteger(categoryId);

            Branch branch = elemeService.findBranchInfo(bigIntegerTenantId, bigIntegerBranchId);
            GoodsCategory goodsCategory = elemeService.findGoodsCategoryInfo(bigIntegerTenantId, bigIntegerBranchId, bigIntegerCategoryId);
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("categoryId", goodsCategory.getId());
            apiRest = ElemeUtils.callElemeSystem("1", tenantId, branch.getType().toString(), branchId, "eleme.product.category.removeCategory", params);
        } catch (Exception e) {
            LogUtils.error("删除商品分类失败", controllerSimpleName, "removeCategory", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    @RequestMapping(value = "/confirmOrderLite")
    @ResponseBody
    public String confirmOrderLite() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            String tenantId = requestParameters.get("tenantId");
            Validate.notNull(tenantId, "参数(tenantId)不能为空！");

            String branchId = requestParameters.get("branchId");
            Validate.notNull(branchId, "参数(branchId)不能为空！");

            String elemeOrderId = requestParameters.get("elemeOrderId");
            Validate.notNull(elemeOrderId, "参数(elemeOrderId)不能为空！");

            BigInteger bigIntegerTenantId = NumberUtils.createBigInteger(tenantId);
            BigInteger bigIntegerBranchId = NumberUtils.createBigInteger(branchId);
            BigInteger bigIntegerElemeOrderId = NumberUtils.createBigInteger(elemeOrderId);

            Branch branch = elemeService.findBranchInfo(bigIntegerTenantId, bigIntegerBranchId);
            ElemeOrder elemeOrder = elemeService.findElemeOrderInfo(bigIntegerTenantId, bigIntegerBranchId, bigIntegerElemeOrderId);

            Map<String, Object> params = new HashMap<String, Object>();
            params.put("orderId", elemeOrder.getOrderId());
            apiRest = ElemeUtils.callElemeSystem("1", tenantId, branch.getType().toString(), branchId, "eleme.order.confirmOrderLite", params);
        } catch (Exception e) {
            LogUtils.error("确认订单失败", controllerSimpleName, "confirmOrderLite", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    @RequestMapping(value = "/cancelOrderLite")
    @ResponseBody
    public String cancelOrderLite() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            CancelOrderLiteModel cancelOrderLiteModel = ApplicationHandler.instantiateObject(CancelOrderLiteModel.class, requestParameters);
            cancelOrderLiteModel.validateAndThrow();

            Branch branch = elemeService.findBranchInfo(cancelOrderLiteModel.getTenantId(), cancelOrderLiteModel.getBranchId());
            ElemeOrder elemeOrder = elemeService.findElemeOrderInfo(cancelOrderLiteModel.getTenantId(), cancelOrderLiteModel.getBranchId(), cancelOrderLiteModel.getElemeOrderId());

            Map<String, Object> params = new HashMap<String, Object>();
            params.put("orderId", elemeOrder.getOrderId());
            apiRest = ElemeUtils.callElemeSystem("1", cancelOrderLiteModel.getTenantId().toString(), branch.getType().toString(), branch.getId().toString(), "eleme.order.cancelOrderLite", params);
        } catch (Exception e) {
            LogUtils.error("取消订单失败", controllerSimpleName, "cancelOrderLite", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    @RequestMapping(value = "/agreeRefundLite")
    @ResponseBody
    public String agreeRefundLite() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            AgreeRefundLiteModel agreeRefundLiteModel = ApplicationHandler.instantiateObject(AgreeRefundLiteModel.class, requestParameters);
            agreeRefundLiteModel.validateAndThrow();

            Branch branch = elemeService.findBranchInfo(agreeRefundLiteModel.getTenantId(), agreeRefundLiteModel.getBranchId());
            ElemeOrder elemeOrder = elemeService.findElemeOrderInfo(agreeRefundLiteModel.getTenantId(), agreeRefundLiteModel.getBranchId(), agreeRefundLiteModel.getElemeOrderId());

            Map<String, Object> params = new HashMap<String, Object>();
            params.put("orderId", elemeOrder.getOrderId());
            apiRest = ElemeUtils.callElemeSystem("1", agreeRefundLiteModel.getTenantId().toString(), branch.getType().toString(), branch.getId().toString(), "eleme.order.agreeRefundLite", params);
        } catch (Exception e) {
            LogUtils.error("同意退单/同意取消单失败", controllerSimpleName, "agreeRefundLite", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    @RequestMapping(value = "/disagreeRefundLite")
    @ResponseBody
    public String disagreeRefundLite() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            DisagreeRefundLiteModel disagreeRefundLiteModel = ApplicationHandler.instantiateObject(DisagreeRefundLiteModel.class, requestParameters);
            disagreeRefundLiteModel.validateAndThrow();

            Branch branch = elemeService.findBranchInfo(disagreeRefundLiteModel.getTenantId(), disagreeRefundLiteModel.getBranchId());
            ElemeOrder elemeOrder = elemeService.findElemeOrderInfo(disagreeRefundLiteModel.getTenantId(), disagreeRefundLiteModel.getBranchId(), disagreeRefundLiteModel.getElemeOrderId());

            Map<String, Object> params = new HashMap<String, Object>();
            params.put("orderId", elemeOrder.getOrderId());
            apiRest = ElemeUtils.callElemeSystem("1", disagreeRefundLiteModel.getTenantId().toString(), branch.getType().toString(), branch.getId().toString(), "eleme.order.disagreeRefundLite", params);
        } catch (Exception e) {
            LogUtils.error("不同意退单/不同意取消单失败", controllerSimpleName, "disagreeRefundLite", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }
}
