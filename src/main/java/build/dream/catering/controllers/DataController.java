package build.dream.catering.controllers;

import build.dream.catering.models.data.UploadDataModel;
import build.dream.catering.services.DataService;
import build.dream.common.controllers.BasicController;
import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.MethodCaller;
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

    @RequestMapping(value = "/uploadData", method = RequestMethod.POST)
    @ResponseBody
    public String uploadData() {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        MethodCaller methodCaller = () -> {
            UploadDataModel uploadDataModel = ApplicationHandler.instantiateObject(UploadDataModel.class, requestParameters);
            uploadDataModel.validateAndThrow();
            return dataService.uploadData(uploadDataModel);
        };
        return ApplicationHandler.callMethod(methodCaller, "上传数据失败", requestParameters);
    }
}
