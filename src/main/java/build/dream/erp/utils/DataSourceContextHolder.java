package build.dream.erp.utils;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class DataSourceContextHolder extends AbstractRoutingDataSource {
    public static final ThreadLocal<String> CONTEXT_HOLDER = new ThreadLocal<String>();

    public static void setDataSourceName(String dataSourceName) {
        CONTEXT_HOLDER.set(dataSourceName);
    }

    public static String getDataSourceName() {
        String dataSourceName = CONTEXT_HOLDER.get();
        return dataSourceName;
    }

    public void clearContext() {
        CONTEXT_HOLDER.remove();
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return DataSourceContextHolder.getDataSourceName();
    }
}
