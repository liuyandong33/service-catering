package build.dream.catering.listeners;

import build.dream.catering.models.dietorder.CancelOrderModel;
import build.dream.catering.services.DietOrderService;
import net.sf.json.JSONObject;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigInteger;

@Component
public class OrderInvalidMessageListener implements MessageListener<String, String> {
    @Autowired
    private DietOrderService dietOrderService;

    @KafkaListener(topics = "${order.invalid.message.topic}")
    @Override
    public void onMessage(ConsumerRecord<String, String> data) {
        String key = data.key();
        String value = data.value();

        JSONObject info = JSONObject.fromObject(value);
        CancelOrderModel cancelOrderModel = new CancelOrderModel();
//        cancelOrderModel.setTenantId(BigInteger.valueOf(info.getLong("tenantId")));
//        cancelOrderModel.setTenantId(BigInteger.valueOf(info.getLong("branchId")));
//        cancelOrderModel.setTenantId(BigInteger.valueOf(info.getLong("orderId")));
        try {
            dietOrderService.cancelOrder(cancelOrderModel);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }
}
