package build.dream.catering.controllers;

import build.dream.catering.models.vip.ObtainVipInfoModel;
import build.dream.catering.models.vip.SaveVipInfoModel;
import build.dream.catering.models.vip.SaveVipTypeModel;
import build.dream.catering.services.VipService;
import build.dream.common.annotations.ApiRestAction;
import build.dream.common.controllers.BasicController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/vip")
public class VipController extends BasicController {
    /**
     * 保存会员类型
     *
     * @return
     */
    @RequestMapping(value = "/saveVipType", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = SaveVipTypeModel.class, serviceClass = VipService.class, serviceMethodName = "saveVipType", error = "保存会员类型失败")
    public String saveVipType() {
        return null;
    }

    /**
     * 获取会员信息
     *
     * @return
     */
    @RequestMapping(value = "/obtainVipInfo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = ObtainVipInfoModel.class, serviceClass = VipService.class, serviceMethodName = "obtainVipInfo", error = "获取会员信息失败")
    public String obtainVipInfo() {
        return null;
    }

    /**
     * 保存会员信息
     *
     * @return
     */
    @RequestMapping(value = "/saveVipInfo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @ApiRestAction(modelClass = SaveVipInfoModel.class, serviceClass = VipService.class, serviceMethodName = "saveVipInfo", error = "获取会员信息失败")
    public String saveVipInfo() {
        return null;
    }
}
