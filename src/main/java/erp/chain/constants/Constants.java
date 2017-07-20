package erp.chain.constants;

/**
 * Created by liuyandong on 2017/5/5.
 */
public class Constants {
    public static final String SUCCESS = "SUCCESS";
    public static final String FAILURE = "FAILURE";
    public static final Integer COOKIE_MAX_AGE = 60 * 60 * 24 * 7;
    public static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String DEPLOYMENT_ENVIRONMENT = "deployment.environment";
    public static final String PARTITION_CODE = "partition.code";
    public static final String SERVICE_NAME = "service.name";
    public static final String CONFIGURATION = "_configuration";

    public static final String DATABASE = "DATABASE";
    public static final String TABLE = "table";
    public static final String CREATE = "CREATE";
    public static final String CREATE_DATABASE = "CREATE DATABASE";
    public static final String CREATE_TABLE = "CREATE TABLE";
    public static final String NO = "NO";
    public static final String YES =  "YES";
    public static final String NOT_NULL = "NOT NULL";
    public static final String COMMENT = "COMMENT";
    public static final String PRI = "PRI";
    public static final String AUTO_INCREMENT = "auto_increment";
    public static final String PRIMARY_KEY = "PRIMARY KEY";

    /**
     * 配置相关常量
     */
    public static final String APPLICATION_PROPERTIES = "application.properties";
    public static final String PRODUCTION_PROPERTIES = "production.properties";
    public static final String APPLICATION_PROPERTIES_RELATIVE_PATH = "WEB-INF/classes/application.properties";
    public static final String PRODUCTION_PROPERTIES_RELATIVE_PATH = "WEB-INF/classes/production.properties";
    public static final String LOGBACK_XML_RELATIVE_PATH = "WEB-INF/classes/logback.xml";
    public static final String LOGBACK_XML = "logback.xml";
    public static final String WAR_DIRECTORY_NAME = "wars";
    public static final String CONFIGURATION_DIRECTORY_NAME = "configurations";
    public static final Integer APPLICATION_TYPE_SNAPSHOT = 1;
    public static final Integer APPLICATION_TYPE_RELEASE = 2;
    public static final String SNAPSHOT_APPLICATION_DIRECTORY_NAME = "snapshot";
    public static final String RELEASE_APPLICATION_DIRECTORY_NAME = "release";

    /**
     * 字符集相关常量
     */
    public static final String CHARSET_UTF_8 = "UTF-8";
    public static final String CHARSET_GBK = "GBK";

    /**
     * 配置相关常量
     */
    public static final String CONFIGURATION_KEY = "configurationKey";
    public static final String CONFIGURATION_VALUE = "configurationValue";
    public static final Integer CONFIGURATION_TYPE_APPLICATION = 1;
    public static final Integer CONFIGURATION_TYPE_LOGBACK = 2;
    public static final Integer CONFIGURATION_TYPE_PRODUCTION = 3;

    public static final String FALSE = "false";
    public static final String TRUE = "true";

    /**
     * 共享文件夹目录
     */
    public static final String FILE_SHARE_DIRECTORY = "file.share.directory";

    /**
     * redis 配置相关常量
     */
    public static final String JEDIS_SENTINEL_POOL = "JedisSentinelPool";
    public static final String JEDIS_POOL = "JedisPool";
    public static final String REDIS_POOL_TYPE = "redis.pool.type";
    public static final String REDIS_POOL_HOST = "redis.pool.host";
    public static final String REDIS_POOL_PORT = "redis.pool.port";
    public static final String REDIS_POOL_PASSWORD = "redis.pool.password";

    public static final String SERVICE_NAME_ERP = "erp";

    public static final Integer SYSTEM_PRIVILEGE_TYPE_ONE_LEVEL_MENU = 4;
    public static final Integer SYSTEM_PRIVILEGE_TYPE_PERMIT_ALL = 1;
    public static final Integer SYSTEM_PRIVILEGE_TYPE_HAS_AUTHORITY = 2;
    public static final Integer SYSTEM_PRIVILEGE_TYPE_AUTHENTICATED = 3;
    public static final Integer SYSTEM_PRIVILEGE_MENU_TYPE_ONE_LEVEL_MENU = 1;
    public static final Integer SYSTEM_PRIVILEGE_MENU_TYPE_TWO_LEVEL_MENU = 2;
    public static final Integer SYSTEM_PRIVILEGE_MENU_TYPE_BUTTON = 3;
    public static final Integer SYSTEM_PRIVILEGE_MENU_TYPE_OTHER = 4;
    public static final String AUTHENTICATED = "authenticated";
    public static final String PERMIT_ALL = "permitAll";
    public static final String HAS_AUTHORITY_FORMAT = "hasAuthority('role_%s')";
    public static final String SIMPLE_GRANTED_AUTHORITY_ROLE_FORMAT = "role_%s";

    public static final Integer USER_TYPE_SUPER_ADMINISTRATOR = 1;
    public static final Integer USER_TYPE_ORDINARY_USER = 2;

    public static final String SEQUENCE_NAME_SYSTEM_USER_CODE = "system_user_code";
    public static final String SEQUENCE_NAME_POWER_STATION_CODE = "power_station_code";
    public static final String SEQUENCE_NAME_SYSTEM_ROLE_CODE = "system_role_code";

    public static final Integer DAILY_DELIVERY_TYPE_PHOTOVOLTAIC_DATA = 1;
    public static final Integer DAILY_DELIVERY_TYPE_WIND_POWER_DATA = 2;
    public static final Integer DAILY_DELIVERY_STATUS_NOT_AUDITED = 1;
    public static final Integer DAILY_DELIVERY_STATUS_AUDITED = 2;
    public static final Integer DAILY_DELIVERY_STATUS_REJECTED = 3;
    public static final String MONTHLY_COMPLETION_RATE_SALT_VALUE = "monthly.completion.rate.salt.value";
    public static final String ANNUAL_COMPLETION_RATE_SALT_VALUE = "annual.completion.rate.salt.value";

    public static final String OUTSIDE = "outside";
    public static final String SERVICE_DOMAIN = "service.domain";

    public static final String HTTP = "http://";
    public static final String HTTPS = "https://";
    public static final String LOGIN_LOGO_CONFIGURATION_KEY = "login.logo";
    public static final String LOGO_CONFIGURATION_KEY = "logo";
    public static final String FAVICON_CONFIGURATION_KEY = "favicon";
    public static final String COPYRIGHT_CONFIGURATION_KEY = "copyright";

    public static final String CONTENT_FOLDER = "contentFolder";

    /** 错误日志格式化，错误描述：controllerName.actionName-exceptionSimpleName-exceptionMessage-请求参数*/
    public static final String LOGGER_ERROR_FORMAT = "{}:{}.{}-{}-{}-{}";

    public static final String TRUNCATE_TABLE_JOB_CRON_EXPRESSION = "truncate.table.job.cron.expression";
    public static final String SOCKET_SERVER_PORT = "socket.server.port";

    public static final String SEQUENCE_NAME_TENANT_CODE = "tenant_code";

    public static final String MD5 = "MD5";
    public static final String WEI_XIN_PAY_UNIFIEDORDER_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
}
