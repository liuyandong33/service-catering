package build.dream.erp.configurations;

import build.dream.erp.utils.DataSourceContextHolder;
import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DataSourceConfiguration {
    @Bean(name = "primaryDataSource")
    @ConfigurationProperties(prefix = "datasource.primary")
    public DataSource primaryDataSource() {
        return DataSourceBuilder.create().type(DruidDataSource.class).build();
    }

    @Bean(name = "secondaryDataSource")
    @ConfigurationProperties(prefix = "datasource.secondary")
    public DataSource secondaryDataSource() {
        return DataSourceBuilder.create().type(DruidDataSource.class).build();
    }

    @Bean(name = "multipleDataSource")
    @Primary
    public DataSource dataSourceContextHolder() {
        DataSourceContextHolder dataSourceContextHolder = (DataSourceContextHolder) DataSourceBuilder.create().type(DataSourceContextHolder.class).build();
        Map<Object, Object> targetDataSources = new HashMap<Object, Object>();
        targetDataSources.put("primaryDataSource", primaryDataSource());
        targetDataSources.put("secondaryDataSource", secondaryDataSource());
        dataSourceContextHolder.setDefaultTargetDataSource(primaryDataSource());
        dataSourceContextHolder.setTargetDataSources(targetDataSources);
        return dataSourceContextHolder;
    }
}
