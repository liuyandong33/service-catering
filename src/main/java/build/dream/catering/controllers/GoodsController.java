package build.dream.catering.controllers;

import build.dream.catering.models.goods.GoodsFlavorGroupModel;
import build.dream.catering.models.goods.ListGoodsesModel;
import build.dream.catering.models.goods.SaveGoodsModel;
import build.dream.catering.models.goods.SavePackageModel;
import build.dream.catering.services.GoodsService;
import build.dream.common.api.ApiRest;
import build.dream.common.controllers.BasicController;
import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.GsonUtils;
import build.dream.common.utils.LogUtils;
import org.apache.commons.lang.Validate;
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
            String goodsFlavorGroup = requestParameters.get("goodsFlavorGroup");
            Validate.notNull(goodsFlavorGroup);

            List<GoodsFlavorGroupModel> goodsFlavorGroupModels = GsonUtils.jsonToList(goodsFlavorGroup, GoodsFlavorGroupModel.class);
            saveGoodsModel.setGoodsFlavorGroupModels(goodsFlavorGroupModels);
            apiRest = goodsService.saveGoods(saveGoodsModel);
        } catch (Exception e) {
            LogUtils.error("保存菜品失败", controllerSimpleName, "saveGoods", e, requestParameters);
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
            String packageGroups = requestParameters.get("packageGroups");
            ApplicationHandler.notEmpty(packageGroups, "packageGroups");
            List<SavePackageModel.PackageGroupModel> packageGroupModels = GsonUtils.jsonToList(packageGroups, SavePackageModel.PackageGroupModel.class);
            savePackageModel.setPackageGroupModels(packageGroupModels);
            savePackageModel.validateAndThrow();

            apiRest = goodsService.savePackage(savePackageModel);
        } catch (Exception e) {
            LogUtils.error("保存套餐失败", controllerSimpleName, "savePackage", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }
}
