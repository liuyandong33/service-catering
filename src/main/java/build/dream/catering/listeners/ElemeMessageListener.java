package build.dream.catering.listeners;

import build.dream.catering.constants.Constants;
import build.dream.catering.services.ElemeService;
import build.dream.catering.tools.HandleElemeMessageRunnable;
import build.dream.common.domains.catering.ElemeCallbackMessage;
import build.dream.common.utils.JacksonUtils;
import build.dream.common.utils.ValidateUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.Map;

@Component
public class ElemeMessageListener implements MessageListener<String, String> {
    @Autowired
    private ElemeService elemeService;

    @KafkaListener(topics = "${eleme.message.topic}")
    @Override
    public void onMessage(ConsumerRecord<String, String> data) {
        String elemeMessage = data.value();
        if (StringUtils.isBlank(elemeMessage)) {
            return;
        }

        if (!ValidateUtils.isJson(elemeMessage)) {
            return;
        }

        if (!ValidateUtils.isRightJson(elemeMessage, Constants.ELEME_MESSAGE_SCHEMA_FILE_PATH)) {
            return;
        }

        Map<String, Object> elemeMessageMap = JacksonUtils.readValueAsMap(elemeMessage, String.class, Object.class);
        String uuid = MapUtils.getString(elemeMessageMap, "uuid");
        Map<String, Object> callbackRequestBody = MapUtils.getMap(elemeMessageMap, "callbackRequestBody");
        int count = MapUtils.getIntValue(elemeMessageMap, "count");
        BigInteger tenantId = BigInteger.valueOf(MapUtils.getLongValue(elemeMessageMap, "tenantId"));
        String tenantCode = MapUtils.getString(elemeMessageMap, "tenantCode");
        BigInteger branchId = BigInteger.valueOf(MapUtils.getLongValue(elemeMessageMap, "branchId"));

        String requestId = MapUtils.getString(callbackRequestBody, "requestId");
        int type = MapUtils.getIntValue(callbackRequestBody, "type");
        BigInteger appId = BigInteger.valueOf(MapUtils.getLongValue(callbackRequestBody, "appId"));
        String message = MapUtils.getString(callbackRequestBody, "message");
        BigInteger shopId = BigInteger.valueOf(MapUtils.getLongValue(callbackRequestBody, "shopId"));
        long timestamp = MapUtils.getLongValue(callbackRequestBody, "timestamp");
        String signature = MapUtils.getString(callbackRequestBody, "signature");
        BigInteger userId = BigInteger.valueOf(MapUtils.getLongValue(callbackRequestBody, "userId"));

        ElemeCallbackMessage elemeCallbackMessage = new ElemeCallbackMessage();
        elemeCallbackMessage.setTenantId(tenantId);
        elemeCallbackMessage.setTenantCode(tenantCode);
        elemeCallbackMessage.setBranchId(branchId);
        elemeCallbackMessage.setRequestId(requestId);
        elemeCallbackMessage.setType(type);
        elemeCallbackMessage.setAppId(appId);
        elemeCallbackMessage.setMessage(message);
        elemeCallbackMessage.setShopId(shopId);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        elemeCallbackMessage.setTimestamp(calendar.getTime());
        elemeCallbackMessage.setSignature(signature);
        elemeCallbackMessage.setUserId(userId);

        new Thread(new HandleElemeMessageRunnable(elemeService, elemeCallbackMessage, count, 1000, uuid)).start();
    }
}
