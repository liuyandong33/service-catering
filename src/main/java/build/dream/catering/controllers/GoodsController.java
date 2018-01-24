package build.dream.catering.controllers;

import build.dream.catering.models.goods.*;
import build.dream.catering.services.GoodsService;
import build.dream.common.api.ApiRest;
import build.dream.common.controllers.BasicController;
import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.GsonUtils;
import build.dream.common.utils.LogUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/goods")
public class GoodsController extends BasicController {
    @Autowired
    private GoodsService goodsService;

    @RequestMapping(value = "/listGoodses")
    @ResponseBody
    public String listGoodses() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            ListGoodsesModel listGoodsesModel = ApplicationHandler.instantiateObject(ListGoodsesModel.class, requestParameters);
            listGoodsesModel.validateAndThrow();
            apiRest = goodsService.listGoodses(listGoodsesModel);
        } catch (Exception e) {
            LogUtils.error("查询菜品失败", controllerSimpleName, "listGoodses", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    @RequestMapping(value = "/saveGoods")
    @ResponseBody
    public String saveGoods() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            SaveGoodsModel saveGoodsModel = ApplicationHandler.instantiateObject(SaveGoodsModel.class, requestParameters);

            String goodsSpecificationInfos = requestParameters.get("goodsSpecificationInfos");
            ApplicationHandler.notEmpty(goodsSpecificationInfos, "goodsSpecificationInfos");
            saveGoodsModel.setGoodsSpecificationInfos(goodsSpecificationInfos);

            String flavorGroupInfos = requestParameters.get("flavorGroupInfos");
            if (StringUtils.isNotBlank(flavorGroupInfos)) {
                saveGoodsModel.setFlavorGroupInfos(flavorGroupInfos);
            }

            saveGoodsModel.validateAndThrow();
            apiRest = goodsService.saveGoods(saveGoodsModel);
        } catch (Exception e) {
            LogUtils.error("保存菜品失败", controllerSimpleName, "saveGoods", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    /**
     * 删除菜品规格
     *
     * @return
     */
    @RequestMapping(value = "/deleteGoodsSpecification")
    @ResponseBody
    public String deleteGoodsSpecification() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            DeleteGoodsSpecificationModel deleteGoodsSpecificationModel = ApplicationHandler.instantiateObject(DeleteGoodsSpecificationModel.class, requestParameters);
            deleteGoodsSpecificationModel.validateAndThrow();
            apiRest = goodsService.deleteGoodsSpecification(deleteGoodsSpecificationModel);
        } catch (Exception e) {
            LogUtils.error("删除菜品规格失败！", controllerSimpleName, "deleteGoodsSpecification", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    /**
     * 保存套餐
     *
     * @return
     */
    @RequestMapping(value = "/savePackage")
    @ResponseBody
    public String savePackage() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            SavePackageModel savePackageModel = ApplicationHandler.instantiateObject(SavePackageModel.class, requestParameters);
            savePackageModel.validateAndThrow();
            apiRest = goodsService.savePackage(savePackageModel);
        } catch (Exception e) {
            LogUtils.error("保存套餐失败", controllerSimpleName, "savePackage", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    /**
     * 查询菜品分类
     *
     * @return
     */
    @RequestMapping(value = "/listCategories")
    @ResponseBody
    public String listCategories() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            ListCategoriesModel listCategoriesModel = ApplicationHandler.instantiateObject(ListCategoriesModel.class, requestParameters);
            listCategoriesModel.validateAndThrow();

            apiRest = goodsService.listCategories(listCategoriesModel);
        } catch (Exception e) {
            LogUtils.error("查询菜品分类失败", controllerSimpleName, "listCategories", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    /**
     * 查询菜品分类
     *
     * @return
     */
    @RequestMapping(value = "/deleteGoods")
    @ResponseBody
    public String deleteGoods() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            DeleteGoodsModel deleteGoodsModel = ApplicationHandler.instantiateObject(DeleteGoodsModel.class, requestParameters);
            deleteGoodsModel.validateAndThrow();
            apiRest = goodsService.deleteGoods(deleteGoodsModel);
        } catch (Exception e) {
            LogUtils.error("删除菜品失败", controllerSimpleName, "deleteGoods", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }
}
