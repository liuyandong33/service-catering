package build.dream.catering.controllers;

import build.dream.catering.models.pos.OfflinePosModel;
import build.dream.catering.models.pos.OnlinePosModel;
import build.dream.catering.services.PosService;
import build.dream.common.annotations.ApiRestAction;
import build.dream.common.api.ApiRest;
import build.dream.common.controllers.BasicController;
import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.CacheUtils;
import build.dream.common.utils.GsonUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
