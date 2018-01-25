package build.dream.catering.controllers;

import build.dream.common.api.ApiRest;
import build.dream.common.controllers.BasicController;
import build.dream.common.erp.catering.domains.Branch;
import build.dream.common.erp.catering.domains.ElemeOrder;
import build.dream.common.erp.catering.domains.GoodsCategory;
import build.dream.common.utils.*;
import build.dream.catering.constants.Constants;
import build.dream.catering.models.eleme.*;
import build.dream.catering.services.ElemeService;
import build.dream.catering.utils.ElemeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
        File file = new File("C:\\Users\\liuyandong\\Desktop\\image.png");
        String mimeType = MimeMappingUtils.obtainMimeTypeByFileName(file.getName());
        return mimeType;
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
        result = result.replaceAll("\\$\\{serviceName}", ConfigurationUtils.getConfiguration(Constants.SERVICE_NAME));
        result = result.replaceAll("\\$\\{tenantId}", requestParameters.get("tenantId"));
        result = result.replaceAll("\\$\\{branchId}", requestParameters.get("branchId"));
        result = result.replaceAll("\\$\\{userId}", requestParameters.get("userId"));
        result = result.replaceAll("\\$\\{partitionCode}", ConfigurationUtils.getConfiguration(Constants.PARTITION_CODE));
        result = result.replaceAll("\\$\\{doBindingStoreUrl}", SystemPartitionUtils.getOutsideUrl(Constants.SERVICE_NAME_POSAPI, "proxy", "doPostPermit"));
        result = StringUtils.join(result.split("\\$\\{ui-dialog\\.css}"), readResource("ui-dialog.css"));
        result = StringUtils.join(result.split("\\$\\{jquery-3\\.2\\.1\\.min.js}"), readResource("jquery-3.2.1.min.js"));
        result = StringUtils.join(result.split("\\$\\{dialog\\.js}"), readResource("dialog.js"));
        return result;
    }

    private String readResource(String resourceName) throws IOException {
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
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
        int length = 0;
        char[] buffer = new char[1024];
        while ((length = inputStreamReader.read(buffer, 0, 1024)) != -1) {
            result.append(buffer, 0, length);
        }
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

    @RequestMapping(value = "/test")
    @ResponseBody
    public String test() throws IOException {
        String message = "{\"id\":\"1216547752852466738\",\"orderId\":\"1216547752852466738\",\"address\":\"青岛房地产公寓瞿塘峡路22 3楼\",\"createdAt\":\"2018-01-25T11:07:57\",\"activeAt\":\"2018-01-25T11:07:57\",\"deliverFee\":4.18,\"deliverTime\":null,\"description\":\"3份餐具\",\"groups\":[{\"name\":\"用户 发起人1号... 的篮子\",\"type\":\"normal\",\"items\":[{\"id\":780496771,\"skuId\":399588693192,\"name\":\"微辣\",\"categoryId\":1,\"price\":0.0,\"quantity\":1,\"total\":0.0,\"additions\":[],\"newSpecs\":[],\"attributes\":[],\"extendCode\":\"\",\"barCode\":\"\",\"weight\":1.0,\"userPrice\":0.0,\"shopPrice\":0.0,\"vfoodId\":743839547},{\"id\":780542854,\"skuId\":399635882184,\"name\":\"单人套餐（带米饭1份）\",\"categoryId\":1,\"price\":26.0,\"quantity\":1,\"total\":26.0,\"additions\":[],\"newSpecs\":[],\"attributes\":[],\"extendCode\":\"\",\"barCode\":\"\",\"weight\":1.0,\"userPrice\":0.0,\"shopPrice\":0.0,\"vfoodId\":743879204}]},{\"name\":\"用户 发起人2号... 的篮子\",\"type\":\"normal\",\"items\":[{\"id\":781146632,\"skuId\":400254150856,\"name\":\"微微辣\",\"categoryId\":1,\"price\":0.0,\"quantity\":1,\"total\":0.0,\"additions\":[],\"newSpecs\":[],\"attributes\":[],\"extendCode\":\"\",\"barCode\":\"\",\"weight\":1.0,\"userPrice\":0.0,\"shopPrice\":0.0,\"vfoodId\":744280737},{\"id\":780267873,\"skuId\":399354301640,\"name\":\"牛肚\",\"categoryId\":1,\"price\":5.5,\"quantity\":1,\"total\":5.5,\"additions\":[],\"newSpecs\":[],\"attributes\":[],\"extendCode\":\"\",\"barCode\":\"\",\"weight\":1.0,\"userPrice\":0.0,\"shopPrice\":0.0,\"vfoodId\":743748079},{\"id\":780263079,\"skuId\":399349392584,\"name\":\"蟹肉棒\",\"categoryId\":1,\"price\":5.5,\"quantity\":1,\"total\":5.5,\"additions\":[],\"newSpecs\":[],\"attributes\":[],\"extendCode\":\"\",\"barCode\":\"\",\"weight\":1.0,\"userPrice\":0.0,\"shopPrice\":0.0,\"vfoodId\":743734049},{\"id\":780253570,\"skuId\":399339655368,\"name\":\"甜不辣\",\"categoryId\":1,\"price\":5.5,\"quantity\":1,\"total\":5.5,\"additions\":[],\"newSpecs\":[],\"attributes\":[],\"extendCode\":\"\",\"barCode\":\"\",\"weight\":1.0,\"userPrice\":0.0,\"shopPrice\":0.0,\"vfoodId\":743715720},{\"id\":780190851,\"skuId\":399275431112,\"name\":\"白菜\",\"categoryId\":1,\"price\":3.5,\"quantity\":1,\"total\":3.5,\"additions\":[],\"newSpecs\":[],\"attributes\":[],\"extendCode\":\"\",\"barCode\":\"\",\"weight\":1.0,\"userPrice\":0.0,\"shopPrice\":0.0,\"vfoodId\":743662015},{\"id\":780230922,\"skuId\":399316463816,\"name\":\"土豆片\",\"categoryId\":1,\"price\":3.5,\"quantity\":1,\"total\":3.5,\"additions\":[],\"newSpecs\":[],\"attributes\":[],\"extendCode\":\"\",\"barCode\":\"\",\"weight\":1.0,\"userPrice\":0.0,\"shopPrice\":0.0,\"vfoodId\":743706464},{\"id\":780227024,\"skuId\":399312472264,\"name\":\"莲藕片\",\"categoryId\":1,\"price\":3.5,\"quantity\":1,\"total\":3.5,\"additions\":[],\"newSpecs\":[],\"attributes\":[],\"extendCode\":\"\",\"barCode\":\"\",\"weight\":1.0,\"userPrice\":0.0,\"shopPrice\":0.0,\"vfoodId\":743693620},{\"id\":780262668,\"skuId\":399348971720,\"name\":\"墨鱼丸\",\"categoryId\":1,\"price\":5.5,\"quantity\":1,\"total\":5.5,\"additions\":[],\"newSpecs\":[],\"attributes\":[],\"extendCode\":\"\",\"barCode\":\"\",\"weight\":1.0,\"userPrice\":0.0,\"shopPrice\":0.0,\"vfoodId\":743733885},{\"id\":780286755,\"skuId\":399373636808,\"name\":\"蘑菇\",\"categoryId\":1,\"price\":3.5,\"quantity\":1,\"total\":3.5,\"additions\":[],\"newSpecs\":[],\"attributes\":[],\"extendCode\":\"\",\"barCode\":\"\",\"weight\":1.0,\"userPrice\":0.0,\"shopPrice\":0.0,\"vfoodId\":743720863}]},{\"name\":\"用户 发起人3号... 的篮子\",\"type\":\"normal\",\"items\":[{\"id\":780262668,\"skuId\":399348971720,\"name\":\"墨鱼丸\",\"categoryId\":1,\"price\":5.5,\"quantity\":1,\"total\":5.5,\"additions\":[],\"newSpecs\":[],\"attributes\":[],\"extendCode\":\"\",\"barCode\":\"\",\"weight\":1.0,\"userPrice\":0.0,\"shopPrice\":0.0,\"vfoodId\":743733885},{\"id\":780263079,\"skuId\":399349392584,\"name\":\"蟹肉棒\",\"categoryId\":1,\"price\":5.5,\"quantity\":1,\"total\":5.5,\"additions\":[],\"newSpecs\":[],\"attributes\":[],\"extendCode\":\"\",\"barCode\":\"\",\"weight\":1.0,\"userPrice\":0.0,\"shopPrice\":0.0,\"vfoodId\":743734049},{\"id\":780190851,\"skuId\":399275431112,\"name\":\"白菜\",\"categoryId\":1,\"price\":3.5,\"quantity\":1,\"total\":3.5,\"additions\":[],\"newSpecs\":[],\"attributes\":[],\"extendCode\":\"\",\"barCode\":\"\",\"weight\":1.0,\"userPrice\":0.0,\"shopPrice\":0.0,\"vfoodId\":743662015},{\"id\":780227024,\"skuId\":399312472264,\"name\":\"莲藕片\",\"categoryId\":1,\"price\":3.5,\"quantity\":1,\"total\":3.5,\"additions\":[],\"newSpecs\":[],\"attributes\":[],\"extendCode\":\"\",\"barCode\":\"\",\"weight\":1.0,\"userPrice\":0.0,\"shopPrice\":0.0,\"vfoodId\":743693620},{\"id\":780250373,\"skuId\":399336381640,\"name\":\"海带结\",\"categoryId\":1,\"price\":3.5,\"quantity\":1,\"total\":3.5,\"additions\":[],\"newSpecs\":[],\"attributes\":[],\"extendCode\":\"\",\"barCode\":\"\",\"weight\":1.0,\"userPrice\":0.0,\"shopPrice\":0.0,\"vfoodId\":743713060},{\"id\":780285919,\"skuId\":399372780744,\"name\":\"亲亲肠\",\"categoryId\":1,\"price\":5.5,\"quantity\":1,\"total\":5.5,\"additions\":[],\"newSpecs\":[],\"attributes\":[],\"extendCode\":\"\",\"barCode\":\"\",\"weight\":1.0,\"userPrice\":0.0,\"shopPrice\":0.0,\"vfoodId\":743720728},{\"id\":780258449,\"skuId\":399344651464,\"name\":\"鱼豆腐\",\"categoryId\":1,\"price\":5.5,\"quantity\":1,\"total\":5.5,\"additions\":[],\"newSpecs\":[],\"attributes\":[],\"extendCode\":\"\",\"barCode\":\"\",\"weight\":1.0,\"userPrice\":0.0,\"shopPrice\":0.0,\"vfoodId\":743743026},{\"id\":780488925,\"skuId\":399580658888,\"name\":\"麻辣\",\"categoryId\":1,\"price\":0.0,\"quantity\":1,\"total\":0.0,\"additions\":[],\"newSpecs\":[],\"attributes\":[],\"extendCode\":\"\",\"barCode\":\"\",\"weight\":1.0,\"userPrice\":0.0,\"shopPrice\":0.0,\"vfoodId\":743835366},{\"id\":784943716,\"skuId\":404142364872,\"name\":\"牛百叶\",\"categoryId\":1,\"price\":5.5,\"quantity\":1,\"total\":5.5,\"additions\":[],\"newSpecs\":[],\"attributes\":[],\"extendCode\":\"\",\"barCode\":\"\",\"weight\":1.0,\"userPrice\":0.0,\"shopPrice\":0.0,\"vfoodId\":746894142},{\"id\":780286755,\"skuId\":399373636808,\"name\":\"蘑菇\",\"categoryId\":1,\"price\":3.5,\"quantity\":1,\"total\":3.5,\"additions\":[],\"newSpecs\":[],\"attributes\":[],\"extendCode\":\"\",\"barCode\":\"\",\"weight\":1.0,\"userPrice\":0.0,\"shopPrice\":0.0,\"vfoodId\":743720863}]},{\"name\":\"其它费用\",\"type\":\"extra\",\"items\":[{\"id\":-70000,\"skuId\":-1,\"name\":\"餐盒\",\"categoryId\":102,\"price\":2.0,\"quantity\":1,\"total\":2.0,\"additions\":[],\"newSpecs\":null,\"attributes\":null,\"extendCode\":\"\",\"barCode\":\"\",\"weight\":null,\"userPrice\":0.0,\"shopPrice\":0.0,\"vfoodId\":0}]}],\"invoice\":null,\"book\":false,\"onlinePaid\":true,\"railwayAddress\":null,\"phoneList\":[\"18678963882\"],\"shopId\":161505259,\"shopName\":\"辣真格麻辣香锅（金茂湾店）\",\"daySn\":1,\"status\":\"unprocessed\",\"refundStatus\":\"noRefund\",\"userId\":199212273,\"totalPrice\":78.08,\"originalPrice\":109.68,\"consignee\":\"张鑫(女士)\",\"deliveryGeo\":\"120.30810004,36.05558001\",\"deliveryPoiAddress\":\"青岛房地产公寓瞿塘峡路22 3楼\",\"invoiced\":false,\"income\":62.67,\"serviceRate\":0.17,\"serviceFee\":-12.83,\"hongbao\":-1.6,\"packageFee\":2.0,\"activityTotal\":-30.0,\"shopPart\":-30.0,\"elemePart\":-0.0,\"downgraded\":false,\"vipDeliveryFeeDiscount\":0.0,\"openId\":\"6769Z100004565\",\"secretPhoneExpireTime\":null,\"orderActivities\":[{\"categoryId\":12,\"name\":\"在线支付立减优惠\",\"amount\":-30.0,\"elemePart\":0.0,\"restaurantPart\":-30.0,\"id\":831019881}],\"invoiceType\":null,\"taxpayerId\":\"\",\"coldBoxFee\":0.0,\"cancelOrderDescription\":null,\"cancelOrderCreatedAt\":null}";
        elemeService.saveElemeOrder(BigInteger.valueOf(161505259L), message, 10, UUID.randomUUID().toString());
        return null;
    }
}
