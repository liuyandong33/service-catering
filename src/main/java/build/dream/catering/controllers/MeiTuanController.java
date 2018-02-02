package build.dream.catering.controllers;

import build.dream.catering.models.meituan.GenerateBindingStoreLinkModel;
import build.dream.catering.models.meituan.PullMeiTuanOrderModel;
import build.dream.catering.services.MeiTuanService;
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
@RequestMapping(value = "/meiTuan")
public class MeiTuanController extends BasicController {
    @Autowired
    private MeiTuanService meiTuanService;

    /**
     * 生成门店绑定链接
     *
     * @return
     */
    @RequestMapping(value = "/generateBindingStoreLink")
    @ResponseBody
    public String generateBindingStoreLink() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            GenerateBindingStoreLinkModel generateBindingStoreLinkModel = ApplicationHandler.instantiateObject(GenerateBindingStoreLinkModel.class, requestParameters);
            generateBindingStoreLinkModel.validateAndThrow();
            apiRest = meiTuanService.generateBindingStoreLink(generateBindingStoreLinkModel);
        } catch (Exception e) {
            LogUtils.error("生成门店绑定链接失败", controllerSimpleName, "createCategoryWithChildren", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    /**
     * 拉取美团订单
     *
     * @return
     */
    @RequestMapping(value = "/pullMeiTuanOrder")
    @ResponseBody
    public String pullMeiTuanOrder() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            PullMeiTuanOrderModel pullMeiTuanOrderModel = ApplicationHandler.instantiateObject(PullMeiTuanOrderModel.class, requestParameters);
            pullMeiTuanOrderModel.validateAndThrow();

            apiRest = meiTuanService.pullMeiTuanOrder(pullMeiTuanOrderModel);
        } catch (Exception e) {
            LogUtils.error("拉取美团订单失败！", controllerSimpleName, "pullMeiTuanOrder", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }
}
