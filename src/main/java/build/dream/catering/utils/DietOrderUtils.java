package build.dream.catering.utils;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

public class DietOrderUtils {
    private static final ConcurrentHashMap<String, Timer> CONCURRENT_HASH_MAP = new ConcurrentHashMap<String, Timer>();

    public static void startTimer(String orderNumber) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                CONCURRENT_HASH_MAP.remove(orderNumber);
                timer.cancel();
            }
        }, 0, 1000);
        CONCURRENT_HASH_MAP.put(orderNumber, timer);
    }

    public static void stopTimer(String orderNumber) {
        Timer timer = CONCURRENT_HASH_MAP.remove(orderNumber);
        if (timer != null) {
            timer.cancel();
        }
    }
}
