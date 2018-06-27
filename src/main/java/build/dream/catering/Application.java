package build.dream.catering;

import build.dream.common.erp.catering.domains.Goods;
import build.dream.common.utils.GsonUtils;
import build.dream.common.utils.NamingStrategyUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.kafka.annotation.EnableKafka;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.*;

@ServletComponentScan
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableDiscoveryClient
@EnableKafka
public class Application extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(Application.class);
    }

    public static void main(String[] args) throws SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException, InvocationTargetException {
//        SpringApplication.run(Application.class, args);
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/catering-db?serverTimezone=GMT%2B8&useSSL=true", "root", "root");
        Statement statement = connection.createStatement();
        statement.execute("select * from goods");
        ResultSet resultSet = statement.getResultSet();
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        int columnCount = resultSetMetaData.getColumnCount();

        resultSet.next();
        String[] columnNames = new String[columnCount];
        int[] columnTypes = new int[columnCount];
        for (int index = 1; index <= columnCount; index++) {
            columnNames[index - 1] = resultSetMetaData.getColumnName(index);
            columnTypes[index - 1] = resultSetMetaData.getColumnType(index);
        }

        List<Goods> results = new ArrayList<Goods>();
        while (resultSet.next()) {
            Map<String, Object> result = new LinkedHashMap<String, Object>();
            for (int index = 0; index < columnCount; index++) {
                int columnIndex = index + 1;
                Object object = null;
                int columnType = columnTypes[index];
                if (columnType == Types.BIT) {
                    object = resultSet.getByte(columnIndex);
                } else if (columnType == Types.TINYINT) {
                    object = resultSet.getInt(columnIndex);
                } else if (columnType == Types.SMALLINT) {
                    object = resultSet.getInt(columnIndex);
                } else if (columnType == Types.INTEGER) {
                    object = resultSet.getInt(columnIndex);
                } else if (columnType == Types.BIGINT) {
                    object = resultSet.getLong(columnIndex);
                } else if (columnType == Types.FLOAT) {

                } else if (columnType == Types.REAL) {

                } else if (columnType == Types.DOUBLE) {

                } else if (columnType == Types.NUMERIC) {

                } else if (columnType == Types.DECIMAL) {
                    object = resultSet.getDouble(columnIndex);
                } else if (columnType == Types.CHAR) {
                    object = resultSet.getString(columnIndex);
                } else if (columnType == Types.VARCHAR) {
                    object = resultSet.getString(columnIndex);
                } else if (columnType == Types.LONGVARCHAR) {

                } else if (columnType == Types.DATE) {
                    object = resultSet.getDate(columnIndex);
                } else if (columnType == Types.TIME) {
                    object = resultSet.getTime(columnIndex);
                } else if (columnType == Types.TIMESTAMP) {
                    object = resultSet.getTimestamp(columnIndex);
                } else if (columnType == Types.BINARY) {

                } else if (columnType == Types.VARBINARY) {

                } else if (columnType == Types.LONGVARBINARY) {

                } else if (columnType == Types.NULL) {

                } else if (columnType == Types.OTHER) {

                } else if (columnType == Types.JAVA_OBJECT) {

                } else if (columnType == Types.DISTINCT) {

                } else if (columnType == Types.STRUCT) {

                } else if (columnType == Types.ARRAY) {

                } else if (columnType == Types.BLOB) {

                } else if (columnType == Types.CLOB) {

                } else if (columnType == Types.REF) {

                } else if (columnType == Types.DATALINK) {

                } else if (columnType == Types.BOOLEAN) {

                } else if (columnType == Types.ROWID) {

                }
                result.put(NamingStrategyUtils.underscoreToCamelCase(columnNames[index]), object);
            }
            Goods goods = Goods.class.newInstance();
            BeanUtils.populate(goods, result);
            results.add(goods);
        }
        System.out.println(GsonUtils.toJson(results));
    }
}
