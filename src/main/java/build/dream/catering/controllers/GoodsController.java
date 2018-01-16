package build.dream.catering.controllers;

import build.dream.common.api.ApiRest;
import build.dream.common.controllers.BasicController;
import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.GsonUtils;
import build.dream.common.utils.LogUtils;
import build.dream.catering.models.goods.GoodsFlavorGroupModel;
import build.dream.catering.models.goods.ListGoodsesModel;
import build.dream.catering.models.goods.SaveGoodsModel;
import build.dream.catering.services.GoodsService;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

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

    @RequestMapping(value = "/takeStock")
    public ModelAndView takeStock() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("goods/takeStock");
        return modelAndView;
    }

    @RequestMapping(value = "/uploadDistributionDetailedList")
    @ResponseBody
    private String uploadDistributionDetailedList() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            apiRest = new ApiRest();
        } catch (Exception e) {
            LogUtils.error("上传要货清单失败", controllerSimpleName, "uploadDistributionDetailedList", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    @RequestMapping(value = "/saveActualDistributionDetailedList")
    @ResponseBody
    public String saveActualDistributionDetailedList() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            String barCodes = requestParameters.get("barCodes");
            Validate.notNull(barCodes, "商品条码不能为空！");
            apiRest = goodsService.saveActualDistributionDetailedList(barCodes);
        } catch (Exception e) {
            LogUtils.error("保存失败", controllerSimpleName, "saveActualDistributionDetailedList", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    @RequestMapping(value = "/doTakeStock")
    @ResponseBody
    public String doTakeStock() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            apiRest = goodsService.doTakeStock();
        } catch (Exception e) {
            LogUtils.error("盘点失败", controllerSimpleName, "doTakeStock", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }
}
