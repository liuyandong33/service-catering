package build.dream.erp.utils;

import build.dream.common.api.ApiRest;
import build.dream.common.utils.ConfigurationUtils;
import build.dream.common.utils.ProxyUtils;
import build.dream.common.utils.WebUtils;
import build.dream.erp.constants.Constants;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MeiTuanUtils {
    public static String generateSignature(Map<String, String> requestParameters, String consumerSecret, String url) {
        Map<String, String> sortedRequestParameters = new TreeMap<String, String>(requestParameters);
        List<String> requestParametersPairs = new ArrayList<String>();
        for (Map.Entry<String, String> entry : sortedRequestParameters.entrySet()) {
            requestParametersPairs.add(entry.getKey() + "=" + entry.getValue());
        }

        return DigestUtils.md5Hex(url + "?" + StringUtils.join(requestParametersPairs, "&") + consumerSecret);
    }

    public static ApiRest callMeiTuanSystem(Map<String, String> requestParameters, String uri, String requestMethod) throws IOException {
        requestParameters.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        requestParameters.put("app_id", ConfigurationUtils.getConfiguration(Constants.MEI_TUAN_APP_ID));
        String consumerSecret = ConfigurationUtils.getConfiguration(Constants.MEI_TUAN_CONSUMER_SECRET);
        String url = ConfigurationUtils.getConfiguration(Constants.MEI_TUAN_CONSUMER_SECRET) + uri;
        requestParameters.put("sig", generateSignature(requestParameters, consumerSecret, url));
        requestParameters.put("uri", uri);
        ApiRest apiRest = null;
        if (WebUtils.RequestMethod.GET.equals(requestMethod)) {
            apiRest = ProxyUtils.doGetWithRequestParameters(Constants.SERVICE_NAME_OUT, "meiTuan", "callMeiTuanSystem", requestParameters);
        } else if (WebUtils.RequestMethod.POST.equals(requestMethod)) {
            apiRest = ProxyUtils.doPostWithRequestParameters(Constants.SERVICE_NAME_OUT, "meiTuan", "callMeiTuanSystem", requestParameters);
        }
        return apiRest;
    }
}
