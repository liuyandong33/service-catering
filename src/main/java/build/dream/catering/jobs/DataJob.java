package build.dream.catering.jobs;

import build.dream.catering.constants.Constants;
import build.dream.catering.services.DataService;
import build.dream.common.utils.ConfigurationUtils;
import build.dream.common.utils.LogUtils;
import build.dream.common.utils.QueueUtils;
import org.apache.commons.lang.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

public class DataJob implements Job {
    private static final String DATA_JOB_SIMPLE_NAME = "DataJob";
    private static String KEY_DIET_ORDER_DATA;
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
        long length = QueueUtils.llen(KEY_DIET_ORDER_DATA);
        for (long index = 0; index < length; index++) {
            String dietOrderData = QueueUtils.lpop(KEY_DIET_ORDER_DATA);
            if (StringUtils.isBlank(dietOrderData)) {
                continue;
            }
            try {
                dataService.saveDietOrder(dietOrderData);
            } catch (Exception e) {
                QueueUtils.rpush(KEY_DIET_ORDER_DATA, dietOrderData);
            }
        }
    }
}
