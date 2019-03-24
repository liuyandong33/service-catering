package build.dream.catering.controllers;

import build.dream.catering.models.miniprogram.ObtainSessionWithJsCodeModel;
import build.dream.common.annotations.ApiRestAction;
import build.dream.common.api.ApiRest;
import build.dream.common.beans.WebResponse;
import build.dream.common.controllers.BasicController;
import build.dream.common.utils.*;
import org.apache.commons.collections.MapUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "/miniProgram")
public class MiniProgramController extends BasicController {
    @RequestMapping(value = "/obtainSessionWithJsCode", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(error = "code换取session_key失败")
    public String obtainSessionWithJsCode() throws Exception {
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        ObtainSessionWithJsCodeModel obtainSessionWithJsCodeModel = ApplicationHandler.instantiateObject(ObtainSessionWithJsCodeModel.class, requestParameters);
        obtainSessionWithJsCodeModel.validateAndThrow();

        Map<String, String> obtainSessionRequestParameters = new HashMap<String, String>();
        obtainSessionRequestParameters.put("appid", "wxc375e036b110a28f");
        obtainSessionRequestParameters.put("secret", "415627f3ec7bf51497a98e94546affa7");
        obtainSessionRequestParameters.put("js_code", obtainSessionWithJsCodeModel.getCode());
        obtainSessionRequestParameters.put("grant_type", "authorization_code");

        String url = "https://api.weixin.qq.com/sns/jscode2session";
        WebResponse webResponse = OutUtils.doGetWithRequestParameters(url, null, obtainSessionRequestParameters);
        Map<String, Object> resultMap = JacksonUtils.readValueAsMap(webResponse.getResult(), String.class, Object.class);
        ValidateUtils.isTrue(!resultMap.containsKey("errcode"), MapUtils.getString(resultMap, "errmsg"));

        ApiRest apiRest = ApiRest.builder().data(resultMap).message("code换取session_key成功！").successful(true).build();
        return GsonUtils.toJson(apiRest);
    }
}
