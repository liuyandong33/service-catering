package build.dream.catering.controllers;

import build.dream.catering.models.branch.*;
import build.dream.catering.services.BranchService;
import build.dream.common.controllers.BasicController;
import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.MethodCaller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping(value = "/branch")
public class BranchController extends BasicController {
    @Autowired
    private BranchService branchService;

    @RequestMapping(value = "/initializeBranch")
    @ResponseBody
    public String initializeBranch() {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        MethodCaller methodCaller = () -> {
            InitializeBranchModel initializeBranchModel = ApplicationHandler.instantiateObject(InitializeBranchModel.class, requestParameters);
            initializeBranchModel.validateAndThrow();
            return branchService.initializeBranch(initializeBranchModel);
        };
        return ApplicationHandler.callMethod(methodCaller, "初始化门店失败", requestParameters);
    }

    @RequestMapping(value = "/listBranches")
    @ResponseBody
    public String listBranches() {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        MethodCaller methodCaller = () -> {
            ListBranchesModel listBranchesModel = ApplicationHandler.instantiateObject(ListBranchesModel.class, requestParameters);
            listBranchesModel.validateAndThrow();
            return branchService.listBranches(listBranchesModel);
        };
        return ApplicationHandler.callMethod(methodCaller, "查询门店列表失败", requestParameters);
    }

    /**
     * 删除门店
     *
     * @return
     */
    @RequestMapping(value = "/deleteBranch")
    @ResponseBody
    public String deleteBranch() {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        MethodCaller methodCaller = () -> {
            DeleteBranchModel deleteBranchModel = ApplicationHandler.instantiateObject(DeleteBranchModel.class, requestParameters);
            deleteBranchModel.validateAndThrow();
            return branchService.deleteBranch(deleteBranchModel);
        };
        return ApplicationHandler.callMethod(methodCaller, "删除门店信息失败", requestParameters);
    }

    /**
     * 拉取门店信息
     *
     * @return
     */
    @RequestMapping(value = "/pullBranchInfos")
    @ResponseBody
    public String pullBranchInfos() {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        MethodCaller methodCaller = () -> {
            PullBranchInfosModel pullBranchInfosModel = ApplicationHandler.instantiateObject(PullBranchInfosModel.class, requestParameters);
            pullBranchInfosModel.validateAndThrow();
            return branchService.pullBranchInfos(pullBranchInfosModel);
        };
        return ApplicationHandler.callMethod(methodCaller, "拉取门店信息失败", requestParameters);
    }

    /**
     * 禁用门店产品
     *
     * @return
     */
    @RequestMapping(value = "/disableGoods")
    @ResponseBody
    public String disableGoods() {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        MethodCaller methodCaller = () -> {
            DisableGoodsModel disableGoodsModel = ApplicationHandler.instantiateObject(DisableGoodsModel.class, requestParameters);
            disableGoodsModel.validateAndThrow();
            return branchService.disableGoods(disableGoodsModel);
        };
        return ApplicationHandler.callMethod(methodCaller, "禁用门店产品失败", requestParameters);
    }

    /**
     * 门店续费回调
     *
     * @return
     */
    @RequestMapping(value = "/renewCallback")
    @ResponseBody
    public String renewCallback() {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        MethodCaller methodCaller = () -> {
            RenewCallbackModel renewCallbackModel = ApplicationHandler.instantiateObject(RenewCallbackModel.class, requestParameters);
            renewCallbackModel.validateAndThrow();
            return branchService.handleRenewCallback(renewCallbackModel);
        };
        return ApplicationHandler.callMethod(methodCaller, "处理门店续费回调失败", requestParameters);
    }

    /**
     * 获取门店信息
     *
     * @return
     */
    @RequestMapping(value = "/obtainBranchInfo")
    @ResponseBody
    public String obtainBranchInfo() {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        MethodCaller methodCaller = () -> {
            ObtainBranchInfoModel obtainBranchInfoModel = ApplicationHandler.instantiateObject(ObtainBranchInfoModel.class, requestParameters);
            obtainBranchInfoModel.validateAndThrow();
            return branchService.obtainBranchInfo(obtainBranchInfoModel);
        };
        return ApplicationHandler.callMethod(methodCaller, "获取门店信息失败", requestParameters);
    }
}
