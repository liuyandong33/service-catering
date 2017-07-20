package erp.chain.mappers;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * Created by liuyandong on 2017/6/16.
 */
@Mapper
public interface SequenceMapper {
    Integer nextValue(@Param("sequenceName") String sequenceName);
}
