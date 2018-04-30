package build.dream.catering.listeners;

import build.dream.catering.constants.Constants;
import build.dream.catering.services.ElemeService;
import build.dream.catering.tools.HandleElemeMessageRunnable;
import build.dream.common.erp.catering.domains.ElemeCallbackMessage;
import build.dream.common.utils.ApplicationHandler;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.Calendar;

@Component
public class ElemeMessageListener {
    private static final String KEY_ELEME_CALLBACK_MESSAGE_TOPIC = "_zd1_eleme_callback_message_topic";

    @Autowired
    private ElemeService elemeService;

    @KafkaListener(topics = {KEY_ELEME_CALLBACK_MESSAGE_TOPIC})
    public void listenElemeMessage(String elemeMessage) {
        if (StringUtils.isBlank(elemeMessage)) {
            return;
        }

        if (!ApplicationHandler.isJson(elemeMessage)) {
            return;
        }

        if (!ApplicationHandler.isRightJson(elemeMessage, Constants.ELEME_MESSAGE_SCHEMA_FILE_PATH)) {
            return;
        }

        JSONObject elemeMessageJsonObject = JSONObject.fromObject(elemeMessage);
        JSONObject callbackRequestBodyJsonObject = elemeMessageJsonObject.getJSONObject("callbackRequestBody");
        String uuid = elemeMessageJsonObject.getString("uuid");
        int count = elemeMessageJsonObject.getInt("count");
        int type = callbackRequestBodyJsonObject.getInt("type");

        ElemeCallbackMessage elemeCallbackMessage = new ElemeCallbackMessage();
        elemeCallbackMessage.setRequestId(callbackRequestBodyJsonObject.getString("requestId"));
        elemeCallbackMessage.setType(type);
        elemeCallbackMessage.setAppId(BigInteger.valueOf(callbackRequestBodyJsonObject.getLong("appId")));
        elemeCallbackMessage.setMessage(callbackRequestBodyJsonObject.getString("message"));
        elemeCallbackMessage.setShopId(BigInteger.valueOf(callbackRequestBodyJsonObject.getLong("shopId")));

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(callbackRequestBodyJsonObject.getLong("timestamp"));
        elemeCallbackMessage.setTimestamp(calendar.getTime());
        elemeCallbackMessage.setSignature(callbackRequestBodyJsonObject.getString("signature"));
        elemeCallbackMessage.setUserId(BigInteger.valueOf(callbackRequestBodyJsonObject.getLong("userId")));

        new Thread(new HandleElemeMessageRunnable(elemeService, elemeCallbackMessage, count, 1000, uuid)).start();
    }
}
