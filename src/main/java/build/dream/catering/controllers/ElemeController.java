package build.dream.catering.controllers;

import build.dream.catering.constants.Constants;
import build.dream.catering.models.eleme.*;
import build.dream.catering.services.ElemeService;
import build.dream.catering.utils.ElemeUtils;
import build.dream.common.api.ApiRest;
import build.dream.common.controllers.BasicController;
import build.dream.common.erp.catering.domains.Branch;
import build.dream.common.erp.catering.domains.GoodsCategory;
import build.dream.common.utils.*;
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

@Controller
@RequestMapping(value = "/eleme")
public class ElemeController extends BasicController {
    @Autowired
    private ElemeService elemeService;

    @RequestMapping(value = "/tenantAuthorize")
    @ResponseBody
    public String tenantAuthorize() {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        MethodCaller methodCaller = () -> {
            TenantAuthorizeModel tenantAuthorizeModel = ApplicationHandler.instantiateObject(TenantAuthorizeModel.class, requestParameters);
            tenantAuthorizeModel.validateAndThrow();
            return elemeService.tenantAuthorize(tenantAuthorizeModel);
        };
        return ApplicationHandler.callMethod(methodCaller, "生成授权链接失败", requestParameters);
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

            Branch branch = elemeService.findBranch(BigInteger.valueOf(Long.valueOf(tenantId)), BigInteger.valueOf(Long.valueOf(branchId)));
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

            Branch branch = elemeService.findBranch(BigInteger.valueOf(Long.valueOf(tenantId)), BigInteger.valueOf(Long.valueOf(branchId)));
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

            Branch branch = elemeService.findBranch(NumberUtils.createBigInteger(tenantId), NumberUtils.createBigInteger(branchId));
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

            Branch branch = elemeService.findBranch(bigIntegerTenantId, bigIntegerBranchId);
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

            Branch branch = elemeService.findBranch(bigIntegerTenantId, bigIntegerBranchId);
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

            Branch branch = elemeService.findBranch(bigIntegerTenantId, bigIntegerBranchId);
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

            Branch branch = elemeService.findBranch(bigIntegerTenantId, bigIntegerBranchId);
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

            Branch branch = elemeService.findBranch(bigIntegerTenantId, bigIntegerBranchId);
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

    @RequestMapping(value = "/obtainElemeCallbackMessage")
    @ResponseBody
    public String obtainElemeCallbackMessage() {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        MethodCaller methodCaller = () -> {
            ObtainElemeCallbackMessageModel obtainElemeCallbackMessageModel = ApplicationHandler.instantiateObject(ObtainElemeCallbackMessageModel.class, requestParameters);
            obtainElemeCallbackMessageModel.validateAndThrow();

            return elemeService.obtainElemeCallbackMessage(obtainElemeCallbackMessageModel);
        };
        return ApplicationHandler.callMethod(methodCaller, "获取饿了么回调消息失败", requestParameters);
    }

    @RequestMapping(value = "/obtainElemeOrder")
    @ResponseBody
    public String obtainElemeOrder() {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        MethodCaller methodCaller = () -> {
            ObtainElemeOrderModel obtainElemeOrderModel = ApplicationHandler.instantiateObject(ObtainElemeOrderModel.class, requestParameters);
            obtainElemeOrderModel.validateAndThrow();

            return elemeService.obtainElemeOrder(obtainElemeOrderModel);
        };
        return ApplicationHandler.callMethod(methodCaller, "拉取饿了么订单失败", requestParameters);
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

    @RequestMapping(value = "/doBindingStore")
    @ResponseBody
    public String doBindingStore() {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        MethodCaller methodCaller = () -> {
            DoBindingStoreModel doBindingStoreModel = ApplicationHandler.instantiateObject(DoBindingStoreModel.class, requestParameters);
            doBindingStoreModel.validateAndThrow();
            return elemeService.doBindingStore(doBindingStoreModel);
        };
        return ApplicationHandler.callMethod(methodCaller, "绑定饿了么门店失败", requestParameters);
    }

    /**
     * 分页获取店铺下的商品
     *
     * @return
     */
    @RequestMapping(value = "/queryItemByPage")
    @ResponseBody
    public String queryItemByPage() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            QueryItemByPageModel queryItemByPageModel = ApplicationHandler.instantiateObject(QueryItemByPageModel.class, requestParameters);
            queryItemByPageModel.validateAndThrow();

            Branch branch = elemeService.findBranch(queryItemByPageModel.getTenantId(), queryItemByPageModel.getBranchId());
            Map<String, Object> queryPage = new HashMap<String, Object>();
            queryPage.put("shopId", branch.getShopId());
            queryPage.put("offset", queryItemByPageModel.getOffset());
            queryPage.put("limit", queryItemByPageModel.getLimit());

            Map<String, Object> params = new HashMap<String, Object>();
            params.put("queryPage", queryPage);

            apiRest = ElemeUtils.callElemeSystem(queryItemByPageModel.getTenantId().toString(), queryItemByPageModel.getBranchId().toString(), branch.getElemeAccountType(), "eleme.product.item.queryItemByPage", params);
        } catch (Exception e) {
            LogUtils.error("分页获取店铺下的商品失败", controllerSimpleName, "queryItemByPage", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    /**
     * 设置订单餐盒费
     *
     * @return
     */
    @RequestMapping(value = "/setOrderPackingFee")
    @ResponseBody
    public String setOrderPackingFee() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            SetOrderPackingFeeModel setOrderPackingFeeModel = ApplicationHandler.instantiateObject(SetOrderPackingFeeModel.class, requestParameters);
            setOrderPackingFeeModel.validateAndThrow();

            Branch branch = elemeService.findBranch(setOrderPackingFeeModel.getTenantId(), setOrderPackingFeeModel.getBranchId());
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("shopId", branch.getShopId());
            params.put("status", setOrderPackingFeeModel.getStatus());
            ApplicationHandler.ifNotNullPut(params, "packingFee", setOrderPackingFeeModel.getPackingFee());

            ApiRest callElemeSystemApiRest = ElemeUtils.callElemeSystem(setOrderPackingFeeModel.getTenantId().toString(), setOrderPackingFeeModel.getBranchId().toString(), branch.getElemeAccountType(), "eleme.product.item.setOrderPackingFee", params);
            Validate.isTrue(callElemeSystemApiRest.isSuccessful(), callElemeSystemApiRest.getError());

            apiRest = new ApiRest();
            apiRest.setMessage("设置订单餐盒费成功！");
            apiRest.setSuccessful(true);
        } catch (Exception e) {
            LogUtils.error("设置订单餐盒费失败", controllerSimpleName, "setOrderPackingFee", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    /**
     * 查询店铺活动商品
     *
     * @return
     */
    @RequestMapping(value = "/getShopSalesItems")
    @ResponseBody
    public String getShopSalesItems() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            GetShopSalesItemsModel getShopSalesItemsModel = ApplicationHandler.instantiateObject(GetShopSalesItemsModel.class, requestParameters);
            getShopSalesItemsModel.validateAndThrow();

            Branch branch = elemeService.findBranch(getShopSalesItemsModel.getTenantId(), getShopSalesItemsModel.getBranchId());
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("shopId", branch.getShopId());

            ApiRest callElemeSystemApiRest = ElemeUtils.callElemeSystem(getShopSalesItemsModel.getTenantId().toString(), getShopSalesItemsModel.getBranchId().toString(), branch.getElemeAccountType(), "eleme.product.item.getShopSalesItems", params);
            Validate.isTrue(callElemeSystemApiRest.isSuccessful(), callElemeSystemApiRest.getError());

            apiRest = new ApiRest();
            apiRest.setData(callElemeSystemApiRest.getData());
            apiRest.setMessage("查询店铺活动商品成功！");
            apiRest.setSuccessful(true);
        } catch (Exception e) {
            LogUtils.error("查询店铺活动商品失败", controllerSimpleName, "getShopSalesItems", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    @RequestMapping(value = "/getOrder")
    @ResponseBody
    public String getOrder() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            GetOrderModel getOrderModel = ApplicationHandler.instantiateObject(GetOrderModel.class, requestParameters);
            getOrderModel.validateAndThrow();

            apiRest = elemeService.getOrder(getOrderModel);
        } catch (Exception e) {
            LogUtils.error("获取订单失败", controllerSimpleName, "getOrder", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    @RequestMapping(value = "/batchGetOrders")
    @ResponseBody
    public String batchGetOrders() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            BatchGetOrdersModel batchGetOrdersModel = ApplicationHandler.instantiateObject(BatchGetOrdersModel.class, requestParameters);
            batchGetOrdersModel.validateAndThrow();

            apiRest = elemeService.batchGetOrders(batchGetOrdersModel);
        } catch (Exception e) {
            LogUtils.error("批量获取订单失败", controllerSimpleName, "batchGetOrders", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    /**
     * 确认订单
     *
     * @return
     */
    @RequestMapping(value = "/confirmOrderLite")
    @ResponseBody
    public String confirmOrderLite() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            ConfirmOrderLiteModel confirmOrderLiteModel = ApplicationHandler.instantiateObject(ConfirmOrderLiteModel.class, requestParameters);
            confirmOrderLiteModel.validateAndThrow();

            apiRest = elemeService.confirmOrderLite(confirmOrderLiteModel);
        } catch (Exception e) {
            LogUtils.error("确认订单失败", controllerSimpleName, "confirmOrderLite", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    /**
     * 取消订单
     *
     * @return
     */
    @RequestMapping(value = "/cancelOrderLite")
    @ResponseBody
    public String cancelOrderLite() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            CancelOrderLiteModel cancelOrderLiteModel = ApplicationHandler.instantiateObject(CancelOrderLiteModel.class, requestParameters);
            cancelOrderLiteModel.validateAndThrow();

            apiRest = elemeService.cancelOrderLite(cancelOrderLiteModel);
        } catch (Exception e) {
            LogUtils.error("取消订单失败", controllerSimpleName, "cancelOrderLite", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    /**
     * 同意退单/同意取消单(推荐)
     *
     * @return
     */
    @RequestMapping(value = "/agreeRefundLite")
    @ResponseBody
    public String agreeRefundLite() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            AgreeRefundLiteModel agreeRefundLiteModel = ApplicationHandler.instantiateObject(AgreeRefundLiteModel.class, requestParameters);
            agreeRefundLiteModel.validateAndThrow();

            apiRest = elemeService.agreeRefundLite(agreeRefundLiteModel);
        } catch (Exception e) {
            LogUtils.error("同意退单/同意取消单失败", controllerSimpleName, "agreeRefundLite", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    /**
     * 不同意退单/不同意取消单
     *
     * @return
     */
    @RequestMapping(value = "/disagreeRefundLite")
    @ResponseBody
    public String disagreeRefundLite() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            DisagreeRefundLiteModel disagreeRefundLiteModel = ApplicationHandler.instantiateObject(DisagreeRefundLiteModel.class, requestParameters);
            disagreeRefundLiteModel.validateAndThrow();
            apiRest = elemeService.disagreeRefundLite(disagreeRefundLiteModel);
        } catch (Exception e) {
            LogUtils.error("不同意退单/不同意取消单失败", controllerSimpleName, "disagreeRefundLite", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    /**
     * 获取订单配送记录
     *
     * @return
     */
    @RequestMapping(value = "/getDeliveryStateRecord")
    @ResponseBody
    public String getDeliveryStateRecord() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            GetDeliveryStateRecordModel getDeliveryStateRecordModel = ApplicationHandler.instantiateObject(GetDeliveryStateRecordModel.class, requestParameters);
            getDeliveryStateRecordModel.validateAndThrow();
            apiRest = elemeService.getDeliveryStateRecord(getDeliveryStateRecordModel);
        } catch (Exception e) {
            LogUtils.error("获取订单配送记录失败", controllerSimpleName, "getDeliveryStateRecord", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    /**
     * 批量获取订单最新配送记录
     *
     * @return
     */
    @RequestMapping(value = "/batchGetDeliveryStates")
    @ResponseBody
    public String batchGetDeliveryStates() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            BatchGetDeliveryStatesModel batchGetDeliveryStatesModel = ApplicationHandler.instantiateObject(BatchGetDeliveryStatesModel.class, requestParameters);
            batchGetDeliveryStatesModel.validateAndThrow();

            apiRest = elemeService.batchGetDeliveryStates(batchGetDeliveryStatesModel);
        } catch (Exception e) {
            LogUtils.error("批量获取订单最新配送记录失败", controllerSimpleName, "batchGetDeliveryStates", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    /**
     * 配送异常或者物流拒单后选择自行配送
     *
     * @return
     */
    @RequestMapping(value = "/deliveryBySelfLite")
    @ResponseBody
    public String deliveryBySelfLite() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            DeliveryBySelfLiteModel deliveryBySelfLiteModel = ApplicationHandler.instantiateObject(DeliveryBySelfLiteModel.class, requestParameters);
            deliveryBySelfLiteModel.validateAndThrow();

            apiRest = elemeService.deliveryBySelfLite(deliveryBySelfLiteModel);
        } catch (Exception e) {
            LogUtils.error("配送异常或者物流拒单后选择自行配送失败", controllerSimpleName, "deliveryBySelfLite", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    /**
     * 配送异常或者物流拒单后选择不再配送
     *
     * @return
     */
    @RequestMapping(value = "/noMoreDeliveryLite")
    @ResponseBody
    public String noMoreDeliveryLite() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            NoMoreDeliveryLiteModel noMoreDeliveryLiteModel = ApplicationHandler.instantiateObject(NoMoreDeliveryLiteModel.class, requestParameters);
            noMoreDeliveryLiteModel.validateAndThrow();

            apiRest = elemeService.noMoreDeliveryLite(noMoreDeliveryLiteModel);
        } catch (Exception e) {
            LogUtils.error("配送异常或者物流拒单后选择不再配送失败", controllerSimpleName, "noMoreDeliveryLite", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    /**
     * 订单确认送达
     *
     * @return
     */
    @RequestMapping(value = "/receivedOrderLite")
    @ResponseBody
    public String receivedOrderLite() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            ReceivedOrderLiteModel receivedOrderLiteModel = ApplicationHandler.instantiateObject(ReceivedOrderLiteModel.class, requestParameters);
            receivedOrderLiteModel.validateAndThrow();

            apiRest = elemeService.receivedOrderLite(receivedOrderLiteModel);
        } catch (Exception e) {
            LogUtils.error("订单确认送达失败", controllerSimpleName, "receivedOrderLite", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    /**
     * 回复催单
     *
     * @return
     */
    @RequestMapping(value = "/replyReminder")
    @ResponseBody
    public String replyReminder() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            ReplyReminderModel replyReminderModel = ApplicationHandler.instantiateObject(ReplyReminderModel.class, requestParameters);
            replyReminderModel.validateAndThrow();

            apiRest = elemeService.replyReminder(replyReminderModel);
        } catch (Exception e) {
            LogUtils.error("回复催单失败", controllerSimpleName, "replyReminder", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    /**
     * 获取指定订单菜品活动价格
     *
     * @return
     */
    @RequestMapping(value = "getCommodities")
    @ResponseBody
    public String getCommodities() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            GetCommoditiesModel getCommoditiesModel = ApplicationHandler.instantiateObject(GetCommoditiesModel.class, requestParameters);
            getCommoditiesModel.validateAndThrow();

            apiRest = elemeService.getCommodities(getCommoditiesModel);
        } catch (Exception e) {
            LogUtils.error("获取指定订单菜品活动价格失败", controllerSimpleName, "getCommodities", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    /**
     * 批量获取订单菜品活动价格
     *
     * @return
     */
    @RequestMapping(value = "batchGetCommodities")
    @ResponseBody
    public String batchGetCommodities() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            BatchGetCommoditiesModel batchGetCommoditiesModel = ApplicationHandler.instantiateObject(BatchGetCommoditiesModel.class, requestParameters);
            batchGetCommoditiesModel.validateAndThrow();

            apiRest = elemeService.batchGetCommodities(batchGetCommoditiesModel);
        } catch (Exception e) {
            LogUtils.error("批量获取订单菜品活动价格失败", controllerSimpleName, "batchGetCommodities", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    /**
     * 获取订单退款信息
     *
     * @return
     */
    @RequestMapping(value = "getRefundOrder")
    @ResponseBody
    public String getRefundOrder() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            GetRefundOrderModel getRefundOrderModel = ApplicationHandler.instantiateObject(GetRefundOrderModel.class, requestParameters);
            getRefundOrderModel.validateAndThrow();

            apiRest = elemeService.getRefundOrder(getRefundOrderModel);
        } catch (Exception e) {
            LogUtils.error("获取订单退款信息失败", controllerSimpleName, "getRefundOrder", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    /**
     * 批量获取订单退款信息
     *
     * @return
     */
    @RequestMapping(value = "batchGetRefundOrders")
    @ResponseBody
    public String batchGetRefundOrders() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            BatchGetRefundOrdersModel batchGetRefundOrdersModel = ApplicationHandler.instantiateObject(BatchGetRefundOrdersModel.class, requestParameters);
            batchGetRefundOrdersModel.validateAndThrow();

            apiRest = elemeService.batchGetRefundOrders(batchGetRefundOrdersModel);
        } catch (Exception e) {
            LogUtils.error("批量获取订单退款信息失败", controllerSimpleName, "batchGetRefundOrders", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    /**
     * 取消呼叫配送
     *
     * @return
     */
    @RequestMapping(value = "cancelDelivery")
    @ResponseBody
    public String cancelDelivery() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            CancelDeliveryModel cancelDeliveryModel = ApplicationHandler.instantiateObject(CancelDeliveryModel.class, requestParameters);
            cancelDeliveryModel.validateAndThrow();

            apiRest = elemeService.cancelDelivery(cancelDeliveryModel);
        } catch (Exception e) {
            LogUtils.error("取消呼叫配送失败", controllerSimpleName, "cancelDelivery", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    /**
     * 呼叫配送
     *
     * @return
     */
    @RequestMapping(value = "callDelivery")
    @ResponseBody
    public String callDelivery() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            CallDeliveryModel callDeliveryModel = ApplicationHandler.instantiateObject(CallDeliveryModel.class, requestParameters);
            callDeliveryModel.validateAndThrow();

            apiRest = elemeService.callDelivery(callDeliveryModel);
        } catch (Exception e) {
            LogUtils.error("呼叫配送失败", controllerSimpleName, "callDelivery", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    /**
     * 获取店铺未回复的催单
     *
     * @return
     */
    @RequestMapping(value = "getUnreplyReminders")
    @ResponseBody
    public String getUnreplyReminders() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            GetUnreplyRemindersModel getUnreplyRemindersModel = ApplicationHandler.instantiateObject(GetUnreplyRemindersModel.class, requestParameters);
            getUnreplyRemindersModel.validateAndThrow();

            apiRest = elemeService.getUnreplyReminders(getUnreplyRemindersModel);
        } catch (Exception e) {
            LogUtils.error("获取店铺未回复的催单失败", controllerSimpleName, "getUnreplyReminders", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    /**
     * 查询店铺未处理订单
     *
     * @return
     */
    @RequestMapping(value = "getUnprocessOrders")
    @ResponseBody
    public String getUnprocessOrders() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            GetUnprocessOrdersModel getUnprocessOrdersModel = ApplicationHandler.instantiateObject(GetUnprocessOrdersModel.class, requestParameters);
            getUnprocessOrdersModel.validateAndThrow();

            apiRest = elemeService.getUnprocessOrders(getUnprocessOrdersModel);
        } catch (Exception e) {
            LogUtils.error("查询店铺未处理订单失败", controllerSimpleName, "getUnprocessOrders", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    /**
     * 查询店铺未处理的取消单
     *
     * @return
     */
    @RequestMapping(value = "getCancelOrders")
    @ResponseBody
    public String getCancelOrders() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            GetCancelOrdersModel getCancelOrdersModel = ApplicationHandler.instantiateObject(GetCancelOrdersModel.class, requestParameters);
            getCancelOrdersModel.validateAndThrow();

            apiRest = elemeService.getCancelOrders(getCancelOrdersModel);
        } catch (Exception e) {
            LogUtils.error("查询店铺未处理的取消单失败", controllerSimpleName, "getCancelOrders", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    /**
     * 查询店铺未处理的退单
     *
     * @return
     */
    @RequestMapping(value = "getRefundOrders")
    @ResponseBody
    public String getRefundOrders() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            GetRefundOrdersModel getRefundOrdersModel = ApplicationHandler.instantiateObject(GetRefundOrdersModel.class, requestParameters);
            getRefundOrdersModel.validateAndThrow();

            apiRest = elemeService.getRefundOrders(getRefundOrdersModel);
        } catch (Exception e) {
            LogUtils.error("查询店铺未处理的退单失败", controllerSimpleName, "getRefundOrders", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    /**
     * 查询全部订单
     *
     * @return
     */
    @RequestMapping(value = "getAllOrders")
    @ResponseBody
    public String getAllOrders() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            GetAllOrdersModel getAllOrdersModel = ApplicationHandler.instantiateObject(GetAllOrdersModel.class, requestParameters);
            getAllOrdersModel.validateAndThrow();

            apiRest = elemeService.getAllOrders(getAllOrdersModel);
        } catch (Exception e) {
            LogUtils.error("查询全部订单失败", controllerSimpleName, "getAllOrders", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    /**
     * 批量查询订单是否支持索赔
     *
     * @return
     */
    @RequestMapping(value = "querySupportedCompensationOrders")
    @ResponseBody
    public String querySupportedCompensationOrders() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            QuerySupportedCompensationOrdersModel querySupportedCompensationOrdersModel = ApplicationHandler.instantiateObject(QuerySupportedCompensationOrdersModel.class, requestParameters);
            querySupportedCompensationOrdersModel.validateAndThrow();

            apiRest = elemeService.querySupportedCompensationOrders(querySupportedCompensationOrdersModel);
        } catch (Exception e) {
            LogUtils.error("批量查询订单是否支持索赔失败", controllerSimpleName, "querySupportedCompensationOrders", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    /**
     * 批量查询订单是否支持索赔
     *
     * @return
     */
    @RequestMapping(value = "batchApplyCompensations")
    @ResponseBody
    public String batchApplyCompensations() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            BatchApplyCompensationsModel batchApplyCompensationsModel = ApplicationHandler.instantiateObject(BatchApplyCompensationsModel.class, requestParameters);

            batchApplyCompensationsModel.setRequests(requestParameters.get("requests"));
            batchApplyCompensationsModel.validateAndThrow();

            apiRest = elemeService.batchApplyCompensations(batchApplyCompensationsModel);
        } catch (Exception e) {
            LogUtils.error("批量申请索赔失败", controllerSimpleName, "batchApplyCompensations", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    /**
     * 批量查询索赔结果
     *
     * @return
     */
    @RequestMapping(value = "queryCompensationOrders")
    @ResponseBody
    public String queryCompensationOrders() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            QueryCompensationOrdersModel queryCompensationOrdersModel = ApplicationHandler.instantiateObject(QueryCompensationOrdersModel.class, requestParameters);
            queryCompensationOrdersModel.validateAndThrow();

            apiRest = elemeService.queryCompensationOrders(queryCompensationOrdersModel);
        } catch (Exception e) {
            LogUtils.error("批量查询索赔结果失败", controllerSimpleName, "queryCompensationOrders", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    /**
     * 众包订单询价，获取配送费
     *
     * @return
     */
    @RequestMapping(value = "getDeliveryFeeForCrowd")
    @ResponseBody
    public String getDeliveryFeeForCrowd() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            GetDeliveryFeeForCrowdModel getDeliveryFeeForCrowdModel = ApplicationHandler.instantiateObject(GetDeliveryFeeForCrowdModel.class, requestParameters);
            getDeliveryFeeForCrowdModel.validateAndThrow();

            apiRest = elemeService.getDeliveryFeeForCrowd(getDeliveryFeeForCrowdModel);
        } catch (Exception e) {
            LogUtils.error("众包订单询价，获取配送费失败", controllerSimpleName, "getDeliveryFeeForCrowd", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    /**
     * 评价骑手
     *
     * @return
     */
    @RequestMapping(value = "evaluateRider")
    @ResponseBody
    public String evaluateRider() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            EvaluateRiderModel evaluateRiderModel = ApplicationHandler.instantiateObject(EvaluateRiderModel.class, requestParameters);
            evaluateRiderModel.validateAndThrow();

            apiRest = elemeService.evaluateRider(evaluateRiderModel);
        } catch (Exception e) {
            LogUtils.error("评价骑手失败", controllerSimpleName, "evaluateRider", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    /**
     * 批量获取骑手评价信息
     *
     * @return
     */
    @RequestMapping(value = "batchGetEvaluationInfos")
    @ResponseBody
    public String batchGetEvaluationInfos() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            BatchGetEvaluationInfosModel batchGetEvaluationInfosModel = ApplicationHandler.instantiateObject(BatchGetEvaluationInfosModel.class, requestParameters);
            batchGetEvaluationInfosModel.validateAndThrow();

            apiRest = elemeService.batchGetEvaluationInfos(batchGetEvaluationInfosModel);
        } catch (Exception e) {
            LogUtils.error("批量获取骑手评价信息失败", controllerSimpleName, "batchGetEvaluationInfos", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    /**
     * 批量获取是否可以评价骑手
     *
     * @return
     */
    @RequestMapping(value = "batchGetEvaluationStatus")
    @ResponseBody
    public String batchGetEvaluationStatus() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            BatchGetEvaluationStatusModel batchGetEvaluationStatusModel = ApplicationHandler.instantiateObject(BatchGetEvaluationStatusModel.class, requestParameters);
            batchGetEvaluationStatusModel.validateAndThrow();

            apiRest = elemeService.batchGetEvaluationStatus(batchGetEvaluationStatusModel);
        } catch (Exception e) {
            LogUtils.error("批量获取骑手评价信息失败", controllerSimpleName, "batchGetEvaluationStatus", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    /**
     * 批量获取订单加小费信息
     *
     * @return
     */
    @RequestMapping(value = "batchGetDeliveryTipInfos")
    @ResponseBody
    public String batchGetDeliveryTipInfos() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            BatchGetDeliveryTipInfosModel batchGetDeliveryTipInfosModel = ApplicationHandler.instantiateObject(BatchGetDeliveryTipInfosModel.class, requestParameters);
            batchGetDeliveryTipInfosModel.validateAndThrow();

            apiRest = elemeService.batchGetDeliveryTipInfos(batchGetDeliveryTipInfosModel);
        } catch (Exception e) {
            LogUtils.error("批量获取订单加小费信息失败", controllerSimpleName, "batchGetDeliveryTipInfos", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    /**
     * 订单加小费
     *
     * @return
     */
    @RequestMapping(value = "addDeliveryTipByOrderId")
    @ResponseBody
    public String addDeliveryTipByOrderId() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            AddDeliveryTipByOrderIdModel addDeliveryTipByOrderIdModel = ApplicationHandler.instantiateObject(AddDeliveryTipByOrderIdModel.class, requestParameters);
            addDeliveryTipByOrderIdModel.validateAndThrow();

            apiRest = elemeService.addDeliveryTipByOrderId(addDeliveryTipByOrderIdModel);
        } catch (Exception e) {
            LogUtils.error("订单加小费失败", controllerSimpleName, "addDeliveryTipByOrderId", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    /**
     * 非自配送餐厅标记已出餐
     *
     * @return
     */
    @RequestMapping(value = "setOrderPrepared")
    @ResponseBody
    public String setOrderPrepared() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            SetOrderPreparedModel setOrderPreparedModel = ApplicationHandler.instantiateObject(SetOrderPreparedModel.class, requestParameters);
            setOrderPreparedModel.validateAndThrow();

            apiRest = elemeService.setOrderPrepared(setOrderPreparedModel);
        } catch (Exception e) {
            LogUtils.error("非自配送餐厅标记已出餐失败", controllerSimpleName, "setOrderPrepared", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    /**
     * 查询已出餐列表
     *
     * @return
     */
    @RequestMapping(value = "getPreparedTimesByOrderIds")
    @ResponseBody
    public String getPreparedTimesByOrderIds() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            GetPreparedTimesByOrderIdsModel getPreparedTimesByOrderIdsModel = ApplicationHandler.instantiateObject(GetPreparedTimesByOrderIdsModel.class, requestParameters);
            getPreparedTimesByOrderIdsModel.validateAndThrow();

            apiRest = elemeService.getPreparedTimesByOrderIds(getPreparedTimesByOrderIdsModel);
        } catch (Exception e) {
            LogUtils.error("查询已出餐列表失败", controllerSimpleName, "getPreparedTimesByOrderIds", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    /**
     * 查询店铺信息
     *
     * @return
     */
    @RequestMapping(value = "getShop")
    @ResponseBody
    public String getShop() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            GetShopModel getShopModel = ApplicationHandler.instantiateObject(GetShopModel.class, requestParameters);
            getShopModel.validateAndThrow();

            apiRest = elemeService.getShop(getShopModel);
        } catch (Exception e) {
            LogUtils.error("查询店铺信息失败", controllerSimpleName, "getShop", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    /**
     * 查询店铺信息
     *
     * @return
     */
    @RequestMapping(value = "updateShop")
    @ResponseBody
    public String updateShop() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            UpdateShopModel updateShopModel = ApplicationHandler.instantiateObject(UpdateShopModel.class, requestParameters);
            updateShopModel.validateAndThrow();

            apiRest = elemeService.updateShop(updateShopModel);
        } catch (Exception e) {
            LogUtils.error("查询店铺信息失败", controllerSimpleName, "updateShop", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    /**
     * 查询店铺信息
     *
     * @return
     */
    @RequestMapping(value = "batchGetShopStatus")
    @ResponseBody
    public String batchGetShopStatus() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            BatchGetShopStatusModel batchGetShopStatusModel = ApplicationHandler.instantiateObject(BatchGetShopStatusModel.class, requestParameters);
            batchGetShopStatusModel.validateAndThrow();

            apiRest = elemeService.batchGetShopStatus(batchGetShopStatusModel);
        } catch (Exception e) {
            LogUtils.error("批量获取店铺简要失败", controllerSimpleName, "batchGetShopStatus", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    /**
     * 查询店铺信息
     *
     * @return
     */
    @RequestMapping(value = "setDeliveryTime")
    @ResponseBody
    public String setDeliveryTime() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            SetDeliveryTimeModel setDeliveryTimeModel = ApplicationHandler.instantiateObject(SetDeliveryTimeModel.class, requestParameters);
            setDeliveryTimeModel.validateAndThrow();

            apiRest = elemeService.setDeliveryTime(setDeliveryTimeModel);
        } catch (Exception e) {
            LogUtils.error("设置送达时间失败", controllerSimpleName, "setDeliveryTime", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    /**
     * 设置是否支持在线退单
     *
     * @return
     */
    @RequestMapping(value = "/setOnlineRefund")
    @ResponseBody
    public String setOnlineRefund() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            SetOnlineRefundModel setOnlineRefundModel = ApplicationHandler.instantiateObject(SetOnlineRefundModel.class, requestParameters);
            setOnlineRefundModel.validateAndThrow();

            apiRest = elemeService.setOnlineRefund(setOnlineRefundModel);
        } catch (Exception e) {
            LogUtils.error("设置是否支持在线退单失败", controllerSimpleName, "setOnlineRefund", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    /**
     * 设置是否支持预定单及预定天数
     *
     * @return
     */
    @RequestMapping(value = "/setBookingStatus")
    @ResponseBody
    public String setBookingStatus() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            SetBookingStatusModel setBookingStatusModel = ApplicationHandler.instantiateObject(SetBookingStatusModel.class, requestParameters);
            setBookingStatusModel.validateAndThrow();

            apiRest = elemeService.setBookingStatus(setBookingStatusModel);
        } catch (Exception e) {
            LogUtils.error("设置是否支持预定单及预定天数失败", controllerSimpleName, "setBookingStatus", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    /**
     * 获取商户账号信息
     *
     * @return
     */
    @RequestMapping(value = "/getUser")
    @ResponseBody
    public String getUser() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            GetUserModel getUserModel = ApplicationHandler.instantiateObject(GetUserModel.class, requestParameters);
            getUserModel.validateAndThrow();

            apiRest = elemeService.getUser(getUserModel);
        } catch (Exception e) {
            LogUtils.error("获取商户账号信息失败", controllerSimpleName, "getUser", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    /**
     * 查询店铺当前生效合同类型
     *
     * @return
     */
    @RequestMapping(value = "/getEffectServicePackContract")
    @ResponseBody
    public String getEffectServicePackContract() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            GetEffectServicePackContractModel getEffectServicePackContractModel = ApplicationHandler.instantiateObject(GetEffectServicePackContractModel.class, requestParameters);
            getEffectServicePackContractModel.validateAndThrow();

            apiRest = elemeService.getEffectServicePackContract(getEffectServicePackContractModel);
        } catch (Exception e) {
            LogUtils.error("查询店铺当前生效合同类型失败", controllerSimpleName, "getEffectServicePackContract", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    /**
     * 获取指定订单的评论
     *
     * @return
     */
    @RequestMapping(value = "/getOrderRateByOrderId")
    @ResponseBody
    public String getOrderRateByOrderId() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            GetOrderRateByOrderIdModel getOrderRateByOrderIdModel = ApplicationHandler.instantiateObject(GetOrderRateByOrderIdModel.class, requestParameters);
            getOrderRateByOrderIdModel.validateAndThrow();

            apiRest = elemeService.getOrderRateByOrderId(getOrderRateByOrderIdModel);
        } catch (Exception e) {
            LogUtils.error("获取指定订单的评论失败", controllerSimpleName, "getOrderRateByOrderId", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }
}
