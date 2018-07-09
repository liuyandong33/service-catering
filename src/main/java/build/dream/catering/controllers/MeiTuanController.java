package build.dream.catering.controllers;

import build.dream.catering.models.meituan.CheckIsBindingModel;
import build.dream.catering.models.meituan.GenerateBindingStoreLinkModel;
import build.dream.catering.models.meituan.ObtainMeiTuanOrderModel;
import build.dream.catering.models.meituan.QueryPoiInfoModel;
import build.dream.catering.services.MeiTuanService;
import build.dream.common.annotations.ApiRestAction;
import build.dream.common.controllers.BasicController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/meiTuan")
public class MeiTuanController extends BasicController {
    @Autowired
    private MeiTuanService meiTuanService;

    /**
     * 生成门店绑定链接
     *
     * @return
     */
    @RequestMapping(value = "/generateBindingStoreLink")
    @ResponseBody
    @ApiRestAction(modelClass = GenerateBindingStoreLinkModel.class, serviceClass = MeiTuanService.class, serviceMethodName = "generateBindingStoreLink", error = "生成门店绑定链接失败")
    public String generateBindingStoreLink() {
        return null;
    }

    /**
     * 拉取美团订单
     *
     * @return
     */
    @RequestMapping(value = "/obtainMeiTuanOrder")
    @ResponseBody
    @ApiRestAction(modelClass = ObtainMeiTuanOrderModel.class, serviceClass = MeiTuanService.class, serviceMethodName = "obtainMeiTuanOrder", error = "拉取美团订单失败")
    public String obtainMeiTuanOrder() {
        return null;
    }

    /**
     * 查询门店是否绑定美团
     *
     * @return
     */
    @RequestMapping(value = "/checkIsBinding")
    @ResponseBody
    @ApiRestAction(modelClass = CheckIsBindingModel.class, serviceClass = MeiTuanService.class, serviceMethodName = "checkIsBinding", error = "查询门店是否绑定美团失败")
    public String checkIsBinding() {
        return null;
    }

    @RequestMapping(value = "/queryPoiInfo")
    @ResponseBody
    @ApiRestAction(modelClass = QueryPoiInfoModel.class, serviceClass = MeiTuanService.class, serviceMethodName = "queryPoiInfo", error = "查询美团门店信息失败")
    public String queryPoiInfo() {
        return null;
    }
}
