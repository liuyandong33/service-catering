package erp.chain.utils;

import erp.chain.constants.Constants;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

/**
 * Created by liuyandong on 2017/7/15.
 */
public class WebUtils {
    public static final class RequestMethod {
        public static final String GET = "GET";
        public static final String POST = "POST";
    }

    public static final String twoHyphens = "--";
    public static final String boundary = UUID.randomUUID().toString();
    public static final String enterNewline = "\r\n";

    public static String doGetWithRequestParameters(String requestUrl, Map<String, String> requestParameters) throws IOException {
        return doGetWithRequestParameters(requestUrl, null, requestParameters);
    }

    public static String doGetWithRequestParameters(String requestUrl, int readTimeout, int connectTimeout, Map<String, String> requestParameters) throws IOException {
        return doGetWithRequestParameters(requestUrl, readTimeout, connectTimeout, null, requestParameters);
    }

    public static String doGetWithRequestParameters(String requestUrl, Map<String, String> headers, Map<String, String> requestParameters) throws IOException {
        return doGetWithRequestParameters(requestUrl, 0, 0, headers, requestParameters);
    }

    public static String doGetWithRequestParameters(String requestUrl, int readTimeout, int connectTimeout, Map<String, String> headers, Map<String, String> requestParameters) throws IOException {
        if (requestParameters != null && !requestParameters.isEmpty()) {
            requestUrl = requestUrl + "?" + buildQueryString(requestParameters);
        }
        HttpURLConnection httpURLConnection = buildHttpURLConnection(requestUrl, RequestMethod.GET, readTimeout, connectTimeout);
        setRequestProperties(httpURLConnection, headers);
        InputStream inputStream = httpURLConnection.getInputStream();
        String result = inputStreamToString(inputStream);
        inputStream.close();
        httpURLConnection.disconnect();
        return result;
    }

    public static String doPostWithRequestParameters(String requestUrl, Map<String, String> requestParameters) throws IOException {
        return doPostWithRequestParameters(requestUrl, null, requestParameters);
    }

    public static String doPostWithRequestParameters(String requestUrl, int readTimeout, int connectTimeout, Map<String, String> requestParameters) throws IOException {
        return doPostWithRequestParameters(requestUrl, readTimeout, connectTimeout, null, requestParameters);
    }

    public static String doPostWithRequestParameters(String requestUrl, Map<String, String> headers, Map<String, String> requestParameters) throws IOException {
        return doPostWithRequestParameters(requestUrl, 0, 0, headers, requestParameters);
    }

    public static String doPostWithRequestParameters(String requestUrl, int readTimeout, int connectTimeout, Map<String, String> headers, Map<String, String> requestParameters) throws IOException {
        HttpURLConnection httpURLConnection = buildHttpURLConnection(requestUrl, RequestMethod.POST, readTimeout, connectTimeout);
        setRequestProperties(httpURLConnection, headers);
        httpURLConnection.setDoInput(true);
        httpURLConnection.setDoOutput(true);
        OutputStream outputStream = httpURLConnection.getOutputStream();
        String requestBody = buildQueryString(requestParameters);
        outputStream.write(requestBody.getBytes(Constants.CHARSET_UTF_8));
        InputStream inputStream = httpURLConnection.getInputStream();
        String result = inputStreamToString(inputStream);
        inputStream.close();
        outputStream.close();
        httpURLConnection.disconnect();
        return result;
    }

    public static String doPostWithFiles(String requestUrl, Map<String, Object> requestParameters) throws IOException {
        return doPostWithFiles(requestUrl, null, requestParameters);
    }

    public static String doPostWithFiles(String requestUrl, int readTimeout, int connectTimeout, Map<String, Object> requestParameters) throws IOException {
        return doPostWithFiles(requestUrl, readTimeout, connectTimeout, null, requestParameters);
    }

    public static String doPostWithFiles(String requestUrl, Map<String, String> headers, Map<String, Object> requestParameters) throws IOException {
        return doPostWithFiles(requestUrl, 0, 0, headers, requestParameters);
    }

