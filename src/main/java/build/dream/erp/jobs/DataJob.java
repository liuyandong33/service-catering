package build.dream.erp.jobs;

import build.dream.common.utils.CacheUtils;
import build.dream.common.utils.ConfigurationUtils;
import build.dream.common.utils.LogUtils;
import build.dream.common.utils.QueueUtils;
import build.dream.erp.constants.Constants;
import build.dream.erp.services.DataService;
import org.apache.commons.codec.digest.DigestUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;

public class DataJob implements Job {
    private static final String DATA_JOB_SIMPLE_NAME = "DataJob";
    private static String KEY_DIET_ORDER_DATA;
    private static final String FLAG = DigestUtils.md5Hex(DATA_JOB_SIMPLE_NAME);
    @Autowired
    private DataService dataService;
    static {
        try {
            String deploymentEnvironment = ConfigurationUtils.getConfiguration(Constants.DEPLOYMENT_ENVIRONMENT);
            String partitionCode = ConfigurationUtils.getConfiguration(Constants.PARTITION_CODE);
            KEY_DIET_ORDER_DATA = deploymentEnvironment + "_" + partitionCode + "_" + Constants.DIET_ORDER;
        } catch (Exception e) {
            LogUtils.error("数据上传定时任务初始化失败", DATA_JOB_SIMPLE_NAME, "", e);
        }
    }
    @Override
    public void execute(JobExecutionContext context) {
        boolean setnxSuccessful = CacheUtils.setnx(FLAG, FLAG);
        if (setnxSuccessful) {
            CacheUtils.expire(FLAG, 120, TimeUnit.SECONDS);
            long length = QueueUtils.llen(KEY_DIET_ORDER_DATA);
            for (long index = 0; index < length; index++) {
                String dietOrderData = QueueUtils.lpop(KEY_DIET_ORDER_DATA);
                try {
                    dataService.saveDietOrder(dietOrderData);
                } catch (Exception e) {
                    QueueUtils.rpush(KEY_DIET_ORDER_DATA, dietOrderData);
                }
            }
        }
    }
}
