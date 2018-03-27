package build.dream.catering;

import build.dream.common.utils.GsonUtils;
import net.sf.json.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@ServletComponentScan
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class Application extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(Application.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        /*String aa = "{\"avgSendTime\":1815.0,\"caution\":\"\",\"cityId\":370724,\"ctime\":1522130913,\"daySeq\":\"4\",\"deliveryTime\":0,\"detail\":\"[{\\\"app_food_code\\\":\\\"三文治刈包+鸡米花+金桔柠檬茶\\\",\\\"box_num\\\":1,\\\"box_price\\\":2,\\\"cart_id\\\":0,\\\"food_discount\\\":1,\\\"food_name\\\":\\\"三文治刈包+鸡米花+金桔柠檬茶\\\",\\\"food_property\\\":\\\"\\\",\\\"price\\\":28,\\\"quantity\\\":1,\\\"sku_id\\\":\\\"\\\",\\\"spec\\\":\\\"\\\",\\\"unit\\\":\\\"份\\\"}]\",\"dinnersNumber\":0,\"ePoiId\":\"2268Z2697\",\"extras\":\"[{\\\"act_detail_id\\\":285533416,\\\"mt_charge\\\":0,\\\"poi_charge\\\":12,\\\"reduce_fee\\\":12,\\\"remark\\\":\\\"满30.0元减12.0元\\\",\\\"type\\\":2},{}]\",\"hasInvoiced\":0,\"invoiceTitle\":\"\",\"isFavorites\":false,\"isPoiFirstOrder\":false,\"isThirdShipping\":0,\"latitude\":36.507332,\"logisticsCode\":\"1004\",\"longitude\":118.548744,\"orderId\":41632522461826449,\"orderIdView\":41632522461826449,\"originalPrice\":34.0,\"payType\":2,\"poiAddress\":\"城关街道兴隆路4327号10幢1059号商铺（全福元家居）\",\"poiFirstOrder\":false,\"poiId\":4163252,\"poiName\":\"熊掌门（G米翅，鸡翅包饭，台湾刈包）\",\"poiPhone\":\"15963640070\",\"poiReceiveDetail\":\"{\\\"actOrderChargeByMt\\\":[{\\\"comment\\\":\\\"活动款\\\",\\\"feeTypeDesc\\\":\\\"活动款\\\",\\\"feeTypeId\\\":10019,\\\"moneyCent\\\":0}],\\\"actOrderChargeByPoi\\\":[{\\\"comment\\\":\\\"满30.0元减12.0元\\\",\\\"feeTypeDesc\\\":\\\"活动款\\\",\\\"feeTypeId\\\":10019,\\\"moneyCent\\\":1200}],\\\"foodShareFeeChargeByPoi\\\":644,\\\"logisticsFee\\\":400,\\\"onlinePayment\\\":2200,\\\"wmPoiReceiveCent\\\":1156}\",\"recipientAddress\":\"馋猫来了时尚炉鱼餐厅 (朐山路御景花园沿街馋猫来了时尚炉鱼餐厅)@#山东省潍坊市临朐县朐山路朐山路馋猫来了时尚炉鱼餐厅\",\"recipientName\":\"冯晓楠(女士)\",\"recipientPhone\":\"13406657365\",\"shipperPhone\":\"\",\"shippingFee\":4.0,\"status\":2,\"taxpayerId\":\"\",\"total\":22.0,\"utime\":1522130913}";
        JSONObject jsonObject = JSONObject.fromObject(aa);
        Map<String, String> orderMap = new HashMap<String, String>();
        Set set = jsonObject.keySet();
        for (Object s : set) {
            orderMap.put(s.toString(), jsonObject.getString(s.toString()));
        }

        Map<String, Object> callbackParameters = new HashMap<String, Object>();
        callbackParameters.put("developerId", 100120);
        callbackParameters.put("ePoiId", "9384019");
        callbackParameters.put("sign", "52b379754c40c7865a48b84a24fb99c1ebb49f11");
        callbackParameters.put("order", orderMap);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("uuid", DigestUtils.md5Hex(aa));
        map.put("callbackParameters", callbackParameters);
        map.put("type", 1);
        map.put("count", 10);
        System.out.println(GsonUtils.toJson(map));*/
    }
}
