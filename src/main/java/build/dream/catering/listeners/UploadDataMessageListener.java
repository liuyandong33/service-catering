package build.dream.catering.listeners;

import build.dream.catering.constants.Constants;
import build.dream.catering.services.DataService;
import net.sf.json.JSONObject;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.stereotype.Component;

@Component
public class UploadDataMessageListener implements MessageListener<String, String> {
    @Autowired
    private DataService dataService;

    @KafkaListener
    @Override
    public void onMessage(ConsumerRecord<String, String> consumerRecord) {
        String value = consumerRecord.value();
        JSONObject valueJsonObject = JSONObject.fromObject(value);
        String dataType = valueJsonObject.getString("dataType");
        String data = valueJsonObject.getString("data");
        if (Constants.DATA_TYPE_DIET_ORDER.equals(dataType)) {
            dataService.saveDietOrder(data);
        }
    }
}
