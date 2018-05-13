package build.dream.catering.controllers;

import build.dream.catering.models.meituan.CheckIsBindingModel;
import build.dream.catering.models.meituan.GenerateBindingStoreLinkModel;
import build.dream.catering.models.meituan.ObtainMeiTuanOrderModel;
import build.dream.catering.services.MeiTuanService;
import build.dream.common.controllers.BasicController;
import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.MethodCaller;
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
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        MethodCaller methodCaller = () -> {
            GenerateBindingStoreLinkModel generateBindingStoreLinkModel = ApplicationHandler.instantiateObject(GenerateBindingStoreLinkModel.class, requestParameters);
            generateBindingStoreLinkModel.validateAndThrow();
            return meiTuanService.generateBindingStoreLink(generateBindingStoreLinkModel);
        };
        return ApplicationHandler.callMethod(methodCaller, "生成门店绑定链接失败", requestParameters);
    }

    /**
     * 拉取美团订单
     *
     * @return
     */
    @RequestMapping(value = "/obtainMeiTuanOrder")
    @ResponseBody
    public String obtainMeiTuanOrder() {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        MethodCaller methodCaller = () -> {
            ObtainMeiTuanOrderModel obtainMeiTuanOrderModel = ApplicationHandler.instantiateObject(ObtainMeiTuanOrderModel.class, requestParameters);
            obtainMeiTuanOrderModel.validateAndThrow();

            return meiTuanService.obtainMeiTuanOrder(obtainMeiTuanOrderModel);
        };
        return ApplicationHandler.callMethod(methodCaller, "拉取美团订单失败", requestParameters);
    }

    /**
     * 查询门店是否绑定美团
     *
     * @return
     */
    @RequestMapping(value = "/checkIsBinding")
    @ResponseBody
    public String checkIsBinding() {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        MethodCaller methodCaller = () -> {
            CheckIsBindingModel checkIsBindingModel = ApplicationHandler.instantiateObject(CheckIsBindingModel.class, requestParameters);
            checkIsBindingModel.validateAndThrow();

            return meiTuanService.checkIsBinding(checkIsBindingModel);
        };
        return ApplicationHandler.callMethod(methodCaller, "查询门店是否绑定美团失败", requestParameters);
    }
}
