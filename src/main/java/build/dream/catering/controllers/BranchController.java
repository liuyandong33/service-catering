package build.dream.catering.controllers;

import build.dream.catering.models.branch.*;
import build.dream.catering.services.BranchService;
import build.dream.common.annotations.ApiRestAction;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/branch")
public class BranchController {
    /**
     * 初始化门店
     *
     * @return
     */
    @RequestMapping(value = "/initializeBranch", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = InitializeBranchModel.class, serviceClass = BranchService.class, serviceMethodName = "initializeBranch", error = "初始化门店失败", datePattern = "HH:mm")
    public String initializeBranch() {
        return null;
    }

    /**
     * 分页查询门店信息
     *
     * @return
     */
    @RequestMapping(value = "/listBranches", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = ListBranchesModel.class, serviceClass = BranchService.class, serviceMethodName = "listBranches", error = "查询门店列表失败")
    public String listBranches() {
        return null;
    }

    /**
     * 删除门店
     *
     * @return
     */
    @RequestMapping(value = "/deleteBranch", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = DeleteBranchModel.class, serviceClass = BranchService.class, serviceMethodName = "deleteBranch", error = "删除门店信息失败")
    public String deleteBranch() {
        return null;
    }

    /**
     * 拉取门店信息
     *
     * @return
     */
    @RequestMapping(value = "/pullBranchInfos", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = PullBranchInfosModel.class, serviceClass = BranchService.class, serviceMethodName = "pullBranchInfos", error = "拉取门店信息失败")
    public String pullBranchInfos() {
        return null;
    }

    /**
     * 禁用门店产品
     *
     * @return
     */
    @RequestMapping(value = "/disableGoods", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = DisableGoodsModel.class, serviceClass = BranchService.class, serviceMethodName = "disableGoods", error = "禁用门店产品失败")
    public String disableGoods() {
        return null;
    }

    /**
     * 门店续费回调
     *
     * @return
     */
    @RequestMapping(value = "/renewCallback", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = RenewCallbackModel.class, serviceClass = BranchService.class, serviceMethodName = "handleRenewCallback", error = "处理门店续费回调失败")
    public String renewCallback() {
        return null;
    }

    /**
     * 获取门店信息
     *
     * @return
     */
    @RequestMapping(value = "/obtainBranchInfo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = ObtainBranchInfoModel.class, serviceClass = BranchService.class, serviceMethodName = "obtainBranchInfo", error = "获取门店信息失败")
    public String obtainBranchInfo() {
        return null;
    }

    /**
     * 获取智慧餐厅门店列表
     *
     * @return
     */
    @RequestMapping(value = "/obtainAllSmartRestaurants", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = ObtainAllSmartRestaurantsModel.class, serviceClass = BranchService.class, serviceMethodName = "obtainAllSmartRestaurants", error = "获取智慧餐厅门店信息失败")
    public String obtainAllSmartRestaurants() {
        return null;
    }

    /**
     * 获取智慧餐厅门店信息
     *
     * @return
     */
    @RequestMapping(value = "/obtainSmartRestaurant", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = ObtainSmartRestaurantModel.class, serviceClass = BranchService.class, serviceMethodName = "obtainSmartRestaurant", error = "获取智慧餐厅门店信息失败")
    public String obtainSmartRestaurant() {
        return null;
    }

    /**
     * 获取总部门店信息
     *
     * @return
     */
    @RequestMapping(value = "/obtainHeadquartersInfo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = ObtainHeadquartersInfoModel.class, serviceClass = BranchService.class, serviceMethodName = "obtainHeadquartersInfo", error = "获取总部门店信息失败")
    public String obtainHeadquartersInfo() {
        return null;
    }
}
