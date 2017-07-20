package erp.chain.utils;

import org.apache.commons.lang.Validate;

/**
 * Created by liuyandong on 2017/7/3.
 */
public class SerialNumberGenerator {
    public static String nextSerialNumber(int digit, int currentValue) {
        String currentValueString = String.valueOf(currentValue);
        int length = currentValueString.length();
        Validate.isTrue(length <= digit, "当前值长度超过指定长度！");
        int zeroCount = digit - length;
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < zeroCount; i++) {
            stringBuffer.append("0");
        }
        return stringBuffer.append(currentValueString).toString();
    }
}
