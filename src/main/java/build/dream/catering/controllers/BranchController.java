package build.dream.catering.controllers;

import build.dream.catering.models.branch.DeleteBranchModel;
import build.dream.catering.models.branch.InitializeBranchModel;
import build.dream.catering.models.branch.ListBranchesModel;
import build.dream.catering.models.branch.PullBranchInfosModel;
import build.dream.catering.services.BranchService;
import build.dream.common.api.ApiRest;
import build.dream.common.controllers.BasicController;
import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.GsonUtils;
import build.dream.common.utils.LogUtils;
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
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            InitializeBranchModel initializeBranchModel = ApplicationHandler.instantiateObject(InitializeBranchModel.class, requestParameters);
            initializeBranchModel.validateAndThrow();
            apiRest = branchService.initializeBranch(initializeBranchModel);
        } catch (Exception e) {
            LogUtils.error("初始化门店失败", controllerSimpleName, "initializeBranch", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    @RequestMapping(value = "/listBranches")
    @ResponseBody
    public String listBranches() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            ListBranchesModel listBranchesModel = ApplicationHandler.instantiateObject(ListBranchesModel.class, requestParameters);
            listBranchesModel.validateAndThrow();
            apiRest = branchService.listBranches(listBranchesModel);
        } catch (Exception e) {
            LogUtils.error("查询门店列表失败", controllerSimpleName, "listBranches", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    /**
     * 删除门店
     *
     * @return
     */
    @RequestMapping(value = "/deleteBranch")
    @ResponseBody
    public String deleteBranch() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            DeleteBranchModel deleteBranchModel = ApplicationHandler.instantiateObject(DeleteBranchModel.class, requestParameters);
            deleteBranchModel.validateAndThrow();
            apiRest = branchService.deleteBranch(deleteBranchModel);
        } catch (Exception e) {
            LogUtils.error("删除门店信息失败", controllerSimpleName, "deleteBranch", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    /**
     * 拉取门店信息
     *
     * @return
     */
    @RequestMapping(value = "/pullBranchInfos")
    @ResponseBody
    public String pullBranchInfos() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            PullBranchInfosModel pullBranchInfosModel = ApplicationHandler.instantiateObject(PullBranchInfosModel.class, requestParameters);
            pullBranchInfosModel.validateAndThrow();
            apiRest = branchService.pullBranchInfos(pullBranchInfosModel);
        } catch (Exception e) {
            LogUtils.error("拉取门店信息失败", controllerSimpleName, "pullBranchInfos", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }
}
