package build.dream.erp.controllers;

import build.dream.common.api.ApiRest;
import build.dream.common.controllers.BasicController;
import build.dream.common.erp.domains.Branch;
import build.dream.common.erp.domains.ElemeOrder;
import build.dream.common.erp.domains.GoodsCategory;
import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.GsonUtils;
import build.dream.common.utils.LogUtils;
import build.dream.erp.constants.Constants;
import build.dream.erp.models.eleme.*;
import build.dream.erp.services.ElemeService;
import build.dream.erp.utils.ElemeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "/eleme")
public class ElemeController extends BasicController {
    private static final int[] ELEME_ORDER_STATE_CHANGE_MESSAGE_TYPES = {12, 14, 15, 17, 18};
    private static final int[] ELEME_REFUND_ORDER_MESSAGE_TYPES = {20, 21, 22, 23, 24, 25, 26, 30, 31, 32, 33, 34, 35, 36};
    private static final int[] ELEME_REMINDER_MESSAGE_TYPES = {45, 46};
    private static final int[] ELEME_DELIVERY_ORDER_STATE_CHANGE_MESSAGE_TYPES = {51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76};
    private static final int[] ELEME_SHOP_STATE_CHANGE_MESSAGE_TYPES = {91, 92};
    @Autowired
    private ElemeService elemeService;

    @RequestMapping(value = "/tenantAuthorize")
    @ResponseBody
    public String tenantAuthorize() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            String tenantId = requestParameters.get("tenantId");
            Validate.notNull(tenantId, ApplicationHandler.obtainParameterErrorMessage("tenantId"));

            String branchId = requestParameters.get("branchId");
            Validate.notNull(branchId, ApplicationHandler.obtainParameterErrorMessage("branchId"));

            String userId = requestParameters.get("userId");
            Validate.notNull(userId, ApplicationHandler.obtainParameterErrorMessage("userId"));

