package build.dream.catering.controllers;

import build.dream.catering.models.branch.*;
import build.dream.catering.services.BranchService;
import build.dream.common.annotations.ApiRestAction;
import build.dream.common.controllers.BasicController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/branch")
public class BranchController extends BasicController {
    @RequestMapping(value = "/initializeBranch")
    @ResponseBody
    @ApiRestAction(modelClass = InitializeBranchModel.class, serviceClass = BranchService.class, serviceMethodName = "initializeBranch", error = "初始化门店失败")
    public String initializeBranch() {
        return null;
    }

    @RequestMapping(value = "/listBranches")
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
    @RequestMapping(value = "/deleteBranch")
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
    @RequestMapping(value = "/pullBranchInfos")
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
    @RequestMapping(value = "/disableGoods")
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
    @RequestMapping(value = "/renewCallback")
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
    @RequestMapping(value = "/obtainBranchInfo")
    @ResponseBody
    @ApiRestAction(modelClass = ObtainBranchInfoModel.class, serviceClass = BranchService.class, serviceMethodName = "obtainBranchInfo", error = "获取门店信息失败")
    public String obtainBranchInfo() {
        return null;
    }
}
