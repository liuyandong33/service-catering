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
     * 查询商品数量
     *
     * @return
     */
    @RequestMapping(value = "/count", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = CountModel.class, serviceClass = GoodsService.class, serviceMethodName = "count", error = "查询商品列表失败")
    public String count() {
        return null;
    }
    /**
     * 查询商品列表
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = ListModel.class, serviceClass = GoodsService.class, serviceMethodName = "list", error = "查询商品列表失败")
    public String list() {
        return null;
    }

    @RequestMapping(value = "/obtainGoodsInfo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
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
    @RequestMapping(value = "/listGoodsInfos", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = ListGoodsInfosModel.class, serviceClass = GoodsService.class, serviceMethodName = "listGoodsInfos", error = "查询商品信息失败")
    public String listGoodsInfos() {
        return null;
    }

    /**
     * 保存菜品信息
     *
     * @return
     */
    @RequestMapping(value = "/saveGoods")
    @ResponseBody
    @ApiRestAction(modelClass = SaveGoodsModel.class, serviceClass = GoodsService.class, serviceMethodName = "saveGoods", error = "保存商品失败")
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
    @ApiRestAction(modelClass = DeleteGoodsSpecificationModel.class, serviceClass = GoodsService.class, serviceMethodName = "deleteGoodsSpecification", error = "删除商品规格失败")
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
    @ApiRestAction(modelClass = ListCategoriesModel.class, serviceClass = GoodsService.class, serviceMethodName = "listCategories", error = "查询商品分类失败")
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
    @ApiRestAction(modelClass = DeleteGoodsModel.class, serviceClass = GoodsService.class, serviceMethodName = "deleteGoods", error = "删除商品失败")
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
