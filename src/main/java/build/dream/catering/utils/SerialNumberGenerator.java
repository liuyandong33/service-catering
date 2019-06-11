package build.dream.catering.utils;

import build.dream.common.orm.SnowflakeIdGenerator;

public class SerialNumberGenerator {
    private static final SnowflakeIdGenerator SNOWFLAKE_ID_GENERATOR = new SnowflakeIdGenerator();

    public static String generateSerialNumber() {
        return SNOWFLAKE_ID_GENERATOR.nextId().toString();
    }

    public static String generateSerialNumber(String prefix) {
        return prefix + SNOWFLAKE_ID_GENERATOR.nextId();
    }
}
