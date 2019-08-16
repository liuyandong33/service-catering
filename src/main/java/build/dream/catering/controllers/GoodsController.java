package build.dream.catering.controllers;

import build.dream.catering.models.goods.*;
import build.dream.catering.services.GoodsService;
import build.dream.common.annotations.ApiRestAction;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/goods")
public class GoodsController {
    /**
     * 查询商品数量
     *
     * @return
     */
    @RequestMapping(value = "/count", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = CountModel.class, serviceClass = GoodsService.class, serviceMethodName = "count", error = "查询商品数量失败")
    public String count() {
        return null;
    }

    /**
     * 查询商品列表
     *
     * @return
     */
    @RequestMapping(value = "/list", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = ListModel.class, serviceClass = GoodsService.class, serviceMethodName = "list", error = "查询商品列表失败", zipped = true, signed = true)
    public String list() {
        return null;
    }

    @RequestMapping(value = "/obtainGoodsInfo", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = ObtainGoodsInfoModel.class, serviceClass = GoodsService.class, serviceMethodName = "obtainGoodsInfo", error = "获取商品信息失败")
    public String obtainGoodsInfo() {
        return null;
    }

    /**
     * 查询菜品信息
     *
     * @return
     */
    @RequestMapping(value = "/obtainAllGoodsInfos", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = ObtainAllGoodsInfosModel.class, serviceClass = GoodsService.class, serviceMethodName = "obtainAllGoodsInfos", zipped = false, error = "获取商品信息失败")
    public String obtainAllGoodsInfos() {
        return null;
    }

    /**
     * 保存菜品信息
     *
     * @return
     */
    @RequestMapping(value = "/saveGoods", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = SaveGoodsModel.class, serviceClass = GoodsService.class, serviceMethodName = "saveGoods", error = "保存商品失败")
    public String saveGoods() {
        return null;
    }

    /**
     * 保存套餐
     *
     * @return
     */
    @RequestMapping(value = "/savePackage", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
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
    @RequestMapping(value = "/listCategories", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = ListCategoriesModel.class, serviceClass = GoodsService.class, serviceMethodName = "listCategories", error = "查询商品分类失败")
    public String listCategories() {
        return null;
    }

    /**
     * 查询菜品分类
     *
     * @return
     */
    @RequestMapping(value = "/deleteGoods", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = DeleteGoodsModel.class, serviceClass = GoodsService.class, serviceMethodName = "deleteGoods", error = "删除商品失败")
    public String deleteGoods() {
        return null;
    }

    /**
     * 导入商品
     *
     * @return
     */
    @RequestMapping(value = "/importGoods", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = ImportGoodsModel.class, serviceClass = GoodsService.class, serviceMethodName = "importGoods", error = "导入商品失败")
    public String importGoods() {
        return null;
    }

    /**
     * 检索商品
     *
     * @return
     */
    @RequestMapping(value = "/searchGoods", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = SearchGoodsModel.class, serviceClass = GoodsService.class, serviceMethodName = "searchGoods", error = "检索商品失败")
    public String searchGoods() {
        return null;
    }
}
