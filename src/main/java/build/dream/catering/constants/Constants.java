package build.dream.catering.constants;

/**
 * Created by liuyandong on 2017/5/5.
 */
public class Constants extends build.dream.common.constants.Constants {
    public static final String GOODS_TABLE_TEMPLATE = "goods_table_template";
    public static final String SALE_TABLE_TEMPLATE = "sale_table_template";
    public static final String ELEME_TENANT_AUTHORIZE_URL_FORMAT = "%s?response_type=%s&client_id=%s&redirect_uri=%s&state=%s&scope=%s";
    public static final String ELEME_ORDER_CALLBACK_SUCCESS_RETURN_VALUE = "{\"message\":\"ok\"}";

    public static final String MEI_MEI_TUAN_SERVICE_URL = "http://api.open.cater.meituan.com";
    public static final String MEI_TUAN_CALLBACK_SUCCESS_RETURN_VALUE = "{\"data\": \"OK\"}";

    public static final String DATA_TYPE_DIET_ORDER = "DIET_ORDER";

    public static final String DATA_JOB_CRON_EXPRESSION = "data.job.cron.expression";

    public static final Integer GOODS_TYPE_ORDINARY_GOODS = 1;
    public static final Integer GOODS_TYPE_PACKAGE = 2;
    public static final Integer GOODS_TYPE_PACKAGE_DETAIL = 3;
    public static final Integer GOODS_TYPE_DELIVER_FEE = 4;
    public static final Integer GOODS_TYPE_PACKAGE_FEE = 5;

    public static final Long FICTITIOUS_GOODS_CATEGORY_ID = 1L;
    public static final String FICTITIOUS_GOODS_CATEGORY_NAME = "虚拟商品";

    public static final Long ELEME_GOODS_CATEGORY_ID = 2L;
    public static final String ELEME_GOODS_CATEGORY_NAME = "饿了么商品";

    public static final Long MEI_TUAN_GOODS_CATEGORY_ID = 3L;
    public static final String MEI_TUAN_GOODS_CATEGORY_NAME = "美团商品";

    public static final Long ELM_PAYMENT_ID = 4L;
    public static final String ELM_PAYMENT_CODE = "ELM";
    public static final String ELM_PAYMENT_NAME = "饿了么线上支付";

    public static final Long MT_PAYMENT_ID = 5L;
    public static final String MT_PAYMENT_CODE = "MT";
    public static final String MT_PAYMENT_NAME = "美团线上支付";

    public static final String BUY_GIVE_ACTIVITY_INFOS_SCHEMA_FILE_PATH = "build/dream/catering/schemas/buyGiveActivityInfosSchema.json";
    public static final String SPECIAL_GOODS_ACTIVITY_INFOS_SCHEMA_FILE_PATH = "build/dream/catering/schemas/specialGoodsActivityInfosSchema.json";
    public static final String ELEME_MESSAGE_SCHEMA_FILE_PATH = "build/dream/catering/schemas/elemeMessageSchema.json";
    public static final String MEI_TUAN_MESSAGE_SCHEMA_FILE_PATH = "build/dream/catering/schemas/meiTuanMessageSchema.json";
    public static final String GROUPS_SCHEMA_FILE_PATH = "build/dream/catering/schemas/groupsSchema.json";
    public static final String GOODS_INFOS_SCHEMA_FILE_PATH = "build/dream/catering/schemas/goodsInfosSchema.json";
    public static final String SAVE_GOODS_SCHEMA_FILE_PATH = "build/dream/catering/schemas/goods/saveGoodsSchema.json";

    public static final String ANUBIS_GET_ACCESS_TOKEN_URI = "/get_access_token";
    public static final String ANUBIS_CHAIN_STORE_URI = "/v2/chain_store";
    public static final String ANUBIS_CHAIN_STORE_QUERY_URI = "/v2/chain_store/query";
    public static final String ANUBIS_CHAIN_STORE_UPDATE_URI = "/v2/chain_store/update";
    public static final String ANUBIS_CHAIN_STORE_DELIVERY_QUERY_URI = "/v2/chain_store/delivery/query";
    public static final String ANUBIS_ORDER_CARRIER_URI = "/v2/order/carrier";
    public static final String ANUBIS_ORDER_URI = "/v2/order";
    public static final String ANUBIS_ORDER_CANCEL_URI = "/v2/order/cancel";
    public static final String ANUBIS_ORDER_QUERY_URI = "/v2/order/query";
    public static final String ANUBIS_ORDER_COMPLAINT_URI = "/v2/order/complaint";

