package build.dream.erp.mappers;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface DataDefinitionMapper {
    long createTable(@Param("tableName") String tableName, @Param("columnDescribes") List<String> columnDescribes);
    long createTableWithTemplate(@Param("tableName") String tableName, @Param("templateTableName") String templateTableName);
}
