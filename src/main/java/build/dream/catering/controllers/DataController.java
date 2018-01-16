package build.dream.catering.controllers;

import build.dream.common.api.ApiRest;
import build.dream.common.controllers.BasicController;
import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.GsonUtils;
import build.dream.common.utils.LogUtils;
import build.dream.catering.models.data.UploadDataModel;
import build.dream.catering.services.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping(value = "/data")
public class DataController extends BasicController {
    @Autowired
    private DataService dataService;

    @RequestMapping(value = "/uploadData", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String uploadData() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            UploadDataModel uploadDataModel = ApplicationHandler.instantiateObject(UploadDataModel.class, requestParameters);
            uploadDataModel.validateAndThrow();
            apiRest = dataService.uploadData(uploadDataModel);
        } catch (Exception e) {
            LogUtils.error("查询门店信息失败", controllerSimpleName, "uploadData", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }
}
