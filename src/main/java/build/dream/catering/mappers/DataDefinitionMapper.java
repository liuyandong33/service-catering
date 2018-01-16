package build.dream.catering.mappers;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DataDefinitionMapper {
    long createTable(@Param("tableName") String tableName, @Param("columnDescribes") List<String> columnDescribes);
    long createTableWithTemplate(@Param("tableName") String tableName, @Param("templateTableName") String templateTableName);
}
