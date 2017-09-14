package build.dream.erp.configurations;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfiguration {
    @Bean(name = "primaryDataSource")
    @ConfigurationProperties(prefix = "datasource.primary")
    public DataSource primaryDataSource() {
        return DataSourceBuilder.create().type(DruidDataSource.class).build();
    }
}
