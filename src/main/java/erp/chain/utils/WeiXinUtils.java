package erp.chain.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.dom4j.DocumentException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by liuyandong on 2017/7/20.
 */
public class WeiXinUtils {
    public static String generateUnifiedOrderSign(Map<String, String> unifiedOrderRequestParameters, String winXinPayKey) {
        if (unifiedOrderRequestParameters.getClass() != TreeMap.class) {
            unifiedOrderRequestParameters = new TreeMap<String, String>(unifiedOrderRequestParameters);
        }
        Set<Map.Entry<String, String>> entries = unifiedOrderRequestParameters.entrySet();

        StringBuffer stringSignTemp = new StringBuffer();
        for (Map.Entry<String, String> entry : entries) {
            stringSignTemp.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        stringSignTemp.append(winXinPayKey);
        return DigestUtils.md5Hex(stringSignTemp.toString()).toUpperCase();
    }

    public static String generateUnifiedOrderFinalData(Map<String, String> unifiedOrderRequestParameters) {
        StringBuffer unifiedOrderFinalData = new StringBuffer();
        unifiedOrderFinalData.append("<xml>");
        Set<Map.Entry<String, String>> entries = unifiedOrderRequestParameters.entrySet();

        for (Map.Entry<String, String> entry : entries) {
            String key = entry.getKey();
            unifiedOrderFinalData.append("<").append(key).append("><![CDATA[").append(entry.getValue()).append("]]></").append(key).append(">");
        }

        unifiedOrderFinalData.append("</xml>");
        return unifiedOrderFinalData.toString();
    }

    public static Map<String, String> doUnifiedOrder(String weiXinPayUnifiedOrderUrl, String unifiedOrderFinalData) throws IOException, DocumentException {
        InputStream inputStream = WebUtils.doPostWithRequestBody(weiXinPayUnifiedOrderUrl, unifiedOrderFinalData);
        return WebUtils.xmlInputStreamToMap(inputStream);
    }
}
