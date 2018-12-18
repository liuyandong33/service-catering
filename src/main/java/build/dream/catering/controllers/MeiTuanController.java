package build.dream.catering.controllers;

import build.dream.catering.constants.Constants;
import build.dream.catering.models.meituan.CheckIsBindingModel;
import build.dream.catering.models.meituan.GenerateBindingStoreLinkModel;
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
            String orderJson = "{\"avgSendTime\":1804.0,\"caution\":\"\",\"cityId\":361123,\"ctime\":1545121520,\"daySeq\":\"1\",\"deliveryTime\":0,\"detail\":\"[{\\\"app_food_code\\\":\\\"鸡丁培根炒饭\\\",\\\"box_num\\\":1,\\\"box_price\\\":0,\\\"cart_id\\\":0,\\\"food_discount\\\":1,\\\"food_name\\\":\\\"鸡丁培根炒饭\\\",\\\"food_property\\\":\\\"\\\",\\\"price\\\":15,\\\"quantity\\\":1,\\\"sku_id\\\":\\\"\\\",\\\"spec\\\":\\\"\\\",\\\"unit\\\":\\\"\\\"},{\\\"app_food_code\\\":\\\"培根鸡柳炒饭\\\",\\\"box_num\\\":1,\\\"box_price\\\":0,\\\"cart_id\\\":0,\\\"food_discount\\\":1,\\\"food_name\\\":\\\"培根鸡柳炒饭\\\",\\\"food_property\\\":\\\"\\\",\\\"price\\\":15,\\\"quantity\\\":1,\\\"sku_id\\\":\\\"\\\",\\\"spec\\\":\\\"\\\",\\\"unit\\\":\\\"\\\"},{\\\"app_food_code\\\":\\\"鸡排热狗炒饭\\\",\\\"box_num\\\":1,\\\"box_price\\\":0,\\\"cart_id\\\":0,\\\"food_discount\\\":1,\\\"food_name\\\":\\\"鸡排热狗炒饭\\\",\\\"food_property\\\":\\\"\\\",\\\"price\\\":15,\\\"quantity\\\":1,\\\"sku_id\\\":\\\"\\\",\\\"spec\\\":\\\"\\\",\\\"unit\\\":\\\"\\\"},{\\\"app_food_code\\\":\\\"老火白粥\\\",\\\"box_num\\\":1,\\\"box_price\\\":0,\\\"cart_id\\\":0,\\\"food_discount\\\":1,\\\"food_name\\\":\\\"老火白粥\\\",\\\"food_property\\\":\\\"\\\",\\\"price\\\":5,\\\"quantity\\\":1,\\\"sku_id\\\":\\\"\\\",\\\"spec\\\":\\\"\\\",\\\"unit\\\":\\\"\\\"}]\",\"dinnersNumber\":0,\"ePoiId\":\"1708Z1956\",\"extras\":\"[{\\\"act_detail_id\\\":1530154103,\\\"mt_charge\\\":9,\\\"poi_charge\\\":0,\\\"reduce_fee\\\":9,\\\"remark\\\":\\\"满50.0元减9.0元\\\",\\\"type\\\":2},{\\\"act_detail_id\\\":274839715,\\\"mt_charge\\\":8,\\\"poi_charge\\\":0,\\\"reduce_fee\\\":8,\\\"remark\\\":\\\"用户使用了支付红包减8元\\\",\\\"type\\\":9},{}]\",\"favorites\":false,\"hasInvoiced\":0,\"invoiceTitle\":\"\",\"isFavorites\":false,\"isPoiFirstOrder\":true,\"isThirdShipping\":0,\"latitude\":28.680318,\"logisticsCode\":\"1004\",\"longitude\":118.243853,\"orderId\":59309931191316390,\"orderIdView\":59309931191316390,\"originalPrice\":50.0,\"payType\":2,\"poiAddress\":\"冰溪镇滨江景园1#楼第一层9号\",\"poiFirstOrder\":true,\"poiId\":5930993,\"poiName\":\"老街锅贴\",\"poiPhone\":\"15179362213\",\"poiReceiveDetail\":\"{\\\"actOrderChargeByMt\\\":[{\\\"comment\\\":\\\"活动款\\\",\\\"feeTypeDesc\\\":\\\"活动款\\\",\\\"feeTypeId\\\":10019,\\\"moneyCent\\\":1700}],\\\"actOrderChargeByPoi\\\":[],\\\"foodShareFeeChargeByPoi\\\":900,\\\"logisticsFee\\\":0,\\\"onlinePayment\\\":3300,\\\"wmPoiReceiveCent\\\":4100}\",\"recipientAddress\":\"御花园@#江西省上饶市玉山县新建路新建路199号三清公园\",\"recipientName\":\"程(先生)\",\"recipientPhone\":\"18720334024\",\"shipperPhone\":\"\",\"shippingFee\":0.0,\"status\":2,\"taxpayerId\":\"\",\"total\":33.0,\"utime\":1545121520}";
            meiTuanService.handleOrderEffectiveCallback(JSONObject.fromObject(orderJson), UUID.randomUUID().toString(), 1);
            returnValue = Constants.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            returnValue = e.getMessage();
        }
        return returnValue;
    }
}
