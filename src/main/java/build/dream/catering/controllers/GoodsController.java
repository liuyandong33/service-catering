package build.dream.catering.controllers;

import build.dream.catering.models.goods.*;
import build.dream.catering.services.GoodsService;
import build.dream.common.annotations.ApiRestAction;
import build.dream.common.controllers.BasicController;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/goods")
public class GoodsController extends BasicController {
    /**
     * 查询菜品信息
     *
     * @return
     */
    @RequestMapping(value = "/listGoodses", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = ListGoodsesModel.class, serviceClass = GoodsService.class, serviceMethodName = "listGoodses", error = "查询菜品信息失败")
    public String listGoodses() {
        return null;
    }

    /**
     * 保存菜品信息
     *
     * @return
     */
    @RequestMapping(value = "/saveGoods")
    @ResponseBody
    @ApiRestAction(modelClass = SaveGoodsModel.class, serviceClass = GoodsService.class, serviceMethodName = "saveGoods", error = "保存菜品失败")
    public String saveGoods() {
        return null;
    }

    /**
     * 删除菜品规格
     *
     * @return
     */
    @RequestMapping(value = "/deleteGoodsSpecification")
    @ResponseBody
    @ApiRestAction(modelClass = DeleteGoodsSpecificationModel.class, serviceClass = GoodsService.class, serviceMethodName = "deleteGoodsSpecification", error = "删除菜品规格失败")
    public String deleteGoodsSpecification() {
        return null;
    }

    /**
     * 保存套餐
     *
     * @return
     */
    @RequestMapping(value = "/savePackage")
    @ResponseBody
    @ApiRestAction(modelClass = SavePackageModel.class, serviceClass = GoodsService.class, serviceMethodName = "savePackage", error = "保存套餐失败")
    public String savePackage() {
        return null;
    }

    /**
     * 查询菜品分类
     *
     * @return
     */
    @RequestMapping(value = "/listCategories")
    @ResponseBody
    @ApiRestAction(modelClass = ListCategoriesModel.class, serviceClass = GoodsService.class, serviceMethodName = "listCategories", error = "查询菜品分类失败")
    public String listCategories() {
        return null;
    }

    /**
     * 查询菜品分类
     *
     * @return
     */
    @RequestMapping(value = "/deleteGoods")
    @ResponseBody
    @ApiRestAction(modelClass = DeleteGoodsModel.class, serviceClass = GoodsService.class, serviceMethodName = "deleteGoods", error = "删除菜品失败")
    public String deleteGoods() {
        return null;
    }

    /**
     * 导入商品
     *
     * @return
     */
    @RequestMapping(value = "/importGoods")
    @ResponseBody
    @ApiRestAction(modelClass = ImportGoodsModel.class, serviceClass = GoodsService.class, serviceMethodName = "importGoods", error = "导入商品失败")
    public String importGoods() {
        return null;
    }
}
