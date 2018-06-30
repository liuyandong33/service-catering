package build.dream.catering.controllers;

import build.dream.catering.constants.Constants;
import build.dream.catering.models.pos.OfflinePosModel;
import build.dream.catering.models.pos.OnlinePosModel;
import build.dream.catering.services.PosService;
import build.dream.common.annotations.ApiRestAction;
import build.dream.common.api.ApiRest;
import build.dream.common.controllers.BasicController;
import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.CacheUtils;
import build.dream.common.utils.ConfigurationUtils;
import build.dream.common.utils.GsonUtils;
import org.apache.commons.lang.Validate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Controller
@RequestMapping(value = "/pos")
public class PosController extends BasicController {
    /**
     * 上线POS
     *
     * @return
     */
    @RequestMapping(value = "/onlinePos")
    @ResponseBody
    @ApiRestAction(modelClass = OnlinePosModel.class, serviceClass = PosService.class, serviceMethodName = "onlinePos", error = "上线POS失败")
    public String onlinePos() {
        return null;
    }

    /**
     * 下线POS
     *
     * @return
     */
    @RequestMapping(value = "/offlinePos")
    @ResponseBody
    @ApiRestAction(modelClass = OfflinePosModel.class, serviceClass = PosService.class, serviceMethodName = "offlinePos", error = "下线POS失败")
    public String offlinePos() {
        return null;
    }

    @RequestMapping(value = "/uploadFile")
    @ResponseBody
    @ApiRestAction(error = "上传文件失败")
    public String uploadFile(HttpServletRequest httpServletRequest) throws IOException {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        Validate.isTrue(httpServletRequest instanceof MultipartHttpServletRequest, "请上传文件！");
        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) httpServletRequest;

        MultipartFile multipartFile = multipartHttpServletRequest.getFile("file");
        Validate.notNull(multipartFile, "请上传文件！");

        String tenantId = requestParameters.get("tenantId");
        ApplicationHandler.notBlank(tenantId, "tenantId");

        String branchId = requestParameters.get("branchId");
        ApplicationHandler.notBlank(branchId, "branchId");

        String userId = requestParameters.get("userId");
        ApplicationHandler.notBlank(userId, "userId");

        String type = requestParameters.get("type");
        ApplicationHandler.notBlank(type, "type");

        String originalFilename = multipartFile.getOriginalFilename();
        String posDataPath = ConfigurationUtils.getConfiguration(Constants.POS_DATA_PATH);
        String directoryPath = null;
        if ("database".equals(type)) {
            directoryPath = posDataPath + File.separator + tenantId + File.separator + branchId + File.separator + userId + File.separator + "databases";
        } else if ("log".equals(type)) {
            directoryPath = posDataPath + File.separator + tenantId + File.separator + branchId + File.separator + userId + File.separator + "logs";
        }
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File file = new File(directoryPath + File.separator + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + originalFilename);
        multipartFile.transferTo(file);

        ApiRest apiRest = new ApiRest();
        apiRest.setMessage("上传文件成功！");
        return GsonUtils.toJson(apiRest);
    }

    /**
     * 回执
     *
     * @return
     */
    @RequestMapping(value = "/receipt")
    @ResponseBody
    @ApiRestAction(error = "回执失败")
    public String receipt() {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        String uuid = requestParameters.get("uuid");
        ApplicationHandler.notBlank(uuid, "uuid");

        CacheUtils.delete(uuid);
        ApiRest apiRest = new ApiRest();
        apiRest.setMessage("回执成功！");
        apiRest.setSuccessful(true);
        return GsonUtils.toJson(apiRest);
    }
}
