package build.dream.catering.controllers;

import build.dream.catering.models.goods.*;
import build.dream.catering.services.GoodsService;
import build.dream.common.annotations.ApiRestAction;
import build.dream.common.api.ApiRest;
import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.GsonUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

@Controller
@RequestMapping(value = "/goods")
public class GoodsController {
    /**
     * 查询商品数量
     *
     * @return
     */
    @RequestMapping(value = "/count", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
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
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = ListModel.class, serviceClass = GoodsService.class, serviceMethodName = "list", error = "查询商品列表失败", zipped = true, signed = true)
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
    @RequestMapping(value = "/obtainAllGoodsInfos", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = ObtainAllGoodsInfosModel.class, serviceClass = GoodsService.class, serviceMethodName = "obtainAllGoodsInfos", error = "获取商品信息失败")
    public String obtainAllGoodsInfos() {
        return null;
    }

    /**
     * 保存菜品信息
     *
     * @return
     */
    @RequestMapping(value = "/saveGoods", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
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
    @RequestMapping(value = "/deleteGoodsSpecification", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
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
    @RequestMapping(value = "/savePackage", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
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
    @RequestMapping(value = "/listCategories", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
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
    @RequestMapping(value = "/deleteGoods", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
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
    @RequestMapping(value = "/importGoods", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
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
    @RequestMapping(value = "/searchGoods", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = SearchGoodsModel.class, serviceClass = GoodsService.class, serviceMethodName = "searchGoods", error = "检索商品失败")
    public String searchGoods() {
        return null;
    }

    @Autowired
    private GoodsService goodsService;

    @RequestMapping(value = "/test", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String test() throws IOException {
        String tenantId = ApplicationHandler.getRequestParameter("tenantId");
        String branchId = ApplicationHandler.getRequestParameter("branchId");
        ApiRest apiRest = goodsService.test(NumberUtils.createBigInteger(tenantId), NumberUtils.createBigInteger(branchId));
        return GsonUtils.toJson(apiRest);
    }
}
