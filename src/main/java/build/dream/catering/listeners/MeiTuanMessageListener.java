package build.dream.catering.listeners;

import build.dream.catering.constants.Constants;
import build.dream.catering.services.MeiTuanService;
import build.dream.catering.tools.HandleMeiTuanMessageRunnable;
import build.dream.common.utils.JacksonUtils;
import build.dream.common.utils.ValidateUtils;
import net.sf.json.JSONObject;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MeiTuanMessageListener implements MessageListener<String, String> {
    @Autowired
    private MeiTuanService meiTuanService;

    @KafkaListener(topics = "${mei.tuan.message.topic}")
    @Override
    public void onMessage(ConsumerRecord<String, String> data) {
        String meiTuanMessage = data.value();
        if (StringUtils.isBlank(meiTuanMessage)) {
            return;
        }

        if (!ValidateUtils.isJson(meiTuanMessage)) {
            return;
        }

        if (!ValidateUtils.isRightJson(meiTuanMessage, Constants.MEI_TUAN_MESSAGE_SCHEMA_FILE_PATH)) {
            return;
        }

        Map<String, Object> meiTuanMessageMap = JacksonUtils.readValueAsMap(meiTuanMessage, String.class, Object.class);
        Map<String, String> callbackParameters = MapUtils.getMap(meiTuanMessageMap, "callbackParameters");
        String uuid = MapUtils.getString(meiTuanMessageMap, "uuid");
        int count = MapUtils.getIntValue(meiTuanMessageMap, "count");
        int type = MapUtils.getIntValue(meiTuanMessageMap, "type");
        new Thread(new HandleMeiTuanMessageRunnable(meiTuanService, callbackParameters, count, 1000, uuid, type)).start();
    }
}
