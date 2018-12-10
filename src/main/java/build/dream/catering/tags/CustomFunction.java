package build.dream.catering.tags;

import build.dream.common.utils.UrlUtils;

public class CustomFunction {
    public static String encode(String originalString) {
        return UrlUtils.encode(originalString);
    }
}
