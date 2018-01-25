package build.dream.catering.constants;

/**
 * Created by liuyandong on 2017/5/5.
 */
public class Constants extends build.dream.common.constants.Constants {
    public static final String GOODS_TABLE_TEMPLATE = "goods_table_template";
    public static final String SALE_TABLE_TEMPLATE = "sale_table_template";
    public static final String ELEME_APP_ID = "eleme.app.id";
    public static final String ELEME_APP_KEY = "eleme.app.key";
    public static final String ELEME_APP_SECRET = "eleme.app.secret";
    public static final String ELEME_SERVICE_URL = "eleme.service.url";
    public static final String KEY_ELEME_TOKEN = "_eleme_token";
    public static final String ELEME_TENANT_AUTHORIZE_URL_FORMAT = "%s?response_type=%s&client_id=%s&redirect_uri=%s&state=%s&scope=%s";
    public static final String ELEME_ORDER_CALLBACK_SUCCESS_RETURN_VALUE = "{\"message\":\"ok\"}";

    public static final String MEI_TUAN_APP_ID = "mei.tuan.app.id";
    public static final String MEI_TUAN_CONSUMER_SECRET = "mei.tuan.consumer.secret";
    public static final String MEI_TUAN_SERVICE_URL = "mei.tuan.service.url";
    public static final String MEI_TUAN_ERP_SERVICE_URL = "mei.tuan.erp.service.url";
    public static final String MEI_TUAN_DEVELOPER_ID = "mei.tuan.developer.id";
    public static final String MEI_TUAN_SIGN_KEY = "mei.tuan.sign.key";
    public static final String MEI_TUAN_PATH_STORE_MAP = "/storemap";

    public static final String KEY_MEI_TUAN_APP_AUTH_TOKENS = "_mei_tuan_app_auth_tokens";
    public static final String MEI_MEI_TUAN_SERVICE_URL = "http://api.open.cater.meituan.com";
    public static final String MEI_TUAN_CALLBACK_SUCCESS_RETURN_VALUE = "{\"data\": \"OK\"}";

    public static final String DATA_TYPE_DIET_ORDER = "dietOrder";
    public static final String DIET_ORDER = "diet_order";

    public static final String DATA_JOB_CRON_EXPRESSION = "data.job.cron.expression";

    public static Integer GOODS_TYPE_ORDINARY_GOODS = 1;
    public static Integer GOODS_TYPE_PACKAGE = 2;

    public static final String KEY_DATA_HANDLE_SIGNATURES = "_data_handle_signatures";

    public static final String GOODS_SPECIFICATION_INFOS_SCHEMA_FILE_PATH = "build/dream/catering/schemas/goodsSpecificationInfosSchema.json";
    public static final String FLAVOR_GROUP_INFOS_SCHEMA_FILE_PATH = "build/dream/catering/schemas/flavorGroupInfosSchema.json";
}
