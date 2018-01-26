package build.dream.catering.controllers;

import build.dream.catering.models.vip.ObtainVipInfoModel;
import build.dream.catering.models.vip.SaveVipInfoModel;
import build.dream.catering.services.VipService;
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
@RequestMapping(value = "/vip")
public class VipController extends BasicController {
    @Autowired
    private VipService vipService;

    @RequestMapping(value = "/obtainVipInfo")
    @ResponseBody
    public String obtainVipInfo() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            ObtainVipInfoModel obtainVipInfoModel = ApplicationHandler.instantiateObject(ObtainVipInfoModel.class, requestParameters);
            obtainVipInfoModel.validateAndThrow();
            apiRest = vipService.obtainVipInfo(obtainVipInfoModel);
        } catch (Exception e) {
            LogUtils.error("获取会员信息失败", controllerSimpleName, "obtainVipInfo", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    @RequestMapping(value = "/saveVipInfo")
    @ResponseBody
    public String saveVipInfo() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            SaveVipInfoModel saveVipInfoModel = ApplicationHandler.instantiateObject(SaveVipInfoModel.class, requestParameters, "yyyy-MM-dd", "");
            saveVipInfoModel.validateAndThrow();

            apiRest = vipService.saveVipInfo(saveVipInfoModel);
        } catch (Exception e) {
            LogUtils.error("获取会员信息失败", controllerSimpleName, "saveVipInfo", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }
}
