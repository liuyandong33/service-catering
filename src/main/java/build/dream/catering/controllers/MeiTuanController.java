package build.dream.catering.controllers;

import build.dream.catering.constants.Constants;
import build.dream.catering.models.meituan.CheckIsBindingModel;
import build.dream.catering.models.meituan.GenerateBindingStoreLinkModel;
import build.dream.catering.models.meituan.ObtainMeiTuanOrderModel;
import build.dream.catering.models.meituan.QueryPoiInfoModel;
import build.dream.catering.services.MeiTuanService;
import build.dream.common.annotations.ApiRestAction;
import build.dream.common.controllers.BasicController;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.UUID;

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


    @RequestMapping(value = "/test")
    @ResponseBody
    public String test() {
        String returnValue = null;
        try {
            String orderJson = "{\"deliveryTime\":0,\"originalPrice\":35.2,\"orderId\":5523780463661605,\"latitude\":29.629082,\"extras\":\"[{\\\"act_detail_id\\\":410727619,\\\"mt_charge\\\":0,\\\"poi_charge\\\":3,\\\"reduce_fee\\\":3,\\\"remark\\\":\\\"满30.0元减3.0元\\\",\\\"type\\\":2},{}]\",\"cityId\":500100,\"poiReceiveDetail\":\"{\\\"actOrderChargeByMt\\\":[{\\\"comment\\\":\\\"活动款\\\",\\\"feeTypeDesc\\\":\\\"活动款\\\",\\\"feeTypeId\\\":10019,\\\"moneyCent\\\":0}],\\\"actOrderChargeByPoi\\\":[{\\\"comment\\\":\\\"满30.0元减3.0元\\\",\\\"feeTypeDesc\\\":\\\"活动款\\\",\\\"feeTypeId\\\":10019,\\\"moneyCent\\\":300}],\\\"foodShareFeeChargeByPoi\\\":522,\\\"logisticsFee\\\":320,\\\"onlinePayment\\\":3220,\\\"wmPoiReceiveCent\\\":2378}\",\"daySeq\":\"4\",\"total\":32.2,\"payType\":2,\"ePoiId\":\"8111Z100006448\",\"hasInvoiced\":0,\"avgSendTime\":1811.0,\"poiPhone\":\"17783190525\",\"ctime\":1531272591,\"isFavorites\":false,\"recipientName\":\"邱*\",\"poiId\":552378,\"recipientAddress\":\"春*山\",\"caution\":\"收餐人隐私号 15696414069_4013，手机号 158****8838 不需要餐具\",\"invoiceTitle\":\"\",\"isPoiFirstOrder\":true,\"taxpayerId\":\"\",\"longitude\":106.545025,\"recipientPhone\":\"156*013\",\"dinnersNumber\":99,\"poiName\":\"龙麦轩包粥坊（天湖美镇店）\",\"utime\":1531272591,\"poiFirstOrder\":true,\"logisticsCode\":\"1001\",\"isThirdShipping\":0,\"shipperPhone\":\"\",\"poiAddress\":\"和睦北路2号附71号\",\"shippingFee\":3.2,\"detail\":\"[{\\\"app_food_code\\\":\\\"烧麦\\\",\\\"box_num\\\":3,\\\"box_price\\\":0,\\\"cart_id\\\":0,\\\"food_discount\\\":1,\\\"food_name\\\":\\\"烧麦\\\",\\\"food_property\\\":\\\"\\\",\\\"price\\\":2,\\\"quantity\\\":3,\\\"sku_id\\\":\\\"\\\",\\\"spec\\\":\\\"\\\",\\\"unit\\\":\\\"份\\\"},{\\\"app_food_code\\\":\\\"鲜/酱肉包子各3个\\\",\\\"box_num\\\":2,\\\"box_price\\\":1,\\\"cart_id\\\":0,\\\"food_discount\\\":1,\\\"food_name\\\":\\\"鲜/酱肉包子各3个\\\",\\\"food_property\\\":\\\"\\\",\\\"price\\\":6,\\\"quantity\\\":2,\\\"sku_id\\\":\\\"\\\",\\\"spec\\\":\\\"\\\",\\\"unit\\\":\\\"份\\\"},{\\\"app_food_code\\\":\\\"青菜粥\\\",\\\"box_num\\\":3,\\\"box_price\\\":1,\\\"cart_id\\\":0,\\\"food_discount\\\":1,\\\"food_name\\\":\\\"青菜粥\\\",\\\"food_property\\\":\\\"\\\",\\\"price\\\":3,\\\"quantity\\\":3,\\\"sku_id\\\":\\\"\\\",\\\"spec\\\":\\\"\\\",\\\"unit\\\":\\\"份\\\"}]\",\"orderIdView\":5523780463661605,\"status\":2}";
            meiTuanService.handleOrderEffectiveCallback(JSONObject.fromObject(orderJson), UUID.randomUUID().toString(), 1);
            returnValue = Constants.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            returnValue = e.getMessage();
        }
        return returnValue;
    }
}
