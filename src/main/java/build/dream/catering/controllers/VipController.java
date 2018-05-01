package build.dream.catering.controllers;

import build.dream.catering.models.vip.ObtainVipInfoModel;
import build.dream.catering.models.vip.SaveVipInfoModel;
import build.dream.catering.services.VipService;
import build.dream.common.controllers.BasicController;
import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.MethodCaller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping(value = "/vip")
public class VipController extends BasicController {
    @Autowired
    private VipService vipService;

    /**
     * 获取会员信息
     *
     * @return
     */
    @RequestMapping(value = "/obtainVipInfo")
    @ResponseBody
    public String obtainVipInfo() {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        MethodCaller methodCaller = () -> {
            ObtainVipInfoModel obtainVipInfoModel = ApplicationHandler.instantiateObject(ObtainVipInfoModel.class, requestParameters);
            obtainVipInfoModel.validateAndThrow();
            return vipService.obtainVipInfo(obtainVipInfoModel);
        };
        return ApplicationHandler.callMethod(methodCaller, "获取会员信息失败", requestParameters);
    }

    /**
     * 保存会员信息
     *
     * @return
     */
    @RequestMapping(value = "/saveVipInfo")
    @ResponseBody
    public String saveVipInfo() {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        MethodCaller methodCaller = () -> {
            SaveVipInfoModel saveVipInfoModel = ApplicationHandler.instantiateObject(SaveVipInfoModel.class, requestParameters, "yyyy-MM-dd", "");
            saveVipInfoModel.validateAndThrow();

            return vipService.saveVipInfo(saveVipInfoModel);
        };
        return ApplicationHandler.callMethod(methodCaller, "获取会员信息失败", requestParameters);
    }
}
