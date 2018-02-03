package build.dream.catering.controllers;

import build.dream.catering.models.anubis.ChainStoreModel;
import build.dream.catering.services.AnubisService;
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
@RequestMapping(value = "/anubis")
public class AnubisController extends BasicController {
    @Autowired
    private AnubisService anubisService;
    /*@RequestMapping(value = "/getAccessToken")
    @ResponseBody
    public String getAccessToken() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            Map<String, String> params = new HashMap<String, String>();
            params.put("app_id", ConfigurationUtils.getConfiguration(Constants.ANUBIS_APP_ID));
            params.put("salt", "1500");
            apiRest = AnubisUtils.callAnubisSystem(ConfigurationUtils.getConfiguration(Constants.ANUBIS_SERVICE_URL) + Constants.ANUBIS_GET_ACCESS_TOKEN_URI, params, ConfigurationUtils.getConfiguration(Constants.ANUBIS_APP_SECRET));
            int a = 100;
        } catch (Exception e) {
            LogUtils.error("获取token失败", controllerSimpleName, "getAccessToken", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }*/

    /**
     * 添加门店
     *
     * @return
     */
    @RequestMapping(value = "/chainStore")
    @ResponseBody
    public String chainStore() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            ChainStoreModel chainStoreModel = ApplicationHandler.instantiateObject(ChainStoreModel.class, requestParameters);
            chainStoreModel.validateAndThrow();

            apiRest = anubisService.chainStore(chainStoreModel);
        } catch (Exception e) {
            LogUtils.error("添加门店失败", controllerSimpleName, "getAccessToken", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }
}