    public static Integer POSITION_SOURCE_TENCENT_MAP = 1;
    public static Integer POSITION_SOURCE_BAIDU_MAP = 2;
    public static Integer POSITION_SOURCE_GAODE_MAP = 3;

    public static final String MESSAGE_CODE_ELEME_MESSAGE = "1001";
    public static final String MESSAGE_CODE_MEI_TUAN_MESSAGE = "2001";
    public static final String MESSAGE_CODE_UPLOAD_LOG = "3001";
    public static final String MESSAGE_CODE_UPLOAD_DATABASE = "4001";
    public static final String MESSAGE_CODE_EXECUTE_SQL = "5001";

    public static final String POS_DATA_PATH = "pos.data.path";

    public static final String WEI_XIN_CARD_CREATE_URI = "/card/create";
    public static final String WEI_XIN_CARD_MEMBER_CARD_ACTIVATE_USER_FORM_SET_URI = "/card/membercard/activateuserform/set";
    public static final String WEI_XIN_CARD_QRCODE_CREATE_URI = "/card/qrcode/create";
    public static final String WEI_XIN_CARD_PAY_GIFT_CARD_ADD_URI = "/card/paygiftcard/add";
    public static final String WEI_XIN_CARD_DELETE_URI = "/card/delete";

    public static final String ELASTICSEARCH_CLUSTER_NAME = "elasticsearch.cluster.name";
    public static final String ELASTICSEARCH_CLUSTER_NODES = "elasticsearch.cluster.nodes";
    public static final String ELASTICJOB_ZOOKEEPER_SERVER_LISTS = "elasticjob.zookeeper.server-lists";
    public static final String ELASTICJOB_ZOOKEEPER_NAMESPACE = "elasticjob.zookeeper.namespace";

    public static final Integer STOCK_FLOW_TYPE_PURCHASE = 1;
    public static final Integer STOCK_FLOW_TYPE_REFUND = 2;
    public static final Integer STOCK_FLOW_TYPE_SALE = 3;

    public static final String ELASTICSEARCH_HIGHLIGHT_PRE_TAG = "<span class=\"elasticsearch_highlight\">";
    public static final String ELASTICSEARCH_HIGHLIGHT_POST_TAG = "</span>";
    public static final String ELASTICSEARCH_INDEX_GOODS = "goods";
    public static final String ELASTICSEARCH_INDEX_BRANCH = "branch";

    /**
     * 套餐组类型
     *
     * @see #PACKAGE_GROUP_TYPE_OPTIONAL_GROUP: 可选组
     * @see #PACKAGE_GROUP_TYPE_REQUIRED_GROUP: 必选组
     */
    public static final Integer PACKAGE_GROUP_TYPE_OPTIONAL_GROUP = 1;
    public static final Integer PACKAGE_GROUP_TYPE_REQUIRED_GROUP = 2;

    /**
     * 活动类型
     *
     * @see #ACTIVITY_TYPE_BUY_GIVE_ACTIVITY: 买A赠B活动
     * @see #PACKAGE_GROUP_TYPE_OPTIONAL_GROUP: 整单满减活动
     * @see #PACKAGE_GROUP_TYPE_OPTIONAL_GROUP: 特价商品活动
     * @see #PACKAGE_GROUP_TYPE_OPTIONAL_GROUP: 支付促销
     */
    public static final Integer ACTIVITY_TYPE_BUY_GIVE_ACTIVITY = 1;
    public static final Integer ACTIVITY_TYPE_FULL_REDUCTION_ACTIVITY = 2;
    public static final Integer ACTIVITY_TYPE_SPECIAL_GOODS_ACTIVITY = 3;
    public static final Integer ACTIVITY_TYPE_PAYMENT_ACTIVITY = 4;

    public static final String DINGTALK_ERROR_NOTIFY_MESSAGE_FORMAT = "%s-%s-%s-%s";
}