            apiRest = elemeService.tenantAuthorize(NumberUtils.createBigInteger(tenantId), NumberUtils.createBigInteger(branchId), NumberUtils.createBigInteger(userId));
        } catch (Exception e) {
            LogUtils.error("生成授权链接失败", controllerSimpleName, "tenantAuthorize", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
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
            apiRest = ElemeUtils.callElemeSystem(tenantId.toString(), branchId.toString(), branch.getElemeAccountType(), "eleme.product.category.getShopCategories", params);
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
            apiRest = ElemeUtils.callElemeSystem(tenantId, branchId, branch.getElemeAccountType(), "eleme.product.category.getShopCategoriesWithChildren", params);
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

            Branch branch = elemeService.findBranchInfo(NumberUtils.createBigInteger(tenantId), NumberUtils.createBigInteger(branchId));
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("shopId", branch.getShopId());
            apiRest = ElemeUtils.callElemeSystem(tenantId, branchId, branch.getElemeAccountType(), "eleme.product.category.getShopCategoriesWithChildren", params);
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
            apiRest = ElemeUtils.callElemeSystem(tenantId, branchId, branch.getElemeAccountType(), "eleme.product.category.createCategory", params);
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
            apiRest = ElemeUtils.callElemeSystem(tenantId, branchId, branch.getElemeAccountType(), "eleme.product.category.createCategoryWithChildren", params);
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
            apiRest = ElemeUtils.callElemeSystem(tenantId, branchId, branch.getElemeAccountType(), "eleme.product.category.updateCategory", params);
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
            apiRest = ElemeUtils.callElemeSystem(tenantId, branchId, branch.getElemeAccountType(), "eleme.product.category.updateCategoryWithChildren", params);
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
            apiRest = ElemeUtils.callElemeSystem(tenantId, branchId, branch.getElemeAccountType(), "eleme.product.category.removeCategory", params);
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
            apiRest = ElemeUtils.callElemeSystem(tenantId, branchId, branch.getElemeAccountType(), "eleme.order.confirmOrderLite", params);
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
            apiRest = ElemeUtils.callElemeSystem(cancelOrderLiteModel.getTenantId().toString(), branch.getId().toString(), branch.getElemeAccountType(), "eleme.order.cancelOrderLite", params);
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
            apiRest = ElemeUtils.callElemeSystem(agreeRefundLiteModel.getTenantId().toString(), branch.getId().toString(), branch.getElemeAccountType(), "eleme.order.agreeRefundLite", params);
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
            apiRest = ElemeUtils.callElemeSystem(disagreeRefundLiteModel.getTenantId().toString(), branch.getId().toString(), branch.getElemeAccountType(), "eleme.order.disagreeRefundLite", params);
        } catch (Exception e) {
            LogUtils.error("不同意退单/不同意取消单失败", controllerSimpleName, "disagreeRefundLite", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    @RequestMapping(value = "/getDeliveryStateRecord")
    @ResponseBody
    public String getDeliveryStateRecord() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            GetDeliveryStateRecordModel deliveryStateRecordModel = ApplicationHandler.instantiateObject(GetDeliveryStateRecordModel.class, requestParameters);
            deliveryStateRecordModel.validateAndThrow();

            Branch branch = elemeService.findBranchInfo(deliveryStateRecordModel.getTenantId(), deliveryStateRecordModel.getBranchId());
            ElemeOrder elemeOrder = elemeService.findElemeOrderInfo(deliveryStateRecordModel.getTenantId(), deliveryStateRecordModel.getBranchId(), deliveryStateRecordModel.getElemeOrderId());

            Map<String, Object> params = new HashMap<String, Object>();
            params.put("orderId", elemeOrder.getOrderId());
            apiRest = ElemeUtils.callElemeSystem(deliveryStateRecordModel.getTenantId().toString(), branch.getId().toString(), branch.getElemeAccountType(), "eleme.order.getDeliveryStateRecord", params);
        } catch (Exception e) {
            LogUtils.error("获取订单配送记录失败", controllerSimpleName, "getDeliveryStateRecord", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    @RequestMapping(value = "/deliveryBySelfLite")
    @ResponseBody
    public String deliveryBySelfLite() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            DeliveryBySelfLiteModel deliveryBySelfLiteModel = ApplicationHandler.instantiateObject(DeliveryBySelfLiteModel.class, requestParameters);
            deliveryBySelfLiteModel.validateAndThrow();

            Branch branch = elemeService.findBranchInfo(deliveryBySelfLiteModel.getTenantId(), deliveryBySelfLiteModel.getBranchId());
            ElemeOrder elemeOrder = elemeService.findElemeOrderInfo(deliveryBySelfLiteModel.getTenantId(), deliveryBySelfLiteModel.getBranchId(), deliveryBySelfLiteModel.getElemeOrderId());

            Map<String, Object> params = new HashMap<String, Object>();
            params.put("orderId", elemeOrder.getOrderId());
            apiRest = ElemeUtils.callElemeSystem(deliveryBySelfLiteModel.getTenantId().toString(), branch.getId().toString(), branch.getElemeAccountType(), "eleme.order.deliveryBySelfLite", params);
        } catch (Exception e) {
            LogUtils.error("配送异常或者物流拒单后选择自行配送失败", controllerSimpleName, "deliveryBySelfLite", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    @RequestMapping(value = "/noMoreDeliveryLite")
    @ResponseBody
    public String noMoreDeliveryLite() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            NoMoreDeliveryLiteModel moreDeliveryLiteModel = ApplicationHandler.instantiateObject(NoMoreDeliveryLiteModel.class, requestParameters);
            moreDeliveryLiteModel.validateAndThrow();

            Branch branch = elemeService.findBranchInfo(moreDeliveryLiteModel.getTenantId(), moreDeliveryLiteModel.getBranchId());
            ElemeOrder elemeOrder = elemeService.findElemeOrderInfo(moreDeliveryLiteModel.getTenantId(), moreDeliveryLiteModel.getBranchId(), moreDeliveryLiteModel.getElemeOrderId());

            Map<String, Object> params = new HashMap<String, Object>();
            params.put("orderId", elemeOrder.getOrderId());
            apiRest = ElemeUtils.callElemeSystem(moreDeliveryLiteModel.getTenantId().toString(), branch.getId().toString(), branch.getElemeAccountType(), "eleme.order.noMoreDeliveryLite", params);
        } catch (Exception e) {
            LogUtils.error("配送异常或者物流拒单后选择不再配送失败", controllerSimpleName, "noMoreDeliveryLite", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    @RequestMapping(value = "/obtainElemeDeliveryOrderStateChangeMessage")
    @ResponseBody
    public String obtainElemeDeliveryOrderStateChangeMessage() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            ObtainElemeDeliveryOrderStateChangeMessageModel obtainElemeDeliveryOrderStateChangeMessageModel = ApplicationHandler.instantiateObject(ObtainElemeDeliveryOrderStateChangeMessageModel.class, requestParameters);
            obtainElemeDeliveryOrderStateChangeMessageModel.validateAndThrow();

            apiRest = elemeService.obtainElemeDeliveryOrderStateChangeMessage(obtainElemeDeliveryOrderStateChangeMessageModel);
        } catch (Exception e) {
            LogUtils.error("获取饿了么订单运单状态变更信息失败！", controllerSimpleName, "obtainElemeDeliveryOrderStateChangeMessage", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    @RequestMapping(value = "/pullElemeOrder")
    @ResponseBody
    public String pullElemeOrder() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            PullElemeOrderModel pullElemeOrderModel = ApplicationHandler.instantiateObject(PullElemeOrderModel.class, requestParameters);
            pullElemeOrderModel.validateAndThrow();

            apiRest = elemeService.pullElemeOrder(pullElemeOrderModel);
        } catch (Exception e) {
            LogUtils.error("拉取饿了么订单失败！", controllerSimpleName, "pullElemeOrder", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    @RequestMapping(value = "/bindingStore")
    @ResponseBody
    public String bindingStore() throws IOException {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        String result = readResource("bindingStore.html");
        result = result.replaceAll("\\$\\{tenantId}", requestParameters.get("tenantId"));
        result = result.replaceAll("\\$\\{branchId}", requestParameters.get("branchId"));
        result = result.replaceAll("\\$\\{userId}", requestParameters.get("userId"));
        result = StringUtils.join(result.split("\\$\\{ui-dialog\\.css}"), readResource("ui-dialog.css"));
        result = StringUtils.join(result.split("\\$\\{jquery-3\\.2\\.1\\.min.js}"), readResource("jquery-3.2.1.min.js"));
        result = StringUtils.join(result.split("\\$\\{dialog\\.js}"), readResource("dialog.js"));
        return result;
    }

    private String readResource(String resourceName) throws IOException {
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        String line = null;
        ClassLoader classLoader = this.getClass().getClassLoader();
        StringBuffer result = new StringBuffer();
        if ("bindingStore.html".equals(resourceName)) {
            inputStream = classLoader.getResourceAsStream("views/eleme/bindingStore.html");
        } else if ("ui-dialog.css".equals(resourceName)) {
            inputStream = classLoader.getResourceAsStream("libraries/artDialog/css/ui-dialog.css");
        } else if ("jquery-3.2.1.min.js".equals(resourceName)) {
            inputStream = classLoader.getResourceAsStream("libraries/jquery/jquery-3.2.1.min.js");
        } else if ("dialog.js".equals(resourceName)) {
            inputStream = classLoader.getResourceAsStream("libraries/artDialog/dist/dialog.js");
        }
        inputStreamReader = new InputStreamReader(inputStream, Constants.CHARSET_NAME_UTF_8);
        bufferedReader = new BufferedReader(inputStreamReader);
        int length = 0;
        char[] buffer = new char[1024];
        while ((length = inputStreamReader.read(buffer, 0, 1024)) != -1) {
            result.append(buffer, 0, length);
        }
        bufferedReader.close();
        inputStreamReader.close();
        inputStream.close();
        return result.toString();
    }

    @RequestMapping(value = "/doBindingStore")
    @ResponseBody
    public String doBindingStore() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            DoBindingStoreModel doBindingStoreModel = ApplicationHandler.instantiateObject(DoBindingStoreModel.class, requestParameters);
            doBindingStoreModel.validateAndThrow();
            apiRest = elemeService.doBindingStore(doBindingStoreModel);
        } catch (Exception e) {
            LogUtils.error("绑定饿了么门店失败！", controllerSimpleName, "doBindingStore", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }
}
