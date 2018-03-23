package build.dream.catering.controllers;

import build.dream.catering.models.pos.InitPosModel;
import build.dream.catering.services.PosService;
import build.dream.common.api.ApiRest;
import build.dream.common.controllers.BasicController;
import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.GsonUtils;
import build.dream.common.utils.LogUtils;
import javafx.geometry.Pos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

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
}
