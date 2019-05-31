package build.dream.catering.ibatis;

import build.dream.common.utils.DatabaseUtils;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Properties;

@Component
public class DatabaseIdProvider implements org.apache.ibatis.mapping.DatabaseIdProvider {
    @Override
    public void setProperties(Properties properties) {
    }

    @Override
    public String getDatabaseId(DataSource dataSource) throws SQLException {
        return DatabaseUtils.obtainDatabaseId(dataSource.getConnection(), true);
    }
}