    public static String doPostWithFiles(String requestUrl, int readTimeout, int connectTimeout, Map<String, String> headers, Map<String, Object> requestParameters) throws IOException {
        HttpURLConnection httpURLConnection = buildHttpURLConnection(requestUrl, RequestMethod.POST, readTimeout, connectTimeout);
        if (headers == null) {
            headers = new HashMap<String, String>();
            headers.put("Content-Type", "multipart/form-data;boundary=" + boundary);
        }
        setRequestProperties(httpURLConnection, headers);
        httpURLConnection.setDoInput(true);
        httpURLConnection.setDoOutput(true);
        OutputStream outputStream = httpURLConnection.getOutputStream();
        Set<Map.Entry<String, Object>> entries = requestParameters.entrySet();
        for (Map.Entry<String, Object> entry : entries) {
            outputStream.write((twoHyphens + boundary + enterNewline).getBytes(Constants.CHARSET_UTF_8));
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof String) {
                outputStream.write(("Content-Disposition: form-data; name=\"" + key + "\"" + enterNewline + enterNewline).getBytes(Constants.CHARSET_UTF_8));
                outputStream.write((value.toString()).getBytes(Constants.CHARSET_UTF_8));
            } else if (value instanceof MultipartFile || value instanceof File) {
                InputStream inputStream = null;
                String fileName = null;
                if (value instanceof MultipartFile) {
                    MultipartFile multipartFile = (MultipartFile) value;
                    inputStream = multipartFile.getInputStream();
                    fileName = multipartFile.getOriginalFilename();
                } else if (value instanceof File) {
                    File file = (File) value;
                    inputStream = new FileInputStream(file);
                    fileName = file.getName();
                }
                outputStream.write(("Content-Disposition: form-data; " + "name=\"" + key + "\";filename=\"" + fileName + "\"" + enterNewline).getBytes(Constants.CHARSET_UTF_8));
                outputStream.write(("Content-Type:application/octet-stream" + enterNewline + enterNewline).getBytes(Constants.CHARSET_UTF_8));
                int length = 0;
                byte[] buffer = new byte[1024];
                while ((length = inputStream.read(buffer, 0, 1024)) != -1) {
                    outputStream.write(buffer, 0, length);
                }
                inputStream.close();
            }
            outputStream.write(enterNewline.getBytes(Constants.CHARSET_UTF_8));
        }
        outputStream.write((twoHyphens + boundary + twoHyphens).getBytes(Constants.CHARSET_UTF_8));
        InputStream inputStream = httpURLConnection.getInputStream();
        String result = inputStreamToString(inputStream);
        inputStream.close();
        outputStream.close();
        return result;
    }

    public static InputStream doPostWithRequestBody(String requestUrl, String requestBody) throws IOException {
        return doPostWithRequestBody(requestUrl, null, requestBody);
    }

    public static InputStream doPostWithRequestBody(String requestUrl, int readTimeout, int connectTimeout, String requestBody) throws IOException {
        return doPostWithRequestBody(requestUrl, readTimeout, connectTimeout, null, requestBody);
    }

    public static InputStream doPostWithRequestBody(String requestUrl, Map<String, String> headers, String requestBody) throws IOException {
        return doPostWithRequestBody(requestUrl, 0, 0, headers, requestBody);
    }

    public static InputStream doPostWithRequestBody(String requestUrl, int readTimeout, int connectTimeout, Map<String, String> headers, String requestBody) throws IOException {
        HttpURLConnection httpURLConnection = buildHttpURLConnection(requestUrl, RequestMethod.POST, readTimeout, connectTimeout);
        setRequestProperties(httpURLConnection, headers);
        httpURLConnection.setDoInput(true);
        httpURLConnection.setDoOutput(true);
        OutputStream outputStream = httpURLConnection.getOutputStream();
        outputStream.write(requestBody.getBytes(Constants.CHARSET_UTF_8));
        InputStream inputStream = httpURLConnection.getInputStream();
        outputStream.close();
        return inputStream;
    }

    public static void setRequestProperties(HttpURLConnection httpURLConnection, Map<String, String> headers) {
        if (headers != null && !headers.isEmpty()) {
            Set<Map.Entry<String, String>> entries = headers.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                httpURLConnection.setRequestProperty(entry.getKey(), entry.getValue());
            }
        } else {
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
        }
    }

    public static HttpURLConnection buildHttpURLConnection(String requestUrl, String requestMethod, int readTimeout, int connectTimeout) throws IOException {
        URL url = new URL(requestUrl);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod(requestMethod);
        httpURLConnection.setReadTimeout(readTimeout);
        httpURLConnection.setConnectTimeout(connectTimeout);
        httpURLConnection.setUseCaches(false);
        return httpURLConnection;
    }

    public static String buildQueryString(Map<String, String> requestParameters) {
        Set<Map.Entry<String, String>> entries = requestParameters.entrySet();
        StringBuffer queryString = new StringBuffer();
        for (Map.Entry<String, String> entry : entries) {
            queryString.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        return queryString.deleteCharAt(queryString.length() - 1).toString();
    }

    public static String inputStreamToString(InputStream inputStream) throws IOException {
        int length = 0;
        byte[] buffer = new byte[1024];
        StringBuffer result = new StringBuffer();
        while ((length = inputStream.read(buffer, 0, 1024)) != -1) {
            result.append(new String(buffer, 0, length, Constants.CHARSET_UTF_8));
        }
        return result.toString();
    }

    public static Map<String, String> xmlInputStreamToMap(InputStream inputStream) throws DocumentException {
        SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(inputStream);
        Element rootElement = document.getRootElement();
        List<Element> elements = rootElement.elements();
        Map<String, String> returnValue = new HashMap<String, String>();
        for (Element element : elements) {
            returnValue.put(element.getName(), element.getText());
        }
        return returnValue;
    }
}
