package build.dream.catering.mappers;

import build.dream.common.erp.catering.domains.ElemeRefundOrderMessageGoodsItem;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ElemeRefundOrderMessageGoodsItemMapper {
    long insert(ElemeRefundOrderMessageGoodsItem elemeRefundOrderMessageGoodsItem);
    long insertAll(List<ElemeRefundOrderMessageGoodsItem> elemeRefundOrderMessageGoodsItems);
}
