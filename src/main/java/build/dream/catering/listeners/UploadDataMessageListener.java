package build.dream.catering.listeners;

import build.dream.catering.constants.ConfigurationKeys;
import build.dream.catering.constants.Constants;
import build.dream.catering.services.DataService;
import build.dream.common.utils.ConfigurationUtils;
import build.dream.common.utils.DingtalkUtils;
import build.dream.common.utils.GsonUtils;
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

    @KafkaListener(topics = "${upload.data.message.topic}")
    @Override
    public void onMessage(ConsumerRecord<String, String> consumerRecord) {
        String value = consumerRecord.value();
        try {
            JSONObject valueJsonObject = JSONObject.fromObject(value);
            String dataType = valueJsonObject.getString("dataType");
            String data = valueJsonObject.getString("data");
            if (Constants.DATA_TYPE_DIET_ORDER.equals(dataType)) {
                dataService.saveDietOrder(data);
            }
        } catch (Exception e) {
            DingtalkUtils.send(ConfigurationUtils.getConfiguration(ConfigurationKeys.DINGTALK_ERROR_NOTIFY_CHAT_ID), String.format(Constants.DINGTALK_ERROR_NOTIFY_MESSAGE_FORMAT, "保存POS上传的数据失败", GsonUtils.toJson(value), e.getClass().getName(), e.getMessage()));
        }
    }
}
