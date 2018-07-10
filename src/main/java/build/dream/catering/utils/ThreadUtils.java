package build.dream.catering.utils;

public class ThreadUtils {
    public static void sleepSafe(long millis) {
        try {
            Thread.sleep(millis);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
