package build.dream.catering.controllers;

import build.dream.catering.models.goods.*;
import build.dream.catering.services.GoodsService;
import build.dream.common.controllers.BasicController;
import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.MethodCaller;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping(value = "/goods")
public class GoodsController extends BasicController {
    @Autowired
    private GoodsService goodsService;

    @RequestMapping(value = "/listGoodses")
    @ResponseBody
    public String listGoodses() {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        MethodCaller methodCaller = () -> {
            ListGoodsesModel listGoodsesModel = ApplicationHandler.instantiateObject(ListGoodsesModel.class, requestParameters);
            listGoodsesModel.validateAndThrow();
            return goodsService.listGoodses(listGoodsesModel);
        };
        return ApplicationHandler.callMethod(methodCaller, "查询菜品失败", requestParameters);
    }

    @RequestMapping(value = "/saveGoods")
    @ResponseBody
    public String saveGoods() {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        MethodCaller methodCaller = () -> {
            SaveGoodsModel saveGoodsModel = ApplicationHandler.instantiateObject(SaveGoodsModel.class, requestParameters);

            String goodsSpecificationInfos = requestParameters.get("goodsSpecificationInfos");
            ApplicationHandler.notEmpty(goodsSpecificationInfos, "goodsSpecificationInfos");
            saveGoodsModel.setGoodsSpecificationInfos(goodsSpecificationInfos);

            String flavorGroupInfos = requestParameters.get("flavorGroupInfos");
            if (StringUtils.isNotBlank(flavorGroupInfos)) {
                saveGoodsModel.setFlavorGroupInfos(flavorGroupInfos);
            }

            saveGoodsModel.validateAndThrow();
            return goodsService.saveGoods(saveGoodsModel);
        };
        return ApplicationHandler.callMethod(methodCaller, "保存菜品失败", requestParameters);
    }

    /**
     * 删除菜品规格
     *
     * @return
     */
    @RequestMapping(value = "/deleteGoodsSpecification")
    @ResponseBody
    public String deleteGoodsSpecification() {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        MethodCaller methodCaller = () -> {
            DeleteGoodsSpecificationModel deleteGoodsSpecificationModel = ApplicationHandler.instantiateObject(DeleteGoodsSpecificationModel.class, requestParameters);
            deleteGoodsSpecificationModel.validateAndThrow();
            return goodsService.deleteGoodsSpecification(deleteGoodsSpecificationModel);
        };
        return ApplicationHandler.callMethod(methodCaller, "删除菜品规格失败", requestParameters);
    }

    /**
     * 保存套餐
     *
     * @return
     */
    @RequestMapping(value = "/savePackage")
    @ResponseBody
    public String savePackage() {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        MethodCaller methodCaller = () -> {
            SavePackageModel savePackageModel = ApplicationHandler.instantiateObject(SavePackageModel.class, requestParameters);
            savePackageModel.validateAndThrow();
            return goodsService.savePackage(savePackageModel);
        };
        return ApplicationHandler.callMethod(methodCaller, "保存套餐失败", requestParameters);
    }

    /**
     * 查询菜品分类
     *
     * @return
     */
    @RequestMapping(value = "/listCategories")
    @ResponseBody
    public String listCategories() {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        MethodCaller methodCaller = () -> {
            ListCategoriesModel listCategoriesModel = ApplicationHandler.instantiateObject(ListCategoriesModel.class, requestParameters);
            listCategoriesModel.validateAndThrow();

            return goodsService.listCategories(listCategoriesModel);
        };
        return ApplicationHandler.callMethod(methodCaller, "查询菜品分类失败", requestParameters);
    }

    /**
     * 查询菜品分类
     *
     * @return
     */
    @RequestMapping(value = "/deleteGoods")
    @ResponseBody
    public String deleteGoods() {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        MethodCaller methodCaller = () -> {
            DeleteGoodsModel deleteGoodsModel = ApplicationHandler.instantiateObject(DeleteGoodsModel.class, requestParameters);
            deleteGoodsModel.validateAndThrow();
            return goodsService.deleteGoods(deleteGoodsModel);
        };
        return ApplicationHandler.callMethod(methodCaller, "删除菜品失败", requestParameters);
    }

    /**
     * 导入商品
     *
     * @return
     */
    @RequestMapping(value = "/importGoods")
    @ResponseBody
    public String importGoods() {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        MethodCaller methodCaller = () -> {
            ImportGoodsModel importGoodsModel = ApplicationHandler.instantiateObject(ImportGoodsModel.class, requestParameters);
            importGoodsModel.validateAndThrow();
            return goodsService.importGoods(importGoodsModel);
        };
        return ApplicationHandler.callMethod(methodCaller, "导入商品失败", requestParameters);
    }
}
