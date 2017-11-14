package build.dream.erp.tools;

import build.dream.erp.utils.ElemeUtils;

import java.util.List;

public class ElemeConsumerThread implements Runnable {
    @Override
    public void run() {
        while (true) {
            String elemeMessage = null;
            Integer count = 0;
            try {
                List<String> elemeMessageBody = ElemeUtils.takeElemeMessage();
                elemeMessage = elemeMessageBody.get(0);
                count = Integer.valueOf(elemeMessageBody.get(1));
            } catch (Exception e) {
                try {
                    ElemeUtils.addElemeMessageBlockingQueue(elemeMessage, count - 1);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
}
