package build.dream.erp.mappers;

import build.dream.common.erp.domains.ElemeRefundOrderMessage;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ElemeRefundOrderMessageMapper {
    long insert(ElemeRefundOrderMessage elemeRefundOrderMessage);
}
