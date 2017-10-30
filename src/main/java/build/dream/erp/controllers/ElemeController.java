package build.dream.erp.controllers;

import build.dream.common.api.ApiRest;
import build.dream.common.controllers.BasicController;
import build.dream.common.erp.domains.Branch;
import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.GsonUtils;
import build.dream.common.utils.LogUtils;
import build.dream.erp.constants.Constants;
import build.dream.erp.services.BranchService;
import build.dream.erp.services.ElemeService;
import build.dream.erp.utils.ElemeUtils;
import net.sf.json.JSONObject;
import org.apache.commons.lang.Validate;
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
            orderCallbackRequestBody = "{\"requestId\": \"100000021764401594\",\"type\": 10,\"appId\": 22954133,\"message\": \"{\\\"id\\\":\\\"1200897812792015983\\\",\\\"orderId\\\":\\\"1200897812792015983\\\",\\\"address\\\":\\\"上海市普陀区金沙江路丹巴路119号-NAPOS\\\",\\\"createdAt\\\":\\\"2017-03-06T12:28:50\\\",\\\"activeAt\\\":\\\"2017-03-06T12:28:50\\\",\\\"deliverFee\\\":0.0,\\\"deliverTime\\\":null,\\\"description\\\":\\\"爱吃辣多点辣\\\",\\\"groups\\\":[{\\\"name\\\":\\\"1号篮子\\\",\\\"type\\\":\\\"normal\\\",\\\"items\\\":[{\\\"id\\\":260,\\\"skuId\\\":-1,\\\"name\\\":\\\"红烧肉[重辣]\\\",\\\"categoryId\\\":1,\\\"price\\\":4.0,\\\"quantity\\\":1,\\\"total\\\":4.0,\\\"additions\\\":[]}]},{\\\"name\\\":\\\"2号篮子\\\",\\\"type\\\":\\\"normal\\\",\\\"items\\\":[{\\\"id\\\":262,\\\"skuId\\\":-1,\\\"name\\\":\\\"狮子头\\\",\\\"categoryId\\\":1,\\\"price\\\":5.0,\\\"quantity\\\":1,\\\"total\\\":5.0,\\\"additions\\\":[]}]},{\\\"name\\\":\\\"3号篮子\\\",\\\"type\\\":\\\"normal\\\",\\\"items\\\":[{\\\"id\\\":261,\\\"skuId\\\":-1,\\\"name\\\":\\\"奶茶[去冰+半塘]\\\",\\\"categoryId\\\":1,\\\"price\\\":3.0,\\\"quantity\\\":2,\\\"total\\\":6.0,\\\"additions\\\":[]}]}],\\\"invoice\\\":\\\"上海市拉拉队有限公司\\\",\\\"book\\\":false,\\\"onlinePaid\\\":true,\\\"railwayAddress\\\":null,\\\"phoneList\\\":[\\\"13456789012\\\"],\\\"shopId\\\":720032,\\\"shopName\\\":\\\"测试餐厅001\\\",\\\"daySn\\\":7,\\\"status\\\":\\\"unprocessed\\\",\\\"refundStatus\\\":\\\"noRefund\\\",\\\"userId\\\":13524069,\\\"totalPrice\\\":20.0,\\\"originalPrice\\\":0.0,\\\"consignee\\\":\\\"饿了么 先生\\\",\\\"deliveryGeo\\\":\\\"121.3836479187,31.2299251556\\\",\\\"deliveryPoiAddress\\\":\\\"上海市普陀区金沙江路丹巴路119号-NAPOS\\\",\\\"invoiced\\\":true,\\\"income\\\":0.0,\\\"serviceRate\\\":0.0,\\\"serviceFee\\\":0.0,\\\"hongbao\\\":0.0,\\\"packageFee\\\":0.0,\\\"activityTotal\\\":0.0,\\\"shopPart\\\":0.0,\\\"elemePart\\\":0.0,\\\"downgraded\\\":true,\\\"vipDeliveryFeeDiscount\\\":0.0}\",\"shopId\": 720032,\"timestamp\": 1488774535366,\"signature\": \"2461328351094CA5853415FD25E36E95\",\"userId\": 98587250597500702}";
            Validate.notNull(orderCallbackRequestBody, "参数(orderCallbackRequestBody)不能为空！");

            JSONObject orderCallbackJsonObject = JSONObject.fromObject(orderCallbackRequestBody);
            BigInteger shopId = BigInteger.valueOf(orderCallbackJsonObject.getLong("shopId"));
            JSONObject message = orderCallbackJsonObject.getJSONObject("message");
            Integer type = orderCallbackJsonObject.getInt("type");

            ApiRest apiRest = null;
            switch (type) {
                case 10:
                    apiRest = elemeService.saveElemeOrder(shopId, message, type);
                    break;
                case 12:
                    apiRest = elemeService.handleElemeRefundOrderMessage(shopId, message, type);
                    break;
                case 14:
                    apiRest = elemeService.handleElemeRefundOrderMessage(shopId, message, type);
                    break;
                case 15:
                    apiRest = elemeService.handleElemeRefundOrderMessage(shopId, message, type);
                    break;
                case 17:
                    apiRest = elemeService.handleElemeRefundOrderMessage(shopId, message, type);
                    break;
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
            params.put("shopId", 150898569);
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
            params.put("shopId", 150898569);
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

            Branch branch = elemeService.findBranchInfo(BigInteger.valueOf(Long.valueOf(tenantId)), BigInteger.valueOf(Long.valueOf(branchId)));
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("shopId", 150898569);
            params.put("name", "afafaf");
            params.put("description", "description");
            apiRest = ElemeUtils.callElemeSystem("1", tenantId, branch.getType().toString(), branchId, "eleme.product.category.createCategory", params);
        } catch (Exception e) {
            LogUtils.error("添加商品分类失败", controllerSimpleName, "createCategory", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }
}
