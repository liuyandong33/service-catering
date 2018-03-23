package build.dream.catering.controllers;

import build.dream.catering.constants.Constants;
import build.dream.catering.models.pos.InitPosModel;
import build.dream.catering.services.ElemeService;
import build.dream.catering.services.PosService;
import build.dream.common.api.ApiRest;
import build.dream.common.controllers.BasicController;
import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.ConfigurationUtils;
import build.dream.common.utils.GsonUtils;
import build.dream.common.utils.LogUtils;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping(value = "/pos")
public class PosController extends BasicController {
    @Autowired
    private PosService posService;

    /**
     * 初始化POS
     *
     * @return
     */
    @RequestMapping(value = "/initPos")
    @ResponseBody
    public String initPos() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            InitPosModel initPosModel = ApplicationHandler.instantiateObject(InitPosModel.class, requestParameters);
            initPosModel.validateAndThrow();

            apiRest = posService.initPos(initPosModel);
        } catch (Exception e) {
            LogUtils.error("初始化POS失败", controllerSimpleName, "initPos", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    @RequestMapping(value = "/uploadFile")
    @ResponseBody
    public String uploadFile(HttpServletRequest httpServletRequest) {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            Validate.isTrue(httpServletRequest instanceof MultipartHttpServletRequest, "请上传文件！");
            MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) httpServletRequest;

            MultipartFile multipartFile = multipartHttpServletRequest.getFile("file");
            Validate.notNull(multipartFile, "请上传文件！");

            String tenantId = requestParameters.get("tenantId");
            ApplicationHandler.notBlank(tenantId, "tenantId");

            String branchId = requestParameters.get("branchId");
            ApplicationHandler.notBlank(branchId, "branchId");

            String type = requestParameters.get("type");
            ApplicationHandler.notBlank(type, "type");

            String originalFilename = multipartFile.getOriginalFilename();
            String posDataPath = ConfigurationUtils.getConfiguration(Constants.POS_DATA_PATH);
            String directoryPath = null;
            if ("database".equals(type)) {
                directoryPath = posDataPath + File.separator + tenantId + File.separator + branchId + File.separator + "databases";
            } else if ("log".equals(type)) {
                directoryPath = posDataPath + File.separator + tenantId + File.separator + branchId + File.separator + "logs";
            }
            File directory = new File(directoryPath);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            File file = new File(directoryPath + File.separator + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + originalFilename);
            multipartFile.transferTo(file);

            apiRest = new ApiRest();
            apiRest.setMessage("上传文件成功！");
        } catch (Exception e) {
            LogUtils.error("上传文件失败", controllerSimpleName, "uploadFile", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    @Autowired
    private ElemeService elemeService;

    @RequestMapping(value = "/test")
    @ResponseBody
    public String test() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            int count = Integer.parseInt(requestParameters.get("count"));
            int interval = Integer.parseInt(requestParameters.get("interval"));
            elemeService.pushElemeMessage(BigInteger.ONE, BigInteger.ONE, BigInteger.ZERO, 10, UUID.randomUUID().toString(), count, interval);
            apiRest = new ApiRest();
            apiRest.setMessage("测试成功！");
            apiRest.setSuccessful(true);
        } catch (Exception e) {
            LogUtils.error("测试失败", controllerSimpleName, "test", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }
}
