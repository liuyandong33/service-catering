package build.dream.catering.controllers;

import build.dream.catering.models.pos.InitPosModel;
import build.dream.catering.services.ElemeService;
import build.dream.catering.services.PosService;
import build.dream.common.api.ApiRest;
import build.dream.common.controllers.BasicController;
import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.GsonUtils;
import build.dream.common.utils.LogUtils;
import javafx.geometry.Pos;
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

    @RequestMapping(value = "/uploadDatabase")
    @ResponseBody
    public String uploadDatabase(HttpServletRequest httpServletRequest) {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            Validate.isTrue(httpServletRequest instanceof MultipartHttpServletRequest, "请上传数据库文件！");
            MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) httpServletRequest;

            MultipartFile databaseFile = multipartHttpServletRequest.getFile("database");

            String originalFilename = databaseFile.getOriginalFilename();
            File directory = new File("/Users/liuyandong/Desktop/databases");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            File file = new File("/Users/liuyandong/Desktop/databases/" + originalFilename);
            databaseFile.transferTo(file);

            String tenantId = requestParameters.get("tenantId");
            ApplicationHandler.notBlank(tenantId, "tenantId");

            String branchId = requestParameters.get("branchId");
            ApplicationHandler.notBlank(branchId, "branchId");
        } catch (Exception e) {
            LogUtils.error("上传数据库文件失败", controllerSimpleName, "uploadDatabase", e, requestParameters);
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
